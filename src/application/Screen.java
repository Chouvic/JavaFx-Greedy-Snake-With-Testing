package application;

import java.util.HashMap;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import junit.framework.Test;

/*
 * This class includes all elements into the screen
 * deal with the key states
 * updating all elements every specified seconds
 *
 */

public class Screen {

	private Group root;
	private Canvas canvas;
	private GraphicsContext gc;
	private HashMap<KeyCode, String> keyMap;

	private GameInfo gameinfo;
	private Snake snake;
	private Food food;
	private AIsnake aisnake;
	private MyPoint2D snakePos;
	private MyPoint2D foodPos;
	private double defaultSpeed = 5;

	private boolean running = false;
	private boolean restart;
	private boolean pause = false;
	// game state 0 is running, 1 is pause
	private int gameState = 1;

	private Image[] foodimage = new Image[10];
	private Image shead = new Image("source/snakehead.jpg");
	private ImagePattern imagePattern = new ImagePattern(shead);
	private Image aihead = new Image("source/headAi.png");
	private ImagePattern aiimagePattern = new ImagePattern(aihead);
	private MediaPlayer mediaplayer;

	// count the running time
	private int time = 0;

	// Create the create with a new group
	Screen() {
		root = new Group();
	}

	// Only return this root for setting up the screen
	Group getRoot() {
		return root;
	}

	// initialise the screen
	void init() {
		initFoodImage();
		setScreen();
		setGraphicsContent();
		drawBgLines();
		renderFood();
		// render();
	}

	// initialise the food image with source images
	private void initFoodImage() {
		String s = "source/food";
		for (int i = 0; i < 10; i++) {
			foodimage[i] = new Image(s + i + ".png");
		}
	}

	// Initialise the screen, set the position of new snake and food, add all game
	// contents
	private void setScreen() {
		keyMap = new HashMap<KeyCode, String>();
		gameinfo = new GameInfo();
		foodPos = getRandPositon();
		snakePos = new MyPoint2D(Constants.APP_W / 2, Constants.APP_H / 2);
		snake = new Snake(snakePos, defaultSpeed);
		aisnake = new AIsnake(getRandPositon(), defaultSpeed);
		food = new Food(foodPos);

	}

	// add the canvas and game information into the group root, initialise the
	// graphics content
	private void setGraphicsContent() {
		canvas = new Canvas(Constants.APP_W, Constants.APP_H);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		root.getChildren().add(gameinfo.drawScore());
		root.getChildren().add(gameinfo.drawAiScore());
		root.getChildren().add(gameinfo.drawTime());
		root.getChildren().add(gameinfo.drawBoostInfo());
		root.getChildren().add(gameinfo.drawPauseInfo());
		root.getChildren().add(gameinfo.drawWelcomeText());
	}

	// update the game logic and draw all snakes
	void update() {
		updateGameLogic();
		drawAllComponents();
	}

	// game logic of the snakes and check game over
	private void updateGameLogic() {
		if (!running) {
			return;
		}
		updateSnake(snake);
		updateSnake(aisnake);
		countRunningTime();
		checkGameOver();
	}

	// update the snake with its logic including hitting with food
	// and out of wall.
	private void updateSnake(Snake snake) {
		if (!pause) {
			checkHitFood(snake);
			checkOutWall(snake);
			boostSpeed();
			snake.update();
			if (snake.isAi()) {
				aisnake.updateDirection(food.getPosition());
			}
		}
	}

	// count the running time according to the running times of the updateGameLogic Method
	private void countRunningTime() {
		if (!pause) {
			time++;
			if (time % 20 == 0) {
				gameinfo.setRemainTime((int) (time / 20));
			}
		}
	}

	// check if the snake hits the food,
	// set the new food position and draw it into the screen
	private void checkHitFood(Snake snake) {
		if (snake.isHitWithPosition(food.getPosition())) {
			setNewFood();
			renderFood();
			snake.grow();
			if (snake.isAi()) {
				gameinfo.addAiScore();
			} else {
				gameinfo.addScore();
			}
		}
	}

	// if snake moves beyond the wall, clear the drawing of snake
	// set the position to wall's opposite site to make snake move around
	private void checkOutWall(Snake snake) {
		if (snake.isOutOfWall()) {
			clearSnakeBody();
			if (snake.getIndexBody(0).getX() < 0) {
				MyPoint2D temp = snake.getIndexBody(0);
				temp.setX(Constants.APP_W);
			} else if (snake.getIndexBody(0).getX() > Constants.APP_W) {
				snake.getIndexBody(0).setX(0);
			} else if (snake.getIndexBody(0).getY() < 0) {
				snake.getIndexBody(0).setY(Constants.APP_H);
			} else if (snake.getIndexBody(0).getY() > Constants.APP_H) {
				snake.getIndexBody(0).setY(0);
			}
		}
	}

