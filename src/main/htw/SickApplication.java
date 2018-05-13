package main.htw;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import main.htw.database.SickDatabase;
import main.htw.gui.AddFenceGUI;
import main.htw.gui.ConfigureRobotPositionGUI;
import main.htw.gui.EmulatorGUI;
import main.htw.handler.RTLSConnectionHandler;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ConnectionStatusType;
import main.htw.utils.SickUtils;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.XMLMarshler;

public class SickApplication extends Application implements Observer {

	private static final String APP_TITLE = "This is S!ck";
	private static final String MENU_FILE = "File";
	private static final String MENU_CONFIGURE = "Configure";
	private static final String START_BUTTON = "Start Sick";
	private static final String STOP_BUTTON = "Stop Sick";

	private static final String AREAS_TITLE = "Geo Fence Areas";
	private static final String ID_COLUMN = "ID";
	private static final String NAME_COLUMN = "Name";
	private static final String LAYER_COLUMN = "Layer";
	private static final String DISTANCE_COLUMN = "Distance to Robot";
	private static final String ADD_AREA_BUTTON = "Add";
	private static final String EDIT_AREA_BUTTON = "Edit";
	private static final String REMOVE_AREA_BUTTON = "Remove";

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

	private static MenuBar sickMenuBar = null;
	private static TableView<Area> table = null;
	private static ObservableList<Area> tableData = null;
	private static FlowPane areaButtonPane = null;

	private Map<String, Image> appIconSet = new LinkedHashMap<>();
	private ObjectProperty<ConnectionStatusType> rtlsStatus = new SimpleObjectProperty<>(ConnectionStatusType.NEW);
	private ObjectProperty<ConnectionStatusType> robotStatus = new SimpleObjectProperty<>(ConnectionStatusType.NEW);
	private ObjectProperty<ConnectionStatusType> lightStatus = new SimpleObjectProperty<>(ConnectionStatusType.NEW);

	private SickDatabase database;

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		try {
			database = SickDatabase.getInstance();
			database.addObserver(this);
			propManager = CFGPropertyManager.getInstance();
			if (propManager != null) {
				width = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_WIDTH));
				height = Double.parseDouble(propManager.getProperty(PropertiesKeys.APP_HEIGHT));

