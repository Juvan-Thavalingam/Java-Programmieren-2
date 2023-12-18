package ch.zhaw.prog2.mandelbrot;

import ch.zhaw.prog2.mandelbrot.processors.MandelbrotCallableProcessor;
import ch.zhaw.prog2.mandelbrot.processors.MandelbrotExecutorProcessor;
import ch.zhaw.prog2.mandelbrot.processors.MandelbrotProcessor;
import ch.zhaw.prog2.mandelbrot.processors.MandelbrotTaskProcessor;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MandelbrotMockTest {
    private static final int width = 10;
    private static final int height = 5;

    @Mock private MandelbrotProcessorListener listener;

    private MandelbrotProcessorListener mandelbrotProcessorListenerTester = new MandelbrotProcessorListener() {
        @Override
        public void processingStopped(long duration) {
            System.out.println("Duration(ms): " + duration);
            assertNotEquals(0,duration, "Duration must not be 0");
        }

        @Override
        public void rowProcessed(ImageRow row) {
            assertNotNull(row);
            assertNotNull(row.pixels);
            assertEquals(width, row.pixels.length, "Length of Pixel-Array not matching");
            System.out.print("row-nr: " + row.rowNumber + "  ");
            System.out.print("row-pixels: " + Arrays.stream(row.pixels)
                .map(Color::toString).collect(Collectors.joining(", ")));
            System.out.println();
        }
    };

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMandelbrotTaskProcessorListenerCalls() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotTaskProcessor(listener, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
        verify(listener, times(height)).rowProcessed(any(ImageRow.class));
        verify(listener, times(1)).processingStopped(anyLong());
    }

    @Test
    public void testMandelbrotTaskProcessorListenerContent() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotTaskProcessor(mandelbrotProcessorListenerTester, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
    }

    @Test
    public void testMandelbrotExecutorProcessorListenerCalls() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotExecutorProcessor(listener, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
        verify(listener, times(height)).rowProcessed(any(ImageRow.class));
        verify(listener, times(1)).processingStopped(anyLong());
    }

    @Test
    public void testMandelbrotExecutorProcessorListenerContent() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotExecutorProcessor(mandelbrotProcessorListenerTester, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
    }

    @Test
    public void testMandelbrotCallableProcessorListenerCalls() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotCallableProcessor(listener, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
        verify(listener, times(height)).rowProcessed(any(ImageRow.class));
        verify(listener, times(1)).processingStopped(anyLong());
    }

    @Test
    public void testMandelbrotCallableProcessorListenerContent() throws InterruptedException {
        MandelbrotProcessor processor = new MandelbrotCallableProcessor(mandelbrotProcessorListenerTester, width, height);
        processor.startProcessing(1);
        Thread.sleep(2000);
    }


}
