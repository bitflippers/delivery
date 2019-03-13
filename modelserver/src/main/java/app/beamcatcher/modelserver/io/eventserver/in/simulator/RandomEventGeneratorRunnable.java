package app.beamcatcher.modelserver.io.eventserver.in.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javafaker.Faker;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.io.eventserver.in.EventMarkerPlacedMessage;
import app.beamcatcher.modelserver.io.eventserver.in.EventUserGrantedAccessMessage;
import app.beamcatcher.modelserver.io.eventserver.in.EventUserLeftMessage;
import app.beamcatcher.modelserver.model.Game;
import app.beamcatcher.modelserver.model.Slot;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.RandomMGRSWGS84Coordinates;
import app.beamcatcher.modelserver.util.Util;

public class RandomEventGeneratorRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(RandomEventGeneratorRunnable.class);

	public void run() {

		logger.info("Started random event generator !");
		logger.info("Event generation every " + Configuration.MAXIMUM_EVENT_PRODUCTION_RATE_IN_MILLISECONDS + " ms");

		try {
			Integer sleepTime = Configuration.MAXIMUM_EVENT_PRODUCTION_RATE_IN_MILLISECONDS;
			do {
				final Long startTime = System.currentTimeMillis();

				WorldSemaphore.semaphore.acquire();

				final Boolean atLeastOneUser = (WorldSingleton.INSTANCE.getMapUser() != null)
						&& (!WorldSingleton.INSTANCE.getMapUser().isEmpty());

				final Integer totalNumberOfUsers = WorldSingleton.INSTANCE.getMapUser().keySet().size();

				Boolean eventAlreadyPublished = Boolean.FALSE;
				String generatedEvent = null;

				// USER_JOINS
				if (totalNumberOfUsers < Configuration.WORLD_MAXIMUM_NUMBER_OF_USERS) {
					if (randomPercentage(4)) {
						final String username = new Faker().gameOfThrones().character();
						final EventUserGrantedAccessMessage eventUserGrantedAccessMessage = new EventUserGrantedAccessMessage();
						eventUserGrantedAccessMessage.setEventIdentifier(Configuration.EVENT_IDENTIFIER_USER_JOINED);
						eventUserGrantedAccessMessage.setUsername(username);
						final Integer slotIdentifier = getRandomFreeSlot();
						eventUserGrantedAccessMessage.setSlotIdentifier(slotIdentifier);
						generatedEvent = publishEvent(eventUserGrantedAccessMessage);
						eventAlreadyPublished = Boolean.TRUE;
					}
				}

				if (!eventAlreadyPublished && atLeastOneUser) {
					// USER_EXITS
					if (randomPercentage(8) && atLeastOneUser) {
						final UUID userThatExitedUUID = getRandomExistingUser();
						final String userThatExited = (String) WorldSingleton.INSTANCE.getMapUser()
								.get(userThatExitedUUID).getUsername();
						final EventUserLeftMessage eventUserLeftMessage = new EventUserLeftMessage();
						eventUserLeftMessage.setEventIdentifier(Configuration.EVENT_IDENTIFIER_USER_LEFT);
						eventUserLeftMessage.setUsername(userThatExited);
						generatedEvent = publishEvent(eventUserLeftMessage);
						eventAlreadyPublished = Boolean.TRUE;
					}
				}

				if (!eventAlreadyPublished && randomPercentage(2) && atLeastOneUser) {
					// MARKER_ADDED
					final String eventIdentifier = Configuration.EVENT_IDENTIFIER_USER_PLACED_MARKER;
					final UUID userUUID = getRandomExistingUser();
					final User user = WorldSingleton.INSTANCE.getMapUser().get(userUUID);
					final CharSequence username = user.getUsername();
					final Integer numberOfUserMarkers = user.getMapMarker().size();
					final Boolean markersLeft = (numberOfUserMarkers < Configuration.USER_MARKER_MAX);
					if (markersLeft) {
						final RandomMGRSWGS84Coordinates randomLocation = Util.getRandomLocation();
						Double latitude = randomLocation.getLatitude();
						Double longitude = randomLocation.getLongitude();
						Integer requestedUnits = Util.getRandomRequestedUnits();
						Integer priority = Util.getRandomPriority();
						final EventMarkerPlacedMessage eventMarkerPlacedMessage = new EventMarkerPlacedMessage();
						eventMarkerPlacedMessage.setEventIdentifier(eventIdentifier);
						eventMarkerPlacedMessage.setLatitude(latitude);
						eventMarkerPlacedMessage.setLongitude(longitude);
						eventMarkerPlacedMessage.setPriority(priority);
						eventMarkerPlacedMessage.setRequestedUnits(requestedUnits);
						eventMarkerPlacedMessage.setUsername((String) username);
						generatedEvent = publishEvent(eventMarkerPlacedMessage);
					}
				}

				WorldSemaphore.semaphore.release();

				final Long endTime = System.currentTimeMillis();

				final Integer currentNumberOfQueuedEvents = getNumberOfQueuedEvents();

				final Integer delta = 100;

				// throttle
				if (currentNumberOfQueuedEvents > 10) {
					sleepTime = sleepTime + delta;
				} else {
					if (sleepTime > delta
							&& (sleepTime - delta) > Configuration.MAXIMUM_EVENT_PRODUCTION_RATE_IN_MILLISECONDS) {
						sleepTime = sleepTime - delta;
					}
				}

				final Long elapsedTime = endTime - startTime;
				logger.info("Event generated in " + elapsedTime + " ms: " + generatedEvent);
				logger.info("Events in queue: " + currentNumberOfQueuedEvents);
				logger.info("Throttle is " + sleepTime + " ms");
				Thread.sleep(sleepTime);

			} while (true);

		} catch (

		InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Integer getRandomFreeSlot() {
		final Game game = WorldSingleton.INSTANCE.getGame();
		final Set<Slot> freeSlots = game.getFreeSlots();
		final Object[] arrayOfFreeSlot = freeSlots.toArray();
		final Integer numberOfFreeSlots = arrayOfFreeSlot.length;
		final Random random = new Random();
		final Integer randomSlotIndex = random.nextInt(numberOfFreeSlots);
		final Slot slot = (Slot) arrayOfFreeSlot[randomSlotIndex];
		return slot.getIdentifier();
	}

	// pInteger low, more chance
	// i.e. pInteger = 3, returns true 70 % chance
	private static Boolean randomPercentage(final Integer pInteger) {
		int randomInt = new Random().nextInt(10) + 1;
		if (pInteger < randomInt) {
			return true;
		} else {
			return false;
		}

	}

	private static String publishEvent(final Object pObject) {

		String jsonString = null;
		String eventIdentifier = null;

		if (pObject instanceof EventMarkerPlacedMessage) {
			EventMarkerPlacedMessage eventMarkerPlacedMessage = (EventMarkerPlacedMessage) pObject;
			ModelValidator.validate(eventMarkerPlacedMessage);
			jsonString = Util.getJSON(eventMarkerPlacedMessage);
			eventIdentifier = eventMarkerPlacedMessage.getEventIdentifier();
		} else if (pObject instanceof EventUserGrantedAccessMessage) {
			EventUserGrantedAccessMessage eventUserGrantedAccessMessage = (EventUserGrantedAccessMessage) pObject;
			ModelValidator.validate(eventUserGrantedAccessMessage);
			jsonString = Util.getJSON(eventUserGrantedAccessMessage);
			eventIdentifier = eventUserGrantedAccessMessage.getEventIdentifier();
		} else if (pObject instanceof EventUserLeftMessage) {
			EventUserLeftMessage eventUserLeftMessage = (EventUserLeftMessage) pObject;
			ModelValidator.validate(eventUserLeftMessage);
			jsonString = Util.getJSON(eventUserLeftMessage);
			eventIdentifier = eventUserLeftMessage.getEventIdentifier();
		}

		final Date date = new Date();
		final Long timestamp = date.getTime();
		UUID uuid = UUID.randomUUID();

		final String filename = Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA + "/" + timestamp + "-" + uuid
				+ "-" + eventIdentifier + ".json";

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		BufferedWriter writer = new BufferedWriter(fileWriter);
		try {
			writer.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		final String filenameSignal = Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL + "/" + timestamp + "-"
				+ uuid + "-" + eventIdentifier + "." + Configuration.SADREMA_CSV_SIGNAL_SUFFIX;

		final File signalFile = new File(filenameSignal);

		try {
			signalFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return jsonString;

	}

	private static UUID getRandomExistingUser() {
		final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
		final Set<UUID> setUserUUID = mapUser.keySet();
		final Object[] arrayUSerUUID = setUserUUID.toArray();
		final Integer totalNumberOfUsers = arrayUSerUUID.length;
		final Integer randomUserIndex = new Random().nextInt(totalNumberOfUsers);
		final UUID userUUID = (UUID) arrayUSerUUID[randomUserIndex];
		return userUUID;
	}

	private Integer getNumberOfQueuedEvents() {
		return new File(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL).listFiles().length;
	}

}
