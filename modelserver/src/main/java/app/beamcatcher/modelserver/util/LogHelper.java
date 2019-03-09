package app.beamcatcher.modelserver.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import app.beamcatcher.modelserver.configuration.Configuration;

public class LogHelper {

	public static void logError(final String pMessage) {
		System.err.println(pMessage);
	}

	public static void logEventMessage(final String pMessage) {
		try {
			File f = new File(Configuration.MODEL_SERVER_EVENT_LOG);
			f.getParentFile().mkdirs();
			f.createNewFile();
			Files.write(Paths.get(Configuration.MODEL_SERVER_EVENT_LOG), pMessage.getBytes(),
					StandardOpenOption.APPEND);
			Files.write(Paths.get(Configuration.MODEL_SERVER_EVENT_LOG), System.lineSeparator().getBytes(),
					StandardOpenOption.APPEND);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
