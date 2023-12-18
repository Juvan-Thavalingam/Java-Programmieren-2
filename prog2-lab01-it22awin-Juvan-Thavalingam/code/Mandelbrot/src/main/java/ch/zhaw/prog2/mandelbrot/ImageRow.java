package ch.zhaw.prog2.mandelbrot;

import javafx.scene.paint.Color;

/**
 * This is a container class which holds the data for one row to be drawn on the canvas.
 * No getter and setters. Just use direct access to the fields.
 */
public class ImageRow {
    public final int rowNumber;
    public final Color[] pixels;

    public ImageRow(int rowNumber, int width) {
        this.rowNumber = rowNumber;
        this.pixels = new Color[width];
    }
}
