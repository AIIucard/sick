package main.htw.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import main.htw.database.SickDatabase;

public class ConfigureRobotPositionGUI extends Dialog<Pair<Double, Double>> {

	private static final String DIALOG_TITLE = "Configure Robot Position...";
	private static final String TASK = "Enter the corresponding values:";
	private static final String CONFIGURE_BUTTON = "Configure";
	private static final String X_COORDINATE_LABEL = "X Coordinate:";
	private static final String X_COORDINATE_PROMPT = "X";
	private static final String Y_COORDINATE_LABEL = "Y Coordinate:";
	private static final String Y_COORDINATE_PROMPT = "Y";

	private ButtonType configureButtonType;
	private TextField xCoordinate;
	private TextField yCoordinate;

	public ConfigureRobotPositionGUI() {
		this.setTitle(DIALOG_TITLE);
		this.setHeaderText(TASK);

		createButtons();
		createLayout();
	}

	private void createButtons() {
		configureButtonType = new ButtonType(CONFIGURE_BUTTON, ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(configureButtonType, ButtonType.CANCEL);
	}

	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		xCoordinate = new TextField();
		xCoordinate.setPromptText(X_COORDINATE_PROMPT);
		xCoordinate.setText("" + SickDatabase.getInstance().getRobotPositionX());

		// Force the field to be numeric only
		xCoordinate.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,2}([\\.]\\d{0,2})?")) {
					xCoordinate.setText(oldValue);
				}
			}
		});

		yCoordinate = new TextField();
		yCoordinate.setPromptText(Y_COORDINATE_PROMPT);
		yCoordinate.setText("" + SickDatabase.getInstance().getRobotPositionY());

		// Force the field to be numeric only
		yCoordinate.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,2}([\\.]\\d{0,2})?")) {
					yCoordinate.setText(oldValue);
				}
			}
		});

		grid.add(new Label(X_COORDINATE_LABEL), 0, 0);
		grid.add(xCoordinate, 1, 0);
		grid.add(new Label(Y_COORDINATE_LABEL), 0, 1);
		grid.add(yCoordinate, 1, 1);

		Node configureButton = this.getDialogPane().lookupButton(configureButtonType);
		configureButton.setDisable(true);

		xCoordinate.textProperty().addListener((observable, oldValue, newValue) -> {
			configureButton.setDisable(newValue.trim().isEmpty() || yCoordinate.textProperty().get().trim().isEmpty());
		});

		yCoordinate.textProperty().addListener((observable, oldValue, newValue) -> {
			configureButton.setDisable(newValue.trim().isEmpty() || xCoordinate.textProperty().get().trim().isEmpty());
		});

		this.getDialogPane().setContent(grid);
		Platform.runLater(() -> xCoordinate.requestFocus());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == configureButtonType) {
				return new Pair<>(new Double(xCoordinate.getText()), new Double(yCoordinate.getText()));
			}
			return null;
		});
	}
}