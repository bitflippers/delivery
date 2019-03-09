package app.beamcatcher.modelserver.test;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import app.beamcatcher.modelserver.io.eventserver.in.EventMarkerPlacedMessage;
import app.beamcatcher.modelserver.util.Util;

public class Temp {

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
//		String eventIdentifier = "USER_PLACED_MARKER";
//		String username = "username";
//		Double latitude = 0.0d;
//		Double longitude = 0.0d;
//		Integer requestedUnits = 1;
//		Integer priority = 1;
//		EventMarkerPlacedMessage eventMarkerPlacedMessage = new EventMarkerPlacedMessage();
//		eventMarkerPlacedMessage.setEventIdentifier(eventIdentifier);
//		eventMarkerPlacedMessage.setUsername(username);
//		eventMarkerPlacedMessage.setLatitude(latitude);
//		eventMarkerPlacedMessage.setLongitude(longitude);
//		eventMarkerPlacedMessage.setRequestedUnits(requestedUnits);
//		eventMarkerPlacedMessage.setPriority(priority);
//		String json = Util.getJSON(eventMarkerPlacedMessage);
//		System.out.println(json);
//
//		Util.OBJECT_MAPPER.readValue(json, EventUserGrantedAccessMessage.class);

		String eventIdentifier = "USER_PLACED_MARKER";
		String username = "username";
		EventMarkerPlacedMessage eventMarkerPlacedMessage = new EventMarkerPlacedMessage();
		eventMarkerPlacedMessage.setEventIdentifier(eventIdentifier);
		eventMarkerPlacedMessage.setUsername(username);
		eventMarkerPlacedMessage.setLatitude(0.0d);
		eventMarkerPlacedMessage.setLongitude(0.0d);
		eventMarkerPlacedMessage.setRequestedUnits(0);
		eventMarkerPlacedMessage.setPriority(0);
		String json = Util.getJSON(eventMarkerPlacedMessage);
		System.out.println(json);

		EventMarkerPlacedMessage object = (EventMarkerPlacedMessage) Util.OBJECT_MAPPER.readValue(json,
				EventMarkerPlacedMessage.class);

		System.out.println(object.toString());
	}

}
