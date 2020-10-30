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

	private static MainScreen main;

	@Override
	public void start(Stage primaryStage) {

		main = new MainScreen(new Simulator());

		Scene scene = new Scene(main, WIDTH, HEIGHT);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(true);
		primaryStage.getIcons().add(new Image("conwaygameoflifeicon.png"));

		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			main.flushThreads();
		});
	}

	public static void setGen(int g) {
		main.setGen(g);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
