package application;

public class AIsnake extends Snake {
	
	private boolean ai = true;

	AIsnake(MyPoint2D pos, double speed) {
		super(pos, speed);
	}
	
	boolean isAi() {return ai;}
	
	// automatically update the ai's direction according to food position
	void updateDirection(MyPoint2D pos) {
		if (pos.getX() > getX() && pos.getY() > getY()) {
			setDirection(Directions.DOWNRIGHT);
		} else if (pos.getX() > getX() && pos.getY() < getY()) {
			setDirection(Directions.UPRIGHT);
		} else if (pos.getX() < getX() && pos.getY() < getY()) {
			setDirection(Directions.UPLEFT);
		} else if (pos.getX() < getX() && pos.getY() > getY()) {
			setDirection(Directions.DOWNLEFT);
		} 
	}

}
