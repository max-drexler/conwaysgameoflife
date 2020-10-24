package md.gameoflife.application;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

//TODO:
/*
 * Add "infinite" borders
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
	private int squareWidth = 18;
	private int canvasWidth = 720;
	private int canvasHeight = 360;
	private int num_squaresX = this.canvasWidth / this.squareWidth;
	private int num_squaresY = this.canvasHeight / this.squareWidth;
	private int borderWidth = 5;

	private Canvas canvas;
	private GraphicsContext gc;
	private Thread drawThread;
	private SimulationThread runnable;
	private boolean[][] board;

	public Simulator() {
		this.board = new boolean[num_squaresX + 2 * this.borderWidth][num_squaresY + 2 * this.borderWidth];
		this.canvas = new Canvas(canvasWidth, canvasHeight);
		this.drawThread = new Thread(runnable = new SimulationThread(this));
		this.gc = canvas.getGraphicsContext2D();

		this.canvas.setOnMouseClicked((MouseEvent e) -> {
			this.onClick(e.getX(), e.getY());
		});
		this.canvas.setOnMouseDragged((MouseEvent e) -> {

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
		for (Pair<Integer, Integer> pair : list) {
			board[pair.getKey()][pair.getValue()] = !board[pair.getKey()][pair.getValue()];
		}
		draw();
	}

	public void resizeScreen(double factor) {
		num_squaresX = (int) (num_squaresX / factor);
		draw();
	}

	private void onClick(double x, double y) {
		int screenToBoardX = (int) ((x * (double) this.num_squaresX) / (double) this.canvasWidth);
		int screenToBoardY = (int) ((y * (double) this.num_squaresY) / (double) this.canvasHeight);

		this.board[screenToBoardX + this.borderWidth][screenToBoardY
				+ this.borderWidth] = !this.board[screenToBoardX + this.borderWidth][screenToBoardY + this.borderWidth];

		this.draw(screenToBoardX, screenToBoardY);
	}

	private void draw(int x, int y) {
		gc.setLineWidth(2);
		if (board[x + this.borderWidth][y + this.borderWidth])
			gc.setFill(Color.WHITE);
		else
			gc.setFill(Color.SLATEGREY);
		gc.fillRect(x * this.squareWidth, y * this.squareWidth, this.squareWidth, this.squareWidth);
		gc.setFill(Color.BLACK);
		gc.strokeRect(x * this.squareWidth, y * this.squareWidth, this.squareWidth, this.squareWidth);
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
			}
		}
		gc.setFill(Color.BLACK);

		for (int i = 0; i <= this.num_squaresX; i++) {
			gc.strokeLine(i * this.squareWidth, 0, i * this.squareWidth, canvasHeight);
		}
		for (int i = 0; i <= this.num_squaresY; i++) {
			gc.strokeLine(0, i * squareWidth, canvasWidth, i * squareWidth);
		}

	}

	public boolean[][] getBoard() {
		return this.board;
	}

	public Canvas getCavnas() {
		return this.canvas;
	}

	public void setCanvas(Canvas c) {
		this.canvas = c;
	}

	public void resetCanvas() {
		this.board = new boolean[this.num_squaresX + 2 * this.borderWidth][this.num_squaresY + 2 * this.borderWidth];
		draw();
	}
}
