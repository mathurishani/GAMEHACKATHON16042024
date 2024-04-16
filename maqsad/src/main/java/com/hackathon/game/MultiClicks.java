package com.hackathon.game;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MultiClicks extends Application {
    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private int score = 0;
    private final int winThreshold = 20; // Change this threshold as desired
    private Label scoreLabel;
    private Button clickButton;
    private Timeline gameTimer;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        try {
            root.setBackground(new Background(new BackgroundImage(new Image(new FileInputStream("C:\\Users\\shoya\\IdeaProjects\\game\\src\\main\\resources\\assets\\textures\\anim\\grass.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,new BackgroundSize(100, 100,true,true, false, true))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 20; -fx-text-fill: blue;");
        BorderPane.setAlignment(scoreLabel, Pos.CENTER);
        root.setTop(scoreLabel);

        clickButton = new Button("Click Me!");
        clickButton.setStyle("-fx-font-size: 16;");
        clickButton.setOnAction(event -> incrementScore());
        BorderPane.setAlignment(clickButton, Pos.CENTER);
        root.setCenter(clickButton);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        primaryStage.setTitle("Click the Button Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        startTimer(primaryStage);
    }

    private void startTimer(Stage stage) {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> endGame(stage)));
        gameTimer.setCycleCount(1);
        gameTimer.play();
    }

    private void endGame(Stage stage) {
        clickButton.setDisable(true);
        if (score >= winThreshold) {
            scoreLabel.setText("Congratulations! You Win!");
        } else {
            scoreLabel.setText("Game Over! Your Score: " + score);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        Parent root = fxmlLoader.load();
        HelloController controller = fxmlLoader.getController();;
        controller.setPrimaryStage(stage);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.setResizable(false);

        stage.show();
    }

    private void incrementScore() {
        score++;
        scoreLabel.setText("Score: " + score);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
