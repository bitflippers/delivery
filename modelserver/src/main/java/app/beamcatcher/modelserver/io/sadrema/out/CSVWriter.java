package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import app.beamcatcher.modelserver.configuration.Configuration;

public class CSVWriter {

	public static UUID writeToFiles(final StringBuffer pMarkerString) {

		final Date date = new Date();
		final Long timestamp = date.getTime();
		final UUID uuid = UUID.randomUUID();

		writerMarkersData(pMarkerString, timestamp, uuid);

		return uuid;
	}

	private static void writerMarkersData(final StringBuffer pMarkerString, final Long timestamp, final UUID uuid) {
		final String filename = Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA + "/markers.csv";

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		BufferedWriter writer = new BufferedWriter(fileWriter);
		try {
			writer.write(pMarkerString.toString());
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
	}

}
