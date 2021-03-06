package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Footprint;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.SADREMAGridCell;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.Util;

public class SadremaSolutionRetrieverRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SadremaSolutionRetrieverRunnable.class);

	public void run() {

		logger.info("SadremaSolutionRetriever started !");
		logger.info("SadremaSolutionRetriever rate in milliseconds: "
				+ Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

		logger.info("Starting polling !");

		String allMarkerJSONPrevious = "";

		try {

			do {

				logger.info("Analyzing world...");

				WorldSemaphore.semaphore.acquire();

				final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
				final Map<UUID, Marker> mapAllMarker = new HashMap<UUID, Marker>();
				final Set<UUID> setUUIDUser = mapUser.keySet();
				final List<UUID> listAllMarkerUUID = new ArrayList<UUID>();
				Integer totalMarkers = 0;
				for (UUID userUUID : setUUIDUser) {
					final User user = mapUser.get(userUUID);
					final HashMap<UUID, Marker> mapMarkerForUser = user.getMapMarker();
					totalMarkers = totalMarkers + mapMarkerForUser.size();
					final Set<UUID> setUUIDMarkerForUser = mapMarkerForUser.keySet();
					for (UUID uuidMarker : setUUIDMarkerForUser) {
						listAllMarkerUUID.add(uuidMarker);
						final Marker marker = mapMarkerForUser.get(uuidMarker);
						mapAllMarker.put(uuidMarker, marker);
					}
				}

				if (totalMarkers > 0) {

					List<Marker> listMarker = new ArrayList<Marker>();
					Collections.sort(listAllMarkerUUID);
					for (UUID uuidMarker : listAllMarkerUUID) {
						final Marker marker = mapAllMarker.get(uuidMarker);
						listMarker.add(marker);
					}
					String allMarkerJSON = Util.getJSON(listMarker);

					if (!allMarkerJSON.equals(allMarkerJSONPrevious)) {
						logger.info("Markers changed !");
						logger.info("Invoking sadrema...");
						final StringBuffer markersCSVInput = WorldToMarkersCSVFileMapper.toMarkersCSVFile();
						CSVWriter.writeToFiles(markersCSVInput);
						final File[] files = SadremaHelper.obtainSolution(listMarker);
						applyBeams(files[1], listMarker);
						files[0].delete();
						File input = new File(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA + "/markers.csv");
						input.delete();
						allMarkerJSONPrevious = allMarkerJSON;
					} else {
						logger.info("No change in markers");
						logger.info("Sadrema not invoked");
					}

				} else {
					logger.info("No markers");
					logger.info("Sadrema not invoked");
				}

				WorldSemaphore.semaphore.release();

				Thread.sleep(Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

			} while (true);

		} catch (

		Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void applyBeams(final File pFile, final List<Marker> pMarker) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pFile));
			final Map<String, Set<Beam>> beamsPerSat = new HashMap<String, Set<Beam>>();

			beamsPerSat.put(Configuration.WORLD_SATELLITE_1_NAME, new HashSet<Beam>());
			beamsPerSat.put(Configuration.WORLD_SATELLITE_2_NAME, new HashSet<Beam>());
			beamsPerSat.put(Configuration.WORLD_SATELLITE_3_NAME, new HashSet<Beam>());

			String line;
			while ((line = br.readLine()) != null) {
				final String[] splitByColon = line.split(":");
				String satelliteName = splitByColon[0].trim();
				String[] beamAndCapacity = splitByColon[1].trim().split(" ");
				String beamName = beamAndCapacity[0];
				String capacityWithEquals = beamAndCapacity[1];
				String[] yaSplit = capacityWithEquals.split("=");
				String capacity = yaSplit[1];
				String[] cellsSplit = line.split("cells");
				String cellsRawComma = cellsSplit[1];
				String[] cells = cellsRawComma.split(",");
				final Beam beam = getBeam(beamName, capacity, cells);
				if (satelliteName.equals(Configuration.WORLD_SATELLITE_1_NAME)) {
					beamsPerSat.get(Configuration.WORLD_SATELLITE_1_NAME).add(beam);
				} else if (satelliteName.equals(Configuration.WORLD_SATELLITE_2_NAME)) {
					beamsPerSat.get(Configuration.WORLD_SATELLITE_2_NAME).add(beam);
				} else if (satelliteName.equals(Configuration.WORLD_SATELLITE_3_NAME)) {
					beamsPerSat.get(Configuration.WORLD_SATELLITE_3_NAME).add(beam);
				} else {
					throw new IllegalStateException();
				}
			}

			final Set<Beam> sat1Beam = new HashSet<Beam>();
			final Set<Beam> sat2Beam = new HashSet<Beam>();
			final Set<Beam> sat3Beam = new HashSet<Beam>();

			final Set<String> keySet = beamsPerSat.keySet();
			for (String satelliteName : keySet) {
				final Set<Beam> setBeam = beamsPerSat.get(satelliteName);
				for (Beam beam : setBeam) {
					if (satelliteName.equals(Configuration.WORLD_SATELLITE_1_NAME)) {
						sat1Beam.add(beam);
					} else if (satelliteName.equals(Configuration.WORLD_SATELLITE_2_NAME)) {
						sat2Beam.add(beam);
					} else if (satelliteName.equals(Configuration.WORLD_SATELLITE_3_NAME)) {
						sat3Beam.add(beam);
					} else {
						throw new IllegalStateException();
					}
				}
			}

			// Apply to model

			// If world is not valid due to being mutated in the meantime, discard...

			final String oldWorldJSON = Util.getJSON(WorldSingleton.INSTANCE);
			final World newWorld = Util.OBJECT_MAPPER.readValue(oldWorldJSON, World.class);
			newWorld.addBeamsToSatellite(Configuration.WORLD_SATELLITE_1_NAME, sat1Beam);
			newWorld.addBeamsToSatellite(Configuration.WORLD_SATELLITE_2_NAME, sat2Beam);
			newWorld.addBeamsToSatellite(Configuration.WORLD_SATELLITE_3_NAME, sat3Beam);
			if (ModelValidator.isValid(newWorld)) {
				WorldSingleton.INSTANCE = newWorld;
				SadremaHelper.doPNG(pMarker, pFile.getParent(), beamsPerSat);
				final String hostname = InetAddress.getLocalHost().getHostName();
				final String context = "archive";
				final String[] split = pFile.getParent().split("/");
				final String dirname = split[split.length - 1];
				final String URL = "http://" + hostname + "/" + context + "/" + dirname;
				logger.info("Results at " + URL);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}

	private Beam getBeam(final String pBeamName, final String pCapacity, final String[] pCells) {
		final Set<SADREMAGridCell> setSADREMAGridCell = new HashSet<SADREMAGridCell>();
		final Integer deliveredUnits = Integer.valueOf(pCapacity);
		for (int i = 0; i < pCells.length; i++) {
			final String cellXDashY = pCells[i].trim();
			String[] split = cellXDashY.split("-");
			final String x = split[0];
			final String y = split[1];

			final Integer rowIndex = Integer.valueOf(x);
			final Integer columnIndex = Integer.valueOf(y);

			final SADREMAGridCell sadremaGridCell = new SADREMAGridCell(rowIndex, columnIndex);
			setSADREMAGridCell.add(sadremaGridCell);
		}

		final Footprint footprint = new Footprint(setSADREMAGridCell);
		final Beam beam = new Beam(pBeamName, deliveredUnits, footprint);
		return beam;
	}

}
