//Oskar Berggren, osbe8976

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class InfoDescDialog extends Alert {

    public InfoDescDialog(String name, Position position, String description) {
        super(AlertType.INFORMATION);
        GridPane grid = new GridPane();
        Label nameLabel = new Label("Name: " + name + " [ " + position + " ] " );
        Label describeLabel = new Label("Description: " + description);
        grid.addRow(0, nameLabel);
        grid.addRow(1, describeLabel);
        getDialogPane().setContent(grid);
        setHeaderText(null);
        setTitle("Info");
    }
}