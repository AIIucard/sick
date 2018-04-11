package main.htw;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.htw.gui.EmulatorGUI;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.XMLMarshler;

public class SickApplication extends Application {

	private static CFGPropertyManager propManager = null;
	private static XMLMarshler xmlMarshaller = null;
	private static ApplicationManager appManager = null;

	private Button startButton;
	private Button stopButton;

	private static double width = 200;
	private static double height = 200;

	private static AreaList areaList = null;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		try {
			propManager = CFGPropertyManager.getInstance();
			if (propManager != null) {
				width = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_WIDTH));
				height = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_HEIGHT));
			} else {
				log.info("Continue without loaded properties...");
			}

			xmlMarshaller = XMLMarshler.getInstance();
			if (xmlMarshaller != null) {
				areaList = xmlMarshaller.unMarshalAreaList();
			} else {
				log.info("Continue without loaded areas...");
			}
		} catch (IOException e) {
			log.error("IOException thrown: " + e);
		}
	}

	@Override
	public void start(Stage primaryStage) {
		createPrimaryStage(primaryStage);
		addPropertyListener(primaryStage);
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

	public void createPrimaryStage(Stage primaryStage) {

		primaryStage.setTitle("This is S!ck");
		BorderPane borderPane = new BorderPane();

		HBox startStopButtons = createStartStopButtons();
		VBox areaTable = createAreaTable();

		BorderPane.setAlignment(startStopButtons, Pos.CENTER);
		borderPane.setTop(startStopButtons);

		BorderPane.setAlignment(areaTable, Pos.CENTER);
		borderPane.setCenter(areaTable);

		EmulatorGUI emulatorGUI = new EmulatorGUI();
		borderPane.setBottom(emulatorGUI);

		primaryStage.setScene(new Scene(borderPane, width, height));
	}

	private HBox createStartStopButtons() {

		startButton = new Button();
		startButton.setText("Start Sick");
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.info("Started Application...");
				try {
					appManager = ApplicationManager.getInstance();
					appManager.startApplication();
					startButton.setDisable(true);
					stopButton.setDisable(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		stopButton = new Button();
		stopButton.setText("Stop Sick");
		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					appManager = ApplicationManager.getInstance();
					appManager.stopApplication();
					startButton.setDisable(false);
					stopButton.setDisable(true);
					log.info("Stopped Application...");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		startButton.setDisable(false);
		stopButton.setDisable(true);
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(startButton, stopButton);

		return hBox;
	}

	private VBox createAreaTable() {
		final Label label = new Label("Geo Fence Areas");
		label.setFont(new Font("Source Sans Pro", 16));

		TableView<Area> table = new TableView<Area>();
		ObservableList<Area> data = FXCollections.observableArrayList(areaList.getAreas());
		table.setEditable(true);

		TableColumn<Area, Integer> idColumn = new TableColumn<Area, Integer>("ID");
		idColumn.setMinWidth(20);
		idColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("ID"));

		TableColumn<Area, String> nameColumn = new TableColumn<Area, String>("Name");
		nameColumn.setMinWidth(80);
		nameColumn.setCellValueFactory(new PropertyValueFactory<Area, String>("Name"));

		TableColumn<Area, Integer> layerColumn = new TableColumn<Area, Integer>("Layer");
		layerColumn.setMinWidth(20);
		layerColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("Layer"));

		TableColumn<Area, Double> distanceColumn = new TableColumn<Area, Double>("Distance to Robot");
		distanceColumn.setMinWidth(80);
		distanceColumn.setCellValueFactory(new PropertyValueFactory<Area, Double>("Distance to Robot"));

		table.getColumns().addAll(idColumn, nameColumn, layerColumn, distanceColumn);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(label, table);

		return vbox;
	}

	private void addPropertyListener(Stage primaryStage) {
		primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
			if (propManager != null) {
				propManager.setProperty(PropertiesKeys.APP_WIDTH, "" + newVal);
			} else {
				log.error("Cannot change app width property!");
			}
		});

		primaryStage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
			if (propManager != null) {
				propManager.setProperty(PropertiesKeys.APP_HEIGHT, "" + newVal);
			} else {
				log.error("Cannot change app height property!");
			}
		});
	}
}
