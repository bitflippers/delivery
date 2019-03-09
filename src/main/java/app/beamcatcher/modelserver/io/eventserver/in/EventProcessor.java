package app.beamcatcher.modelserver.io.eventserver.in;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.LogHelper;
import app.beamcatcher.modelserver.util.ModelValidator;
import app.beamcatcher.modelserver.util.Util;

public class EventProcessor implements Runnable {

	public void run() {

		System.out.println("Event processor started !");

		System.out.println("Event signals directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		System.out.println("Event data directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA);
		System.out.println(
				"Events polling rate in milliseconds: " + Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS);

		System.out.println("Starting polling !");

		try {

			do {

				final Boolean signalsFound = getSignalsFound();

				if (signalsFound) {

					System.out.println("EventProcessor requesting lock...");
					WorldSemaphore.semaphore.acquire();
					System.out.println("EventProcessor acquired lock !!!");
					System.out.println("EventProcessor getting to work...");

					// Signalfile
					final File[] orderedListSignalFiles = getOrderedListSignalFiles();
					final Integer numberOfSignalFiles = orderedListSignalFiles.length;
					System.out.println(numberOfSignalFiles + " Signal(s) found !");
					final File mostRecentSignalEventFile = orderedListSignalFiles[0];
					final String mostRecentSignalEventFileAbsolutePath = mostRecentSignalEventFile.getAbsolutePath();
					System.out.println("Most recent signal (absolute path): " + mostRecentSignalEventFileAbsolutePath);
					final String mostRecentSignalEventFileFilename = mostRecentSignalEventFile.getName();
					System.out.println("Most recent signal (filename): " + mostRecentSignalEventFileFilename);
					final String timestampAsString = getTimestamp(mostRecentSignalEventFileFilename);
					final String uuidAsString = getUUID(mostRecentSignalEventFileFilename);
					final String eventIdentifier = getEventIdentifier(mostRecentSignalEventFileFilename);
					checkFilenameValidity(timestampAsString, uuidAsString, eventIdentifier);
					System.out.println("Timestamp: " + timestampAsString + " UUID: " + uuidAsString
							+ " eventIdentifier: " + eventIdentifier);

					// DataFile
					final String dataFileAbsolutePath = getDataFileAbsolutePath(timestampAsString, uuidAsString,
							eventIdentifier);
					System.out.println("Looking for data file: " + dataFileAbsolutePath);
					checkDataFile(dataFileAbsolutePath);
					System.out.println("File found !");
					final String dataFileContents = getDataFileContents(dataFileAbsolutePath);
					System.out.println("Event contents: " + dataFileContents);

					// Process event
					System.out.println("Processing event...");
					final World world = WorldSingleton.INSTANCE;
					final String logMessage = processEvent(dataFileContents, eventIdentifier, world);
					LogHelper.logEventMessage(logMessage);
					postEventActions(mostRecentSignalEventFileAbsolutePath, dataFileAbsolutePath, world);
					System.out.println("Done !");

					System.out.println("EventProcessor finished work ! realising lock !!!");
					WorldSemaphore.semaphore.release();

				} else {
					System.out.println("No signals found...");
				}

				System.out.println(
						"Sleeping for " + Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS + " milliseconds...");
				Thread.sleep(Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS);

			} while (true);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void postEventActions(final String pMostRecentSignalEventFileAbsolutePath,
			final String pDataFileAbsolutePath, final World pWorld) {

		System.out.println("Validating model...");
		ModelValidator.validate(pWorld);
		// Services.performTimedOperations(WorldSingleton.INSTANCE);
		// SadremaHelper.mutateBeams(WorldSingleton.INSTANCE);

		System.out.println("Removing file: " + pMostRecentSignalEventFileAbsolutePath);
		System.out.println("Removing file: " + pDataFileAbsolutePath);

		removeFiles(pMostRecentSignalEventFileAbsolutePath, pDataFileAbsolutePath);
	}

	private void removeFiles(final String pMostRecentSignalEventFileAbsolutePath, final String pDataFileAbsolutePath) {
		File filenameData = new File(pMostRecentSignalEventFileAbsolutePath);
		filenameData.delete();
		File filenameSignal = new File(pDataFileAbsolutePath);
		filenameSignal.delete();
	}

	private String processEvent(final String pDataFileContents, final String pEventIdentifier, final World pWorld) {

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
		return logMessage;
	}

	private String processEvent(final EventMarkerPlacedMessage pEventMarkerPlacedMessage, final World pWorld) {
		String username = pEventMarkerPlacedMessage.getUsername();
		Double latitude = pEventMarkerPlacedMessage.getLatitude();
		Double longitude = pEventMarkerPlacedMessage.getLongitude();
		Integer priority = pEventMarkerPlacedMessage.getPriority();
		Integer requestedUnits = pEventMarkerPlacedMessage.getRequestedUnits();
		pWorld.addMarkerToUser(username, latitude, longitude, requestedUnits, priority);
		return "User '" + username + "' placed a marker at latlong: (" + latitude + "," + longitude
				+ ") with requestedUnits: '" + requestedUnits + "' and priority: '" + priority + "'";
	}

	private String processEvent(final EventUserLeftMessage pEventUserLeftMessage, final World pWorld) {
		final String username = pEventUserLeftMessage.getUsername();
		pWorld.removeUser(username);
		return "User '" + username + "' left !";
	}

	private String processEvent(final EventUserGrantedAccessMessage pEventUserGrantedAccessMessage,
			final World pWorld) {
		final String username = pEventUserGrantedAccessMessage.getUsername();
		final Integer slotIdentifier = pEventUserGrantedAccessMessage.getSlotIdentifier();
		pWorld.addNewUser(username, slotIdentifier);
		System.out.println("Total number of users: " + pWorld.getMapUser().keySet().size());
		return "User '" + username + "' joined !";
	}

	private String getDataFileContents(final String pDataFileAbsolutePath) {
		List<String> lines = null;
		try {
			final Path datafileAsPath = Paths.get(pDataFileAbsolutePath);
			lines = Files.readAllLines(datafileAsPath);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		final StringBuffer stringBuffer = new StringBuffer();
		for (String string : lines) {
			stringBuffer.append(string);
		}
		return stringBuffer.toString();
	}

	private void checkDataFile(String dataFileAbsolutePath) {
		File file = new File(dataFileAbsolutePath);
		if (!file.exists()) {
			throw new IllegalStateException("Data file not found ! file: " + dataFileAbsolutePath);
		}
	}

	private String getDataFileAbsolutePath(final String pTimestampAsString, final String pUUIDAsString,
			final String pEventIdentifier) {
		final String datafile = Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA + "/" + pTimestampAsString + "-"
				+ pUUIDAsString + "-" + pEventIdentifier + ".json";
		return datafile;
	}

	private void checkFilenameValidity(final String pTimestampAsString, final String pUUIDAsString,
			final String pEventIdentifier) {
		final Long timestampAsLong = Long.valueOf(pTimestampAsString);
		final UUID uuid = UUID.fromString(pUUIDAsString);

		Boolean eventIdentifierOk = Boolean.FALSE;

		if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_JOINED)) {
			eventIdentifierOk = Boolean.TRUE;
		} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_LEFT)) {
			eventIdentifierOk = Boolean.TRUE;
		} else if (pEventIdentifier.equals(Configuration.EVENT_IDENTIFIER_USER_PLACED_MARKER)) {
			eventIdentifierOk = Boolean.TRUE;
		}

