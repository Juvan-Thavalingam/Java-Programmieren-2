package ch.zhaw.prog2.fxmlcalculator;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller for the MainWindow. One controller per mask (or FXML file)
 * Contains everything the controller has to reach in the view (controls)
 * and all methods the view calls based on events.
 * @author
 * @version 2023
 */
public class ResultWindowController {
    // add datafields

	//@FXML
    @FXML
    public TextArea results;

	//@FXML
    @FXML
    private void closeWindow() {
		Stage stage = (Stage) results.getScene().getWindow();
		stage.close();
	}
}
