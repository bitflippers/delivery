package app.beamcatcher.modelserver.test;

public class ThreadRunnerTest {
	public static void main(String[] args) {

		final Thread thread1 = new Thread(new Runnable1());
		thread1.start();
		final Thread thread2 = new Thread(new Runnable2());
		thread2.start();

	}

}
