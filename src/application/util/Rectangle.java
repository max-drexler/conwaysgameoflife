package application.util;

public class Rectangle {
	private int x;
	private int y;
	private int w;
	private int h;

	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.w;
	}

	public int getHeight() {
		return this.h;
	}

	public void setWidth(int w) {
		this.w = w;
	}

	public void setHeight(int h) {
		this.h = h;
	}

	@Override
	public String toString() {
		return this.x + " " + this.y + " " + this.w + " " + this.h;
	}
}
