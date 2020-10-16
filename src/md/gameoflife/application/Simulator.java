package md.gameoflife.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/*
 * Any live cell with two or three live neighbors survives.
 * Any dead cell with three live neighbors becomes a live cell.
 * All other live cells die in the next generation. Similarly, all other dead cells stay dead
 */
/**
 * @author Max Drexler
 *
 */
public class Simulator {
	private int num_squares = 10;
	private Canvas canvas;
	private GraphicsContext gc;
	private int canvasWidth = 360;
	private int canvasHeight = 360;
	private double ratio = (double) canvasWidth / (double) num_squares;

	private boolean[][] board = new boolean[num_squares][num_squares];

	public Simulator() {
		this.canvas = new Canvas(canvasWidth, canvasHeight);
		this.canvas.setOnMouseClicked((MouseEvent e) -> {
			this.onClick(e.getSceneX(), e.getSceneY());
		});

		this.gc = canvas.getGraphicsContext2D();
		draw();
	}

	public void startSimulation(int speed) {
		
	}

	private int calculateNeighbors(int x, int y) {
		int count = 0;
		try {
			for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
				for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
					if (board[xIndex][yIndex] && !(xIndex == x && yIndex == y))
						count++;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return count;
	}

	
	public void nextStep() {
		ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y]) {
					if (calculateNeighbors(x, y) > 3 || calculateNeighbors(x, y) < 2)
						list.add(new Pair<Integer, Integer>(x, y));
				} else {
					if (calculateNeighbors(x, y) == 3)
						list.add(new Pair<Integer, Integer>(x, y));
				}
			}
		}
		for (Pair<Integer, Integer> pair : list) {
			board[pair.getKey()][pair.getValue()] = !board[pair.getKey()][pair.getValue()];
		}
		draw();
	}

	public void resizeScreen(double factor) {
		num_squares = (int) (num_squares / factor);
		draw();
	}

	private void onClick(double x, double y) {
		this.board[(int) (x / ratio)][(int) (y / ratio)] = !this.board[(int) (x / ratio)][(int) (y / ratio)];
		this.draw((int) (x / ratio), (int) (y / ratio));
	}

	private void draw(int x, int y) {
		gc.setLineWidth(2);
		if (board[x][y])
			gc.setFill(Color.WHITE);
		else
			gc.setFill(Color.SLATEGREY);
		gc.fillRect(x * this.ratio, y * this.ratio, this.ratio, this.ratio);
		gc.setFill(Color.BLACK);
		gc.strokeRect(x * this.ratio, y * this.ratio, this.ratio, this.ratio);
	}

	private void draw() {
		gc.setLineWidth(2);
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y])
					gc.setFill(Color.WHITE);
				else {
					gc.setFill(Color.SLATEGREY);

				}
				gc.fillRect(x * canvasWidth / num_squares, y * canvasHeight / num_squares, canvasWidth / num_squares,
						canvasHeight / num_squares);
			}
		}

		for (int i = 0; i <= this.num_squares; i++) {
			gc.strokeLine(i * canvasWidth / num_squares, 0, i * canvasWidth / num_squares, canvasHeight);
		}
		for (int i = 0; i <= this.num_squares; i++) {
			gc.strokeLine(0, i * canvasHeight / num_squares, canvasWidth, i * canvasHeight / num_squares);
		}

	}

	public Canvas getCavnas() {
		return this.canvas;
	}
}
