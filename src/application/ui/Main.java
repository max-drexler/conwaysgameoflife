package application.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Main class, sets up primary stage and displays the application's main screen
 * 
 * @author Max Drexler
 *
 */
public class Main extends Application {
	public static final int WIDTH = 710;
	public static final int HEIGHT = 410;

	private static MainScreen main;

	@Override
	public void start(Stage primaryStage) {
		// SETUP
		main = new MainScreen();
		Scene scene = new Scene(main, WIDTH, HEIGHT);
		primaryStage.setTitle("Conway's Game of Life");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon16.png")),
				new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon32.png")),
				new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon64.png")),
				new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon128.png")),
				new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon256.png")),
				new Image(getClass().getResourceAsStream("/imgs/conwaygameoflifeicon512.png")));

		// ends simulation thread when window is closed
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			main.flushThreads();
		});
	}

	/**
	 * updates the main screen's generation count
	 * 
	 * @param g generation number
	 */
	public static void setGen(int g) {
		main.setGen(g);
	}

	/**
	 * launches JavaFX window
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
