package com.walmart.ticketservice.venue.seats;

import java.util.ArrayList;
import java.util.List;

public class Seats {

	private List<List<Seat>> seats;
	
	public Seats(int rowNum, int colNum) {
		seats = new ArrayList<List<Seat>>();
		for (int i = 0; i < rowNum; i++) {
			List<Seat> row = new ArrayList<Seat>();
			for (int j = 0; j < colNum; j++) {
				row.add(new Seat(i + j));
			}
			seats.add(row);
		}
	}

	public int getRowLength() {
		return seats.size();
	}

	public int getColumnLength() {
		return seats.get(0).size();
	}

	public Seat getSeat(int row, int col) {
		Seat s = null;
		if (row >= 0 && row < seats.size() && col >= 0 && col < seats.get(0).size()) {
			s = seats.get(row).get(col);
		}
		return s;
	}
}
