package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Footprint;
import app.beamcatcher.modelserver.model.SADREMAGridCell;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.persistence.WorldSingleton;

public class SadremaSolutionRetrieverRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SadremaSolutionRetrieverRunnable.class);

	public void run() {

		logger.info("SadremaSolutionRetriever started !");
		logger.info("SadremaSolutionRetriever rate in milliseconds: "
				+ Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

		logger.info("Starting polling !");

		try {

			do {

				logger.info("SadremaSolutionRetriever requesting lock...");
				WorldSemaphore.semaphore.acquire();
				logger.info("SadremaSolutionRetriever acquired lock !!!");
				logger.info("SadremaSolutionRetriever getting to work...");

				final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
				final Set<UUID> setUUIDUser = mapUser.keySet();
				Integer totalMarkers = 0;
				for (UUID userUUID : setUUIDUser) {
					final User user = mapUser.get(userUUID);
					totalMarkers = totalMarkers + user.getMapMarker().size();
				}

				logger.info("Total markers: " + totalMarkers);

				if (totalMarkers > 0) {

					logger.info("Writing CSVs...");

					final StringBuffer markersCSVInput = WorldToMarkersCSVFileMapper
							.toMarkersCSVFile(WorldSingleton.INSTANCE);

					CSVWriter.writeToFiles(markersCSVInput);

					logger.info("Done !");

					final File beamsTXTFile = SadremaHelper.obtainSolution();

					applyBeams(beamsTXTFile);

					beamsTXTFile.delete();

					File input = new File(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA + "/markers.csv");

					input.delete();

				} else {
					logger.info("No markers, nothing to do !");
				}

				logger.info("SadremaSolutionRetriever finished work ! realising lock !!!");
				WorldSemaphore.semaphore.release();

				logger.info("Sleeping for " + Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS
						+ " milliseconds...");
				Thread.sleep(Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

			} while (true);

		} catch (

		Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void applyBeams(final File pFile) {
		logger.info("Processing beams from Sadrema:");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pFile));
			final Map<String, Set<Beam>> beamsPerSat = new HashMap<String, Set<Beam>>();

			beamsPerSat.put(Configuration.WORLD_SATELLITE_1_NAME, new HashSet<Beam>());
			beamsPerSat.put(Configuration.WORLD_SATELLITE_2_NAME, new HashSet<Beam>());
			beamsPerSat.put(Configuration.WORLD_SATELLITE_3_NAME, new HashSet<Beam>());

			String line;
			while ((line = br.readLine()) != null) {
				logger.info(line);

				final String[] splitByColon = line.split(":");
				String satelliteName = splitByColon[0].trim();
				String[] beamAndCapacity = splitByColon[1].trim().split(" ");
				String beamName = beamAndCapacity[0];
				String capacityWithEquals = beamAndCapacity[1];
				String[] yaSplit = capacityWithEquals.split("=");
				String capacity = yaSplit[1];
				logger.info("SatelliteName: " + satelliteName);
				logger.info("BeamName: " + beamName);
				logger.info("Capacity: " + capacity);
				String[] cellsSplit = line.split("cells");
				String cellsRawComma = cellsSplit[1];
				logger.info("Cells: " + cellsRawComma);
				String[] cells = cellsRawComma.split(",");
				for (int i = 0; i < cells.length; i++) {
					String cellTemp = cells[i].trim();
					logger.info("Cell (" + i + "/" + cells.length + "): " + cellTemp);
				}
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

			logger.info("Beams on satelite 1:");
			for (Beam beam : sat1Beam) {
				logger.info(beam.toString());
			}
			logger.info("Beams on satelite 2:");
			for (Beam beam : sat2Beam) {
				logger.info(beam.toString());
			}
			logger.info("Beams on satelite 3:");
			for (Beam beam : sat3Beam) {
				logger.info(beam.toString());
			}

			// Apply to model
			WorldSingleton.INSTANCE.addBeamsToSatellite(Configuration.WORLD_SATELLITE_1_NAME, sat1Beam);
			WorldSingleton.INSTANCE.addBeamsToSatellite(Configuration.WORLD_SATELLITE_2_NAME, sat2Beam);
			WorldSingleton.INSTANCE.addBeamsToSatellite(Configuration.WORLD_SATELLITE_3_NAME, sat3Beam);

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
