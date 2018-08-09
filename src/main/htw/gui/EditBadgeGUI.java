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

/**
 * EditBadgeGUI is used as a configuration dialog for determining the role of a
 * badge. It contains the following actions:
 * <ul>
 * <li>Creating the dialog layout
 * <li>Creating the dialog buttons
 * </ul>
 * The dialog label an task description can be changed inside these class.
 */
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
	private TableView<Badge> badgeTableView;

	/**
	 * Creates the dialog with the layout and buttons. Saves the badge table view
	 * for getting the selected item from the badge table.
	 * 
	 * @param badgeTableView
	 *            the are table view for getting the selected item from the badge
	 *            table
	 */
	public EditBadgeGUI(TableView<Badge> badgeTableView) {
		this.setTitle(DIALOG_TITLE);
		this.setHeaderText(TASK);
		this.badgeTableView = badgeTableView;

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
	 * <li>a label for the badge address
	 * <li>a label for the badge name
	 * <li>a combo box for selecting the role
	 * <li>the corresponding labels
	 * </ul>
	 * The OK Button is disabled until the text fields contain valid values.
	 */
	private void createLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ObservableList<Badge> selectedItems = badgeTableView.getSelectionModel().getSelectedItems();

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
