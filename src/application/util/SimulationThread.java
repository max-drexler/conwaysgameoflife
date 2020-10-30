package application.util;

public class SimulationThread implements Runnable {
	private Simulator sim;
	private long delay;
	private boolean running;

	public SimulationThread(Simulator sim) {
		this.sim = sim;
		this.delay = 1000;
		this.running = false;
	}

	@Override
	public void run() {
		this.running = true;
		do {
			this.sim.nextStep();
			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (this.running);
	}

	public void setDelay(long newDelay) {
		this.delay = newDelay;
	}

	public void stop() {
		this.running = false;
	}

}
