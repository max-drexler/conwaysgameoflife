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
 * @author Max Drexler
 *
 */
public class Simulator extends Canvas {

	private int canvasHeight;
	private int borderWidth = 5;
	private int generation;

	private Rectangle viewBox;
	private GraphicsContext gc;
	private Thread drawThread;
	private SimulationThread runnable;
	private boolean[][] board;
	private double widthRatio = .02;

	public Simulator(double width, double height) {
		super(width, height);
		this.board = new boolean[500][250];
		this.drawThread = new Thread(runnable = new SimulationThread(this));
		this.gc = this.getGraphicsContext2D();
		this.generation = 0;
		this.viewBox = new Rectangle(62, 28, (int) (getWidth() * this.widthRatio), 8);
		this.canvasHeight = (int) height;

		gc.setLineWidth(1);
		gc.setLineCap(StrokeLineCap.SQUARE);
		gc.setStroke(new Color(0, 0, 0, 1));
		gc.setMiterLimit(10);
		gc.setLineJoin(StrokeLineJoin.ROUND);

		this.setOnMouseClicked((MouseEvent e) -> {
			int screenToBoardX = (int) (e.getX() * this.viewBox.getWidth() / (double) this.getWidth());
			int screenToBoardY = (int) (e.getY() * this.viewBox.getHeight() / (double) this.canvasHeight);

			this.board[(int) (screenToBoardX + this.viewBox.getX())][(int) (screenToBoardY + this.viewBox
					.getY())] = !this.board[(int) (screenToBoardX + this.viewBox.getX())][(int) (screenToBoardY
							+ this.viewBox.getY())];

			this.draw((int) (screenToBoardX + this.viewBox.getX()), (int) (screenToBoardY + this.viewBox.getY()));
		});

		widthProperty().addListener(evt -> {
			this.viewBox.setWidth((int) (this.getWidth() * this.widthRatio));
			draw();

		});
		heightProperty().addListener(evt -> {
			// this.viewBox.setHeight((int) this.getHeight());
			draw();
		});

		draw();
	}

	@Override
	public double prefWidth(double width) {
		return getWidth();

	}

	@Override
	public double prefHeight(double height) {
		return getHeight();
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	/**
	 * @return
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

	/**
	 * @param x
	 * @param y
	 * @return
	 */
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

	/**
	 * 
	 */
	public void nextStep() {
		Platform.runLater(() -> {
			Main.setGen(this.generation);
		});
		this.generation++;
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

	}

	/**
	 * @param factor
	 */
	public void resizeScreen(double factor) {
		int newWidth = (int) ((this.board.length - 2 * this.borderWidth) / factor);
		int newHeight = newWidth / 2;
		this.viewBox.setBounds((this.board.length - 2 * this.borderWidth) / 2 - newWidth / 2,
				(this.board[0].length - 2 * this.borderWidth) / 2 - newHeight / 2, newWidth, newHeight);
		gc.clearRect(0, 0, this.getWidth(), this.canvasHeight);
		draw();
	}

	/**
	 * @param x
	 * @param y
	 */
	private void draw(int x, int y) {
		if (board[x][y])
			gc.setFill(Color.WHITE);
		else
			gc.setFill(Color.SLATEGREY);
		Rectangle rec = new Rectangle((int) ((x - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth()),
				(y - this.viewBox.getY()) * this.canvasHeight / this.viewBox.getHeight(),
				(int) (this.getWidth() / this.viewBox.getWidth()), this.canvasHeight / this.viewBox.getHeight());
		gc.fillRect(rec.getX() + 1, rec.getY() + 1, rec.getWidth() - 1, rec.getHeight() - 1);
		gc.strokeRect(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight());
	}

	/**
	 * @param x
	 * @param y
	 */
	private void drawNeighbors(int x, int y) {
		for (int xIndex = x - 1; xIndex <= x + 1; xIndex++) {
			for (int yIndex = y - 1; yIndex <= y + 1; yIndex++) {
				if (board[x][y])
					gc.setFill(Color.WHITE);
				else
					gc.setFill(Color.SLATEGREY);
				gc.fillRect((x - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth() + 1,
						(y - this.viewBox.getY()) * this.canvasHeight / this.viewBox.getHeight() + 1,
						this.getWidth() / this.viewBox.getWidth() - 1,
						this.canvasHeight / this.viewBox.getHeight() - 1);
				gc.strokeRect((x - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth(),
						(y - this.viewBox.getY()) * this.canvasHeight / this.viewBox.getHeight(),
						this.getWidth() / this.viewBox.getWidth(), this.canvasHeight / this.viewBox.getHeight());
			}
		}
	}

	/**
	 * 
	 */
	private void draw() {
		gc.clearRect(0, 0, this.getWidth(), this.canvasHeight);
		//gc.strokeRect(0, 0, this.getWidth(), this.getHeight());
		for (int x = (int) this.viewBox.getX(); x < this.viewBox.getX() + this.viewBox.getWidth(); x++) {
			for (int y = (int) this.viewBox.getY(); y < this.viewBox.getY() + this.viewBox.getHeight(); y++) {
				if (board[x][y])
					gc.setFill(Color.WHITE);
				else {
					gc.setFill(Color.SLATEGREY);
				}
				gc.fillRect((int) ((x - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth() + 1),
						(y - this.viewBox.getY()) * this.canvasHeight / this.viewBox.getHeight() + 1,
						(int) (this.getWidth() / this.viewBox.getWidth()),
						this.canvasHeight / this.viewBox.getHeight() - 1);
				gc.strokeRect((x - this.viewBox.getX()) * this.getWidth() / this.viewBox.getWidth(),
						(y - this.viewBox.getY()) * this.canvasHeight / this.viewBox.getHeight(),
						this.getWidth() / this.viewBox.getWidth(), this.canvasHeight / this.viewBox.getHeight());
			}
		}
	}

	/**
	 * @param factor
	 */
	public void changeSpeed(double factor) {
		this.runnable.setDelay(1000 / (long) factor);
	}

	/**
	 * @return
	 */
	public boolean[][] getBoard() {
		return this.board;
	}

	/**
	 * 
	 */
	public void resetCanvas() {
		this.board = new boolean[150][75];
		// this.canvas = new Canvas(getWidth(), canvasHeight);
		Main.setGen(this.generation = 0);
		draw();
	}

	/**
	 * @return
	 */
	public boolean isRunning() {
		return this.drawThread.isAlive();
	}
}
