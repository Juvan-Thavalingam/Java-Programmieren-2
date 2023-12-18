package ch.zhaw.prog2.heartbeat;

import ch.zhaw.prog2.heartbeat.parts.Valve;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a human heart (https://en.wikipedia.org/wiki/Heart).
 * <p>
 * <p>
 * Example tests: before Chamber::contract() is called, the atrioventricular
 * valves must be closed.
 *
 * @author wahl
 */
public class Heart {
    private final static int AVERAGE_HEART_RATE = 60;

    /**
     * Encodes the states of the heart
     *
     * @author wahl
     */
    public enum State {
        SYSTOLE, DIASTOLE
    }

    private List<Half> halves;
    private State state;
    private int heartRate;

    /**
     * The default constructor calls the more specific constructor with a new left
     * and right half.
     */
    public Heart() {
        this(new Half(Half.Side.LEFT), new Half(Half.Side.RIGHT));
    }

    /**
     * The default constructor adds the left and the right half that are specified
     * as parameters and sets the start state to diastole.
     */
    public Heart(Half leftHalf, Half rightHalf) {
        state = State.DIASTOLE;
        heartRate = AVERAGE_HEART_RATE;
        halves = new ArrayList<>();
        halves.add(leftHalf);
        halves.add(rightHalf);
        initalizeState();
    }

    public void initalizeState(){
        for(Half half : halves){
            half.initializeState(state);
        }
    }

    /**
     * Executes a sequence of diastole and systole, beginning with the current
     * state. Exercise 2.1 b (Extension in 2.3 c)
     *
     * @throws HeartBeatDysfunctionException when Valve.IllegalValveStateException or
     *                                       InvalidValvePositionException occurs during diastole or systole
     * TODO Implement a pause mechanism based on the current heart rate
     */
    public void executeHeartBeat() throws HeartBeatDysfunctionException, Valve.IllegalValveStateException {
        try{
            if (state.equals(State.SYSTOLE)){
                executeSystole();
                executeDiastole();
            } else {
                executeDiastole();
                executeSystole();
            }
        } catch (Valve.IllegalValveStateException e){
            throw new HeartBeatDysfunctionException("HeartBeatException", e);
        }
    }

    /**
     * Executes the diastole phase of the heart.
     * Exercise 2.1 b (Extension in 2.3 c)
     *
     * @throws Valve.IllegalValveStateException when one of the valves has an illegal State
     */
    public void executeDiastole() throws Valve.IllegalValveStateException {
        if(state.equals(State.SYSTOLE)){
            throw new Valve.IllegalValveStateException();
        }
        for (Half half : halves){
            half.openAtrioventricularValve();
            half.closeSemilunarValve();
            half.relaxAtrium();
            half.relaxVentricle();
        }
        setState(State.SYSTOLE);
    }

    /**
     * Executes the systole phase of the heart.
     * Exercise 2.1 b (Extension in 2.3 c)
     *
     * @throws Valve.IllegalValveStateException when one of the valves has an illegal State
     */
    public void executeSystole() throws Valve.IllegalValveStateException {
        if (state.equals(State.DIASTOLE)){
            throw new Valve.IllegalValveStateException();
        }
        for (Half half : halves){
            half.openSemilunarValve();
            half.closeAtrioventricularValve();
            half.contractAtrium();
            half.contractVentricle();
        }
        setState(State.DIASTOLE);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * Sets the heart rate to the parameter value in bpm (beats per minute).
     * Rate has to be inside range >30 && <220
     * If rate is outside of range method rejects the change
     * and returns false.
     *
     * @param frequencyInBpm which should be applied to the heart
     * @return true if the heart rate could be set, false otherwise
     */
    public boolean setHeartRate(int frequencyInBpm) {
        if (frequencyInBpm < 30 || frequencyInBpm > 220) {
            return false;
        }
        this.heartRate = frequencyInBpm;
        return true;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public List<Half> getHalves() {
        return halves;
    }

    public static class HeartBeatDysfunctionException extends Exception {
        public HeartBeatDysfunctionException() {
        }

        public HeartBeatDysfunctionException(String message) {
            super(message);
        }

        public HeartBeatDysfunctionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * The main method instantiates a heart object and executes a single heartbeat.
     *
     * @param args
     */
    public static void main(String[] args) throws HeartBeatDysfunctionException, Valve.IllegalValveStateException {
        Heart heart = new Heart();
        heart.executeHeartBeat();
    }
}
