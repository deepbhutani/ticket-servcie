package com.walmart.ticketservice.venue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.walmart.ticketservice.venue.booking.Booking;
import com.walmart.ticketservice.venue.seats.Seat;
import com.walmart.ticketservice.venue.seats.SeatState;
import com.walmart.ticketservice.venue.seats.Seats;

@Component
public class Venue {

	private Seats seats;
	private final int totalCapacity;
	private Map<String, Booking> seatHoldTracker;
	private Map<String, Booking> seatConfirmationTracker;
	private static final int DEFAULT_ROW_COUNT = 10;
	private static final int DEFAULT_COLUMN_COUNT = 10;

	public Venue() {
		this(DEFAULT_ROW_COUNT,DEFAULT_COLUMN_COUNT);
	}

	public Venue(int row, int col) {
		if (row <= 0)
			throw new IllegalArgumentException("Invalid row count");
		if (col <= 0)
			throw new IllegalArgumentException("Invalid col count");

		totalCapacity = row * col;
		seats = new Seats(row, col);
		seatHoldTracker = new ConcurrentHashMap<String, Booking>();
		seatConfirmationTracker = new ConcurrentHashMap<String, Booking>();
	}

	public void processSeat(Seat s, SeatState ss) throws Exception {
		List<Seat> sl = new LinkedList<Seat>();
		sl.add(s);
		processSeats(sl, ss);
	}

	public synchronized void processSeats(List<Seat> seats, SeatState seatState) {

		for (Seat se : seats) {
			switch (seatState) {
			case OPEN:
				se.markOpen();
				break;
			case HOLD:
				se.markHold();
				break;
			case RESERVED:
				se.markReserved();
				break;
			case SYS_HOLD:
				se.markSysHold();
			}

		}
	}

	public int getTotalCapacity() {
		return totalCapacity;
	}

	public Map<String, Booking> getSeatHoldTracker() {
		return seatHoldTracker;
	}

	public void setSeatHoldTracker(Map<String, Booking> seatHoldTracker) {
		this.seatHoldTracker = seatHoldTracker;
	}

	public Map<String, Booking> getSeatConfirmationTracker() {
		return seatConfirmationTracker;
	}

	public void setSeatConfirmationTracker(Map<String, Booking> seatConfirmationTracker) {
		this.seatConfirmationTracker = seatConfirmationTracker;
	}

	public void setSeats(Seats seats) {
		this.seats = seats;
	}

	public Seats getSeats() {
		return seats;
	}

}
