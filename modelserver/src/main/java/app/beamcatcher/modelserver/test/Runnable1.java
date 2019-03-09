package app.beamcatcher.modelserver.test;

public class Runnable1 implements Runnable {

	public void run() {

		do {
			try {

				System.out.println("runnable1 trying to get lock...");
				TestSemaphore.semaphore.acquire();
				System.out.println("runnable1 got lock!!!!!!!!!!");
				// do work
				// do work
				System.out.println("runnable 1 doing work");
				Thread.sleep(5000);
				System.out.println("Hello from runnable 2");
				System.out.println("runnable1 finished");
				TestSemaphore.semaphore.release();
				System.out.println("runnable1 released lock");
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (true);

	}

}
