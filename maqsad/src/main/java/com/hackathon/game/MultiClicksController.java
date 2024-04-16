package com.hackathon.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MultiClicksController {
    private Stage primaryStage;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button clickButton;

    private int score = 0;
    private Timeline gameTimer;
    private final int winThreshold = 20;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        updateScoreLabel();
        startTimer();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void startTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            try {
                endGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        gameTimer.setCycleCount(1);
        gameTimer.play();
    }

    @FXML
    private void incrementScore(ActionEvent event) {
        score++;
        updateScoreLabel();
    }

    private void endGame() throws IOException {
        clickButton.setDisable(true);
        if (score >= winThreshold) {
            scoreLabel.setText("Congratulations! You Win!");
        } else {
            scoreLabel.setText("Game Over! Your Score: " + score);
        }

        VBox instructionCard = new VBox();
        instructionCard.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20px; -fx-spacing: 10px;");

        // Add title label
        Label titleLabel = new Label(scoreLabel.getText());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        instructionCard.getChildren().add(titleLabel);

//        // Add instruction labels
//        Label step1Label = new Label("Step 1: Click on the circles to start playing.");
//        Label step2Label = new Label("Step 2: Follow the instructions on each circle.");
//        Label step3Label = new Label("Step 3: Complete all steps to win the game.");
//        step1Label.setStyle("-fx-font-size: 14px;");
//        step2Label.setStyle("-fx-font-size: 14px;");
//        step3Label.setStyle("-fx-font-size: 14px;");
//        instructionCard.getChildren().addAll(step1Label, step2Label, step3Label);

        // Create an alert to display the instruction card
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Instructions");
        alert.getDialogPane().setContent(instructionCard);

        // Show the alert

        alert.setOnCloseRequest(dialogEvent -> {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 600, 400);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            HelloController controller = fxmlLoader.getController();
            controller.gameCompleted(0, 1, !scoreLabel.getText().contains("Game Over"));
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(scene);
        });
        alert.show();

    }
}

