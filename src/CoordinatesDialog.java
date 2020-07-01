//Oskar Berggren, osbe8976

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CoordinatesDialog extends Alert {
    private TextField xfield = new TextField();
    private TextField yfield = new TextField();

    public CoordinatesDialog(){
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("x: "), xfield);
        grid.addRow(1, new Label("y: "), yfield);
        getDialogPane().setContent(grid);
        setHeaderText(null);
        setTitle("Input Coordinates: ");
    }

    public int getXfield() {
        return Integer.parseInt(xfield.getText());
    }

    public int getYfield() {
        return Integer.parseInt(yfield.getText());
    }
}
