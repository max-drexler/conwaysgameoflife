package md.gameoflife.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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

		Slider zoomSlider = new Slider(.1, 10, 1);
		zoomSlider.setMajorTickUnit(10);
		zoomSlider.setMinorTickCount(2);
		zoomSlider.setSnapToTicks(true);

		bottomButtonBar.getChildren().addAll(startToggleButton, stepButton, resetButton, zoomSlider);
		vbox.getChildren().addAll(graphics.getCavnas(), bottomButtonBar);

		bottomButtonBar.setAlignment(Pos.BASELINE_CENTER);
		vbox.setAlignment(Pos.BASELINE_CENTER);

		Scene scene = new Scene(vbox, WIDTH, HEIGHT);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(true);
		primaryStage.getIcons().add(new Image("conwaygameoflifeicon.png"));

		resetButton.setOnMouseClicked((MouseEvent e) -> {
			this.graphics.resetCanvas();
			
		});
		stepButton.setOnMouseClicked((MouseEvent e) -> {
			this.graphics.nextStep();
		});
		zoomSlider.setOnDragOver((DragEvent e) -> {
			System.out.println(zoomSlider.getValue());
			// graphics.resizeScreen(zoom.getValue());
		});
		startToggleButton.setOnMouseClicked((MouseEvent e) -> {
			boolean state = graphics.toggleSimulation();
			startToggleButton.setText(state ? "Stop" : "Start");
			stepButton.setDisable(state);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