	// boost the snake if users press the SPACE button
	private void boostSpeed() {
		if (checkContainKey(KeyCode.SPACE) && (keyMap.get(KeyCode.SPACE) == "1")) {
			snake.setSpeed(10.0);
		} else if (checkContainKey(KeyCode.SPACE) && (keyMap.get(KeyCode.SPACE) == "0")) {
			snake.setSpeed(5.0);
		}
	}

	// check if the key map library contains a specific key code
	private boolean checkContainKey(KeyCode code) {
		return keyMap.containsKey(code);
	}

	// keep checking the remain time, when it is 0 show game over message
	private void checkGameOver() {
		if (gameinfo.getRemainTime() == 0) {
			clearSnakeBody();
			root.getChildren().add(gameinfo.drawGameOverInfo());
			mediaplayer.stop();
			pause = true;
			running = false;
			restart = true;
			time = 0;
			gameinfo.setRemainTime(time);
		}
	}

	// draw the game info and snakes
	private void drawAllComponents() {
		renderSnake(snake);
		renderSnake(aisnake);
	}

	// draw the snake every time stamp, clear the tail of it, fill an image as head
	private void renderSnake(Snake snake) {
		clearSnakeTail(snake);
		drawSnakeBody(snake);
	}

	// clear previous tail of the snake
	private void clearSnakeTail(Snake snake) {
		gc.clearRect(snake.getPreviousTail().getX(), snake.getPreviousTail().getY(), Constants.objectSize,
				Constants.objectSize);
	}

	// draw snake head first and then draw body, with different settings of the user
	// snake and AI snake
	private void drawSnakeBody(Snake snake) {
		for (int i = snake.getLength() - 1; i >= 0; i--) {
			MyPoint2D curPoint = snake.getIndexBody(i);
			if (i == 0) {
				if (snake.isAi()) {
					gc.setFill(aiimagePattern);
				} else {
					gc.setFill(imagePattern);
				}
			} else {
				if (snake.isAi()) {
					gc.setFill(Color.BLUE);
				} else {
					gc.setFill(Color.GREEN);
				}
			}
			gc.fillOval(curPoint.getX(), curPoint.getY(), Constants.objectSize, Constants.objectSize);
		}
	}

	// show the screen with setting the screen and rendering elements
	void startGame() {
		root.getChildren().remove(gameinfo.getWelcomeText());
		gameinfo.startCountTime();
		playMusic();
		mediaplayer.play();
		pause = false;
		restart = false;
		running = true;
	}

	// pause the time line if it is running
	private void pauseGame() {
		if (running) {
			pause = true;
			mediaplayer.pause();
		}
	}

	// restart the game from pause if it is running
	private void restartFromPause() {
		if (running) {
			pause = false;
			mediaplayer.play();
		}
	}

	// stop the game
	private void stopGame() {
		running = false;
		mediaplayer.stop();
		snake = new Snake(snakePos, defaultSpeed);
	}

	// set scores and time to previous state and remove the game over text from the
	// screen
	private void newGame() {
		clearSnakeBody();
		gameinfo.restoreScore();
		gameinfo.startCountTime();
		root.getChildren().remove(gameinfo.getGameOverText());
		stopGame();
		startGame();
	}

	// clear the users snake body and its previous tail
	private void clearSnakeBody() {
		gc.clearRect(snake.getPreviousTail().getX(), snake.getPreviousTail().getY(), Constants.objectSize,
				Constants.objectSize);
		for (int i = 0; i < snake.getLength(); i++) {
			gc.clearRect(snake.getIndexBody(i).getX(), snake.getIndexBody(i).getY(), Constants.objectSize,
					Constants.objectSize);
		}
	}

	// draw food with new position
	private void setNewFood() {
		gc.clearRect(food.getX(), food.getY(), Constants.objectSize, Constants.objectSize);
		food.setPosition(getRandPositon());
	}

	// draw the food image
	private void renderFood() {
		gc.setFill(getFoodImage());
		gc.fillOval(food.getX(), food.getY(), Constants.objectSize, Constants.objectSize);
	}

	// get a random fruit image for food
	private ImagePattern getFoodImage() {
		ImagePattern imagePattern = new ImagePattern(foodimage[(int) (Math.random() * 10)]);
		return imagePattern;
	}

	// draw lines as background of screen
	private void drawBgLines() {
		for (int i = 0; i < Constants.APP_W; i += 20) {
			Line line = new Line(i, 0, i, Constants.APP_W);
			line.setStroke(Color.web("#999", 0.3));
			Line line1 = new Line(0, i, Constants.APP_W, i);
			line1.setStroke(Color.web("#999", 0.3));
			root.getChildren().add(line);
			root.getChildren().add(line1);
		}
	}

