package app.beamcatcher.modelserver.io.sadrema.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import app.beamcatcher.modelserver.model.Satellite;
import app.beamcatcher.modelserver.model.World;

public class WorldToSatellitesCSVFileMapper {

	public static StringBuffer toSatellitesCSVFile(final World pWorld) {

		final StringBuffer output = new StringBuffer();

		final Map<UUID, Satellite> mapSatellite = pWorld.getMapSatellite();

		final Set<UUID> setSatelliteUUID = mapSatellite.keySet();

		for (UUID satelliteUUID : setSatelliteUUID) {
			final Satellite satellite = mapSatellite.get(satelliteUUID);
			final CharSequence satelliteName = satellite.getName();
			final Integer producedUnits = satellite.getProducedUnits();
			output.append(satelliteName + "," + producedUnits);
			output.append(System.getProperty("line.separator"));
		}

		return output;
	}

}
