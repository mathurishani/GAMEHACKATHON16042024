package com.hackathon.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    private Stage primaryStage;
    private  CatchMe catchMe;
    private RandomNumberGuess randomNumberGuess;
    private MultiClicks multiClicks;
    private SnakeGame snakeGame;
    private RPS rps;
    @FXML
    static int[][] gameLevels = {
        {
            1, 0, 0, 0
        },
        {
            0, 0, 0, 0
        },
        {
            0, 0, 0, 0
        },
        {
            0, 0, 0, 0
        }
    };
    @FXML
    public static boolean isHidden00() {
        return HelloController.isHidden(0, 0);
    }
    @FXML
    public boolean isHidden01() {
        return isHidden(0, 1);
    }
    @FXML
    public boolean isHidden02() {
        return isHidden(0, 2);
    }
    @FXML
    public boolean isHidden03() {
        return isHidden(0, 3);
    }
    @FXML
    public boolean isHidden10() {
        return isHidden(1, 0);
    }
    @FXML
    public boolean isHidden11() {
        return isHidden(1, 1);
    }
    @FXML
    public boolean isHidden12() {
        return isHidden(1, 2);
    }
    @FXML
    public boolean isHidden13() {
        return isHidden(1, 3);
    }
    @FXML
    public boolean isHidden20() {
        return isHidden(2, 0);
    }
    @FXML
    public boolean isHidden21() {
        return isHidden(2, 1);
    }
    @FXML
    public boolean isHidden22() {
        return isHidden(2, 2);
    }
    @FXML
    public boolean isHidden23() {
        return isHidden(2, 3);
    }
    @FXML
    public boolean isHidden30() {
        return isHidden(3, 0);
    }
    @FXML
    public boolean isHidden31() {
        return isHidden(3, 1);
    }
    @FXML
    public boolean isHidden32() {
        return isHidden(3, 2);
    }
    @FXML
    public boolean isHidden33() {
        return isHidden(3, 3);
    }
    @FXML
    static boolean isHidden(int row, int col) {
        // Check if the given cell is within the bounds of the gameLevels array
        if(gameLevels[row][col] == 1){
            return true;
        }

        // Check if the cell is hidden (has a value of 0)
        if (gameLevels[row][col] == 0) {
            // Check adjacent cells (up, down, left, right)
            if (row > 0 && gameLevels[row - 1][col] == 1) { // Check up
                return true;
            }
            if (row < gameLevels.length - 1 && gameLevels[row + 1][col] == 1) { // Check down
                return true;
            }
            if (col > 0 && gameLevels[row][col - 1] == 1) { // Check left
                return true;
            }
            if (col < gameLevels[0].length - 1 && gameLevels[row][col + 1] == 1) { // Check right
                return true;
            }
        }

        return false;
    }
    public void gameCompleted(int row, int col, boolean status){
        gameLevels[row][col] = status ? 1 : 0;
    }

    public void initialize(){
        catchMe = new CatchMe();
        randomNumberGuess = new RandomNumberGuess();
        snakeGame = new SnakeGame();
        multiClicks = new MultiClicks();
        rps = new RPS();
    }
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    @FXML
    private Label welcomeText;
    @FXML
    private Text start;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    public void onLevel1Clicked() throws IOException {
//        System.out.print("Comes here");
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MultiClicks.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//        MultiClicksController controller = fxmlLoader.getController();;
//        controller.setPrimaryStage(primaryStage);
//        primaryStage.setScene(scene);
        multiClicks.start(primaryStage);
    }
    @FXML
    protected void onStartClick(){
        // Create the instruction card
        VBox instructionCard = new VBox();
        instructionCard.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20px; -fx-spacing: 10px;");

        // Add title label
        Label titleLabel = new Label("Game Instructions");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        instructionCard.getChildren().add(titleLabel);

        // Add instruction labels
        Label step1Label = new Label("Step 1: Click on the circles to start playing.");
        Label step2Label = new Label("Step 2: Follow the instructions on each circle.");
        Label step3Label = new Label("Step 3: Complete all steps to win the game.");
        step1Label.setStyle("-fx-font-size: 14px;");
        step2Label.setStyle("-fx-font-size: 14px;");
        step3Label.setStyle("-fx-font-size: 14px;");
        instructionCard.getChildren().addAll(step1Label, step2Label, step3Label);

        // Create an alert to display the instruction card
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Instructions");
        alert.getDialogPane().setContent(instructionCard);

        // Show the alert
        alert.showAndWait();
    }

    @FXML
    protected void onWinClick(){
        // Create the instruction card
        VBox instructionCard = new VBox();
        instructionCard.setStyle("-fx-background-color: #F0F0F0; -fx-padding: 20px; -fx-spacing: 10px;");

        // Add title label
        Label titleLabel = new Label("Congratulations");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        instructionCard.getChildren().add(titleLabel);

        // Add instruction labels
        Label step1Label = new Label("You Won");
//        Label step2Label = new Label("You won");
//        Label step3Label = new Label("Step 3: Complete all steps to win the game.");
        step1Label.setStyle("-fx-font-size: 14px;");
//        step2Label.setStyle("-fx-font-size: 14px;");
//        step3Label.setStyle("-fx-font-size: 14px;");
        instructionCard.getChildren().addAll(step1Label);

        // Create an alert to display the instruction card
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("win win");
        alert.getDialogPane().setContent(instructionCard);

        // Show the alert
        alert.showAndWait();
    }

    @FXML
    protected void onLevel2Clicked() throws IOException {
//        System.out.print("Comes here");
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("catchme.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//        CatchMeController controller = fxmlLoader.getController();;
//        controller.setPrimaryStage(primaryStage);
//        primaryStage.setScene(scene);
        catchMe.start(primaryStage);
    }
    @FXML
    protected void onLevel3Clicked() throws IOException {
//        System.out.print("Comes here");
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("catchme.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//        CatchMeController controller = fxmlLoader.getController();;
//        controller.setPrimaryStage(primaryStage);
//        primaryStage.setScene(scene);
        randomNumberGuess.start(primaryStage);
    }
    @FXML
    protected void onLevel4Clicked() throws IOException {
//        System.out.print("Comes here");
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("catchme.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//        CatchMeController controller = fxmlLoader.getController();;
//        controller.setPrimaryStage(primaryStage);
//        primaryStage.setScene(scene);
        snakeGame.start(primaryStage);
    }
    @FXML
    protected void onLevel5Clicked() throws IOException {
//        System.out.print("Comes here");
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("catchme.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
////        Parent root = fxmlLoader.load();
//        CatchMeController controller = fxmlLoader.getController();;
//        controller.setPrimaryStage(primaryStage);
//        primaryStage.setScene(scene);
        rps.start(primaryStage);
    }
}