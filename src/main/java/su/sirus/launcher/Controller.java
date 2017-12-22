package su.sirus.launcher;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {

    public Button startBtn;
    public Label statusLabel;

    public void actionStart(ActionEvent actionEvent)
    {
        statusLabel.setText("Prepare for download...");
    }
}
