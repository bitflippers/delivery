package app.beamcatcher.modelserver.io.sadrema.out;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import app.beamcatcher.modelserver.configuration.Configuration;
import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.User;
import app.beamcatcher.modelserver.persistence.WorldSingleton;

public class SadremaSolutionRetriever implements Runnable {

	public void run() {

		System.out.println("SadremaSolutionRetriever started !");
		System.out.println("SadremaSolutionRetriever rate in milliseconds: "
				+ Configuration.SADREMA_SOLUTION_RETRIEVAL_FREQUENCY_IN_MILLISECONDS);

		System.out.println("Starting polling !");

		try {

			do {

				System.out.println("SadremaSolutionRetriever requesting lock...");
				WorldSemaphore.semaphore.acquire();
				System.out.println("SadremaSolutionRetriever acquired lock !!!");
				System.out.println("SadremaSolutionRetriever getting to work...");

				final Map<UUID, User> mapUser = WorldSingleton.INSTANCE.getMapUser();
				final Set<UUID> setUUIDUser = mapUser.keySet();
				Integer totalMarkers = 0;
				for (UUID userUUID : setUUIDUser) {
					final User user = mapUser.get(userUUID);
					totalMarkers = totalMarkers + user.getMapMarker().size();
				}

				System.out.println("Total markers: " + totalMarkers);

				if (totalMarkers > 0) {

					System.err.println("Writing CSVs...");

					final StringBuffer markersCSVInput = WorldToMarkersCSVFileMapper
							.toMarkersCSVFile(WorldSingleton.INSTANCE);
					final StringBuffer satellitesCSVInput = WorldToSatellitesCSVFileMapper
							.toSatellitesCSVFile(WorldSingleton.INSTANCE);
					CSVWriter.writeToFiles(markersCSVInput, satellitesCSVInput);

					System.err.println("Done !");

				} else {
					System.out.println("No markers, nothing to do !");
				}

				System.out.println("SadremaSolutionRetriever finished work ! realising lock !!!");
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
