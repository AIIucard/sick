package main.htw;

import java.io.IOException;
import java.util.Optional;

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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import main.htw.gui.AddFenceGUI;
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
	private Button addAreaButton;
	private Button editAreaButton;
	private Button removeAreaButton;

	private static double width = 200;
	private static double height = 200;

	private static AreaList areaList = null;
	private static TableView<Area> table = null;
	private static FlowPane areaButtonPane = null;

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
		if (xmlMarshaller != null && areaList != null) {
			xmlMarshaller.marshalAreaList(areaList);
		} else {
			log.error("Cannot store areas!");
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

	@SuppressWarnings("unchecked")
	private VBox createAreaTable() {
		areaButtonPane = createAreaButtonPane();

		table = new TableView<Area>();
		ObservableList<Area> data = FXCollections.observableArrayList(areaList.getAreas());
		table.setEditable(true);
		table.setMaxWidth(width - 20);

		TableColumn<Area, Integer> idColumn = new TableColumn<Area, Integer>("ID");
		idColumn.setMinWidth(20);
		idColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("id"));

		TableColumn<Area, String> nameColumn = new TableColumn<Area, String>("Name");
		nameColumn.setMinWidth(80);
		nameColumn.setCellValueFactory(new PropertyValueFactory<Area, String>("name"));

		TableColumn<Area, Integer> layerColumn = new TableColumn<Area, Integer>("Layer");
		layerColumn.setMinWidth(20);
		layerColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("layer"));

		TableColumn<Area, Double> distanceColumn = new TableColumn<Area, Double>("Distance to Robot");
		distanceColumn.setMinWidth(80);
		distanceColumn.setCellValueFactory(new PropertyValueFactory<Area, Double>("distanceToRobot"));

		table.setItems(data);
		table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			ObservableList<Area> selectedItems = table.getSelectionModel().getSelectedItems();
			handleTableSelection(selectedItems);
		});

		table.setRowFactory(new Callback<TableView<Area>, TableRow<Area>>() {
			@Override
			public TableRow<Area> call(TableView<Area> tableView2) {
				final TableRow<Area> row = new TableRow<>();
				row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						final int index = row.getIndex();
						if (index >= 0 && index < table.getItems().size()
								&& table.getSelectionModel().isSelected(index)) {
							table.getSelectionModel().clearSelection();
							event.consume();
							ObservableList<Area> selectedItems = table.getSelectionModel().getSelectedItems();
							handleTableSelection(selectedItems);
						}
					}
				});
				return row;
			}
		});

		table.getColumns().addAll(idColumn, nameColumn, layerColumn, distanceColumn);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(areaButtonPane, table);

		return vbox;
	}

	private FlowPane createAreaButtonPane() {
		FlowPane areaButtonPane = new FlowPane();
		areaButtonPane.setPadding(new Insets(5, 0, 5, 0));
		areaButtonPane.setVgap(4);
		areaButtonPane.setHgap(4);
		areaButtonPane.setPrefWrapLength(width - 20);
		areaButtonPane.setAlignment(Pos.BOTTOM_LEFT);

		Label label = new Label("Geo Fence Areas");
		label.setFont(new Font("Source Sans Pro", 20));

		addAreaButton = createAddAreaButton();
		editAreaButton = createEditAreaButton();
		editAreaButton.setDisable(true);
		removeAreaButton = createRemoveAreaButton();
		removeAreaButton.setDisable(true);

		areaButtonPane.getChildren().addAll(label, addAreaButton, editAreaButton, removeAreaButton);

		return areaButtonPane;
	}

	private void handleTableSelection(ObservableList<Area> selectedItems) {
		if (selectedItems.size() > 1) {
			editAreaButton.setDisable(true);
			removeAreaButton.setDisable(false);
		} else if (selectedItems.size() == 1) {
			editAreaButton.setDisable(false);
			removeAreaButton.setDisable(false);
		} else {
			editAreaButton.setDisable(true);
			removeAreaButton.setDisable(true);
		}
	}

	private Button createAddAreaButton() {
		Button button = new Button();
		button.setText("Add");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Add Area button hitted...");
				Dialog<Pair<String, Double>> dialog = new AddFenceGUI();
				Optional<Pair<String, Double>> result = dialog.showAndWait();

				result.ifPresent(nameDistance -> {
					System.out.println("Fence Name=" + nameDistance.getKey() + ", Distance=" + nameDistance.getValue());
				});
			}
		});
		return button;
	}

	private Button createEditAreaButton() {
		Button button = new Button();
		button.setText("Edit");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Edit Area button hitted...");
				// TODO: Edit Button Function
			}
		});
		return button;
	}

	private Button createRemoveAreaButton() {
		Button button = new Button();
		button.setText("Remove");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Remove Area button hitted...");
				// TODO: Remove Button Function
			}
		});
		return button;
	}

	private void addPropertyListener(Stage primaryStage) {
		primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
			if (propManager != null) {
				propManager.setProperty(PropertiesKeys.APP_WIDTH, "" + newVal);
				table.setMaxWidth(newVal.doubleValue() - 20);
				areaButtonPane.setMaxWidth(newVal.doubleValue() - 20);
				table.refresh();
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
