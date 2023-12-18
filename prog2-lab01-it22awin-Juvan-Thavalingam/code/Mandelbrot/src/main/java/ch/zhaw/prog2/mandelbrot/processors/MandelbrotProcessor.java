package ch.zhaw.prog2.mandelbrot.processors;

import ch.zhaw.prog2.mandelbrot.MandelbrotProcessorListener;
import javafx.scene.paint.Color;

public abstract class MandelbrotProcessor {
    protected final MandelbrotProcessorListener processorListener;
    protected javafx.scene.paint.Color[] palette;   // the color palette, containing the colors of the spectrum
    protected int width;                // width of the canvas
    protected int height;               //height of the canvas
    protected long startTime;           // used to calculate the runtime for the calculation
    protected int tasksRemaining;       // How many tasks/threads are still running resp. need to be processed

    /**
     * Initialize the Mandelbrot processor.
     * This method also initializes the color palette, containing colors in spectral order.
     * @param processorListener class to notify about processing results
     * @param width with of the canvas in pixel
     * @param height height of the canvas in pixel
     */
    public MandelbrotProcessor(MandelbrotProcessorListener processorListener, int width, int height) {
        this.processorListener = processorListener;
        this.width = width;
        this.height = height;
        // initialize the color palette
        this.palette = new Color[256];
        for (int i = 0; i < 256; i++) {
            this.palette[i] = Color.hsb(360 * (i / 256.0), 1, 1);
        }
    }

    /**
     * This method starts as many new threads as the user has specified,
     * and assigns a different part of the image to each thread.
     *
     * @param numThreads number of thread to start to run the tasks
     */
    public abstract void startProcessing(int numThreads);

    /**
     * Stopp processing tasks and terminate all threads.
     * Also notifies the GUI that the processing has been stopped.
     */
    public abstract void stopProcessing();

    /**
     * This method is called by each task/thread when it terminates.  We keep track
     * of the number of tasks/threads that have terminated, so that when they have
     * all finished, we can put the program into the correct state, such as
     * changing the name of the button to "Start Again" and re-enabling the
     * pop-up menu.
     */
    protected synchronized void taskFinished() {
        tasksRemaining--;
        if (tasksRemaining == 0) { // all threads have finished
            stopProcessing();
        }
    }

}
