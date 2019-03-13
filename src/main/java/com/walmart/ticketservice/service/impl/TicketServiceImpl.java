package com.walmart.ticketservice.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.walmart.ticketservice.service.TicketService;
import com.walmart.ticketservice.util.TicketServiceException;
import com.walmart.ticketservice.util.TicketServiceValidator;
import com.walmart.ticketservice.venue.Venue;
import com.walmart.ticketservice.venue.booking.Booking;
import com.walmart.ticketservice.venue.seats.Seat;
import com.walmart.ticketservice.venue.seats.SeatState;
import com.walmart.ticketservice.venue.seats.Seats;

@Component
public class TicketServiceImpl implements TicketService {

	private static Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

	private Venue venue;
	private Integer holdExpirationTime;

	@Autowired
	public TicketServiceImpl(Venue venue) {
		this.venue = venue;
	}

	public  Booking findAndHoldAvailableSeats(int numSeats, String customerEmail) throws Exception {
		if (seatsAvailable() < numSeats)
			throw new TicketServiceException("Sorry, Insufficient Seats Available");
		if (numSeats <= 0) {
			throw new IllegalArgumentException("Numbrer of seats cannot be Zero or Negative");
		}
		if(!TicketServiceValidator.isValidEmail(customerEmail)) {
			throw new IllegalArgumentException("Invalid Email Address");
		}

		Booking booking;
		List<Seat> seats = new LinkedList<Seat>();
		for (int i = 0; i < numSeats; i++) {
			Seat s = findNextBestSeat();
			seats.add(s);
		}

		try {
			venue.processSeats(seats, SeatState.HOLD);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("Reverting seat hold status");
			venue.processSeats(seats, SeatState.OPEN);
			return null;
		}

		String bookingId = UUID.randomUUID().toString().replace("-", "");
		booking = new Booking(bookingId, customerEmail, seats);
		venue.getSeatHoldTracker().put(booking.getBookingId(), booking);

		return booking;
	}

	public String reserveSeats(String bookingId, String customerEmail) throws TicketServiceException {
		Booking booking = venue.getSeatHoldTracker().get(bookingId);
		if (null == booking)
			throw new TicketServiceException("Invalid Booking Id");

		if(!TicketServiceValidator.isValidEmail(customerEmail)) {
			throw new IllegalArgumentException("Invalid Email Address");
		}
		
		if (!booking.getCustomerEmail().equals(customerEmail))
			throw new TicketServiceException("Email id does not match with booking");
		

		try {
			venue.processSeats(booking.getSeats(), SeatState.RESERVED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("Reverting seat hold status");
			venue.processSeats(booking.getSeats(), SeatState.HOLD);
		}

		String newConfirmationCode = UUID.randomUUID().toString().replace("-", "");
		booking.setConfirmationCode(newConfirmationCode);
		venue.getSeatConfirmationTracker().put(newConfirmationCode, booking);
		venue.getSeatHoldTracker().remove(bookingId, booking);
		return newConfirmationCode;
	}

	/**
	 * Finds next best open seat in venue seating by searching seats from best
	 * (front-left) to worst (rear-right). It is marked as synchronized to avoid 
	 * returning the same seat to two different threads.
	 * 
	 * @return best available seat
	 * @throws Exception
	 */
	private synchronized Seat findNextBestSeat() throws Exception {
		if (seatsAvailable() == 0)
			return null; 
		
		final Seats seats = venue.getSeats();
		for (int i = 0; i < seats.getRowLength(); i++) {
			for (int j = 0; j < seats.getColumnLength(); j++) {
				final Seat s = seats.getSeat(i, j);
				if (SeatState.OPEN.equals(s.getSeatState())) {
					venue.processSeat(s, SeatState.SYS_HOLD);
					return s;
				}
			}
		}
		return null;
	}


	private void removeExpiredBookings() {
		Date cur = new Date();
		Iterator<Map.Entry<String, Booking>> it = venue.getSeatHoldTracker().entrySet().iterator();
		while (it.hasNext()) {
			Booking booking = it.next().getValue();
			long duration = cur.getTime() - booking.getCreateDate().getTime();
			if (duration / 1000 >= holdExpirationTime) {
				venue.processSeats(booking.getSeats(), SeatState.OPEN);
				it.remove();
				venue.getSeatHoldTracker().remove(booking.getBookingId(), booking);
			}
		}
	}

	/**
	 * The number of seats in the venue that are neither held nor reserved. 
	 * 
	 * @return the number of seats available in the venue
	 */
	public int seatsAvailable() {
		this.removeExpiredBookings();
		int seatsOnHold = venue.getSeatHoldTracker().entrySet().stream().parallel()
				.map(entry -> entry.getValue().getSeats().size()).reduce(0, (count1, count2) -> count1 + count2);
		int seatsReserved = venue.getSeatConfirmationTracker().entrySet().stream().parallel()
				.map(entry -> entry.getValue().getSeats().size()).reduce(0, (count1, count2) -> count1 + count2);

		return venue.getTotalCapacity() - (seatsOnHold + seatsReserved);
	}

	public Integer getHoldExpirationTime() {
		return holdExpirationTime;
	}

	@Value("${seat.hold.expiry.seconds}")
	public void setHoldExpirationTime(Integer holdExpirationTime) {
		this.holdExpirationTime = holdExpirationTime;
	}

}