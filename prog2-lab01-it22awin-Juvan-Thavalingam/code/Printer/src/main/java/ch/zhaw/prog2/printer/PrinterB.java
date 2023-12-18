package ch.zhaw.prog2.printer;

public class PrinterB {

    // test program
    public static void main(String[] arg) {
        Runnable runA = new PrinterRunnable('.', 10);
        Runnable runB = new PrinterRunnable('*', 20);
        Thread thread1 = new Thread(runA, "PrinterA");
        Thread thread2 = new Thread(runB, "PrinterB");
        thread1.start(); //Aufgabe d
        thread2.start();
        runA.run();
        try{
            thread1.join();
            thread2.join();
        } catch (InterruptedException e){
            System.out.println("interrupted!");
        }
    }

    private static class PrinterRunnable implements Runnable {
        char symbol;
        int sleepTime;

        public PrinterRunnable(char symbol, int sleeptime) {
            this.symbol = symbol;
            this.sleepTime = sleeptime;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " run started...");
            for (int i = 1; i < 100; i++) {
                System.out.print(symbol);
                Thread.yield(); //Aufgabe c
            }
            System.out.println('\n' + Thread.currentThread().getName() + " run ended.");

        }
    }
}

