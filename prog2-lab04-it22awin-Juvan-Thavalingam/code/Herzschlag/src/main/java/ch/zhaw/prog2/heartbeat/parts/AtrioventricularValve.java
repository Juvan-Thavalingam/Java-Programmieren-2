package ch.zhaw.prog2.heartbeat.parts;

import ch.zhaw.prog2.heartbeat.Half.Side;
import ch.zhaw.prog2.heartbeat.Heart;

public class AtrioventricularValve extends Valve {

	public AtrioventricularValve(Side side) {
		super (side);
	}

    public void initializeState(Heart.State state) {
        if(Heart.State.DIASTOLE.equals(state)){
            setOpen(false);
        }else{
            setOpen(true);
        }
    }
}
