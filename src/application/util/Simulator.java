package application.util;

import java.util.ArrayList;

import application.ui.Main;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Pair;

/**
 * Simulator class that contains all important methods for the function of
 * conway's game of life simulation
 *
 * <pre>
 *  Rules of Conway's Game of Life:
 * 	1. Any live cell with fewer than two live neighbors dies, as if by underpopulation.
 * 	2. Any live cell with two or three live neighbors lives on to the next generation.
 * 	3. Any live cell with more than three live neighbors dies, as if by overpopulation.
 * 	4. Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction
 * </pre>
 * 
 * @author Max Drexler
 */
public class Simulator extends Canvas {
	private Thread drawThread;
	private SimulationRunner runnable;
	private GraphicsContext gc;
	private boolean[][] board; // data structure used to represent living and dead cells in a grid
	private Rectangle viewBox; // a resizable area of the board array that is displayed on the screen
	private int generation;

	private static final int BOARD_WIDTH = 500;
	private static final int BOARD_HEIGHT = 250;
	private static final int BOARD_BORDER = 5; // viewbox never expands past the boarder giving the illusion of an
												// infinite plane

	public Simulator(double width, double height) {
		super(width, height);
		this.board = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		this.drawThread = new Thread(runnable = new SimulationRunner(this));
		this.gc = this.getGraphicsContext2D();
		this.generation = 0;
		this.viewBox = new Rectangle(241, 118, 9, 4); // default area of board array to be displayed

		gc.setLineWidth(1.0);
		gc.setLineCap(StrokeLineCap.SQUARE);
		gc.setStroke(new Color(0, 0, 0, 1));
		gc.setMiterLimit(10);
		gc.setLineJoin(StrokeLineJoin.ROUND);

		/**
		 * toggles in board array and draws to screen the cell where mouse is clicked
		 */
		this.setOnMouseClicked((MouseEvent e) -> {
			int screenToBoardX = (int) (e.getX() * this.viewBox.getWidth() / (double) this.getWidth());
			int screenToBoardY = (int) (e.getY() * this.viewBox.getHeight() / (double) this.getHeight());

			this.board[(int) (screenToBoardX + this.viewBox.getX())][(int) (screenToBoardY + this.viewBox
					.getY())] = !this.board[(int) (screenToBoardX + this.viewBox.getX())][(int) (screenToBoardY
							+ this.viewBox.getY())];

			this.draw(screenToBoardX, screenToBoardY);
		});

		draw();
	}

