package app.beamcatcher.modelserver.bootstrap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.eventserver.in.EventProcessorRunnable;
import app.beamcatcher.modelserver.io.eventserver.in.simulator.RandomEventGeneratorRunnable;
import app.beamcatcher.modelserver.io.eventserver.out.HTTPEndpointHandler;
import app.beamcatcher.modelserver.io.sadrema.out.SadremaSolutionRetrieverRunnable;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		createDirectories();
		createPropertiesFile();
		cleanPreviousState();
		startJetty();
		startRandomEventGenerator();
		startEventProcessor();
		startSadremaSolutionRetriever();
	}

	private static void startSadremaSolutionRetriever() {
		final SadremaSolutionRetrieverRunnable sadremaSolutionRetriever = new SadremaSolutionRetrieverRunnable();
		final Thread eventProcessorThread = new Thread(sadremaSolutionRetriever);
		eventProcessorThread.setName("SADREMA-SOLUTION-RETRIEVER");
		eventProcessorThread.start();
	}

	private static void cleanPreviousState() {
		try {
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_SIGNAL));
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA));
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL));
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA));
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_LOG_DIR));
			FileUtils.cleanDirectory(new File(Configuration.MODEL_SERVER_IO_DIR_SADREMA_IN_DATA));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void startEventProcessor() {
		EventProcessorRunnable eventProcessor = new EventProcessorRunnable();
		final Thread eventProcessorThread = new Thread(eventProcessor);
		eventProcessorThread.setName("EVENT-PROCESSOR-THREAD");
		eventProcessorThread.start();
	}

	private static void startRandomEventGenerator() {
		final RandomEventGeneratorRunnable randomEventGenerator = new RandomEventGeneratorRunnable();
		final Thread randomEventGeneratorThread = new Thread(randomEventGenerator);
		randomEventGeneratorThread.setName("RANDOM-EVENT-GENERATOR");
		randomEventGeneratorThread.start();
	}

	private static void startJetty() throws Exception {
		final HTTPEndpointHandler jettyServer = new HTTPEndpointHandler();
		jettyServer.start();
	}

	public static final void createDirectories() {
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_SIGNAL);
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA);
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_SIGNAL);
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_EVENT_SERVER_IN_DATA);
		createDirectory(Configuration.MODEL_SERVER_LOG_DIR);
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_SADREMA_IN_DATA);
		createDirectory(Configuration.MODEL_SERVER_IO_DIR_SADREMA_ARCHIVE);
	}

	private static final void createDirectory(final String pDirectory) {
		new File(pDirectory).mkdirs();
	}

	public static void createPropertiesFile() {

		try {
			StringBuffer properties = Configuration.getProperties();

			final String filename = Configuration.MODEL_SERVER_BASE_DIR + "/"
					+ Configuration.MODEL_SERVER_PROPERTIES_FILENAME;

			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(filename);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			BufferedWriter writer = new BufferedWriter(fileWriter);
			try {
				writer.write(properties.toString());
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

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
