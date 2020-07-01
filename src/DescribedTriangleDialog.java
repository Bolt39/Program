//Oskar Berggren, osbe8976

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DescribedTriangleDialog extends Alert {
    private TextField nameField = new TextField();
    private TextField describedField = new TextField();

    public DescribedTriangleDialog() {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name: "), nameField);
        grid.addRow(1, new Label("Description. "), describedField);
        getDialogPane().setContent(grid);
        setHeaderText(null);
        setTitle("New Described Triangle");
    }

    public String getNameField() {
        return nameField.getText();
    }

    public String getDescribedField() {
        return describedField.getText();
    }
}
