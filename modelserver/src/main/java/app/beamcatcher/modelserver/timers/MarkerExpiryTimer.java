package app.beamcatcher.modelserver.timers;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.persistence.WorldSingleton;

public class MarkerExpiryTimer implements Runnable {

	public void run() {
		System.out.println("MarkerExpiryTimer started !");
		System.out.println("MarkerExpiryTimer rate in milliseconds: "
				+ Configuration.MARKER_EXPIRY_TIMER_FREQUENCY_IN_MILLISECONDS);
		System.out.println("Marker timeout in milliseconds: " + Configuration.MARKER_TIMEOUT);

		System.out.println("Starting timer !");

		try {

			do {

				System.out.println("MarkerExpiryTimer requesting lock...");
				WorldSemaphore.semaphore.acquire();
				System.out.println("MarkerExpiryTimer acquired lock !!!");
				System.out.println("MarkerExpiryTimer getting to work...");

				final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
				final Set<UUID> setUUIDUser = mapUser.keySet();
				Integer totalMarkers = 0;
				for (UUID userUUID : setUUIDUser) {
					final User user = mapUser.get(userUUID);
					totalMarkers = totalMarkers + user.getMapMarker().size();
				}

				System.out.println("Total markers: " + totalMarkers);

				if (totalMarkers > 0) {

				} else {
					System.out.println("No markers, nothing to do !");
				}

				System.out.println("MarkerExpiryTimer finished work ! realising lock !!!");
				WorldSemaphore.semaphore.release();

				System.out.println("Sleeping for " + Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS
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
