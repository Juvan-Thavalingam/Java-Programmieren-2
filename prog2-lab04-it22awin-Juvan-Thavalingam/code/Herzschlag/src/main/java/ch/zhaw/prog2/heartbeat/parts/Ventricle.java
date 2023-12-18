package ch.zhaw.prog2.heartbeat.parts;

import ch.zhaw.prog2.heartbeat.Half.Side;

public class Ventricle {
	private Side side;

	public Ventricle (Side side) {
		this.side = side;
	}

	public void contract() {
		System.out.println("Chamber "+ side + " is contracting.");
	}

	public void relax() {
		System.out.println("Chamber "+ side + " is relaxing.");
	}

}
