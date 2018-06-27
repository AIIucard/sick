package main.htw.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import main.htw.datamodell.RoleType;
import main.htw.xml.Badge;

public class EditBadgeGUI extends Dialog<String> {

	private static final String DIALOG_TITLE = "Edit Badge...";
	private static final String TASK = "Enter the corresponding values:";
	private static final String EDIT_BUTTON = "Edit";
	private static final String BADGE_ADDRESS_LABEL = "Badge Address:";
	private static final String BADGE_NAME_LABEL = "Badge Name:";
	private static final String BADGE_ROLE_LABEL = "Badge Role:";
	private static final String BADGE_ROLE_PROMPT = "Role";

	private ButtonType editButtonType;
	private Label badgeAdress;
	private Label badgeName;
	private ComboBox<String> badgeRole;
	private TableView<Badge> table;

	public EditBadgeGUI(TableView<Badge> table) {
		this.setTitle(DIALOG_TITLE);
		this.setHeaderText(TASK);
		this.table = table;

		createButtons();
		createLayout();
	}

	private void createButtons() {
		editButtonType = new ButtonType(EDIT_BUTTON, ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);
	}

	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ObservableList<Badge> selectedItems = table.getSelectionModel().getSelectedItems();

		badgeAdress = new Label();
		badgeAdress.setText("" + selectedItems.get(0).getAddress());

		badgeName = new Label();
		badgeName.setText("" + selectedItems.get(0).getName());

		badgeRole = new ComboBox<>();
		badgeRole.getItems().addAll(RoleType.VISITOR.toString(), RoleType.LABORANT.toString(),
				RoleType.PROFESSOR.toString());
		badgeRole.setPromptText(BADGE_ROLE_PROMPT);
		badgeRole.setValue("" + selectedItems.get(0).getRole());

		grid.add(new Label(BADGE_ADDRESS_LABEL), 0, 0);
		grid.add(badgeAdress, 1, 0);
		grid.add(new Label(BADGE_NAME_LABEL), 0, 1);
		grid.add(badgeName, 1, 1);
		grid.add(new Label(BADGE_ROLE_LABEL), 0, 2);
		grid.add(badgeRole, 1, 2);

		Node editButton = this.getDialogPane().lookupButton(editButtonType);
		editButton.setDisable(true);

		badgeRole.valueProperty().addListener((observable, oldValue, newValue) -> {
			editButton.setDisable(newValue.equals(oldValue));
		});

		this.getDialogPane().setContent(grid);
		Platform.runLater(() -> badgeRole.requestFocus());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == editButtonType) {
				return badgeRole.getValue();
			}
			return null;
		});
	}
}
