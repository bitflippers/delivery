package app.beamcatcher.modelserver.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.Beam;
import app.beamcatcher.modelserver.model.Footprint;
import app.beamcatcher.modelserver.model.SADREMAGridCell;
import app.beamcatcher.modelserver.model.Satellite;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.RandomMGRSWGS84Coordinates;
import app.beamcatcher.modelserver.util.Util;

public class Test {

	public static List<String> USERS = new ArrayList<String>();

	public static void addUsers(final Integer pNumberOfUsers) {
		World world = WorldSingleton.INSTANCE;
		for (int i = 1; i <= pNumberOfUsers; i++) {
			String randomUsername = Util.getRandomUsername();
			world.addNewUser(null, null);
			USERS.add(randomUsername);
			System.out.println("Added user '" + randomUsername + "' (" + i + "/" + pNumberOfUsers + ")");
		}
	}

	public static void addMarkers(final Integer pMarkersPerUserRatio, final World pWorld) {

		final Integer markersToAddPerUser = Configuration.USER_MARKER_MAX / pMarkersPerUserRatio;

		System.out.println("Adding " + markersToAddPerUser + " markers per user");

		for (String username : USERS) {

			for (int i = 1; i <= markersToAddPerUser; i++) {
				RandomMGRSWGS84Coordinates randomLocation = Util.getRandomLocation();
				Double pLatitude = randomLocation.getLatitude();
				Double pLongitude = randomLocation.getLongitude();
				Integer pRequestedUnits = Util.getRandomRequestedUnits();
				Integer pPriority = Util.getRandomPriority();
				pWorld.addMarkerToUser(username, pLatitude, pLongitude, pRequestedUnits, pPriority);
				System.out.println("Added marker (" + i + "/" + markersToAddPerUser + ") for user " + username);
			}

		}
	}

	public static void addBeams(final Integer pNumberBeamsRatio, final Integer pCapacityUsageDistribution,
			final World pWorld) {
		final Integer beamsToAddPerSatellite = Configuration.SATELLITE_MAXIMUM_NUMBER_OF_BEAMS / pNumberBeamsRatio;
		System.out.println("Will add " + beamsToAddPerSatellite + " beams per satellite");
		final Map<UUID, Satellite> mapSatellite = pWorld.getMapSatellite();
		final Set<UUID> keySet = mapSatellite.keySet();
		final Integer totalNumberOfSatellites = keySet.size();
		Integer numberOfProcessedSatellites = 0;
		for (UUID uuid : keySet) {
			numberOfProcessedSatellites++;
			Satellite satellite = mapSatellite.get(uuid);
			CharSequence satelliteName = satellite.getName();
			System.out.println("Processing satellite '" + satelliteName + "' (" + numberOfProcessedSatellites + "/"
					+ totalNumberOfSatellites + ")");
			Integer unitsPerBeam = satellite.getProducedUnits() / pCapacityUsageDistribution;
			final Set<Beam> setBeam = new HashSet<Beam>();
			for (int i = 1; i <= beamsToAddPerSatellite; i++) {
				final Set<SADREMAGridCell> randomSetSADREMAGridCellForBeam = Util.getRandomSetSADREMAGridCellForBeam();
				final Footprint footprint = new Footprint(randomSetSADREMAGridCellForBeam);
				String beamName = satelliteName + "-BEAM-" + i;
				final Beam beam = new Beam(beamName, unitsPerBeam, footprint);
				setBeam.add(beam);
				System.out.println("Added beam '" + beamName + "' (" + i + "/" + beamsToAddPerSatellite + ") : units: "
						+ unitsPerBeam);
			}
			pWorld.addBeamToSatellite((String) satelliteName, setBeam);
			numberOfProcessedSatellites++;
		}
	}

	public static void addSatellites(final Integer pNumberOfSatellites, final Integer pCapacityPerSatellite) {
		final Set<Satellite> setSatellite = new HashSet<Satellite>();

		for (int i = 1; i <= pNumberOfSatellites; i++) {
			final Satellite satellite = new Satellite("SATELLITE-" + i, pCapacityPerSatellite);
			setSatellite.add(satellite);
		}
		WorldSingleton.INSTANCE = new World(setSatellite, null);
	}

	public static void main(String[] args) throws JsonProcessingException {

		System.out.println("Test requesting lock...");
		try {
			WorldSemaphore.semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Test acquired lock !!!");
		System.out.println("Test getting to work...");

		Integer numberOfSatellites = 3;
		Integer capacityPerSatellite = 20;
		Integer numberOfUsers = 4;
		Integer markersPerUserRatio = 1;
		Integer numberBeamsRatio = Configuration.SATELLITE_MAXIMUM_NUMBER_OF_BEAMS;
		Integer capacityUsageDistribution = Configuration.SATELLITE_MAXIMUM_NUMBER_OF_BEAMS;
		if (args != null && args.length == 6) {
			numberOfSatellites = Integer.valueOf(args[0]);
			capacityPerSatellite = Integer.valueOf(args[1]);
			numberOfUsers = Integer.valueOf(args[2]);
			markersPerUserRatio = Integer.valueOf(args[3]);
			numberBeamsRatio = Integer.valueOf(args[4]);
			capacityUsageDistribution = Integer.valueOf(args[5]);
		}

		addSatellites(numberOfSatellites, capacityPerSatellite);
		final World world = WorldSingleton.INSTANCE;
		addUsers(numberOfUsers);
		addMarkers(markersPerUserRatio, world);
		addBeams(numberBeamsRatio, capacityUsageDistribution, world);

		USERS.clear();

		ModelValidator.validate(world);

		System.out.println("Test finished work ! realeasing lock !!!");
		WorldSemaphore.semaphore.release();
	}
}