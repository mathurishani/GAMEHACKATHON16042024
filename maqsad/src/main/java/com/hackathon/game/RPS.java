package com.hackathon.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class RPS extends Application {
    private static final String []CHOICES={"Rock","Paper","Scissors"};
    public static final Random RANDOM=new Random();
    private String playerChoice;
    private String CompChoice;
    private Label result;
    private boolean win;
    @Override
    public void start(Stage primaryStage)  {
        Label titleLabel=new Label("Rock,Paper,Scissors");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        Label playerLabel = new Label("Your Choice: ");
        Label computerLabel = new Label("Computer's Choice: ");
        result = new Label();
        Button rockButton = createChoiceButton("Rock", primaryStage);
        Button paperButton = createChoiceButton("Paper", primaryStage);
        Button scissorsButton = createChoiceButton("Scissors", primaryStage);

        VBox root=new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, playerLabel, rockButton, paperButton, scissorsButton,
                computerLabel, result);
        Scene scene=new Scene(root,400, 300);
        primaryStage.setTitle("Rock, Paper, Scissors");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createChoiceButton(String choice, Stage stage) {
        Button button=new Button(choice);
        button.setOnAction(event->{
            playerChoice=choice;
            CompChoice =  generateComputerChoice();
            determinWinner(stage);
        });
        return button;
    }

    private void determinWinner(Stage stage) {
        if (playerChoice.equals(CompChoice)) {
            win = false;
            result.setText("It's a draw!");
        } else if ((playerChoice.equals("Rock") && CompChoice.equals("Scissors")) ||
                (playerChoice.equals("Paper") && CompChoice.equals("Rock")) ||
                (playerChoice.equals("Scissors") && CompChoice.equals("Paper"))) {
            win=true;
            result.setText("You win!");
        } else {
            win=false;
            result.setText("Computer wins!");
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

    private String generateComputerChoice() {
        return CHOICES[RANDOM.nextInt(CHOICES.length)];
    }

    public static void main(String[] args) {
        launch(args);
    }
}