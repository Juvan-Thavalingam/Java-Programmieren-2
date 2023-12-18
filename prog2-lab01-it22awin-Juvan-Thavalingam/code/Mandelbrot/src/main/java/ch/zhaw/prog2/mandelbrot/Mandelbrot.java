package ch.zhaw.prog2.mandelbrot;

import javafx.application.Application;

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
public class Mandelbrot {

    /**
     * This Wrapper Class is only required to allow IDEs to start the FX-Applications
     */
    public static void main(String[] args) {
        Application.launch(MandelbrotGui.class, args);
    }

} // end Mandelbrot
