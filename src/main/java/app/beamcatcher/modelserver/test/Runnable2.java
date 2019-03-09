package app.beamcatcher.modelserver.test;

public class Runnable2 implements Runnable {

	public void run() {
		do {
			try {
				System.out.println("runnable2 trying to get lock...");
				TestSemaphore.semaphore.acquire();
				System.out.println("runnable2 got lock!!!!!!!!!!!!");
				// do work
				System.out.println("runnable 2 doing work");
				Thread.sleep(1000);
				System.out.println("Hello from runnable 2");
				System.out.println("runnable2 finished");
				TestSemaphore.semaphore.release();
				System.out.println("runnable2 released lock");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (true);

	}

}
