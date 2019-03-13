package com.walmart.ticketservice.venue.seats;

/**
 * Seat is a single seat of the venue.
 */
public class Seat {

	private int id;
	private SeatState ss;

	public Seat(int id) {
		this.id = id;
		ss = SeatState.OPEN;
	}

	public int getId() {
		return id;
	}

	public SeatState getSeatState() {
		return ss;
	}

	public void markOpen() {
		ss = SeatState.OPEN;
	}

	public void markHold() {
		ss = SeatState.HOLD;
	}

	public void markReserved() {
		ss = SeatState.RESERVED;
	}

	public void markSysHold() {
		ss = SeatState.SYS_HOLD;
	}

}