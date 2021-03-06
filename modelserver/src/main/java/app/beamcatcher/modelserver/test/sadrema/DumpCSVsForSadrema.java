package app.beamcatcher.modelserver.test.sadrema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import app.beamcatcher.modelserver.bootstrap.Bootstrap;
import app.beamcatcher.modelserver.io.sadrema.out.CSVWriter;
import app.beamcatcher.modelserver.io.sadrema.out.WorldToMarkersCSVFileMapper;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.Satellite;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.test.Test;

public class DumpCSVsForSadrema {

//java -cp <jar>.jar app.beamcatcher.modelserver.test.DumpCSVsForSadrema 3 20 10 1 5 5

	public static void main(String[] args) throws JsonProcessingException {

		Bootstrap.createDirectories();
		Bootstrap.createPropertiesFile();

		System.out.println(
				"Usage: numberOfSatellites capacityPerSatellite numberOfUsers markersPerUserRatio numberBeamsRatio capacityUsageDistribution");
		System.out.println("Example: 3 20 4 1 5 5");

		// String[] argstemp = new String[] { "3", "20", "20", "1", "5", "5" };

		Test.main(args);
		final World world = WorldSingleton.INSTANCE;
		final StringBuffer markersCSVFile = WorldToMarkersCSVFileMapper.toMarkersCSVFile();

		final UUID uuid = CSVWriter.writeToFiles(markersCSVFile);

		final Integer totalRequestedCapacity = doPNG(uuid);
		final Integer totalSatelliteCapacity = getTotalSatelliteCapacity();
		final Integer numberOfSatellites = world.getMapSatellite().size();

		System.out.println("Total requested capacity: " + totalRequestedCapacity);
		System.out.println("Total satellite capacity: " + totalSatelliteCapacity);
		System.out.println("Total satellites: " + numberOfSatellites);

	}

	private static Integer doPNG(final UUID uuid) {
		final Set<Marker> setMarker = new HashSet<Marker>();
		final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
		final Set<UUID> setUserUUID = mapUser.keySet();
		Integer totalRequestedCapacity = 0;
		for (UUID userUUID : setUserUUID) {
			final User user = mapUser.get(userUUID);
			final HashMap<UUID, Marker> mapMarker = user.getMapMarker();
			Set<UUID> setMarkerUUID = mapMarker.keySet();
			for (UUID markerUUID : setMarkerUUID) {
				final Marker marker = mapMarker.get(markerUUID);
				setMarker.add(marker);
				totalRequestedCapacity = totalRequestedCapacity + marker.getRequestedUnits();
			}
		}
		return totalRequestedCapacity;
	}

	private static Integer getTotalSatelliteCapacity() {
		final Map<UUID, Satellite> mapSatellite = WorldSingleton.INSTANCE.getMapSatellite();
		final Set<UUID> setSatelliteUUID = mapSatellite.keySet();
		Integer totalSatelliteCapacity = 0;
		for (UUID satelliteUUID : setSatelliteUUID) {
			final Satellite satellite = mapSatellite.get(satelliteUUID);
			Integer producedUnits = satellite.getProducedUnits();
			totalSatelliteCapacity = totalSatelliteCapacity + producedUnits;
		}
		return totalSatelliteCapacity;
	}

}
