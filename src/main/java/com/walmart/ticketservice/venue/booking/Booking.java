package com.walmart.ticketservice.venue.booking;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.walmart.ticketservice.venue.seats.Seat;


/**
 * Booking refers to a collection of seat(s) which a customer has a held or
 * reserved and other  booking details.
 * 
 */
public class Booking {

	private final String bookingId;
	private final String customerEmail;
	private List<Seat> seats;
	private Date createDate;
	private String confirmationCode;

	public Booking(String seatHoldId, String customerEmail, List<Seat> seats) {
		this.bookingId = seatHoldId;
		this.customerEmail = customerEmail;
		this.seats = new LinkedList<Seat>(seats);
		createDate = new Date(); 
	}

	public String getBookingId() {
		return bookingId;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setConfirmationCode(String cc) {
		confirmationCode = cc;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}
}
