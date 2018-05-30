package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Scene;

// main enter class of the game, 
// init the screen and pass the scene to game screen
public class SnakeApp extends Application {
	Screen gameScreen;
	Group root;
	private Timeline timeline;

	
	@Override
	public void start(Stage primaryStage) {
		timeline = new Timeline();
		gameScreen = new Screen();
		root = gameScreen.getRoot();
		try {
			Scene scene = new Scene(root, Constants.APP_W, Constants.APP_H);
			gameScreen.init();
			gameScreen.dealWithKeyPressed(scene);
			primaryStage.setScene(scene);
			update();
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// game rendering system
	 void update() {
			KeyFrame frame = new KeyFrame(Duration.seconds(0.05), event -> {
				gameScreen.update();
			});
			timeline.getKeyFrames().add(frame);
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();
		}
	
	public static void main(String[] args) {
		launch(args);
	}
}
