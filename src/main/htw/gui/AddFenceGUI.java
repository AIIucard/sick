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

public class AddFenceGUI extends Dialog<Pair<String, Double>> {

	private static final String DIALOG_TITLE = "Add new Fence...";
	private static final String TASK = "Enter the corresponding values:";
	private static final String ADD_BUTTON = "Add";
	private static final String FENCE_NAME_LABEL = "Fence Name:";
	private static final String FENCE_NAME_PROMPT = "Name";
	private static final String DISTANCE_LABEL = "Distance to Robot:";
	private static final String DISTANCE_PROMPT = "Distance";

	private ButtonType addButtonType;
	private TextField fenceName;
	private TextField distance;

	public AddFenceGUI() {
		this.setTitle(DIALOG_TITLE);
		this.setHeaderText(TASK);

		createButtons();
		createLayout();
	}

	private void createButtons() {
		addButtonType = new ButtonType(ADD_BUTTON, ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
	}

	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		fenceName = new TextField();
		fenceName.setPromptText(FENCE_NAME_PROMPT);

		distance = new TextField();
		distance.setPromptText(DISTANCE_PROMPT);

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
		grid.add(fenceName, 1, 0);
		grid.add(new Label(DISTANCE_LABEL), 0, 1);
		grid.add(distance, 1, 1);

		Node addButton = this.getDialogPane().lookupButton(addButtonType);
		addButton.setDisable(true);

		fenceName.textProperty().addListener((observable, oldValue, newValue) -> {
			addButton.setDisable(newValue.trim().isEmpty() || distance.textProperty().get().trim().isEmpty());
		});

		distance.textProperty().addListener((observable, oldValue, newValue) -> {
			addButton.setDisable(newValue.trim().isEmpty() || fenceName.textProperty().get().trim().isEmpty());
		});

		this.getDialogPane().setContent(grid);
		Platform.runLater(() -> fenceName.requestFocus());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return new Pair<>(fenceName.getText(), new Double(distance.getText()));
			}
			return null;
		});
	}
}
