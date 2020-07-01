//Oskar Berggren, osbe8976

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class NamedTriangleDialog extends Alert {
    private TextField nameField = new TextField();

    public NamedTriangleDialog() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name: "), nameField);
        getDialogPane().setContent(grid);
        setHeaderText(null);
        setTitle("New named triangle");
    }

    public String getNameField() {
        return nameField.getText();
    }
}
