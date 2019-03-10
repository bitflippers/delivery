package app.beamcatcher.modelserver.timers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.persistence.WorldSingleton;

public class MarkerExpiryTimer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MarkerExpiryTimer.class);

	public void run() {
		logger.info("MarkerExpiryTimer started !");
		logger.info("MarkerExpiryTimer rate in milliseconds: "
				+ Configuration.MARKER_EXPIRY_TIMER_FREQUENCY_IN_MILLISECONDS);
		logger.info("Marker timeout in milliseconds: " + Configuration.MARKER_TIMEOUT);

		logger.info("Starting timer !");

		try {

			do {

				logger.info("MarkerExpiryTimer requesting lock...");
				WorldSemaphore.semaphore.acquire();
				logger.info("MarkerExpiryTimer acquired lock !!!");
				logger.info("MarkerExpiryTimer getting to work...");

				final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
				final Set<UUID> setUUIDUser = mapUser.keySet();
				Integer totalMarkers = 0;
				for (UUID userUUID : setUUIDUser) {
					final User user = mapUser.get(userUUID);
					totalMarkers = totalMarkers + user.getMapMarker().size();
				}

				logger.info("Total markers: " + totalMarkers);

				if (totalMarkers > 0) {

				} else {
					logger.info("No markers, nothing to do !");
				}

				logger.info("MarkerExpiryTimer finished work ! realising lock !!!");
				WorldSemaphore.semaphore.release();

				logger.info("Sleeping for " + Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS
						+ " milliseconds...");
				Thread.sleep(Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

			} while (true);

		} catch (

		Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
