package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import app.beamcatcher.modelserver.configuration.Configuration;

public class CSVWriter {

	public static UUID writeToFiles(final StringBuffer pMarkerString, final StringBuffer pSatellitesString) {

		final Date date = new Date();
		final Long timestamp = date.getTime();
		final UUID uuid = UUID.randomUUID();

		writerMarkersData(pMarkerString, timestamp, uuid);
		// writerSatellitesData(pSatellitesString, timestamp, uuid);
		// writeSignal(timestamp, uuid);

		return uuid;
	}

	private static void writeSignal(final Long timestamp, final UUID uuid) {
		final String filenameSignal = Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_SIGNAL + "/" + timestamp + "-"
				+ uuid + "." + Configuration.SADREMA_CSV_SIGNAL_SUFFIX;

		final File signalFile = new File(filenameSignal);

		try {
			signalFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
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

	private static void writerSatellitesData(final StringBuffer pSatellitesString, final Long timestamp,
			final UUID uuid) {
		final String filename = Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_DATA + "/" + timestamp + "-" + uuid + "-"
				+ "satellites.csv";

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		BufferedWriter writer = new BufferedWriter(fileWriter);
		try {
			writer.write(pSatellitesString.toString());
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
