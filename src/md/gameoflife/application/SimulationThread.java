package md.gameoflife.application;

public class SimulationThread implements Runnable {
	private Simulator sim;
	private long delay;

	public SimulationThread(Simulator sim) {
		this.sim = sim;
		this.delay = 1000;
	}

	@Override
	public void run() {
		while (true) {
			this.sim.nextStep();
			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDelay(long newDelay) {
		this.delay = newDelay;
	}

}
