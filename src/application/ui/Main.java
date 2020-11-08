package application.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	public static final int WIDTH = 710;
	public static final int HEIGHT = 400;

	private static MainScreen main;

	@Override
	public void start(Stage primaryStage) {
		main = new MainScreen();
		;
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
