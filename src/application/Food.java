package application;

// Food is a MyPoint2D position, with set/get method to set/get the positions
public class Food {
	
	private MyPoint2D pos;

	// Initialise the food with a position 
	Food(MyPoint2D pos) {
		this.pos = pos;
	}

	// return the position of food
	MyPoint2D getPosition() {
		return pos;
	}

	// set the food position with another position
	void setPosition(MyPoint2D pos) {
		this.pos = pos;
	}

	// get the x coordinate of food's position
	double getX() {
		return pos.getX();
	}

	// get the y coordinate of food's position
	double getY() {
		return pos.getY();
	}
}
