package ch.zhaw.prog2.fxmlcalculator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Controller for the MainWindow. One controller per mask (or FXML file)
 * Contains everything the controller has to reach in the view (controls)
 * and all methods the view calls based on events.
 * @author Juvan Thavalingam
 * @version 2023
 */
public class MainWindowController {

    @FXML
    TextField rateId;

    @FXML
    TextField costId;

    @FXML
    TextField yearId;

    @FXML
    TextField amountId;

    @FXML
    TextArea resultId;

    private ValueHandler valueHandler = new ValueHandler();

    private ResultWindowController resultWindowCon;

    private static final String INFO = """
        Enter valid values to
        - Initial amount (> 0)
        - Return in % (can be +/- or 0)
        - Annual Costs (> 0)
        - Number of years (> 0)
        Calculate displays the annual balance development!";
        """;

    @FXML
    void calculate(ActionEvent actionEvent) {
        valueHandler.validateAndSetValues(amountId.getText(), rateId.getText(), costId.getText(), yearId.getText());
        String result = valueHandler.getResult();

        resultId.setText(result);
        if(resultWindowCon != null){
            resultWindowCon.results.setText(result);
        }

        validate();
    }

    @FXML
    void setClose(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    void clearValues(ActionEvent actionEvent) {
        amountId.clear();
        rateId.clear();
        costId.clear();
        yearId.clear();
    }

    @FXML
    void clearResults(ActionEvent actionEvent) {
        resultId.clear();
        colorBorder(resultId, Color.BLACK);
    }

    @FXML
    void showHelp(ActionEvent actionEvent) {
        showHelpText();
    }

    @FXML
    void openResultWindow(ActionEvent actionEvent) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultWindow.fxml"));
            Pane rootPane = loader.load();
            resultWindowCon = loader.getController();
            Scene scene = new Scene(rootPane);

            Stage stageOfNewWindow = new Stage();
            stageOfNewWindow.setScene(scene);
            stageOfNewWindow.setTitle("Result Window");
            stageOfNewWindow.show();
        } catch (Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @FXML
    void handleClearInitialAmount(ActionEvent actionEvent) {
        amountId.clear();
    }

    @FXML
    void handleClearReturn(ActionEvent actionEvent) {
        rateId.clear();
    }

    @FXML
    void handleClearAnnualCosts(ActionEvent actionEvent) {
        costId.clear();
    }

    @FXML
    void handleClearNumberOfYears(ActionEvent actionEvent) {
        yearId.clear();
    }

    @FXML
    void handleKeyOnPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.F1)){
            showHelpText();
        }
    }

    private void showHelpText(){
        resultId.setText(INFO);
        colorBorder(resultId, Color.BLUE);
    }

    private void colorBorder(TextArea area, Color color){
        area.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
    }

    private void validate(){
        if(amountId.getText().isEmpty() || rateId.getText().isEmpty() || costId.getText().isEmpty() || yearId.getText().isEmpty()){
            colorBorder(resultId, Color.RED);
        } else {
            colorBorder(resultId, Color.GREEN);
        }
    }
}