		if (!eventIdentifierOk) {
			throw new IllegalStateException("Unknown event: " + pEventIdentifier);
		}

	}

	private String getEventIdentifier(final String pMostRecentSignalEventFileFilename) {
		final String[] split = pMostRecentSignalEventFileFilename.split("-");
		final String eventIdAndSuffix = split[6];
		final String eventId = eventIdAndSuffix.replaceFirst("[.][^.]+$", "");
		return eventId;
	}

	private String getUUID(final String pMostRecentSignalEventFileFilename) {
		final String[] split = pMostRecentSignalEventFileFilename.split("-");
		final String uuid = split[1] + "-" + split[2] + "-" + split[3] + "-" + split[4] + "-" + split[5];
		return uuid;
	}

	private String getTimestamp(final String pMostRecentSignalEventFileFilename) {
		final String[] split = pMostRecentSignalEventFileFilename.split("-");
		return split[0];
	}

	private Boolean getSignalsFound() {
		Boolean result = Boolean.FALSE;
		final File eventServerInSignalFile = new File(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		final File[] eventServerInSignalFiles = eventServerInSignalFile.listFiles();
		final Integer totalNumberOfSignalFilesFound = eventServerInSignalFiles.length;
		if (totalNumberOfSignalFilesFound > 0) {
			result = Boolean.TRUE;
		}
		return result;
	}

	private File[] getOrderedListSignalFiles() {
		final File eventServerInSignalFile = new File(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		File[] listSignalFiles = eventServerInSignalFile.listFiles();
		Arrays.sort(listSignalFiles);
		return listSignalFiles;
	}

}
