package ch.zhaw.prog2.heartbeat.parts;

import ch.zhaw.prog2.heartbeat.Half.Side;
import ch.zhaw.prog2.heartbeat.Heart;

public class SemilunarValve extends Valve {

	public SemilunarValve(Side side) {
		super (side);
	}

    public void initializeState(Heart.State state) {
        if(Heart.State.DIASTOLE.equals(state)){
            setOpen(true);
        }else{
            setOpen(false);
        }
    }

}
