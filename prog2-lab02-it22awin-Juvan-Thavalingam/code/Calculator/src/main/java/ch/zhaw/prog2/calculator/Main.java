package ch.zhaw.prog2.calculator;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		mainWindow();
	}

	/*
	 * Create the window, call methods to create the different parts of the
	 * scene graph. Put the parts together in appropriate panes.
	 */
	private void mainWindow() {
		try {
			BorderPane rootPane = new BorderPane();
			// BorderPane top
	        MenuBar menuBar = new MenuBar();
			// Create scene with root node with size
			Scene scene = new Scene(rootPane, 600, 400);
			// scene.getStylesheets().add(getClass().getResource("MyLabel.css").toExternalForm());
			primaryStage.setMinWidth(280);
			// Set stage properties
			primaryStage.setTitle("Return on Investment Calculator");
			// Add scene to the stage and make it visible
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
		}
	}


}

