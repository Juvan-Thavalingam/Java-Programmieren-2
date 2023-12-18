package ch.zhaw.prog2.mandelbrot.processors;

import ch.zhaw.prog2.mandelbrot.ImageRow;
import ch.zhaw.prog2.mandelbrot.MandelbrotProcessorListener;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MandelbrotCallableProcessor extends MandelbrotProcessor {

    private AtomicBoolean terminate = new AtomicBoolean(false); // signal the threads to abort processing and terminate
    private ExecutorService executorService;
    private List<Future<ImageRow>> listFuture = new ArrayList<>();
    /**
     * Initialize the Mandelbrot processor.
     * This method also initializes the color palette, containing colors in spectral order.
     * @param processorListener class to notify about processing results
     * @param width with of the canvas in pixel
     * @param height height of the canvas in pixel
     */
    public MandelbrotCallableProcessor(MandelbrotProcessorListener processorListener, int width, int height) {
        super(processorListener, width, height);
    }

    /**
     * This method starts as many new threads as the user has specified,
     * and assigns a different part of the image to each thread.
     * The threads are run at lower priority than the event-handling thread,
     * in order to keep the GUI responsive.
     *
     * @param numThreads number of thread to start to run the tasks
     */
    @Override
    public void startProcessing(int numThreads) {
        terminate.set(false);  // Set the signal before starting the threads!
        super.tasksRemaining = height;  // Records how many of the threads are still running
        super.startTime = System.currentTimeMillis();

        // TODO : Start the the executor service with the given number of threads.
        executorService = Executors.newFixedThreadPool(numThreads);

        // TODO : process all rows using the Callable MandelbrotTask and store returned Futures in a list
        for (int i = 0; i < height; i++){
            listFuture.add(executorService.submit(new MandelbrotTask(i)));
        }

        long duration = System.currentTimeMillis()-startTime;
        System.out.println("Tasks submitted after " + duration + "ms");

        // TODO : get results from Future list and send them to the processListener (GUI)
        // make sure to handle all Exceptions
        try {
            for(Future<ImageRow> future : listFuture){
                processorListener.rowProcessed(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e.getMessage());
        } finally {
            // stop processing and shutdown executor
            stopProcessing();
            executorService.shutdown();
        }
    }

    /**
     * Stopp processing tasks and terminate all threads.
     * Also notifies the GUI that the processing has been stopped.
     */
    @Override
    public void stopProcessing() {
        terminate.set(true);  // signal the threads to abort
        // TODO : shutdown executor service
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(1, TimeUnit.MINUTES)){
                executorService.shutdown();
            }
        } catch (InterruptedException e){
            throw new IllegalStateException(e.getMessage());
        }

        // calculate processing time
        long duration = System.currentTimeMillis() - startTime;
        // notify the listener that the processing is completed
        processorListener.processingStopped(duration);
    }


    /**
     * This class defines the thread that does the computation.
     * The run method computes the image one pixel at a time.
     * After computing the colors for each row of pixels, the colors are
     * copied into the image, and the part of the display that shows that
     * row is repainted.
     *
     * Extended to implement also Callable.
     * the call() method calculates and return only the result for the startRow.
     */
    private class MandelbrotTask implements Callable<ImageRow> {
        // TODO : Use Task implementation from MandelbrotExecutorProcessor change it to a Callable.
        private final int row;
        private final double xmin, xmax, ymin, ymax, dx, dy;
        private final int maxIterations;

        private MandelbrotTask(int row){
            this.row = row;
            xmin = -1.6744096740931858;
            xmax = -1.674409674093473;
            ymin = 4.716540768697223E-5;
            ymax = 4.716540790246652E-5;
            dx = (xmax - xmin) / (width - 1);
            dy = (ymax - ymin) / (height - 1);
            maxIterations = 10000;
        }


        public ImageRow call()  {
            return calculateRow(row); // Compute one row of pixels.
        }

        private ImageRow calculateRow(int row) {
            final ImageRow imageRow = new ImageRow(row, width);
            double x;
            double y = ymax - dy * row;

            for (int col = 0; col < width; col++) {
                x = xmin + dx * col;
                int count = 0;
                double xx = x;
                double yy = y;
                while (count < maxIterations && (xx * xx + yy * yy) < 4) {
                    count++;
                    double newxx = xx * xx - yy * yy + x;
                    yy = 2 * xx * yy + y;
                    xx = newxx;
                }
                // select color based on count of iterations
                imageRow.pixels[col] = (count != maxIterations) ?
                    palette[count % palette.length] : Color.BLACK;
                // Check for the signal to immediately abort the computation.
                if (terminate.get()) return null;
            }
            return imageRow;
        }
    }
} // end MandelbrotTask
