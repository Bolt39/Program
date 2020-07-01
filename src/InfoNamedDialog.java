//Oskar Berggren, osbe8976

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class InfoNamedDialog extends Alert {

    public InfoNamedDialog(String name, Position position) {
        super(AlertType.INFORMATION);
        Label nameLabel = new Label("Name: " + name + " [ " + position + " ] " );
        getDialogPane().setContent(nameLabel);
        setHeaderText(null);
        setTitle("Info");
    }
}
