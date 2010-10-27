package metrobotics;

import java.awt.Color;
import java.awt.Rectangle;


public class Wall {
	private Color color;
	private Rectangle rect;
	private String name;
	public Wall(Color color, Rectangle rect, String name) {
		super();
		this.color = color;
		this.rect = rect;
		this.name = name;
	}
	public Color getColor() {
		return color;
	}
	public Rectangle getRect() {
		return rect;
	}
	public String getName() {
		return name;
	}

}
