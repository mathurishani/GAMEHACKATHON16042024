package com.hackathon.game;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class CatchMe extends Application {
    private Circle ball;
    Stage primaryStage;
    private boolean win=false;
    private Random random = new Random();
    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private boolean isMoving = false;
    private int score = 0;
    private final int winThreshold = 10; // Change this threshold as desired
    private Label scoreLabel;
    private Label message;
    private Timeline gameTimer;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        message = new Label("You'll never catch me! Try to click on me once LOL");
        message.setStyle("-fx-font-size: 24; -fx-text-fill: red;");
        message.setLayoutX((screenWidth - message.getWidth()) / 4 - 55);
        message.setLayoutY(screenHeight - message.getHeight() - 30);
        root.getChildren().add(message);


        ball = createBall();
        root.getChildren().add(ball);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-font-size: 16; -fx-text-fill: red;");
        root.getChildren().add(scoreLabel);

        Scene scene = new Scene(root, screenWidth, screenHeight);
        scene.setOnMouseMoved(event -> moveBall(event.getSceneX(), event.getSceneY()));
        scene.setOnMouseClicked(event -> handleClick(event.getSceneX(), event.getSceneY()));

        primaryStage.setTitle("Escape the Cursor Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the game timer
        startTimer(primaryStage);
    }

    private Circle createBall() {
        Circle circle = new Circle(20, Color.BLUE);
        circle.relocate(random.nextInt(screenWidth - (int) circle.getRadius() * 2) + circle.getRadius(),
                random.nextInt(screenHeight - (int) circle.getRadius() * 2) + circle.getRadius());
        return circle;
    }

    private void startTimer(Stage stage) {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            try {
                endGame(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        gameTimer.setCycleCount(1); // Run only once
        gameTimer.play();
    }

    private void endGame(Stage stage) throws IOException {
        if (score >= winThreshold) {
            win=true;
        } else {
            System.out.println("Game Over!");
            win = false;


            stage.show();

        }
VBox instructionCard = new VBox();
    instructionCard.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20px; -fx-spacing: 10px;");

    // Add title label
    Label titleLabel = new Label("Go back");
    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
    instructionCard.getChildren().add(titleLabel);

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Instructions");
    alert.getDialogPane().setContent(instructionCard);

    alert.setOnCloseRequest(dialogEvent -> {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HelloController controller = fxmlLoader.getController();
        controller.gameCompleted(1,0, win);
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
    });
        alert.show();
    }



    private void moveBall(double mouseX, double mouseY) {
        if (isMoving) {
            return;
        }

        double distanceX = Math.abs(ball.getLayoutX() + ball.getRadius() - mouseX);
        double distanceY = Math.abs(ball.getLayoutY() + ball.getRadius() - mouseY);

        if (distanceX < 50 && distanceY < 50) {
            isMoving = true;
            double newX = random.nextInt(screenWidth - (int) ball.getRadius() * 2) + ball.getRadius();
            double newY = random.nextInt(screenHeight - (int) ball.getRadius() * 2) + ball.getRadius();

            Timeline transition = new Timeline(new KeyFrame(Duration.millis(200), e -> {
                ball.setLayoutX(newX);
                ball.setLayoutY(newY);
                isMoving = false;
            }));
            transition.setCycleCount(1);
            transition.play();
        }
    }

    private void handleClick(double mouseX, double mouseY) {
        double distanceX = Math.abs(ball.getLayoutX() + ball.getRadius() - mouseX);
        double distanceY = Math.abs(ball.getLayoutY() + ball.getRadius() - mouseY);

        if (distanceX < 20 && distanceY < 20) {
            score++;
            scoreLabel.setText("Score: " + score);
            ball.setLayoutX(random.nextInt(screenWidth - (int) ball.getRadius() * 2) + ball.getRadius());
            ball.setLayoutY(random.nextInt(screenHeight - (int) ball.getRadius() * 2) + ball.getRadius());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
