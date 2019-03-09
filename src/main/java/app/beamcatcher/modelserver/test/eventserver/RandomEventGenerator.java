package app.beamcatcher.modelserver.test.eventserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.github.javafaker.Faker;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.io.eventserver.in.EventMarkerPlacedMessage;
import app.beamcatcher.modelserver.io.eventserver.in.EventUserGrantedAccessMessage;
import app.beamcatcher.modelserver.io.eventserver.in.EventUserLeftMessage;
import app.beamcatcher.modelserver.model.Game;
import app.beamcatcher.modelserver.model.Slot;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.RandomMGRSWGS84Coordinates;
import app.beamcatcher.modelserver.util.Util;

public class RandomEventGenerator implements Runnable {

	private static final Integer EVENT_PRODUCTION_RATE_IN_MILLISECONDS = 2000;

	public void run() {

		System.out.println("Started random event generator !");
		System.out.println("Event generation every " + EVENT_PRODUCTION_RATE_IN_MILLISECONDS + " milliseconds!");

		try {
			do {

				System.out.println("RandomEventGenerator requesting lock...");
				WorldSemaphore.semaphore.acquire();
				System.out.println("RandomEventGenerator acquired lock !!!");
				System.out.println("RandomEventGenerator getting to work...");

				final World world = WorldSingleton.INSTANCE;

				final Boolean atLeastOneUser = (world.getMapUser() != null) && (!world.getMapUser().isEmpty());

				final Integer totalNumberOfUsers = world.getMapUser().keySet().size();
				System.out.println("RandomEventGenerator --> Total users: " + totalNumberOfUsers);

				Boolean eventAlreadyPublished = Boolean.FALSE;

				// USER_JOINS
				if (totalNumberOfUsers < Configuration.WORLD_MAXIMUM_NUMBER_OF_USERS) {
					System.err.println("TOTALUSERS " + totalNumberOfUsers);
					if (randomPercentage(4)) {
						final String username = new Faker().gameOfThrones().character();
						final EventUserGrantedAccessMessage eventUserGrantedAccessMessage = new EventUserGrantedAccessMessage();
						eventUserGrantedAccessMessage.setEventIdentifier(Configuration.EVENT_IDENTIFIER_USER_JOINED);
						eventUserGrantedAccessMessage.setUsername(username);
						final Integer slotIdentifier = getRandomSlot();
						if (slotIdentifier > 0) {
							eventUserGrantedAccessMessage.setSlotIdentifier(slotIdentifier);
							publishEvent(eventUserGrantedAccessMessage);
						}
						eventAlreadyPublished = Boolean.TRUE;
					}
				}

				if (!eventAlreadyPublished && atLeastOneUser) {
					// USER_EXITS
					if (randomPercentage(8) && atLeastOneUser) {
						final UUID userThatExitedUUID = getRandomExistingUser(world);
						final String userThatExited = (String) WorldSingleton.INSTANCE.getMapUser()
								.get(userThatExitedUUID).getUsername();
						final EventUserLeftMessage eventUserLeftMessage = new EventUserLeftMessage();
						eventUserLeftMessage.setEventIdentifier(Configuration.EVENT_IDENTIFIER_USER_LEFT);
						eventUserLeftMessage.setUsername(userThatExited);
						publishEvent(eventUserLeftMessage);
						eventAlreadyPublished = Boolean.TRUE;
					}
				}

				if (randomPercentage(4) && atLeastOneUser) {
					// MARKER_ADDED
					String eventIdentifier = Configuration.EVENT_IDENTIFIER_USER_PLACED_MARKER;
					UUID userUUID = getRandomExistingUser(world);
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
						publishEvent(eventMarkerPlacedMessage);
					}
				}

				System.out.println("RandomEventGenerator finished work, releasing lock !!");
				WorldSemaphore.semaphore.release();

				System.out.println("Sleeping for " + EVENT_PRODUCTION_RATE_IN_MILLISECONDS + " milliseconds...");
				Thread.sleep(EVENT_PRODUCTION_RATE_IN_MILLISECONDS);

			} while (true);

		} catch (

		InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Integer getRandomSlot() {
		final Game game = WorldSingleton.INSTANCE.getGame();
		final Set<Slot> freeSlots = game.getFreeSlots();
		final Object[] arrayOfFreeSlot = freeSlots.toArray();
		final Integer numberOfFreeSlots = arrayOfFreeSlot.length;
		// TODO: HACK: No idea what is going on here
		if (numberOfFreeSlots > 0) {
			final Random random = new Random();
			System.err.println("FREESLOTS " + numberOfFreeSlots);
			final Integer randomSlotIndex = random.nextInt(numberOfFreeSlots);
			final Slot slot = (Slot) arrayOfFreeSlot[randomSlotIndex];
			return slot.getIdentifier();
		} else {
			return -1;
		}

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

	private static void publishEvent(final Object pObject) {

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

		System.out.println("Generated:");
		System.out.println(filename);
		System.out.println(filenameSignal);
		System.out.println(jsonString);

	}

	private static UUID getRandomExistingUser(final World pWorld) {
		final Map<UUID, User> mapUser = pWorld.getMapUser();
		final Set<UUID> setUserUUID = mapUser.keySet();
		final Object[] arrayUSerUUID = setUserUUID.toArray();
		final Integer totalNumberOfUsers = arrayUSerUUID.length;
		final Integer randomUserIndex = new Random().nextInt(totalNumberOfUsers);
		final UUID userUUID = (UUID) arrayUSerUUID[randomUserIndex];
		return userUUID;
	}

}
