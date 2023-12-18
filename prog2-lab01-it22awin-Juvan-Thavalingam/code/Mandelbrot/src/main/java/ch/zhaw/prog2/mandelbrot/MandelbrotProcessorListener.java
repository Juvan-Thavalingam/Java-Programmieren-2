package ch.zhaw.prog2.mandelbrot;

public interface MandelbrotProcessorListener {
    /**
     * This method is called from the Processor when the processing is stopped or completed.
     * GUI state needs to be reset.
     *
     * @param duration Duration of the processing in Milliseconds
     */
    void processingStopped(long duration);

    /**
     * This method is called from the Processor when a row has been processed
     * and needs to be added to the image.
     *
     * @param row the row of pixels whose colors are to be set
     */
    void rowProcessed(ImageRow row);
}