	// get random position adjusting with a small bias
	private MyPoint2D getRandPositon() {
		double startX = Constants.objectSize / 2 + (Math.random() * (Constants.APP_W - Constants.objectSize));
		double startY = Constants.objectSize / 2 + (Math.random() * (Constants.APP_H - Constants.objectSize));
		return new MyPoint2D(startX, startY);
	}

	// play music and repeat it
	private void playMusic() {
		String bgfile = Test.class.getResource("/source/snakebgmusic.mp3").toString();
		Media musicFile = new Media(bgfile);
		mediaplayer = new MediaPlayer(musicFile);
		mediaplayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaplayer.setVolume(0.3);
	}

	// set the key pressed event and record the key states in the key map
	// deal with the game state according to some keys
	void dealWithKeyPressed(Scene scene) {
		scene.setOnKeyPressed(event -> {
			KeyCode tmpKeyCode = event.getCode();
			keyMap.put(tmpKeyCode, "1");
			dealWithGameStates(tmpKeyCode);
			dealWithUserSnakeMoves(tmpKeyCode);
		});

		scene.setOnKeyReleased(event -> {
			KeyCode code = event.getCode();
			keyMap.put(code, "0");
		});
	}

	// Use P to pause and continue game, N to restart a new game, S to start a game
	private void dealWithGameStates(KeyCode tmpKeyCode) {
		if (tmpKeyCode == KeyCode.P) {
			if (gameState == 1) {
				pauseGame();
				gameState = 0;
			} else if (gameState == 0) {
				restartFromPause();
				gameState = 1;
			}
		} else if (tmpKeyCode == KeyCode.R && restart == true) {
			newGame();
		} else if (tmpKeyCode == KeyCode.S && running == false && restart == false) {
			startGame();
		}
	}

	// set the movement of snake with 8 directions
	// disable the opposite and the same direction of the previous direction
	private void dealWithUserSnakeMoves(KeyCode tmpKeyCode) {
		if (tmpKeyCode == KeyCode.UP) {
			switch (snake.getDirection()) {
			case DOWNLEFT:
				snake.setDirection(Directions.LEFT);
				break;
			case LEFT:
				snake.setDirection(Directions.UPLEFT);
				break;
			case UPLEFT:
				snake.setDirection(Directions.UP);
				break;
			case DOWNRIGHT:
				snake.setDirection(Directions.RIGHT);
				break;
			case RIGHT:
				snake.setDirection(Directions.UPRIGHT);
				break;
			case UPRIGHT:
				snake.setDirection(Directions.UP);
				break;
			default:
				break;
			}
		} else if (tmpKeyCode == KeyCode.DOWN) {
			switch (snake.getDirection()) {
			case DOWNLEFT:
				snake.setDirection(Directions.DOWN);
				break;
			case LEFT:
				snake.setDirection(Directions.DOWNLEFT);
				break;
			case UPLEFT:
				snake.setDirection(Directions.LEFT);
				break;
			case DOWNRIGHT:
				snake.setDirection(Directions.DOWN);
				break;
			case RIGHT:
				snake.setDirection(Directions.DOWNRIGHT);
				break;
			case UPRIGHT:
				snake.setDirection(Directions.RIGHT);
				break;
			default:
				break;
			}

		} else if (tmpKeyCode == KeyCode.LEFT) {
			switch (snake.getDirection()) {
			case DOWNLEFT:
				snake.setDirection(Directions.LEFT);
				break;
			case UP:
				snake.setDirection(Directions.UPLEFT);
				break;
			case UPLEFT:
				snake.setDirection(Directions.LEFT);
				break;
			case DOWN:
				snake.setDirection(Directions.DOWNLEFT);
				break;
			case DOWNRIGHT:
				snake.setDirection(Directions.DOWN);
				break;
			case UPRIGHT:
				snake.setDirection(Directions.UP);
				break;
			default:
				break;
			}
		} else if (tmpKeyCode == KeyCode.RIGHT) {
			switch (snake.getDirection()) {
			case DOWNLEFT:
				snake.setDirection(Directions.DOWN);
				break;
			case DOWNRIGHT:
				snake.setDirection(Directions.RIGHT);
				break;
			case UPLEFT:
				snake.setDirection(Directions.UP);
				break;
			case UPRIGHT:
				snake.setDirection(Directions.RIGHT);
				break;
			case UP:
				snake.setDirection(Directions.UPRIGHT);
				break;
			case DOWN:
				snake.setDirection(Directions.DOWNRIGHT);
				break;
			default:
				break;
			}
		}
	}

}
