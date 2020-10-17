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
	public static final int WIDTH = 340;
	public static final int HEIGHT = 400;
	private Simulator graphics = new Simulator();

	@Override
	public void start(Stage primaryStage) {
		HBox hbox = new HBox(10);
		Button start = new Button("Start");
		Button stop = new Button("Stop");
		Button next = new Button("Step");
		Button reset = new Button("Reset");
		VBox vbox = new VBox();

		Slider zoom = new Slider(.1, 10, 1);
		zoom.setMajorTickUnit(10);
		zoom.setMinorTickCount(2);
		zoom.setSnapToTicks(true);

		hbox.getChildren().addAll(reset, next, start, stop, zoom);
		vbox.getChildren().addAll(graphics.getCavnas(), hbox);

		hbox.setAlignment(Pos.BASELINE_CENTER);
		vbox.setAlignment(Pos.BASELINE_CENTER);

		Scene scene = new Scene(vbox, WIDTH, HEIGHT, Color.BLACK);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("conwaygameoflifeicon.png"));

		reset.setOnMouseClicked((MouseEvent e) -> {
			this.graphics.getCavnas().getGraphicsContext2D().clearRect(0, 0, this.graphics.getCavnas().getWidth(),
					this.graphics.getCavnas().getHeight());
			this.graphics = new Simulator();
		});
		next.setOnMouseClicked((MouseEvent e) -> {
			graphics.nextStep();
		});
		zoom.setOnDragOver((DragEvent e) -> {
			System.out.println(zoom.getValue());
			// graphics.resizeScreen(zoom.getValue());
		});
		start.setOnMouseClicked((MouseEvent e) -> {
			graphics.startSimulation(1);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
