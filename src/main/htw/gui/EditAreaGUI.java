package main.htw.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import main.htw.manager.AreaManager;
import main.htw.xml.Area;

/**
 * EditAreaGUI is used as a configuration dialog for determining the distance of
 * an area to the robot and changing the area name. It contains the following
 * actions:
 * <ul>
 * <li>Creating the dialog layout
 * <li>Creating the dialog buttons
 * </ul>
 * The dialog label an task description can be changed inside these class.
 */
public class EditAreaGUI extends Dialog<Pair<String, Double>> {

	private static final String DIALOG_TITLE = "Edit Fence...";
	private static final String TASK = "Enter the corresponding values:";
	private static final String EDIT_BUTTON = "Edit";
	private static final String FENCE_NAME_LABEL = "Area Name:";
	private static final String FENCE_NAME_PROMPT = "Name";
	private static final String DISTANCE_LABEL = "Distance to Robot:";
	private static final String DISTANCE_PROMPT = "Distance";

	private ButtonType editButtonType;
	private TextField areaName;
	private TextField distance;
	private TableView<Area> areaTableView;

	/**
	 * Creates the dialog with the layout and buttons. Saves the area table view for
	 * getting the selected item from the area table.
	 * 
	 * @param areaTableView
	 *            the are table view for getting the selected item from the area
	 *            table
	 */
	public EditAreaGUI(TableView<Area> areaTableView) {
		this.setTitle(DIALOG_TITLE);
		this.setHeaderText(TASK);
		this.areaTableView = areaTableView;

		createButtons();
		createLayout();
	}

	/**
	 * Creates the dialog OK and CANCEL Buttons.
	 */
	private void createButtons() {
		editButtonType = new ButtonType(EDIT_BUTTON, ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);
	}

	/**
	 * Creates the dialogs layout which contains:
	 * <ul>
	 * <li>a text field for entering the area name
	 * <li>a text field for entering the area distance
	 * <li>the corresponding labels
	 * <li>the check if the configured area is duplicated
	 * </ul>
	 * The OK Button is disabled until the text fields contain valid values.
	 */
	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ObservableList<Area> selectedItems = areaTableView.getSelectionModel().getSelectedItems();

		areaName = new TextField();
		areaName.setPromptText(FENCE_NAME_PROMPT);
		areaName.setText("" + selectedItems.get(0).getName());

		distance = new TextField();
		distance.setPromptText(DISTANCE_PROMPT);
		distance.setText("" + selectedItems.get(0).getDistanceToRobot());

		// Force the field to be numeric only
		distance.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,2}([\\.]\\d{0,2})?")) {
					distance.setText(oldValue);
				}
			}
		});

		grid.add(new Label(FENCE_NAME_LABEL), 0, 0);
		grid.add(areaName, 1, 0);
		grid.add(new Label(DISTANCE_LABEL), 0, 1);
		grid.add(distance, 1, 1);

		Node editButton = this.getDialogPane().lookupButton(editButtonType);
		editButton.setDisable(true);

		areaName.textProperty().addListener((observable, oldValue, newValue) -> {
			editButton.setDisable(newValue.trim().isEmpty() || distance.textProperty().get().trim().isEmpty());
		});

		distance.textProperty().addListener((observable, oldValue, newValue) -> {
			editButton.setDisable(newValue.trim().isEmpty() || areaName.textProperty().get().trim().isEmpty());
		});

		this.getDialogPane().setContent(grid);
		Platform.runLater(() -> areaName.requestFocus());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == editButtonType) {
				if (AreaManager.isDublicatedArea(selectedItems.get(0).getName(),
						selectedItems.get(0).getDistanceToRobot(), areaName.getText(),
						new Double(distance.getText()))) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error!");
					alert.setHeaderText("Dublicate Name or Distance to Robot!");
					alert.setContentText("The area name " + areaName.getText() + " or the robot distance "
							+ distance.getText() + " already exist! The area will not be updated!");
					alert.showAndWait();
				} else {
					return new Pair<>(areaName.getText(), new Double(distance.getText()));
				}
			}
			return null;
		});
	}
}
