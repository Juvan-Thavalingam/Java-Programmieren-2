package ch.zhaw.prog2.trafficlight;

public class TrafficLightOperation {
    private static volatile boolean running = true;

    public static void terminate () {
        running = false;
    }

    public static void main(String[] args) {
        TrafficLight[] trafficLights = new TrafficLight[7];
        Car[] cars = new Car[20];
        for (int i = 0; i < trafficLights.length; i++)
            trafficLights[i] = new TrafficLight();
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car("Car " + i, trafficLights);
        }

        // Simulation
        while (running) {
            for (int greenIndex = 0; greenIndex < trafficLights.length; greenIndex = greenIndex + 2) {
                // Display state of simulation
                System.out.println("=====================================================");
                for (int j = 0; j < trafficLights.length; j++) {
                    String lightState;
                    if (j == greenIndex || j == greenIndex + 1)
                        lightState = "✅";
                    else
                        lightState = "🛑";
                    System.out.print(lightState + " at Light " + j + ":");
                    for (int carNumber = 0; carNumber < cars.length; carNumber++) {
                        if (cars[carNumber].position() == j)
                            System.out.print(" " + carNumber);
                    }
                    System.out.println();
                }
                System.out.println("=====================================================");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException logOrIgnore) {
                    System.out.println(logOrIgnore.getMessage());
                }
                trafficLights[greenIndex].switchToGreen();
                if (greenIndex + 1 < trafficLights.length) {
                    trafficLights[greenIndex + 1].switchToGreen();
                }

                // green period
                try {
                    Thread.sleep((int) (Math.random() * 500));
                } catch (InterruptedException logOrIgnore) {
                    System.out.println(logOrIgnore.getMessage());
                }
                trafficLights[greenIndex].switchToRed();
                if (greenIndex + 1 < trafficLights.length)
                    trafficLights[greenIndex + 1].switchToRed();

                // red period
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException logOrIgnore) {
                    System.out.println(logOrIgnore.getMessage());
                }
            }
        }
    }
}
