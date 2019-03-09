package app.beamcatcher.modelserver.io.sadrema.out;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import app.beamcatcher.modelserver.model.Location;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.SADREMAGridCell;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;

public class WorldToMarkersCSVFileMapper {

	public static StringBuffer toMarkersCSVFile(final World pWorld) {

		final StringBuffer output = new StringBuffer();

		final Map<UUID, User> mapUser = pWorld.getMapUser();

		final Set<UUID> setUserUUID = mapUser.keySet();

		for (UUID userUUID : setUserUUID) {
			final User user = mapUser.get(userUUID);
			final CharSequence username = user.getUsername();
			final HashMap<UUID, Marker> mapMarker = user.getMapMarker();
			final Set<UUID> setMarkerUUID = mapMarker.keySet();
			for (UUID markerUUID : setMarkerUUID) {
				final Marker marker = mapMarker.get(markerUUID);
				final Location location = marker.getLocation();
				final SADREMAGridCell sadremaGridCell = location.getSadremaGridCell();
				final Integer rowIndex = sadremaGridCell.getRowIndex();
				final Integer columnIndex = sadremaGridCell.getColumnIndex();
				final Integer requestedUnits = marker.getRequestedUnits();
				final Integer priority = marker.getPriority();
				final String row = columnIndex + "-" + rowIndex + "," + requestedUnits + "," + priority + ","
						+ username;
				output.append(row);
				output.append(System.getProperty("line.separator"));
			}
		}

		return output;
	}

}
