package drexler.max.gameoflife.util;

/**
 * A runnable class with custom run method used to run the simulation
 * concurrently with user inputs
 * 
 * @author Max Drexler
 *
 */
public class SimulationRunner implements Runnable {
	private Simulator sim;
	private long delay;
	private boolean running;

	/**
	 * Constructs a new simulation runnable
	 * 
	 * @param sim simulator instance that this runnable class is connected to
	 */
	public SimulationRunner(Simulator sim) {
		this.sim = sim;
		this.delay = 1000;
		this.running = false;
	}

	/**
	 * run method called when a thread is started with this class as it's runnable
	 */
	@Override
	public void run() {
		this.running = true;
		do {
			this.sim.nextStep(); // runs the next step in the simulation
			try {
				Thread.sleep(this.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} while (this.running);
	}

	/**
	 * sets the delay of the run method
	 * 
	 * @param newDelay new delay of the run method
	 */
	public void setDelay(long newDelay) {
		this.delay = newDelay;
	}

	/**
	 * stops the current run method
	 */
	public void stop() {
		this.running = false;
	}

}
