package application.util;

/**
 * Helper class that represents a rectangle using integer values
 * 
 * @author Max Drexler
 */
public class Rectangle {
	private int x;
	private int y;
	private int w;
	private int h;

	/**
	 * Constructs rectangle
	 * 
	 * @param x initial x value
	 * @param y initial y value
	 * @param w initial width
	 * @param h initial height
	 */
	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	/**
	 * getter method for x field
	 * 
	 * @return x field
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * getter method for y field
	 * 
	 * @return y field
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * getter method for width field
	 * 
	 * @return width field
	 */
	public int getWidth() {
		return this.w;
	}

	/**
	 * getter method for height field
	 * 
	 * @return height field
	 */
	public int getHeight() {
		return this.h;
	}

	/**
	 * returns if given x and y values are within the bounds of current rectangle
	 * 
	 * @param x value to check bounds of
	 * @param y value to check bounds of
	 * @return if both the x and y values are within the bounds of the current
	 *         rectangle
	 */
	public boolean inBounds(int x, int y) {
		return (x >= this.x && x <= this.x + this.w && y >= this.y && y <= this.y + this.h);
	}

	/**
	 * @return string representation of this rectangle
	 */
	@Override
	public String toString() {
		return this.x + " " + this.y + " " + this.w + " " + this.h;
	}
}
