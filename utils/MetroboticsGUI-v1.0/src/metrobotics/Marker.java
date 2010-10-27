package metrobotics;

import java.awt.Color;

public class Marker {
	private Color primary;
	private Color secondary;
	private double xPos;
	private double yPos;
	private String name;
	public Marker(String name, Color primary, Color secondary, double xPos, double yPos) {
		super();
		this.name = name;
		this.primary = primary;
		this.secondary = secondary;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	public Color getPrimaryColor() {
		return primary;
	}
	public Color getSecondaryColor() {
		return secondary;
	}
	public double getxPos() {
		return xPos;
	}
	public double getyPos() {
		return yPos;
	}
	public String getName() {
		return name;
	}
}
