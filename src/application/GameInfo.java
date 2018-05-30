package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/*This class includes all information needed to draw into screen
 * It can update the scores/time, and show welcome and game over info
 * */
public class GameInfo {
	private int score;
	private int aiscore;
	
	// use DoubleProperty to monitor changes of score and time
	private DoubleProperty timeProperty;
	private DoubleProperty scoreProperty;
	private DoubleProperty aiScoreProperty;
	
	private long remainTime;
	private int gameTime;
	private final int defaultTime = 10;
	private Text gameOverText;
	private Text welcomeText;

	GameInfo() {
		score = 0;
		aiscore = 0;
		setTime(defaultTime);
	}
	
	void setTime(int time) {
		gameTime = time;
		remainTime = time;
	}

	int getScore() {
		return score;
	}

	void restoreScore() {
		score = 0;
		aiscore = 0;
		scoreProperty.set(score);
		aiScoreProperty.set(aiscore);
	}

	void addScore() {
		score++;
		scoreProperty.set(score);
	}

	void addAiScore() {
		aiscore++;
		aiScoreProperty.set(aiscore);
	}
	
	void setRemainTime(int time) {
		remainTime = gameTime - time;
		timeProperty.set(remainTime);
	}

	double getRemainTime() {
		return remainTime;
	}

	void startCountTime() {
		setTime(defaultTime);
	}

	Text drawTime() {
		timeProperty = new SimpleDoubleProperty(gameTime);
		Text timeText = drawText(400, 30, "Time: " + timeProperty.get(), 30,Color.RED);
		timeProperty.addListener((ov, curVal, newVal) -> timeText.setText("Time: " + newVal.intValue()));
		return timeText;
	}

	Text drawPauseInfo() {
		Text pauseText = drawText(10, 20, "Press P to Pause Game", 14,Color.RED);
		return pauseText;
	}

	Text drawBoostInfo() {
		Text pauseText = drawText(10, 50, "Press SPACE to boost speed", 14,Color.RED);
		return pauseText;
	}

	Text drawScore() {
		scoreProperty = new SimpleDoubleProperty(score);
		Text scoreText = drawText(200, 30, "Your Score: " + (int) scoreProperty.get(), 30,Color.RED);
		scoreProperty.addListener((ov, curVal, newVal) -> scoreText.setText("Your Score: " + newVal.intValue()));
		return scoreText;
	}

	Text drawAiScore() {
		aiScoreProperty = new SimpleDoubleProperty(aiscore);
		Text aiscoreText = drawText(200, 60, "Ai Score: " + (int) scoreProperty.get(), 30,Color.RED);
		aiScoreProperty.addListener((ov, curVal, newVal) -> aiscoreText.setText("Ai Score: " + newVal.intValue()));
		return aiscoreText;
	}

	Text getWelcomeText() {
		return welcomeText;
	}

	Text drawWelcomeText() {
		welcomeText = drawText(30, 230, "Welcome to snake game, Press S to start"
				+ "\n" + "Green snake is yours"
				+ "\n" + "Use UP/DOWN/LEFT/RIGHT to control."
				, 40,Color.GREEN);
		return welcomeText;
	}

	Text getGameOverText() {
		return gameOverText;
	}

	Text drawGameOverInfo() {
		String winWord;
		if(score > aiscore) winWord = "You win!";
		else if(score < aiscore) winWord = "You lose!";
		else winWord = "It's draw!";
		gameOverText = drawText(150, 230,
				"Time Over! "+winWord+"\n"+  "Your score is " + score + ", "
						+ "AI score is " + aiscore + "\n" + "Press R to restart", 40,Color.RED);
		return gameOverText;
	}

	private Text drawText(int x, int y, String str, int size, Color color) {
		Text t = new Text(x, y, str);
		Font textFont = Font.font("Times New Roman", size);
		t.setFont(textFont);
		t.setFill(color);
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(2.0f);
		dropShadow.setOffsetY(2.0f);
		dropShadow.setColor(Color.rgb(50, 50, 50, .588));
		t.setEffect(dropShadow);
		return t;
	}

}
