package ch.zhaw.prog2.heartbeat;

import ch.zhaw.prog2.heartbeat.parts.*;

/**
 * Represents one half of a heart.
 *
 * @author wahl, muon
 */
public class Half {

    public enum Side {LEFT, RIGHT}

    private Atrium atrium;  // "Vorhof"
    private Ventricle ventricle; // "Herzkammer"
    private AtrioventricularValve atrioventricularValve; // "Segelklappe"
    private SemilunarValve semilunarValve; // "Taschenklappe"
    private final Side side;

    public Half(Side side) {
        this.side = side;
        atrium = new Atrium(side);
        ventricle = new Ventricle(side);
        atrioventricularValve = new AtrioventricularValve(side);
        semilunarValve = new SemilunarValve(side);
    }

    public void initializeState(Heart.State state) {
        atrioventricularValve.initializeState(state);
        semilunarValve.initializeState(state);
    }

    public AtrioventricularValve getAtrioventricularValve() {
        return atrioventricularValve;
    }

    public SemilunarValve getSemilunarValve() {
        return semilunarValve;
    }

    public boolean isAtrioventricularValveOpen() {
        return atrioventricularValve.isOpen();
    }

    public boolean isSemilunarValveOpen() {
        return semilunarValve.isOpen();
    }

    /**
     * Closes the valve
     *
     * @throws IllegalStateException when valve is already closed
     */
    public void closeSemilunarValve() throws Valve.IllegalValveStateException {
        semilunarValve.close();
    }

    /**
     * Opens the valve
     *
     * @throws Valve.IllegalValveStateException when the valve is already open
     */
    public void openSemilunarValve() throws Valve.IllegalValveStateException {
        semilunarValve.open();
    }

    /**
     * Closes the valve
     *
     * @throws Valve.IllegalValveStateException when valve is already closed
     */
    public void closeAtrioventricularValve() throws Valve.IllegalValveStateException {
        atrioventricularValve.close();
    }

    /**
     * Opens the valve
     *
     * @throws Valve.IllegalValveStateException when the valve is already open
     */
    public void openAtrioventricularValve() throws Valve.IllegalValveStateException {
        atrioventricularValve.open();
    }

    public void contractVentricle() {
        ventricle.contract();
    }

    public void relaxVentricle() {
        ventricle.relax();
    }

    public void contractAtrium() {
        atrium.contract();
    }

    public void relaxAtrium() {
        atrium.relax();
    }
}
