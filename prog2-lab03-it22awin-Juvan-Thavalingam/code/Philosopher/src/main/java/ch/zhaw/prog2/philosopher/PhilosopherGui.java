package ch.zhaw.prog2.philosopher;

import ch.zhaw.prog2.philosopher.PhilosopherTable.Philosopher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import static ch.zhaw.prog2.philosopher.PhilosopherTable.PhilosopherState.*;

/**
 * Main- and Graphical-UI classes for the philosopher exercise.
 * You should not change these classes, just use it to run the application.
 */
public class PhilosopherGui extends JFrame {
    private static final int DEFAULT_PHILOSOPHER_COUNT = 5;
    private static final int DEFAULT_BASE_TIME = 75; // milliseconds
    private final PhilosopherTable table;

    public static void main(String[] args) {
        int philosopherCount = args.length >=1 ? Integer.parseInt(args[0]) : DEFAULT_PHILOSOPHER_COUNT;
        int baseTime = args.length >= 2 ? Integer.parseInt(args[1]) : DEFAULT_BASE_TIME;
        new PhilosopherGui(philosopherCount, baseTime);
    }

    public PhilosopherGui(int philosopherCount, int baseTime) {
        setTitle("Philosopher");
        setVisible(true);
        setVisible(false);
        Insets insets = getInsets();
        setSize(insets.left + insets.right + 400, insets.top + insets.bottom + 400);

        table = new PhilosopherTable(philosopherCount, baseTime);
        PhilosopherPanel panel = new PhilosopherPanel(table, philosopherCount);
        new ConsoleLogger(table);
        table.start();

        setContentPane(panel);
        setVisible(true);
        repaint();

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closing();
            }
        });
    }

    private void closing() {
        table.stop();
        System.exit(0);
    }


    static class PhilosopherPanel extends JPanel implements Observer {

        private final PhilosopherTable table;
        private final int philosopherCount; // greater than 2

        public PhilosopherPanel(PhilosopherTable table, int philosopherCount) {
            this.philosopherCount = philosopherCount;
            this.table = table;
            table.addObserver(this);
        }

        public void paint(Graphics g) {
            Insets insets = getInsets();
            Dimension dim = getSize();
            int length = Math.min(dim.width, dim.height);
            double teta;
            double tetaIn;
            double phi = 2 * Math.PI / philosopherCount;
            int plateRadius = (int) (Math.sqrt(Math.pow(length / 2.0, 2.0)
                - Math.pow(Math.cos(phi) * (length / 2.0), 2.0) + Math.sin(phi)
                * (length / 2.0)) * 0.25);
            int tableRadius = length / 2 - plateRadius - 10;
            int halfStickLength = (int) (plateRadius * 1.25);
            int centerX = length / 2 + insets.left;
            int centerY = length / 2 + insets.top;

            super.paint(g);

            for (int philosopherId = 0; philosopherId < philosopherCount; philosopherId++) {
                int transCenterX = centerX - plateRadius;
                int transCenterY = centerY - plateRadius;

                teta = 0;
                switch (table.getPhilosopher(philosopherId).getState()) {
                    case THINKING -> g.setColor(Color.blue);
                    case HUNGRY -> g.setColor(Color.red);
                    case EATING -> g.setColor(Color.yellow);
                }
                int xPositionPlate = (int) Math.round(transCenterX + tableRadius * Math.cos(philosopherId * phi));
                int yPositionPlate = (int) Math.round(transCenterY + tableRadius * Math.sin(philosopherId * phi));
                g.fillOval(xPositionPlate, yPositionPlate, 2 * plateRadius, 2 * plateRadius);

                g.setColor(Color.black);
                g.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
                g.drawString(""+philosopherId, xPositionPlate+plateRadius-5, yPositionPlate+plateRadius+10 );

                if (table.getPhilosopher(philosopherId).getState() == EATING) {
                    teta = (-phi / 7);
                }
                if (table.getPhilosopher(table.leftNeighbourId(philosopherId)).getState() == EATING) {
                    teta = phi / 7;
                }

                tetaIn = teta * 1.75;

                int xStickInner = (int) Math.round(centerX + (tableRadius - halfStickLength) * Math.cos(philosopherId * phi + phi / 2 + tetaIn));
                int yStickInner = (int) Math.round(centerY + (tableRadius - halfStickLength) * Math.sin(philosopherId * phi + phi / 2 + tetaIn));
                int xStickOuter = (int) Math.round(centerX + (tableRadius + halfStickLength) * Math.cos(philosopherId * phi + phi / 2 + teta));
                int yStickOuter = (int) Math.round(centerY + (tableRadius + halfStickLength) * Math.sin(philosopherId * phi + phi / 2 + teta));
                g.drawLine(xStickInner, yStickInner, xStickOuter, yStickOuter);
                g.drawString(""+philosopherId, xStickInner, yStickInner);
            }
        }

        public void update(Observable o, Object arg) {
            repaint();
        }

    }


    static class ConsoleLogger implements Observer {
        public ConsoleLogger(PhilosopherTable table) {
            table.addObserver(this);
        }

        public void update(Observable o, Object arg) {
            Philosopher philosopher = arg != null ? (Philosopher) arg : null;
            if (philosopher == null) {
                System.out.println("Application starting");
                return;
            }
            System.out.println("Philosopher " + philosopher.getId() + " " + getStateString(philosopher));
        }

        private String getStateString(Philosopher philosopher) {
            return switch (philosopher.getState()) {
                case EATING -> "starts eating";
                case THINKING -> "starts thinking";
                case HUNGRY -> "is getting hungry";
            };
        }
    }
}


