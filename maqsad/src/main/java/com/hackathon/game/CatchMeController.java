package com.hackathon.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class CatchMeController {

    @FXML
    private Label scoreLabel;

    @FXML
    private Circle ball;

    private Random random = new Random();
    private final int winThreshold = 10;
    private int score = 0;
    private final int screenWidth = 600;
    private final int screenHeight = 400;
    private boolean isMoving = false;
    private Timeline gameTimer;
    Stage primaryStage;
    private boolean when=false;
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    @FXML
    private void initialize() {
        moveBall(); // Move the ball initially to avoid initial mouse click issues
        startTimer();
    }

    private void startTimer() {
        // Implement your timer logic here
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            endGame();
        }));
        gameTimer.setCycleCount(1);
        gameTimer.play();
    }

    private void endGame() {
        if (score >= winThreshold) {
            when=true;
            // You can display a win message, show a winning animation, etc.
        } else {
            when=false;
            System.out.println("Game Over!");
            // You can display a game over message, show final score, etc.
        }
        VBox instructionCard = new VBox();
        instructionCard.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20px; -fx-spacing: 10px;");

        // Add title label
        Label titleLabel = new Label("Go back");
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
            controller.gameCompleted(1,0, when);
            controller.setPrimaryStage(primaryStage);
            primaryStage.setScene(scene);
        });
        alert.show();
    }

    @FXML
    private void moveBall() {
        double mouseX =0, mouseY=0;
        if (isMoving) {
            return;
        }

        double distanceX = Math.abs(ball.getLayoutX() + ball.getRadius() - mouseX);
        double distanceY = Math.abs(ball.getLayoutY() + ball.getRadius() - mouseY);

        if (distanceX < 50 && distanceY < 50) {
            isMoving = true;
            double newX = random.nextInt(screenWidth - (int) ball.getRadius() * 2) + ball.getRadius();
            double newY = random.nextInt(screenHeight - (int) ball.getRadius() * 2) + ball.getRadius();

            ball.setLayoutX(newX);
            ball.setLayoutY(newY);
            isMoving = false;
        }
    }

    @FXML
    private void handleClick() {
        double mouseX = ball.getLayoutX() + ball.getRadius();
        double mouseY = ball.getLayoutY() + ball.getRadius();

        double distanceX = Math.abs(mouseX - ball.getRadius());
        double distanceY = Math.abs(mouseY - ball.getRadius());

        if (distanceX < 20 && distanceY < 20) {
            score++;
            scoreLabel.setText("Score: " + score);
            ball.setLayoutX(random.nextInt(screenWidth - (int) ball.getRadius() * 2) + ball.getRadius());
            ball.setLayoutY(random.nextInt(screenHeight - (int) ball.getRadius() * 2) + ball.getRadius());
        }
    }
}
