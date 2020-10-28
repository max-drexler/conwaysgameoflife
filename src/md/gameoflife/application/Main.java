package md.gameoflife.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	public static final int WIDTH = 780;
	public static final int HEIGHT = 404;
	private Simulator graphics = new Simulator();

	@Override
	public void start(Stage primaryStage) {
		HBox bottomButtonBar = new HBox(10);
		Button startToggleButton = new Button("Start");
		Button stepButton = new Button("Step");
		Button resetButton = new Button("Reset");
		VBox vbox = new VBox();
		Label genNum = new Label("Generation: ");
		Slider speedSlider = new Slider(1, 4, 1);
		speedSlider.setMajorTickUnit(.5);
		speedSlider.setSnapToTicks(true);
		Slider zoomSlider = new Slider(1, 5, 1);
		zoomSlider.setMajorTickUnit(.5);
		zoomSlider.setMinorTickCount(2);
		zoomSlider.setSnapToTicks(true);

		bottomButtonBar.getChildren().addAll(startToggleButton, stepButton, genNum, resetButton, zoomSlider,
				speedSlider);
		vbox.getChildren().addAll(graphics.getCavnas(), bottomButtonBar);

		bottomButtonBar.setAlignment(Pos.BASELINE_CENTER);
		vbox.setAlignment(Pos.BASELINE_CENTER);

		Scene scene = new Scene(vbox, WIDTH, HEIGHT);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(true);
		primaryStage.getIcons().add(new Image("conwaygameoflifeicon.png"));

		speedSlider.setOnMouseDragged((MouseEvent e) -> {
			this.graphics.changeSpeed(speedSlider.getValue());
		});
		resetButton.setOnMouseClicked((MouseEvent e) -> {
			this.graphics.resetCanvas();
			if (this.graphics.isRunning()) {
				this.graphics.toggleSimulation();
				startToggleButton.setText("Start");
				stepButton.setDisable(false);
			}

		});
		stepButton.setOnMouseClicked((MouseEvent e) -> {
			this.graphics.nextStep();
		});
		zoomSlider.setOnMouseDragged((MouseEvent e) -> {
			//System.out.println(zoomSlider.getValue());
			graphics.resizeScreen(zoomSlider.getValue());
		});
		startToggleButton.setOnMouseClicked((MouseEvent e) -> {
			boolean state = graphics.toggleSimulation();
			startToggleButton.setText(state ? "Stop" : "Start");
			stepButton.setDisable(state);
		});
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			if (graphics.isRunning())
				graphics.toggleSimulation();
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
