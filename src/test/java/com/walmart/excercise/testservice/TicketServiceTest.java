package com.walmart.excercise.testservice;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.walmart.ticketservice.service.impl.TicketServiceImpl;
import com.walmart.ticketservice.util.TicketServiceException;
import com.walmart.ticketservice.venue.Venue;
import com.walmart.ticketservice.venue.booking.Booking;

public class TicketServiceTest {

	TicketServiceImpl ticketService;
	private final int seatHoldExpirySeconds = 5;

	@Before
	public void setup() {
		Venue venue = new Venue(10, 10);
		ticketService = new TicketServiceImpl(venue);
		ticketService.setHoldExpirationTime(seatHoldExpirySeconds);
	}

	@Test
	public void holdTicketsTest() {
		Integer beforeCount = ticketService.seatsAvailable();
		Integer seatsOnHold = 10;
		Booking booking = null;
		try {
			booking = ticketService.findAndHoldAvailableSeats(seatsOnHold, "test@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer afterCount = ticketService.seatsAvailable();

		Assertions.assertThat(afterCount).isEqualTo(beforeCount - seatsOnHold);
		Assertions.assertThat(seatsOnHold).isEqualTo(booking.getSeats().size());
	}

	@Test
	public void reserveTicketsTest() {
		Integer beforeCount = ticketService.seatsAvailable();
		Integer seatsOnHold = 10;
		Booking booking = null;
		try {
			booking = ticketService.findAndHoldAvailableSeats(seatsOnHold, "test@gmail.com");
			ticketService.reserveSeats(booking.getBookingId(), "test@gmail.com");

		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer afterCount = ticketService.seatsAvailable();

		Assertions.assertThat(afterCount).isEqualTo(beforeCount - seatsOnHold);
		Assertions.assertThat(seatsOnHold).isEqualTo(booking.getSeats().size());
	}

	@Test
	public void seatHoldExpiryTest() {
		Integer beforeCount = ticketService.seatsAvailable();
		Integer seatsOnHold = 10;
		try {
			ticketService.findAndHoldAvailableSeats(seatsOnHold, "test@gmail.com");
			Thread.sleep((seatHoldExpirySeconds + 1) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer afterCount = ticketService.seatsAvailable();

		Assertions.assertThat(afterCount).isEqualTo(beforeCount);

	}

	@Test
	public void availableSeatsTest() {
		Integer beforeCount = ticketService.seatsAvailable();
		Integer seatsOnHold = 10;
		Booking booking = null;
		try {
			booking = ticketService.findAndHoldAvailableSeats(seatsOnHold, "test@gmail.com");
			ticketService.reserveSeats(booking.getBookingId(), "test@gmail.com");
			ticketService.findAndHoldAvailableSeats(seatsOnHold, "test123@gmail.com");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Integer afterCount = ticketService.seatsAvailable();

		Assertions.assertThat(afterCount).isEqualTo(beforeCount - (seatsOnHold + seatsOnHold));

	}

	@Test
	public void invalidEmailForReservationTest() {
		Integer seatsOnHold = 10;
		try {
			Booking booking = ticketService.findAndHoldAvailableSeats(seatsOnHold, "test@gmail.com");

			Assertions.assertThatExceptionOfType(TicketServiceException.class)
					.isThrownBy(() -> {
						ticketService.reserveSeats(booking.getBookingId(), "invalid@gmail.com");
					}).withMessage("Invalid Email Id");
		} catch (Exception e) {

		}

	}

}