				database.setRobotPositionX(
						Double.parseDouble(propManager.getProperty(PropertiesKeys.ROBOT_X_COORDINATE)));
				database.setRobotPositionY(
						Double.parseDouble(propManager.getProperty(PropertiesKeys.ROBOT_Y_COORDINATE)));
			} else {
				log.info("Continue without loaded properties...");
			}

			xmlMarshaller = XMLMarshler.getInstance();
			if (xmlMarshaller != null) {
				database.setAreaList(xmlMarshaller.unMarshalAreaList());
				database.setBadgeList(xmlMarshaller.unMarshalBadgeList());
			} else {
				log.info("Continue without loaded areas...");
				log.info("Continue without loaded badges...");
			}

			loadAppIconSet();
		} catch (IOException e) {
			log.error("IOException thrown: " + e.getLocalizedMessage());
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
		saveData();
	}

	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				rtlsStatus.set(database.getRTLSConnectionStatus());
				robotStatus.set(database.getRobotConnectionStatus());
				lightStatus.set(database.getLightConnectionStatus());
			}
		});
	}

	public void createPrimaryStage(Stage primaryStage) {

		primaryStage.setTitle(APP_TITLE);
		BorderPane borderPane = new BorderPane();
		VBox centerVBox = new VBox();

		sickMenuBar = createMenuBar();

		HBox startStopButtons = createStartStopButtons();
		GridPane statusArea = createConnectionStatusArea();
		VBox areaTable = createAreaTable();

		borderPane.setTop(sickMenuBar);

		centerVBox.setAlignment(Pos.CENTER);
		centerVBox.getChildren().add(startStopButtons);
		centerVBox.getChildren().add(statusArea);

		BorderPane.setAlignment(centerVBox, Pos.CENTER);
		borderPane.setCenter(centerVBox);
		centerVBox.getChildren().add(areaTable);

		EmulatorGUI emulatorGUI = new EmulatorGUI();
		borderPane.setBottom(emulatorGUI);

		primaryStage.setScene(new Scene(borderPane, width, height));
	}

	private GridPane createConnectionStatusArea() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(12);
		grid.setAlignment(Pos.CENTER);

		Label rtlsLabel = new Label("RTLS Connection Status: ");
		ImageView rtlsImageView = new ImageView();
		Label rtlsStatusLabel = new Label("");
		rtlsStatusLabel.textProperty().bind(rtlsStatus.asString());
		rtlsImageView.imageProperty()
				.bind(new When(rtlsStatus.isEqualTo(ConnectionStatusType.NEW)).then(appIconSet.get("gray"))
						.otherwise(new When(rtlsStatus.isEqualTo(ConnectionStatusType.OK)).then(appIconSet.get("green"))
								.otherwise(new When(rtlsStatus.isEqualTo(ConnectionStatusType.ERROR))
										.then(appIconSet.get("red")).otherwise(appIconSet.get("yellow")))));

		rtlsStatusLabel.setGraphic(rtlsImageView);
		rtlsStatusLabel.setContentDisplay(ContentDisplay.RIGHT);

		Label robotLabel = new Label("Robot Connection Status: ");
		ImageView robotImageView = new ImageView();
		Label robotStatusLabel = new Label("");
		robotStatusLabel.textProperty().bind(robotStatus.asString());
		robotImageView.imageProperty().bind(new When(robotStatus.isEqualTo(ConnectionStatusType.NEW))
				.then(appIconSet.get("gray"))
				.otherwise(new When(robotStatus.isEqualTo(ConnectionStatusType.OK)).then(appIconSet.get("green"))
						.otherwise(new When(robotStatus.isEqualTo(ConnectionStatusType.ERROR))
								.then(appIconSet.get("red")).otherwise(appIconSet.get("yellow")))));

		robotStatusLabel.setGraphic(robotImageView);
		robotStatusLabel.setContentDisplay(ContentDisplay.RIGHT);

		Label lightLabel = new Label("Light Connection Status: ");
		ImageView lightImageView = new ImageView();
		Label lightStatusLabel = new Label("");
		lightStatusLabel.textProperty().bind(lightStatus.asString());
		lightImageView.imageProperty().bind(new When(lightStatus.isEqualTo(ConnectionStatusType.NEW))
				.then(appIconSet.get("gray"))
				.otherwise(new When(lightStatus.isEqualTo(ConnectionStatusType.OK)).then(appIconSet.get("green"))
						.otherwise(new When(lightStatus.isEqualTo(ConnectionStatusType.ERROR))
								.then(appIconSet.get("red")).otherwise(appIconSet.get("yellow")))));

		lightStatusLabel.setGraphic(lightImageView);
		lightStatusLabel.setContentDisplay(ContentDisplay.RIGHT);

		grid.add(rtlsLabel, 0, 0);
		grid.add(rtlsStatusLabel, 1, 0);
		grid.add(robotLabel, 0, 1);
		grid.add(robotStatusLabel, 1, 1);
		grid.add(lightLabel, 0, 2);
		grid.add(lightStatusLabel, 1, 2);
		return grid;
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu(MENU_FILE);
		Menu menuConfigure = new Menu(MENU_CONFIGURE);

		File saveIconFile = new File("icons" + File.separator + "save.png");
		MenuItem save = new MenuItem("Save Settings", new ImageView(new Image(saveIconFile.toURI().toString())));
		save.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				log.debug("Save Settings button hitted...");
				saveData();
			}
		});

		menuFile.getItems().addAll(save);

		File robotPositionIconConfigure = new File("icons" + File.separator + "robot_position.png");
		MenuItem robotPosition = new MenuItem("Configure Robot Position",
				new ImageView(new Image(robotPositionIconConfigure.toURI().toString())));
		robotPosition.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				log.debug("Configure Robot Position button hitted...");
				Dialog<Pair<Double, Double>> dialog = new ConfigureRobotPositionGUI();
				Optional<Pair<Double, Double>> result = dialog.showAndWait();

				result.ifPresent(xyCoordinates -> {
					SickDatabase database = SickDatabase.getInstance();
					boolean updateNeeded = false;
					double oldXCoordniate = database.getRobotPositionX();
					double oldYCoordniate = database.getRobotPositionY();
					if (oldXCoordniate != xyCoordinates.getKey()) {
						propManager.setProperty(PropertiesKeys.ROBOT_X_COORDINATE, "" + xyCoordinates.getKey());
						database.setRobotPositionX(xyCoordinates.getKey());
						updateNeeded = true;
					}
					if (oldYCoordniate != xyCoordinates.getValue()) {
						propManager.setProperty(PropertiesKeys.ROBOT_Y_COORDINATE, "" + xyCoordinates.getValue());
						database.setRobotPositionX(xyCoordinates.getValue());
						updateNeeded = true;
					}
					if (updateNeeded) {
						SickUtils.updateAreaShapes();
					}
				});
			}
		});

		menuConfigure.getItems().addAll(robotPosition);

		menuBar.getMenus().addAll(menuFile, menuConfigure);

		return menuBar;
	}

	private HBox createStartStopButtons() {

		startButton = new Button();
		startButton.setText(START_BUTTON);
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
		stopButton.setText(STOP_BUTTON);
		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					appManager = ApplicationManager.getInstance();
					appManager.stopApplication();
					startButton.setDisable(false);
					stopButton.setDisable(true);
					resetConnectionStatus();
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

	private void resetConnectionStatus() {
		database.setRTLSConnectionStatus(ConnectionStatusType.NEW);
		database.setRobotConnectionStatus(ConnectionStatusType.NEW);
		database.setLightConnectionStatus(ConnectionStatusType.NEW);
	}

	@SuppressWarnings("unchecked")
	private VBox createAreaTable() {
		areaButtonPane = createAreaButtonPane();

		table = new TableView<Area>();
		tableData = FXCollections.observableArrayList(SickDatabase.getInstance().getAreaList().getAreas());
		table.setEditable(true);
		table.setMaxWidth(width - 20);

		TableColumn<Area, Integer> idColumn = new TableColumn<Area, Integer>(ID_COLUMN);
		idColumn.setMinWidth(20);
		idColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("id"));

		TableColumn<Area, String> nameColumn = new TableColumn<Area, String>(NAME_COLUMN);
		nameColumn.setMinWidth(80);
		nameColumn.setCellValueFactory(new PropertyValueFactory<Area, String>("name"));

		TableColumn<Area, Integer> layerColumn = new TableColumn<Area, Integer>(LAYER_COLUMN);
		layerColumn.setMinWidth(20);
		layerColumn.setCellValueFactory(new PropertyValueFactory<Area, Integer>("layer"));

		TableColumn<Area, Double> distanceColumn = new TableColumn<Area, Double>(DISTANCE_COLUMN);
		distanceColumn.setMinWidth(80);
		distanceColumn.setCellValueFactory(new PropertyValueFactory<Area, Double>("distanceToRobot"));

		table.setItems(tableData);
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

		Label label = new Label(AREAS_TITLE);
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
		button.setText(ADD_AREA_BUTTON);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Add Area button hitted...");
				Dialog<Pair<String, Double>> dialog = new AddFenceGUI();
				Optional<Pair<String, Double>> result = dialog.showAndWait();

				result.ifPresent(nameDistance -> {
					Area newArea = SickUtils.addNewArea(nameDistance.getKey(), nameDistance.getValue());
					tableData.add(newArea);
					RTLSConnectionHandler.getInstance().addArea(newArea);
				});
			}
		});
		return button;
	}

	private Button createEditAreaButton() {
		Button button = new Button();
		button.setText(EDIT_AREA_BUTTON);
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
		button.setText(REMOVE_AREA_BUTTON);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Remove Area button hitted...");
				Area selectedItem = table.getSelectionModel().getSelectedItem();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Delete " + selectedItem.getName() + " with distance to Robot "
						+ selectedItem.getDistanceToRobot());
				alert.setContentText("Do you realy want delete this area?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {
					SickUtils.removeArea(selectedItem);
					tableData.remove(selectedItem);
					RTLSConnectionHandler.getInstance().removeArea(selectedItem);
				}
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

	private void saveData() {
		try {
			AreaList areaList = SickDatabase.getInstance().getAreaList();
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
		} catch (IOException e) {
			log.error("Cannot store properties! IOException thrown: " + e);
		} catch (JAXBException e) {
			log.error("Cannot store areas! JAXBException thrown: " + e);
		}
	}

	private void loadAppIconSet() {
		appIconSet.put("red", new Image(new File("icons" + File.separator + "status_error.png").toURI().toString()));
		appIconSet.put("yellow",
				new Image(new File("icons" + File.separator + "status_pending.png").toURI().toString()));
		appIconSet.put("green", new Image(new File("icons" + File.separator + "status_ok.png").toURI().toString()));
		appIconSet.put("gray", new Image(new File("icons" + File.separator + "status_new.png").toURI().toString()));
	}

	protected static Image createFXImage(String path, String description) {
		return new Image(path);
	}
}
