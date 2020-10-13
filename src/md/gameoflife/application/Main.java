package md.gameoflife.application;

import javafx.application.Application;
import javafx.event.EventHandler;
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
	public static final int WIDTH = 720;
	public static final int HEIGHT = 406;

	@Override
	public void start(Stage primaryStage) {
		HBox hbox = new HBox(10);
		Button start = new Button("Start");
		Button stop = new Button("Stop");
		Button next = new Button("Step");
		VBox vbox = new VBox();
		Simulator graphics = new Simulator();
		Slider zoom = new Slider(.1, 10, 1);
		zoom.setMajorTickUnit(10);
		zoom.setMinorTickCount(2);
		zoom.setSnapToTicks(true);

		hbox.getChildren().add(next);
		hbox.getChildren().add(start);
		hbox.getChildren().add(stop);
		hbox.getChildren().add(zoom);
		vbox.getChildren().add(graphics.getCavnas());
		vbox.getChildren().add(hbox);

		Scene scene = new Scene(vbox, WIDTH, HEIGHT, Color.BLACK);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("conwaygameoflifeicon.png"));

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
