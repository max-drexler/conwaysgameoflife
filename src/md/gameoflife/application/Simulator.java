package md.gameoflife.application;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

//TODO:
/*
 * Make nextStep() more efficient
 * 
 * Implement all buttons/sliders
 * 
 */
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
	private static int MIN_SQUARE_WIDTH = 10;
	private int squareWidth = 72;
	private int canvasWidth = 720;
	private int canvasHeight = 360;
	private int borderWidth = 5;
	private int generation;

	private Canvas canvas;
	private GraphicsContext gc;
	private Thread drawThread;
	private SimulationThread runnable;
	private boolean[][] board;

	public Simulator() {
		this.board = new boolean[this.canvasWidth / MIN_SQUARE_WIDTH
				+ 2 * this.borderWidth][this.canvasHeight / MIN_SQUARE_WIDTH + 2 * this.borderWidth];
		this.canvas = new Canvas(canvasWidth, canvasHeight);
		this.drawThread = new Thread(runnable = new SimulationThread(this));
		this.gc = canvas.getGraphicsContext2D();
		this.generation = 0;

		this.canvas.setOnMouseClicked((MouseEvent e) -> {
			this.onClick(e.getX(), e.getY());
		});
		this.canvas.setOnMousePressed((MouseEvent e) -> {
		});
		this.canvas.setOnMouseDragged((MouseEvent e) -> {
			// System.out.println(e.getX() + " " + e.getY());
		});

		draw();
	}

	public boolean toggleSimulation() {
		if (drawThread.getState().equals(Thread.State.RUNNABLE)
				|| drawThread.getState().equals(Thread.State.TIMED_WAITING)) {
			synchronized (drawThread) {
				try {
					this.runnable.stop();
					drawThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return false;
			}
		} else if (drawThread.getState().equals(Thread.State.WAITING)) {
			drawThread.notify();
			return true;
		} else if (drawThread.getState().equals(Thread.State.NEW)) {
			drawThread.start();
			return true;
		} else if (drawThread.getState().equals(Thread.State.TERMINATED)) {
			this.drawThread = new Thread(runnable);
			return this.toggleSimulation();
		}
		return false;
	}

	private int calculateNeighbors(int x, int y) {
		int count = 0;
		for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
			for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
				try {
					if (board[xIndex][yIndex] && !(xIndex == x && yIndex == y))
						count++;
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
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
		for (Pair<Integer, Integer> pair : list)
			board[pair.getKey()][pair.getValue()] = !board[pair.getKey()][pair.getValue()];

		list.stream().forEach(e -> drawNeighbors(e.getKey(), e.getValue()));
		this.generation++;
	}

	public void resizeScreen(double factor) {
		int possibleResize = (int) ((double) (this.squareWidth) / factor);
		this.squareWidth = MIN_SQUARE_WIDTH > possibleResize ? MIN_SQUARE_WIDTH : possibleResize;
		System.out.println(squareWidth);
		draw();
	}

	private void onClick(double x, double y) {
		int screenToBoardX = (int) (x / (double) this.squareWidth);
		int screenToBoardY = (int) (y / (double) this.squareWidth);

		this.board[screenToBoardX + this.borderWidth][screenToBoardY
				+ this.borderWidth] = !this.board[screenToBoardX + this.borderWidth][screenToBoardY + this.borderWidth];

		this.draw(screenToBoardX + this.borderWidth, screenToBoardY + this.borderWidth);
	}

	private void draw(int x, int y) {
		gc.setLineWidth(2);
		if (board[x][y])
			gc.setFill(Color.WHITE);
		else
			gc.setFill(Color.SLATEGREY);
		gc.fillRect((x - this.borderWidth) * this.squareWidth, (y - this.borderWidth) * this.squareWidth,
				this.squareWidth, this.squareWidth);
		gc.setFill(Color.BLACK);
		gc.strokeRect((x - this.borderWidth) * this.squareWidth, (y - this.borderWidth) * this.squareWidth,
				this.squareWidth, this.squareWidth);
	}

	private void drawNeighbors(int x, int y) {
		gc.setLineWidth(2);
		for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
			for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
				if (board[x][y])
					gc.setFill(Color.WHITE);
				else
					gc.setFill(Color.SLATEGREY);
				gc.fillRect((x - this.borderWidth) * squareWidth, (y - this.borderWidth) * squareWidth, squareWidth,
						squareWidth);
				gc.setFill(Color.BLACK);
				gc.strokeRect((x - this.borderWidth) * this.squareWidth, (y - this.borderWidth) * this.squareWidth,
						this.squareWidth, this.squareWidth);
			}
		}
	}

	private void draw() {
		gc.setLineWidth(2);
		for (int x = this.borderWidth; x < this.canvasWidth / this.squareWidth + this.borderWidth; x++) {
			for (int y = this.borderWidth; y < this.canvasHeight / this.squareWidth + this.borderWidth; y++) {
				if (board[x][y])
					gc.setFill(Color.WHITE);
				else {
					gc.setFill(Color.SLATEGREY);

				}
				gc.fillRect((x - this.borderWidth) * squareWidth, (y - this.borderWidth) * squareWidth, squareWidth,
						squareWidth);
				gc.setFill(Color.BLACK);
				gc.strokeRect((x - this.borderWidth) * this.squareWidth, (y - this.borderWidth) * this.squareWidth,
						this.squareWidth, this.squareWidth);
			}
		}
	}

	public void changeSpeed(double factor) {
		this.runnable.setDelay(1000 / (long) factor);
	}

	public boolean[][] getBoard() {
		return this.board;
	}

	public Canvas getCavnas() {
		return this.canvas;
	}

	public int getGeneration() {
		return this.generation;
	}

	public void setCanvas(Canvas c) {
		this.canvas = c;
	}

	public void resetCanvas() {
		this.board = new boolean[this.canvasWidth / MIN_SQUARE_WIDTH
				+ 2 * this.borderWidth][this.canvasHeight / MIN_SQUARE_WIDTH + 2 * this.borderWidth];
		this.canvas = new Canvas(canvasWidth, canvasHeight);
		this.generation = 0;
		draw();
	}

	public boolean isRunning() {
		return this.drawThread.isAlive();
	}
}
