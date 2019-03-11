package app.beamcatcher.modelserver.io.eventserver.in;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.Satellite;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.Util;

public class EventProcessor {

	private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);

	public static void processEvent(final String pDataFileContents, final String pEventIdentifier) {

		String logMessage = null;

		try {

			if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_JOINED)) {
				EventUserGrantedAccessMessage eventUserGrantedAccessMessage = Util.OBJECT_MAPPER
						.readValue(pDataFileContents, EventUserGrantedAccessMessage.class);
				ModelValidator.validate(eventUserGrantedAccessMessage);
				logMessage = processEvent(eventUserGrantedAccessMessage);
			} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_LEFT)) {
				EventUserLeftMessage eventUserLeftMessage = Util.OBJECT_MAPPER.readValue(pDataFileContents,
						EventUserLeftMessage.class);
				ModelValidator.validate(eventUserLeftMessage);
				logMessage = processEvent(eventUserLeftMessage);
			} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_PLACED_MARKER)) {
				EventMarkerPlacedMessage eventMarkerPlacedMessage = Util.OBJECT_MAPPER.readValue(pDataFileContents,
						EventMarkerPlacedMessage.class);
				ModelValidator.validate(eventMarkerPlacedMessage);
				logMessage = processEvent(eventMarkerPlacedMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		if (logMessage != null) {
			logger.info(logMessage);
			printWorld();
		} else {
			logger.warn("Event discarded - event production too high ?");
		}

	}

	// Some events might have been built on "stale" worlds if events are being
	// produced faster than they can be consumed...
	// Simply discard..
	// Eventually, things should be consistent as consumer has auto-throttle...

	private static void printWorld() {
		final StringBuffer stringBuffer = new StringBuffer();
		Integer totalNumberOfMarkers = 0;
		Integer totalRequestedUnits = 0;
		final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
		final Set<UUID> setUserUUID = mapUser.keySet();
		for (UUID userUUID : setUserUUID) {
			final User user = mapUser.get(userUUID);
			final HashMap<UUID, Marker> mapMarker = user.getMapMarker();
			final int numberOfMarkersForUser = mapMarker.size();
			stringBuffer.append("" + user.getUsername().toString() + " (" + numberOfMarkersForUser + ") ");
			totalNumberOfMarkers += numberOfMarkersForUser;
			final Set<UUID> setMarkerUUID = mapMarker.keySet();
			for (UUID markerUUID : setMarkerUUID) {
				final Marker marker = mapMarker.get(markerUUID);
				totalRequestedUnits = totalRequestedUnits + marker.getRequestedUnits();
			}
		}
		Integer totalNumberOfBeams = 0;
		final Map<UUID, Satellite> mapSatellite = WorldSingleton.INSTANCE.getMapSatellite();
		final Set<UUID> uuidSatellite = mapSatellite.keySet();
		for (UUID uuid : uuidSatellite) {
			final Satellite satellite = mapSatellite.get(uuid);
			totalNumberOfBeams = totalNumberOfBeams + satellite.getMapBeam().size();
		}

		final Integer maximumMarkers = Configuration.WORLD_MAXIMUM_NUMBER_OF_USERS * Configuration.USER_MARKER_MAX;
		final Integer totalSatelliteProducedUnits = Configuration.WORLD_SATELLITE_1_PRODUCED_UNITS
				+ Configuration.WORLD_SATELLITE_2_PRODUCED_UNITS + Configuration.WORLD_SATELLITE_3_PRODUCED_UNITS;
		logger.info("Total Number Of Beams: " + totalNumberOfBeams);
		logger.info("Total Number Of Users: " + setUserUUID.size() + "/" + Configuration.WORLD_MAXIMUM_NUMBER_OF_USERS);
		logger.info("Total Number Of Markers: " + totalNumberOfMarkers + "/" + maximumMarkers);
		logger.info("Total Requested Units: " + totalRequestedUnits + "/" + totalSatelliteProducedUnits);
		logger.info("Markers Per User: " + stringBuffer.toString());
	}

	private static String processEvent(final EventMarkerPlacedMessage pEventMarkerPlacedMessage) {
		String result = null;
		try {
			String username = pEventMarkerPlacedMessage.getUsername();
			Double latitude = pEventMarkerPlacedMessage.getLatitude();
			Double longitude = pEventMarkerPlacedMessage.getLongitude();
			Integer priority = pEventMarkerPlacedMessage.getPriority();
			Integer requestedUnits = pEventMarkerPlacedMessage.getRequestedUnits();
			final String oldWorldJSON = Util.getJSON(WorldSingleton.INSTANCE);
			final World newWorld = Util.OBJECT_MAPPER.readValue(oldWorldJSON, World.class);
			newWorld.addMarkerToUser(username, latitude, longitude, requestedUnits, priority);
			if (ModelValidator.isValid(newWorld)) {
				WorldSingleton.INSTANCE = newWorld;
				result = "User '" + username + "' placed a marker at latlong: (" + latitude + "," + longitude
						+ ") with requestedUnits: '" + requestedUnits + "' and priority: '" + priority;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	private static String processEvent(final EventUserLeftMessage pEventUserLeftMessage) {
		String result = null;
		try {
			final String username = pEventUserLeftMessage.getUsername();
			final String oldWorldJSON = Util.getJSON(WorldSingleton.INSTANCE);
			final World newWorld = Util.OBJECT_MAPPER.readValue(oldWorldJSON, World.class);
			newWorld.removeUser(username);
			if (ModelValidator.isValid(newWorld)) {
				WorldSingleton.INSTANCE = newWorld;
				result = "User '" + username + "' left !";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

	private static String processEvent(final EventUserGrantedAccessMessage pEventUserGrantedAccessMessage) {
		String result = null;
		try {
			final String username = pEventUserGrantedAccessMessage.getUsername();
			final Integer slotIdentifier = pEventUserGrantedAccessMessage.getSlotIdentifier();
			final String oldWorldJSON = Util.getJSON(WorldSingleton.INSTANCE);
			final World newWorld = Util.OBJECT_MAPPER.readValue(oldWorldJSON, World.class);
			newWorld.addNewUser(username, slotIdentifier);
			if (ModelValidator.isValid(newWorld)) {
				WorldSingleton.INSTANCE = newWorld;
				return "User '" + username + "' joined !";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return result;
	}

}
