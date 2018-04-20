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
	private ButtonType addButtonType;
	private TextField fenceName;
	private TextField distance;

	public AddFenceGUI() {
		this.setTitle("Add new Fence...");
		this.setHeaderText("Enter the corresponding values:");

		createButtons();
		createLayout();

	}

	private void createButtons() {
		addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
	}

	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		fenceName = new TextField();
		fenceName.setPromptText("Name");

		distance = new TextField();
		distance.setPromptText("Distance");

		// Force the field to be numeric only
		distance.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d{0,2}([\\.]\\d{0,2})?")) {
					distance.setText(oldValue);
				}
			}
		});

		grid.add(new Label("Fence Name:"), 0, 0);
		grid.add(fenceName, 1, 0);
		grid.add(new Label("Distance to Robot:"), 0, 1);
		grid.add(distance, 1, 1);

		// Enable/Disable login button depending on whether a name was entered.
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
