package ch.zhaw.prog2.mandelbrot;

import ch.zhaw.prog2.mandelbrot.processors.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This  application uses several threads to compute an image "in the background".
 *
 * As rows of pixels in the image are computed, they are copied to the screen.
 * (The image is a small piece of the famous Mandelbrot set, which
 * is used just because it takes some time to compute.  There is no need
 * to understand what the image means.)  The user starts the computation by
 * clicking a "Start" button.  A pop-up menu allows the user to select the
 * number of threads to be used.  The specified number of threads is created
 * and each thread is assigned a region in the image.  The threads are run
 * at lower priority, which will make sure that the GUI thread will get a
 * chance to run to repaint the display as necessary.
 */
public class MandelbrotGui extends Application implements MandelbrotProcessorListener {
    // possible states of the GUI
    private enum GuiState {START, RUNNING, STOPPING, STOPPED}

    private GuiState state = GuiState.START;

    private MandelbrotProcessor processor;

    private Button startButton; // button the user can click to start or abort the thread
    private ComboBox<String> threadCountSelect;  // for specifying the number of threads to be used
    private ComboBox<String> processorSelect;    // for specifying the processor

    private Canvas canvas;      // the canvas where the image is displayed
    private GraphicsContext g;  // the graphics context for drawing on the canvas

    private int width, height;  // the size of the canvas


    /**
     * Set up the GUI and event handling.  The canvas will be 1200-by-1000 pixels,
     * if that fits comfortably on the screen; otherwise, size will be reduced to fit.
     */
    public void start(Stage stage) {
        int screenWidth = (int) Screen.getPrimary().getVisualBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getVisualBounds().getHeight();
        width = Math.min(1200, screenWidth - 50);
        height = Math.min(1000, screenHeight - 120);

        canvas = new Canvas(width, height);
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGRAY);
        g.fillRect(0, 0, width, height);
        startButton = new Button("Start!");
        startButton.setOnAction(e -> startOrStopProcessing());
        // Because the tasks are CPU intensive, the default number of threads should be equal to the number of cores
        int numCores = Runtime.getRuntime().availableProcessors();
        int maxThreads = 2 * numCores;
        threadCountSelect = new ComboBox<>();
        threadCountSelect.setEditable(false);
        for (int i = 1; i <= maxThreads; i++) {
            threadCountSelect.getItems().add("Use " + i + " threads.");
        }
        // use the number of cores as default value
        threadCountSelect.getSelectionModel().select(numCores - 1);

        processorSelect = new ComboBox<>();
        processorSelect.setEditable(false);
        processorSelect.getItems().add("Use Thread-Processor");
        processorSelect.getItems().add("Use Executor-Processor");
        processorSelect.getItems().add("Use Callable-Processor");
        processorSelect.getSelectionModel().select(0);

        HBox bottom = new HBox(8, startButton, threadCountSelect, processorSelect);
        bottom.setStyle("-fx-padding: 6px; -fx-border-color:black; -fx-border-width: 2px 0 0 0");
        bottom.setAlignment(Pos.CENTER);
        BorderPane root = new BorderPane(canvas);
        root.setBottom(bottom);
        root.setStyle("-fx-border-color:black; -fx-border-width: 2px");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();
    }


    /**
     * This method is called when the user clicks the start button.
     * If no computation is currently running, it starts processing with
     * as many new threads as the user has specified.
     * If a computation is in progress when this method is called,
     * the processing is stopped and threads should be terminated.
     */
    private void startOrStopProcessing() {
        if (state == GuiState.RUNNING) {
            setState(GuiState.STOPPING);
            if (processor != null) {
                processor.stopProcessing();
            }
        } else {
            setState(GuiState.RUNNING);
            int threadCount = threadCountSelect.getSelectionModel().getSelectedIndex() + 1;
            processor = getProcessor();
            System.out.println("Start processing using " + processor.getClass().getSimpleName() +
                " with " + threadCount + " threads.");
            // run processing in its own thread
            new Thread(() -> {
                processor.startProcessing(threadCount);

            }).start();
        }
    }

    /**
     * Create an instance of the selected MandelbrotProcessor implementation.
     *
     * @return new Instance of the selected MandelbrotProcessor implementation
     */
    private MandelbrotProcessor getProcessor() {
        MandelbrotProcessor processor;
        int processorIndex = processorSelect.getSelectionModel().getSelectedIndex();
        switch (processorIndex) {
            case 0: processor = new MandelbrotTaskProcessor(this, width, height); break;
            case 1: processor = new MandelbrotExecutorProcessor(this, width, height); break;
            case 2: processor = new MandelbrotCallableProcessor(this, width, height); break;
            default: processor = new MandelbrotTaskProcessor(this, width, height); break;
        }
        return processor;
    }

    /**
     * Update GUI elements for a specific state
     *
     * @param newState the new state the GUI should be configured for
     */
    private void setState(GuiState newState) {
        this.state = newState;
        switch (newState) {
            case RUNNING:
                Platform.runLater(() -> {
                    startButton.setText("Abort"); // change name while computation is in progress
                    threadCountSelect.setDisable(true); // will be re-enabled when all threads finish
                    processorSelect.setDisable(true);   // will be re-enabled when all threads finish
                    g.setFill(Color.LIGHTGRAY);  // fill canvas with gray
                    g.fillRect(0, 0, width, height);
                });
                break;
            case STOPPING:
                Platform.runLater(() -> {
                    // prevent user from trying to stop threads that are already stopping
                    startButton.setDisable(true); // will be re-enabled when all threads have stopped
                });
                break;
            case STOPPED:
                Platform.runLater(() -> {
                    // Make sure state is correct when threads end.
                    startButton.setText("Start Again");
                    startButton.setDisable(false);
                    threadCountSelect.setDisable(false);
                    processorSelect.setDisable(false);
                });
                break;
            default:
                break;
        }
    }

    /**
     * This method is called from the Processor when the processing is stopped or completed.
     * GUI state needs to be reset.
     *
     * @param duration Duration of the processing in Milliseconds
     */
    @Override
    public void processingStopped(long duration) {
        this.setState(GuiState.STOPPED);
        System.out.println("Finished processing after " + duration + "ms");
    }

    /**
     * This method is called from the Processor when a row has been processed
     * and needs to be added to the image.
     *
     * @param row the row of pixels whose colors are to be set
     */
    @Override
    public void rowProcessed(ImageRow row) {
        if (row != null) {
            Platform.runLater(() -> {
                for (int x = 0; x < row.pixels.length; x++) {
                    // Color an individual pixel by filling in a 1-by-1 pixel rectangle.
                    g.setFill(row.pixels[x]);
                    g.fillRect(x, row.rowNumber, 1, 1);
                }
            });
        }
    }

} // end MandelbrotGui
