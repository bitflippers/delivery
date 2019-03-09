package app.beamcatcher.modelserver.io;

import java.util.concurrent.Semaphore;

public class WorldSemaphore {
	public static final Semaphore semaphore = new Semaphore(1);

	private WorldSemaphore() {

	}
}
