package app.beamcatcher.modelserver.io.eventserver.in;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.ModelValidator;

public class EventProcessorRunnable implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(EventProcessorRunnable.class);

	public void run() {

		logger.info("Event processor started");

		logger.info("Event signals directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		logger.info("Event data directory: " + Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA);
		logger.info("Events polling rate in ms: " + Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS);

		logger.info("Starting polling...");

		try {

			Long previousTimestamp = new Date().getTime();

			do {

				final Boolean signalsFound = getSignalsFound();

				if (signalsFound) {
					final Long startTime = System.currentTimeMillis();
					WorldSemaphore.semaphore.acquire();

					// Signalfile
					final File[] orderedListSignalFiles = getOrderedListSignalFiles();
					final Integer numberOfSignalFiles = orderedListSignalFiles.length;
					final File mostRecentSignalEventFile = orderedListSignalFiles[0];
					final String mostRecentSignalEventFileAbsolutePath = mostRecentSignalEventFile.getAbsolutePath();
					final String mostRecentSignalEventFileFilename = mostRecentSignalEventFile.getName();
					final String timestampAsString = getTimestamp(mostRecentSignalEventFileFilename);
					final Long currentTimestamp = Long.valueOf(timestampAsString);
					if (currentTimestamp < previousTimestamp) {
						logger.error("Event out of order !");
						System.exit(-1);
					} else {
						previousTimestamp = currentTimestamp;
					}
					final String uuidAsString = getUUID(mostRecentSignalEventFileFilename);
					final String eventIdentifier = getEventIdentifier(mostRecentSignalEventFileFilename);
					checkFilenameValidity(timestampAsString, uuidAsString, eventIdentifier);

					// DataFile
					final String dataFileAbsolutePath = getDataFileAbsolutePath(timestampAsString, uuidAsString,
							eventIdentifier);
					checkDataFile(dataFileAbsolutePath);
					final String dataFileContents = getDataFileContents(dataFileAbsolutePath);

					// Process event
					EventProcessor.processEvent(dataFileContents, eventIdentifier);
					postEventActions(mostRecentSignalEventFileAbsolutePath, dataFileAbsolutePath);

					WorldSemaphore.semaphore.release();

					final Long endTime = System.currentTimeMillis();
					final Long elapsedTime = endTime - startTime;
					logger.info("Event processed in " + elapsedTime + " ms");
				}

				if (Configuration.THROTTLE_EVENT_CONSUMPTION) {
					Thread.sleep(Configuration.EVENT_POLLING_FREQUENCY_IN_MILLISECONDS);
				}

			} while (true);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void postEventActions(final String pMostRecentSignalEventFileAbsolutePath,
			final String pDataFileAbsolutePath) {

		ModelValidator.validate(WorldSingleton.INSTANCE);
		// Services.performTimedOperations(WorldSingleton.INSTANCE);
		// SadremaHelper.mutateBeams(WorldSingleton.INSTANCE);

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
			logger.error("Data file not found ! file: " + dataFileAbsolutePath);
			System.exit(-1);
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
			logger.error("Unknown event: " + pEventIdentifier);
			System.exit(-1);
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
