package application;

import java.util.ArrayList;
import java.util.List;

/*Snake is a list of positions
 * This is the main logic part of the game, so it has auto-testing
 * It can move with eight directions
 * It can grow
 * It can detect collision with food and moving beyond the wall
 * Every update will refresh the snake with new position
 * */

public class Snake {
	private MyPoint2D head;
	private List<MyPoint2D> snakebody = new ArrayList<MyPoint2D>();
	private MyPoint2D previousTail = new MyPoint2D(0, 0);
	private Directions dir = Directions.RIGHT;
	private boolean checkRemove = false;
	private double speed;
	private boolean ai = false;

	// initialise the snake with head's position and a speed
	Snake(MyPoint2D pos, double speed) {
		this.head = pos;
		this.speed = speed;
		snakebody.add(head);
	}

	// update the state of snake with direction and speed
	// if snake only has one head then remove the old one,
	// replace the head with new position
	// if snake has other body, then remove the last tail
	// add the new position as new head
	void update() {
		checkRemove = getLength() > 1 ? true : false;
		MyPoint2D temp = dealHeadMove(getIndexBody(0), dir, speed);
		if (checkRemove) {
			previousTail = snakebody.remove(getLength() - 1);
			snakebody.add(0, temp);
		} else {
			previousTail = snakebody.get(0);
			snakebody.set(0, temp);
		}
	}

	boolean isAi() {
		return ai;
	}

	// return the head of the snake
	MyPoint2D getPosition() {
		return snakebody.get(0);
	}

	// return previous tail's position
	MyPoint2D getPreviousTail() {
		return previousTail;
	}

	// update one point with a direction and defined speed
	// return a updated point
	MyPoint2D dealHeadMove(MyPoint2D pos, Directions dir, double speed) {
		MyPoint2D temp = new MyPoint2D(pos.getX(), pos.getY());
		if (dir == Directions.UP) {
			temp.moveY(-speed);
		} else if (dir == Directions.DOWN) {
			temp.moveY(speed);
		} else if (dir == Directions.LEFT) {
			temp.moveX(-speed);
		} else if (dir == Directions.RIGHT) {
			temp.moveX(speed);
		} else if (dir == Directions.UPLEFT) {
			temp.moveXY(-speed * Math.sqrt(2) / 2, -speed * Math.sqrt(2) / 2);
		} else if (dir == Directions.UPRIGHT) {
			temp.moveXY(speed * Math.sqrt(2) / 2, -speed * Math.sqrt(2) / 2);
		} else if (dir == Directions.DOWNLEFT) {
			temp.moveXY(-speed * Math.sqrt(2) / 2, speed * Math.sqrt(2) / 2);
		} else if (dir == Directions.DOWNRIGHT) {
			temp.moveXY(speed * Math.sqrt(2) / 2, speed * Math.sqrt(2) / 2);
		} else {
			System.out.println("Wrong Directions");
		}
		return temp;
	}

	// return the length of the snake
	int getLength() {
		return snakebody.size();
	}

	// set the snake moving speed
	void setSpeed(double speed) {
		this.speed = speed;
	}

	// return the x/y coordinate of head
	double getX() {
		return snakebody.get(0).getX();
	}

	double getY() {
		return snakebody.get(0).getY();
	}

	// check collision with one position by checking the four coordinates
	boolean isHitWithPosition(MyPoint2D pos) {
		head = snakebody.get(0);
		return head.getX() < pos.getX() + Constants.objectSize && head.getX() + Constants.objectSize > pos.getX()
				&& head.getY() < pos.getY() + Constants.objectSize && head.getY() + Constants.objectSize > pos.getY();
	}

	// return an indexed position of the snake
	MyPoint2D getIndexBody(int i) {
		return snakebody.get(i);
	}

	// add the previous tail into the snake
	void grow() {
		snakebody.add(previousTail);
	}

	// check whether if the snake is out of the wall
	boolean isOutOfWall() {
		return head.getX() < 0 || head.getY() < 0 || head.getX() > Constants.APP_W || head.getY() > Constants.APP_H;
	}

	// get/set the default direction
	Directions getDirection() {
		return dir;
	}

	void setDirection(Directions dir) {
		this.dir = dir;
	}

	// testing 
	public static void main(String[] args) {
		MyPoint2D start = new MyPoint2D(100, 100);
		Snake program = new Snake(start, 1);
		program.run();
	}

	private void claim(boolean b) {
		if (!b)
			throw new Error("Test failure");
	}

	void run() {
		check_grow();
		check_isHitWithPosition();
		check_SnakeMove();
		check_isOutOfWall();
		System.out.println("Testing of Snake is ok");
	}

	// test the moves of snake with 8 directions
	private void check_SnakeMove() {
		Snake snake = new Snake(new MyPoint2D(100, 100), 1);
		for (Directions dir : Directions.values()) {
			MyPoint2D previousPos = snake.getPosition();
			snake.setDirection(dir);
			snake.update();
			claim(snake.getPosition().getX() == dealHeadMove(previousPos, dir, 1).getX());
			claim(snake.getPosition().getY() == dealHeadMove(previousPos, dir, 1).getY());
		}
	}

	// test growing of snake, set default position to 100,100, updating with speed 1
	// then grow
	// the head will be 101,100, the tail will be 100,100
	private void check_grow() {
		Snake snake = new Snake(new MyPoint2D(100, 100), 1);
		snake.setDirection(Directions.RIGHT);
		snake.update();
		snake.grow();
		claim(snake.getLength() == 2);
		claim(snake.getIndexBody(0).getX() == 101);
		claim(snake.getIndexBody(0).getY() == 100);
		claim(snake.getIndexBody(1).getX() == 100);
		claim(snake.getIndexBody(1).getY() == 100);
	}

	// check the collision between snake and one position with 4 directions
	// set the distance bias with 1 to make the snake near the position
	private void check_isHitWithPosition() {
		MyPoint2D testPos = new MyPoint2D(100, 100);
		int bias = 1;

		MyPoint2D up = new MyPoint2D(testPos.getX(), testPos.getY() - Constants.objectSize + bias);
		Snake snake = new Snake(up, 1);
		claim(snake.isHitWithPosition(testPos));

		MyPoint2D down = new MyPoint2D(testPos.getX(), testPos.getY() + Constants.objectSize - bias);
		Snake snake1 = new Snake(down, 1);
		claim(snake1.isHitWithPosition(testPos));

		MyPoint2D left = new MyPoint2D(testPos.getX() - Constants.objectSize + bias, testPos.getY());
		Snake snake2 = new Snake(left, 1);
		claim(snake2.isHitWithPosition(testPos));

		MyPoint2D right = new MyPoint2D(testPos.getX() + Constants.objectSize - bias, testPos.getY());
		Snake snake3 = new Snake(right, 1);
		claim(snake3.isHitWithPosition(testPos));
	}

	// check if the snake is out of the wall
	private void check_isOutOfWall() {
		head = new MyPoint2D(100, 100);
		claim(isOutOfWall() == false);

		head.setX(-1);
		claim(isOutOfWall() == true);

		head = new MyPoint2D(100, 100);
		head.setY(-1);
		claim(isOutOfWall() == true);

		head = new MyPoint2D(100, 100);
		head.setY(Constants.APP_H + 1);
		claim(isOutOfWall() == true);

		head = new MyPoint2D(100, 100);
		head.setX(Constants.APP_W + 1);
		claim(isOutOfWall() == true);
	}

}
