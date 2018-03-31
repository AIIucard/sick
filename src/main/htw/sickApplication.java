package main.htw;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import main.htw.emulator.EmulatorGUI;

public class sickApplication extends Application {

	private static PropertyManager propManager = null;
	private static ApplicationManager appManager = null;

	private static double width = 200;
	private static double height = 200;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		try {
			propManager = PropertyManager.getInstance();
			if (propManager != null) {
				width = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_WIDTH));
				height = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_HEIGHT));
			} else {
				log.info("Continue without loaded properties...");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("This is S!ck");
		Button startButton = new Button();
		startButton.setText("Start Sick");
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.info("Started Application...");
				try {
					appManager = ApplicationManager.getInstance();
					appManager.startApplication();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		Button stopButton = new Button();
		stopButton.setText("Stop Sick");
		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					appManager = ApplicationManager.getInstance();
					appManager.stopApplication();
					log.info("Stopped Application...");
				} catch (IOException e) {
					e.printStackTrace();
				}
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

		EmulatorGUI emulatorGUI = new EmulatorGUI();
		borderPane.setBottom(emulatorGUI);

		primaryStage.setScene(new Scene(borderPane, width, height));
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if (propManager != null) {
			propManager.storeProperties();
		} else {
			log.error("Cannot store properties!");
		}
	}
}
