package ch.zhaw.prog2.heartbeat.parts;

import ch.zhaw.prog2.heartbeat.Half.Side;

public class Atrium {
	private Side side;

	public Atrium (Side side) {
		this.side = side;
	}

	public void contract() {
		System.out.println("Atrium "+ side + " is contracting.");
	}

	public void relax() {
		System.out.println("Atrium "+ side + " is relaxing.");
	}

}