	/**
	 * Toggles the current state of the simulation between running and off
	 * 
	 * @return if simulation is running
	 */
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
		} else if (drawThread.getState().equals(Thread.State.TERMINATED)) {
			this.drawThread = new Thread(runnable);
			return this.toggleSimulation();
		} else if (drawThread.getState().equals(Thread.State.WAITING)) {
			drawThread.notify();
			return true;
		} else if (drawThread.getState().equals(Thread.State.NEW)) {
			drawThread.start();
			return true;
		}
		return false;
	}

	/**
	 * Calculates the number of living neighbors around cell located at given x and
	 * y coordinate. A neighbor is any cell that is next to or diagonal of the given
	 * cell.
	 * 
	 * @param x value of cell in board array to check neighbors
	 * @param y value of cell in board array to check neighbors
	 * @return number of living neighbors of this cell
	 */
	private int calculateLiveNeighbors(int x, int y) {
		int count = 0;
		for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
			for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
				try {
					if (board[xIndex][yIndex] && !(xIndex == x && yIndex == y))
						count++;
				} catch (ArrayIndexOutOfBoundsException e) {
					// Thrown when x or y at bounds of board array
				}
			}
		}
		return count;
	}

	/**
	 * Performs all necessary calculations to continue the simulation for one
	 * generation
	 */
	public void nextStep() {
		Platform.runLater(() -> {
			Main.setGen(this.generation); // updates generation label
		});
		this.generation++;
		ArrayList<Pair<Integer, Integer>> list = new ArrayList<>(); // cells to update this generation, used to update
																	// all cells at same time
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y]) { // if cell "living"
					if (calculateLiveNeighbors(x, y) > 3 || calculateLiveNeighbors(x, y) < 2) // Rule 1 & 3
						list.add(new Pair<Integer, Integer>(x, y));
					// Rule 2, living cells that don't die aren't updated
				} else {
					if (calculateLiveNeighbors(x, y) == 3) // Rule 4
						list.add(new Pair<Integer, Integer>(x, y));
				}
			}
		}
		for (Pair<Integer, Integer> pair : list)
			board[pair.getKey()][pair.getValue()] = !board[pair.getKey()][pair.getValue()];

		// only draws x,y pairs that are visible on screen (located within current
		// viewbox)
		list.stream().filter(x -> this.viewBox.inBounds(x.getKey(), x.getValue()))
				.forEach(e -> drawNeighbors(e.getKey(), e.getValue()));
	}

	/*-
	 * resizes the viewbox based on given factor, in effect changing how many cells
	 * are drawn to the screen
	 * 
	 * @param factor in which to grow or shrink the screen
	 *		factor < 1 : increase viewbox size -> increase amount of cells drawn to screen
	 *	    factor > 1 : decrease viewbox size -> decrease amount of cells drawn to screen
	 */
	public void resizeScreen(double factor) {
		int newWidth = (int) ((this.board.length - 2 * BOARD_BORDER) / factor);
		int newHeight = newWidth / 2;
		// expands viewbox around center point on screen
		this.viewBox = new Rectangle((this.board.length - 2 * BOARD_BORDER) / 2 - newWidth / 2,
				(this.board[0].length - 2 * BOARD_BORDER) / 2 - newHeight / 2, newWidth, newHeight);
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		draw();
	}

	/**
	 * draws a cell to screen at specified point on screen based on given coordinate
	 * in board array
	 * 
	 * @param x value of cell in board array
	 * @param y value of cell in board array
	 */
	private void draw(int x, int y) {
		if (board[x + this.viewBox.getX()][y + this.viewBox.getY()])
			gc.setFill(Color.WHITE);
		else
			gc.setFill(Color.SLATEGREY);

		gc.fillRect(x * this.getWidth() / this.viewBox.getWidth(), y * this.getHeight() / this.viewBox.getHeight(),
				this.getWidth() / this.viewBox.getWidth(), this.getHeight() / this.viewBox.getHeight());

		gc.strokeRect(x * this.getWidth() / this.viewBox.getWidth(), y * this.getHeight() / this.viewBox.getHeight(),
				this.getWidth() / this.viewBox.getWidth(), this.getHeight() / this.viewBox.getHeight());
	}

	/**
	 * draws cells to screen around and at specified point on screen based on given
	 * coordinate in board array
	 * 
	 * @param x value of cell in board array
	 * @param y value of cell in board array
	 */
	private void drawNeighbors(int x, int y) {
		for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
			for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
				try {
					if (board[xIndex][yIndex])
						gc.setFill(Color.WHITE);
					else
						gc.setFill(Color.SLATEGREY);
				} catch (IndexOutOfBoundsException e) {
				}
				gc.fillRect((xIndex - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth(),
						(yIndex - this.viewBox.getY()) * this.getHeight() / this.viewBox.getHeight(),
						this.getWidth() / this.viewBox.getWidth(), this.getHeight() / this.viewBox.getHeight());
				gc.strokeRect((xIndex - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth(),
						(yIndex - this.viewBox.getY()) * this.getHeight() / this.viewBox.getHeight(),
						this.getWidth() / this.viewBox.getWidth(), this.getHeight() / this.viewBox.getHeight());
			}
		}
	}

	/**
	 * draws the entire viewbox to screen
	 */
	private void draw() {
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		// gc.strokeRect(0, 0, this.getWidth(), this.getHeight());
		for (int x = 0; x < this.viewBox.getWidth(); x++) {
			for (int y = 0; y < this.viewBox.getHeight(); y++) {
				if (board[x + this.viewBox.getX()][y + this.viewBox.getY()])
					gc.setFill(Color.WHITE);
				else {
					gc.setFill(Color.SLATEGREY);
				}
				gc.fillRect(x * this.getWidth() / this.viewBox.getWidth(),
						y * this.getHeight() / this.viewBox.getHeight(), this.getWidth() / this.viewBox.getWidth(),
						this.getHeight() / this.viewBox.getHeight());

				gc.strokeRect(x * this.getWidth() / this.viewBox.getWidth(),
						y * this.getHeight() / this.viewBox.getHeight(), this.getWidth() / this.viewBox.getWidth(),
						this.getHeight() / this.viewBox.getHeight());
			}
		}
	}

	/**
	 * increases or decreases the speed of the simulation based on factor
	 * 
	 * @param factor at which to change the speed of the simulation
	 *
	 */
	public void changeSpeed(double factor) {
		this.runnable.setDelay(1000 / (long) factor);
	}

	/**
	 * completely resets the current simulation by creating a new board array
	 */
	public void resetSimulation() {
		this.board = new boolean[BOARD_WIDTH][BOARD_HEIGHT];
		Main.setGen(this.generation = 0);
		draw();
	}

	/**
	 * @return if simulation is running without toggling simulation
	 */
	public boolean isRunning() {
		return this.drawThread.isAlive();
	}
}
