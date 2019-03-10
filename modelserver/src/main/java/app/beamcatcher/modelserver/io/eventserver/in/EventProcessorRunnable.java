package app.beamcatcher.modelserver.io.eventserver.in;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;

public class EventProcessorRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(EventProcessorRunnable.class);

	public void run() {

		logger.info("Event processor started !");

		logger.info("Event signals directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		logger.info("Event data directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA);
		logger.info("Events polling rate in milliseconds: " + Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS);

		logger.info("Starting polling !");

		try {

			do {

				final Boolean signalsFound = getSignalsFound();

				if (signalsFound) {

					logger.info("EventProcessor requesting lock...");
					WorldSemaphore.semaphore.acquire();
					logger.info("EventProcessor acquired lock !!!");
					logger.info("EventProcessor getting to work...");

					// Signalfile
					final File[] orderedListSignalFiles = getOrderedListSignalFiles();
					final Integer numberOfSignalFiles = orderedListSignalFiles.length;
					logger.info(numberOfSignalFiles + " Signal(s) found !");
					final File mostRecentSignalEventFile = orderedListSignalFiles[0];
					final String mostRecentSignalEventFileAbsolutePath = mostRecentSignalEventFile.getAbsolutePath();
					logger.info("Most recent signal (absolute path): " + mostRecentSignalEventFileAbsolutePath);
					final String mostRecentSignalEventFileFilename = mostRecentSignalEventFile.getName();
					logger.info("Most recent signal (filename): " + mostRecentSignalEventFileFilename);
					final String timestampAsString = getTimestamp(mostRecentSignalEventFileFilename);
					final String uuidAsString = getUUID(mostRecentSignalEventFileFilename);
					final String eventIdentifier = getEventIdentifier(mostRecentSignalEventFileFilename);
					checkFilenameValidity(timestampAsString, uuidAsString, eventIdentifier);
					logger.info("Timestamp: " + timestampAsString + " UUID: " + uuidAsString + " eventIdentifier: "
							+ eventIdentifier);

					// DataFile
					final String dataFileAbsolutePath = getDataFileAbsolutePath(timestampAsString, uuidAsString,
							eventIdentifier);
					logger.info("Looking for data file: " + dataFileAbsolutePath);
					checkDataFile(dataFileAbsolutePath);
					logger.info("File found !");
					final String dataFileContents = getDataFileContents(dataFileAbsolutePath);
					logger.info("Event contents: " + dataFileContents);

					// Process event
					logger.info("Processing event...");
					final World world = WorldSingleton.INSTANCE;
					EventProcessor.processEvent(dataFileContents, eventIdentifier, world);
					postEventActions(mostRecentSignalEventFileAbsolutePath, dataFileAbsolutePath, world);
					logger.info("Done !");

					logger.info("EventProcessor finished work ! realising lock !!!");
					WorldSemaphore.semaphore.release();

				} else {
					logger.info("No signals found...");
				}

				logger.info(
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

		logger.info("Validating model...");
		ModelValidator.validate(pWorld);
		// Services.performTimedOperations(WorldSingleton.INSTANCE);
		// SadremaHelper.mutateBeams(WorldSingleton.INSTANCE);

		logger.info("Removing file: " + pMostRecentSignalEventFileAbsolutePath);
		logger.info("Removing file: " + pDataFileAbsolutePath);

		removeFiles(pMostRecentSignalEventFileAbsolutePath, pDataFileAbsolutePath);
	}

	private void removeFiles(final String pMostRecentSignalEventFileAbsolutePath, final String pDataFileAbsolutePath) {
		File filenameData = new File(pMostRecentSignalEventFileAbsolutePath);
		filenameData.delete();
		File filenameSignal = new File(pDataFileAbsolutePath);
		filenameSignal.delete();
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
