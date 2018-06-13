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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import main.htw.gui.ConfigureRobotPositionGUI;
import main.htw.gui.EditAreaGUI;
import main.htw.gui.EditBadgeGUI;
import main.htw.gui.EmulatorGUI;
import main.htw.handler.RTLSHandler;
import main.htw.manager.AreaManager;
import main.htw.manager.BadgeManager;
import main.htw.properties.CFGPropertyManager;
import main.htw.properties.PropertiesKeys;
import main.htw.utils.ConnectionStatusType;
import main.htw.xml.Area;
import main.htw.xml.AreaList;
import main.htw.xml.Badge;
import main.htw.xml.BadgeList;
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
	private static final String EDIT_AREA_BUTTON = "Edit";

	private static final String BADGES_TITLE = "Badges";
	private static final String ADRESS_COLUMN = "ID";
	private static final String BADGE_NAME_COLUMN = "Name";
	private static final String ROLE_COLUMN = "Role";
	private static final String EDIT_BADGE_BUTTON = "Edit";

	private static CFGPropertyManager propManager = null;
	private static XMLMarshler xmlMarshaller = null;
	private static ApplicationManager appManager = null;

	private Button startButton;
	private Button stopButton;
	private Button editAreaButton;
	private Button editBadgeButton;
	private CheckBox visitorModeCheckBox;
	private boolean isEditAreaDisabled = false;
	private boolean isEditBadgeDisabled = false;

	private static double width = 200;
	private static double height = 200;

	private static MenuBar sickMenuBar = null;
	private static TableView<Area> areaTable = null;
	private static ObservableList<Area> areaTableData = null;
	private static FlowPane areaButtonPane = null;

	private static TableView<Badge> badgeTable = null;
	private static ObservableList<Badge> badgeTableData = null;
	private static FlowPane badgeButtonPane = null;

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

	private void createPrimaryStage(Stage primaryStage) {

		primaryStage.setTitle(APP_TITLE);
		BorderPane borderPane = new BorderPane();
		VBox centerVBox = new VBox();

		sickMenuBar = createMenuBar();

		HBox startStopButtons = createStartStopButtons(this);
		GridPane statusArea = createConnectionStatusArea();
		VBox areaTable = createTables();

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
						AreaManager.updateAreaShapes();
					}
				});
			}
		});

		menuConfigure.getItems().addAll(robotPosition);

		menuBar.getMenus().addAll(menuFile, menuConfigure);

		return menuBar;
	}

	private HBox createStartStopButtons(SickApplication app) {

		startButton = new Button();
		startButton.setText(START_BUTTON);
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.info("Started Application...");

				appManager = ApplicationManager.getInstance();
				appManager.startApplication(app);
				startButton.setDisable(true);
				stopButton.setDisable(false);
				visitorModeCheckBox.setDisable(true);
				editAreaButton.setDisable(true);
				editBadgeButton.setDisable(true);
				isEditAreaDisabled = true;
				isEditBadgeDisabled = true;

				if (visitorModeCheckBox.isSelected()) {
					database.setGodMode(true);
					log.info("GodeMode on");
				} else {
					database.setGodMode(false);
					log.info("GodeMode off");
				}
			}
		});

		visitorModeCheckBox = new CheckBox();
		visitorModeCheckBox.setText("Visitor Mode");
		visitorModeCheckBox.setSelected(false);

		stopButton = new Button();
		stopButton.setText(STOP_BUTTON);
		stopButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				appManager = ApplicationManager.getInstance();
				appManager.stopApplication();
				startButton.setDisable(false);
				visitorModeCheckBox.setDisable(false);
				stopButton.setDisable(true);
				isEditAreaDisabled = false;
				isEditBadgeDisabled = false;
				resetConnectionStatus();
				log.info("Stopped Application...");
			}
		});

		startButton.setDisable(false);
		stopButton.setDisable(true);
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setSpacing(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(startButton, stopButton, visitorModeCheckBox);

		return hBox;
	}

	private void resetConnectionStatus() {
		database.setRTLSConnectionStatus(ConnectionStatusType.NEW);
		database.setRobotConnectionStatus(ConnectionStatusType.NEW);
		database.setLightConnectionStatus(ConnectionStatusType.NEW);
	}

	private VBox createTables() {

		areaTableData = FXCollections.observableArrayList(SickDatabase.getInstance().getAreaList().getAreas());
		areaTable = createAreaTable(areaTableData);
		areaButtonPane = createAreaButtonPane(areaTable);

		badgeTableData = FXCollections.observableArrayList(SickDatabase.getInstance().getBadgeList().getBadges());
		badgeTable = createBadgeTable(badgeTableData);
		badgeButtonPane = createBadgeButtonPane(badgeTable);

		final VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.setPadding(new Insets(10, 0, 0, 10));
		vbox.getChildren().addAll(areaButtonPane, areaTable, badgeButtonPane, badgeTable);

		return vbox;
	}

	@SuppressWarnings("unchecked")
	private TableView<Area> createAreaTable(ObservableList<Area> tableData) {
		TableView<Area> tableView = new TableView<Area>();

		tableView.setEditable(true);
		tableView.setMaxWidth(width - 20);

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

		tableView.setItems(tableData);
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			ObservableList<Area> selectedItems = tableView.getSelectionModel().getSelectedItems();
			handleAreaTableSelection(selectedItems);
		});

		tableView.setRowFactory(new Callback<TableView<Area>, TableRow<Area>>() {
			@Override
			public TableRow<Area> call(TableView<Area> tableView2) {
				final TableRow<Area> row = new TableRow<>();
				row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						final int index = row.getIndex();
						if (index >= 0 && index < tableView.getItems().size()
								&& tableView.getSelectionModel().isSelected(index)) {
							tableView.getSelectionModel().clearSelection();
							event.consume();
							ObservableList<Area> selectedItems = tableView.getSelectionModel().getSelectedItems();
							handleAreaTableSelection(selectedItems);
						}
					}
				});
				return row;
			}
		});

		tableView.getColumns().addAll(idColumn, nameColumn, layerColumn, distanceColumn);
		return tableView;
	}

	@SuppressWarnings("unchecked")
	private TableView<Badge> createBadgeTable(ObservableList<Badge> tableData) {
		TableView<Badge> tableView = new TableView<Badge>();

		tableView.setEditable(true);
		tableView.setMaxWidth(width - 20);

		TableColumn<Badge, String> addressColumn = new TableColumn<Badge, String>(ADRESS_COLUMN);
		addressColumn.setMinWidth(20);
		addressColumn.setCellValueFactory(new PropertyValueFactory<Badge, String>("address"));

		TableColumn<Badge, String> nameColumn = new TableColumn<Badge, String>(BADGE_NAME_COLUMN);
		nameColumn.setMinWidth(80);
		nameColumn.setCellValueFactory(new PropertyValueFactory<Badge, String>("name"));
		nameColumn.setSortType(TableColumn.SortType.ASCENDING);

		TableColumn<Badge, String> roleColumn = new TableColumn<Badge, String>(ROLE_COLUMN);
		roleColumn.setMinWidth(20);
		roleColumn.setCellValueFactory(new PropertyValueFactory<Badge, String>("role"));

		tableView.setItems(tableData);
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			ObservableList<Badge> selectedItems = tableView.getSelectionModel().getSelectedItems();
			handleBadgeTableSelection(selectedItems);
		});

		tableView.setRowFactory(new Callback<TableView<Badge>, TableRow<Badge>>() {
			@Override
			public TableRow<Badge> call(TableView<Badge> tableView2) {
				final TableRow<Badge> row = new TableRow<>();
				row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						final int index = row.getIndex();
						if (index >= 0 && index < tableView.getItems().size()
								&& tableView.getSelectionModel().isSelected(index)) {
							tableView.getSelectionModel().clearSelection();
							event.consume();
							ObservableList<Badge> selectedItems = tableView.getSelectionModel().getSelectedItems();
							handleBadgeTableSelection(selectedItems);
						}
					}
				});
				return row;
			}
		});

		tableView.getColumns().addAll(addressColumn, nameColumn, roleColumn);
		tableView.getSortOrder().add(nameColumn);
		return tableView;
	}

	private FlowPane createAreaButtonPane(TableView<Area> table) {
		FlowPane areaButtonPane = new FlowPane();
		areaButtonPane.setPadding(new Insets(5, 0, 5, 0));
		areaButtonPane.setVgap(4);
		areaButtonPane.setHgap(4);
		areaButtonPane.setPrefWrapLength(width - 20);
		areaButtonPane.setAlignment(Pos.BOTTOM_LEFT);

		Label label = new Label(AREAS_TITLE);
		label.setFont(new Font("Source Sans Pro", 20));

		editAreaButton = createEditAreaButton(table);
		editAreaButton.setDisable(true);

		areaButtonPane.getChildren().addAll(label, editAreaButton);

		return areaButtonPane;
	}

	private FlowPane createBadgeButtonPane(TableView<Badge> table) {
		FlowPane areaButtonPane = new FlowPane();
		areaButtonPane.setPadding(new Insets(5, 0, 5, 0));
		areaButtonPane.setVgap(4);
		areaButtonPane.setHgap(4);
		areaButtonPane.setPrefWrapLength(width - 20);
		areaButtonPane.setAlignment(Pos.BOTTOM_LEFT);

		Label label = new Label(BADGES_TITLE);
		label.setFont(new Font("Source Sans Pro", 20));

		editBadgeButton = createEditBadgeButton(table);
		editBadgeButton.setDisable(true);

		areaButtonPane.getChildren().addAll(label, editBadgeButton);

		return areaButtonPane;
	}

	private void handleAreaTableSelection(ObservableList<Area> selectedItems) {
		if (selectedItems.size() > 1) {
			editAreaButton.setDisable(true);
		} else if (selectedItems.size() == 1 && !isEditAreaDisabled) {
			editAreaButton.setDisable(false);
		} else {
			editAreaButton.setDisable(true);
		}
	}

	private void handleBadgeTableSelection(ObservableList<Badge> selectedItems) {
		if (selectedItems.size() > 1) {
			editBadgeButton.setDisable(true);
		} else if (selectedItems.size() == 1 && !isEditBadgeDisabled) {
			editBadgeButton.setDisable(false);
		} else {
			editBadgeButton.setDisable(true);
		}
	}

	private Button createEditAreaButton(TableView<Area> table) {
		Button button = new Button();
		button.setText(EDIT_AREA_BUTTON);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Edit Area button hitted...");
				Dialog<Pair<String, Double>> dialog = new EditAreaGUI(table);
				Optional<Pair<String, Double>> result = dialog.showAndWait();

				result.ifPresent(nameDistance -> {
					ObservableList<Area> selectedItems = table.getSelectionModel().getSelectedItems();
					Area selectedArea = selectedItems.get(0);

					Area editArea = AreaManager.editArea(selectedArea, nameDistance.getKey(), nameDistance.getValue());
					int pos = 0;
					for (int i = 0; i < areaTableData.size(); ++i) {
						Area currentArea = areaTableData.get(i);
						if (currentArea.getName().equals(selectedArea.getName())
								&& currentArea.getDistanceToRobot().equals(selectedArea.getDistanceToRobot())) {
							currentArea.setName(nameDistance.getKey());
							currentArea.setDistanceToRobot(nameDistance.getValue());
							pos = i;
							break;
						}
					}
					areaTableData.set(pos, editArea);
					RTLSHandler.getInstance().editArea(editArea);
				});
			}
		});
		return button;
	}

	private Button createEditBadgeButton(TableView<Badge> table) {
		Button button = new Button();
		button.setText(EDIT_BADGE_BUTTON);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				log.debug("Edit Badge button hitted...");
				Dialog<String> dialog = new EditBadgeGUI(table);
				Optional<String> result = dialog.showAndWait();

				result.ifPresent(role -> {
					ObservableList<Badge> selectedItems = table.getSelectionModel().getSelectedItems();
					Badge selectedBadge = selectedItems.get(0);

					Badge editBadge = BadgeManager.editBadge(selectedBadge, role);
					int pos = 0;
					for (int i = 0; i < badgeTableData.size(); ++i) {
						Badge currentBadge = badgeTableData.get(i);
						if (currentBadge.getAddress().equals(selectedBadge.getAddress())) {
							currentBadge.setRole(role);
							pos = i;
							break;
						}
					}
					badgeTableData.set(pos, editBadge);
				});
			}
		});
		return button;
	}

	private void addPropertyListener(Stage primaryStage) {
		primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
			if (propManager != null) {
				propManager.setProperty(PropertiesKeys.APP_WIDTH, "" + newVal);
				areaTable.setMaxWidth(newVal.doubleValue() - 20);
				areaButtonPane.setMaxWidth(newVal.doubleValue() - 20);
				areaTable.refresh();
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
			BadgeList badgeList = SickDatabase.getInstance().getBadgeList();
			if (propManager != null) {
				propManager.storeProperties();
			} else {
				log.error("Cannot store properties!");
			}
			if (xmlMarshaller != null && areaList != null && badgeList != null) {
				xmlMarshaller.marshalAreaList(areaList);
				xmlMarshaller.marshalBadgeList(badgeList);
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

	public Button getStartButton() {
		return startButton;
	}

	public void setStartButton(Button startButton) {
		this.startButton = startButton;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public void setStopButton(Button stopButton) {
		this.stopButton = stopButton;
	}

	public static <T> void updateBadgeTable(String adress, Badge badgeToChange) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < badgeTableData.size(); ++i) {
					if (badgeTableData.get(i).getAddress().equals(adress)) {
						System.out.println(badgeTableData.get(i).getName());
						badgeTableData.set(i, badgeToChange);
					}
				}
			}
		});

	}
}
