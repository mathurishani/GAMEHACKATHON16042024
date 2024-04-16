package com.hackathon.game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class RandomNumberGuess extends Application{
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 100;
    private boolean win=false;
    private int secretNumber;
    private TextField guessInput;
    private Label resultLabel;

    @Override
    public void start(Stage primaryStage) {
        generateSecretNumber();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Guess the Number Game");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);
        root.setTop(titleLabel);

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        Label guessLabel = new Label("Enter your guess:");
        guessInput = new TextField();
        Button guessButton = new Button("Guess");
        guessButton.setOnAction(event -> checkGuess(primaryStage));
        inputBox.getChildren().addAll(guessLabel, guessInput, guessButton);
        root.setCenter(inputBox);

        resultLabel = new Label();
        resultLabel.setAlignment(Pos.CENTER);
        root.setBottom(resultLabel);

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Number Guessing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateSecretNumber() {
        Random random = new Random();
        secretNumber = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
    }

    private void checkGuess(Stage stage) {
        try {
            int guess = Integer.parseInt(guessInput.getText());
            if (guess == secretNumber) {
                resultLabel.setText("Congratulations! You guessed the number " + secretNumber + " correctly!");
//                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//                HelloController controller = fxmlLoader.getController();;
//                controller.setPrimaryStage(stage);
//                stage.setTitle("Hello!");
//                stage.setScene(scene);
//                stage.setFullScreen(false);
//                stage.setResizable(false);
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
                win=true;
                guessInput.clear();

            } else if (guess < secretNumber) {
                resultLabel.setText("Too low! Try a higher number.");
            } else {
                resultLabel.setText("Too high! Try a lower number.");
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter a valid number.");
        }



    }

    public static void main(String[] args) {
        launch(args);
    }
}
