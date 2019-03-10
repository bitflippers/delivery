package app.beamcatcher.modelserver.io.eventserver.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.Util;

public class EventProcessor {

	private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);

	public static void processEvent(final String pDataFileContents, final String pEventIdentifier, final World pWorld) {

		String logMessage = null;

		try {

			if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_JOINED)) {
				EventUserGrantedAccessMessage eventUserGrantedAccessMessage = Util.OBJECT_MAPPER
						.readValue(pDataFileContents, EventUserGrantedAccessMessage.class);
				ModelValidator.validate(eventUserGrantedAccessMessage);
				logMessage = processEvent(eventUserGrantedAccessMessage, pWorld);
			} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_LEFT)) {
				EventUserLeftMessage eventUserLeftMessage = Util.OBJECT_MAPPER.readValue(pDataFileContents,
						EventUserLeftMessage.class);
				ModelValidator.validate(eventUserLeftMessage);
				logMessage = processEvent(eventUserLeftMessage, pWorld);
			} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_PLACED_MARKER)) {
				EventMarkerPlacedMessage eventMarkerPlacedMessage = Util.OBJECT_MAPPER.readValue(pDataFileContents,
						EventMarkerPlacedMessage.class);
				ModelValidator.validate(eventMarkerPlacedMessage);
				logMessage = processEvent(eventMarkerPlacedMessage, pWorld);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		logger.info(logMessage);
	}

	public static String processEvent(final EventMarkerPlacedMessage pEventMarkerPlacedMessage, final World pWorld) {
		String username = pEventMarkerPlacedMessage.getUsername();
		Double latitude = pEventMarkerPlacedMessage.getLatitude();
		Double longitude = pEventMarkerPlacedMessage.getLongitude();
		Integer priority = pEventMarkerPlacedMessage.getPriority();
		Integer requestedUnits = pEventMarkerPlacedMessage.getRequestedUnits();
		pWorld.addMarkerToUser(username, latitude, longitude, requestedUnits, priority);
		return "User '" + username + "' placed a marker at latlong: (" + latitude + "," + longitude
				+ ") with requestedUnits: '" + requestedUnits + "' and priority: '" + priority + "'";
	}

	public static String processEvent(final EventUserLeftMessage pEventUserLeftMessage, final World pWorld) {
		final String username = pEventUserLeftMessage.getUsername();
		pWorld.removeUser(username);
		return "User '" + username + "' left !";
	}

	public static String processEvent(final EventUserGrantedAccessMessage pEventUserGrantedAccessMessage,
			final World pWorld) {
		final String username = pEventUserGrantedAccessMessage.getUsername();
		final Integer slotIdentifier = pEventUserGrantedAccessMessage.getSlotIdentifier();
		pWorld.addNewUser(username, slotIdentifier);
		logger.info("Total number of users: " + pWorld.getMapUser().keySet().size());
		return "User '" + username + "' joined !";
	}

}
