module com.hackathon.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.hackathon.game to javafx.fxml;
    exports com.hackathon.game;
}