package application.ui;

import application.util.Simulator;
import javafx.beans.property.Property;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainScreen extends VBox {

	private Simulator sim;
	private Label genNum;

	public MainScreen() {
		this.sim = new Simulator(720, 360);
		this.genNum = new Label("Generation: 0");
		Button startToggleButton = new Button("Start");
		Button stepButton = new Button("Step");
		Button resetButton = new Button("Reset");
		Slider speedSlider = new Slider(1, 4, 1);
		Slider zoomSlider = new Slider(2, 8, 8);
		Label zoomLabel = new Label("Zoom");
		Label speedLabel = new Label("Speed");
		VBox zoomBox = new VBox(zoomSlider, zoomLabel);
		VBox speedBox = new VBox(speedSlider, speedLabel);
		StackPane pane = new StackPane(sim);
		HBox bottomButtonBar = new HBox(10, startToggleButton, stepButton, genNum, resetButton, zoomBox, speedBox);
		AnchorPane anc = new AnchorPane(bottomButtonBar);

		bottomButtonBar.setAlignment(Pos.TOP_CENTER);
		this.getChildren().addAll(anc, pane);
		this.setAlignment(Pos.BASELINE_CENTER);
		bottomButtonBar.setAlignment(Pos.BASELINE_CENTER);
		zoomBox.setAlignment(Pos.BASELINE_CENTER);
		speedBox.setAlignment(Pos.BASELINE_CENTER);
		speedSlider.setMajorTickUnit(.5);
		zoomSlider.setMajorTickUnit(.5);
		zoomSlider.setMinorTickCount(2);
		speedSlider.setSnapToTicks(true);
		zoomSlider.setSnapToTicks(true);
		pane.setPrefSize(720, 360);
		AnchorPane.setTopAnchor(bottomButtonBar, 10.0);
		AnchorPane.setBottomAnchor(bottomButtonBar, 10.0);
		VBox.setVgrow(pane, Priority.ALWAYS);
		anc.setMaxHeight(50.0);
		sim.widthProperty().bind(pane.widthProperty());
		sim.heightProperty().bind(pane.heightProperty());

		speedSlider.setOnMouseDragged((MouseEvent e) -> {
			this.sim.changeSpeed(speedSlider.getValue());
		});
		resetButton.setOnMouseClicked((MouseEvent e) -> {
			this.sim.resetCanvas();
			if (this.sim.isRunning()) {
				this.sim.toggleSimulation();
				startToggleButton.setText("Start");
				stepButton.setDisable(false);
			}

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

	public void flushThreads() {
		if (this.sim.isRunning())
			this.sim.toggleSimulation();
	}

	public void setGen(int g) {
		this.genNum.setText("Generation: " + g);
	}
}
