package main.htw;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class sickApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("This is S!ck");
		Button startButton = new Button();
		startButton.setText("Start Sick");
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Started!");
			}
		});

		Button stopButton = new Button();
		stopButton.setText("Stop Sick");
		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Stopped!");
			}
		});

		HBox hBox = new HBox();
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(startButton, stopButton);

		BorderPane borderPane = new BorderPane();
		BorderPane.setAlignment(hBox, Pos.CENTER);
		borderPane.setTop(hBox);

		primaryStage.setScene(new Scene(borderPane, 300, 250));
		primaryStage.show();
	}

}
