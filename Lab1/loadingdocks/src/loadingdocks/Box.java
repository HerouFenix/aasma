package loadingdocks;

import java.awt.Color;
import java.awt.Point;

public class Box extends Entity {

	public Box(Point point, Color color) {
		super(point, color);
	}
	
	/*****************************
	 ***** AUXILIARY METHODS ***** 
	 *****************************/
	public void pickup(Point newPoint) {
		Board.removeEntity(point);
		point = newPoint;
	}

	public void drop(Point newPoint) {
		Board.insertEntity(this,newPoint);
		point = newPoint;
	}

	public void move(Point newPoint) {
		point = newPoint;
	}
}
