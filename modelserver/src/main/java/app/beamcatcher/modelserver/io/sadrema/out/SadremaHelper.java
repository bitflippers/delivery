package app.beamcatcher.modelserver.io.sadrema.out;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uma.jmetal.runner.multiobjective.chetan.NSGAIIBinaryRunner_SDRM;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.model.Marker;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.test.sadrema.awt.MGRSImageSaver;

public class SadremaHelper {

	private static final Logger logger = LoggerFactory.getLogger(SadremaHelper.class);

	public static File obtainSolution() {

		File beamsTXTFile = null;

		try {
			logger.info("Solution requested  !");
			long startTime = System.currentTimeMillis();
			String[] args = new String[] { "markers.csv" };
			logger.info("Computing...");
			NSGAIIBinaryRunner_SDRM.main(args);
			Boolean sadremaFinished = Boolean.FALSE;
			final String fileToLookFor = Configuration.MODEL_SERVER_IO_DIR_SADREMA_IN_DATA + "/beams.txt";
			beamsTXTFile = new File(fileToLookFor);
			do {
				sadremaFinished = beamsTXTFile.exists();
				Thread.sleep(1000);
			} while (!sadremaFinished);
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			logger.info("Optimal solution found in " + elapsedTime + " milliseconds !");
			return beamsTXTFile;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return beamsTXTFile;

	}

	private static Integer doPNG(final UUID uuid, final World pWorld) {
		final Set<Marker> setMarker = new HashSet<Marker>();
		final Map<UUID, User> mapUser = pWorld.getMapUser();
		final Set<UUID> setUserUUID = mapUser.keySet();
		Integer totalRequestedCapacity = 0;
		for (UUID userUUID : setUserUUID) {
			final User user = mapUser.get(userUUID);
			final HashMap<UUID, Marker> mapMarker = user.getMapMarker();
			Set<UUID> setMarkerUUID = mapMarker.keySet();
			for (UUID markerUUID : setMarkerUUID) {
				final Marker marker = mapMarker.get(markerUUID);
				setMarker.add(marker);
				totalRequestedCapacity = totalRequestedCapacity + marker.getRequestedUnits();
			}
		}
		final String pngFilename = Configuration.MODEL_SERVER_IO_DIR_SADREMA_OUT_PNG + "/" + uuid + ".png";
		MGRSImageSaver.savePNG(setMarker, pngFilename);
		return totalRequestedCapacity;
	}

}
