package application.ui;

import application.util.Simulator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/***
 * The main screen of this JavaFX application
 * 
 * @author Max Drexler
 *
 */
public class MainScreen extends VBox {
	private Simulator sim;
	private Label genNum;

	public MainScreen() {
		// SETUP
		this.sim = new Simulator(720, 360);
		this.genNum = new Label("Generation: 0");
		Button startToggleButton = new Button("Start");
		Button stepButton = new Button("Step");
		Button resetButton = new Button("Reset");
		Slider speedSlider = new Slider(1, 4, 1);
		Slider zoomSlider = new Slider(10, 50, 50);
		Label zoomLabel = new Label("Zoom");
		Label speedLabel = new Label("Speed");
		VBox zoomBox = new VBox(zoomSlider, zoomLabel);
		VBox speedBox = new VBox(speedSlider, speedLabel);
		HBox bottomButtonBar = new HBox(10, startToggleButton, stepButton, genNum, resetButton, zoomBox, speedBox);

		// LAYOUT
		this.getChildren().addAll(sim, bottomButtonBar);
		this.setAlignment(Pos.BASELINE_CENTER);
		bottomButtonBar.setAlignment(Pos.TOP_CENTER);
		bottomButtonBar.setAlignment(Pos.BASELINE_CENTER);
		zoomBox.setAlignment(Pos.BASELINE_CENTER);
		speedBox.setAlignment(Pos.BASELINE_CENTER);
		speedSlider.setMajorTickUnit(.5);
		speedSlider.setSnapToTicks(true);
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMajorTickUnit(.5);
		zoomSlider.setMinorTickCount(2);

		// FUNCTIONALITY
		speedSlider.setOnMouseDragged((MouseEvent e) -> {
			this.sim.changeSpeed(speedSlider.getValue());
		});
		resetButton.setOnMouseClicked((MouseEvent e) -> {
			if (this.sim.isRunning()) {
				this.sim.toggleSimulation(); // end thread
				startToggleButton.setText("Start"); // change back start button text
				stepButton.setDisable(false); // enable step button
			}
			this.sim.resetSimulation();
		});
		stepButton.setOnMouseClicked((MouseEvent e) -> {
			this.sim.nextStep();
		});
		zoomSlider.setOnMouseDragged((MouseEvent e) -> {
			sim.resizeScreen(zoomSlider.getValue());
		});
		startToggleButton.setOnMouseClicked((MouseEvent e) -> {
			boolean state = sim.toggleSimulation();
			startToggleButton.setText(state ? "Stop" : "Start");
			stepButton.setDisable(state);
		});
	}

	/**
	 * ends the simulation thread
	 */
	public void flushThreads() {
		if (this.sim.isRunning())
			this.sim.toggleSimulation();
	}

	/**
	 * sets the generation label to given generation number
	 * 
	 * @param g generation number
	 */
	public void setGen(int g) {
		this.genNum.setText("Generation: " + g);
	}
}
