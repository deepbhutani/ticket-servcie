package com.walmart.ticketservice;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.walmart.ticketservice.service.TicketService;
import com.walmart.ticketservice.util.TicketServiceException;
import com.walmart.ticketservice.venue.booking.Booking;

@Component
public class TicketServiceCommandLineRunner implements CommandLineRunner {

	@Autowired
	private TicketService ticketService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Welcome to ticket Service!");

		int inputCode = 0;
		Scanner scanner = new Scanner(System.in);
		while (inputCode != 4) {
			String inputString = null;
			String customerEmail = null;
			String bookingId = null;

			System.out.println("Please select option for seat booking:");
			System.out.println("1 Query number of tickets available.");
			System.out.println("2 Hold Tickets");
			System.out.println("3 Reserver Tickets");
			System.out.println("4 Exit");
			inputString = scanner.nextLine();

			if (inputString != null && !inputString.isEmpty()) {
				inputCode = Integer.parseInt(inputString);
			} else {
				continue;
			}

			try {
				switch (inputCode) {
				case 1:
					System.out.println("Total seats available: " + ticketService.seatsAvailable());
					break;
				case 2:
					System.out.println("Please enter email id for booking reference:");
					customerEmail = scanner.nextLine();

					System.out.println("How many seats you want to book?");
					String numSeatsString = scanner.nextLine();
					int numSeats = -1;
					if (numSeatsString != null && !numSeatsString.isEmpty()) {
						numSeats = Integer.parseInt(numSeatsString);
					} else {
						System.out.println("Invalid input");
						break;
					}

					Booking booking = null;
					try {
						booking = ticketService.findAndHoldAvailableSeats(numSeats, customerEmail);
					} catch (TicketServiceException | IllegalArgumentException ex) {
						System.out.println(ex.getMessage());
						break;
					}
					System.out.println("Booking ID " + booking.getBookingId());
					break;
				case 3:
					System.out.println("Please enter email id for booking reference:");
					customerEmail = scanner.nextLine();
					System.out.println("Please enter booking id with hold status");
					bookingId = scanner.nextLine();
					try {
						String confirmationId = ticketService.reserveSeats(bookingId, customerEmail);
						System.out.println("Booking Confirmation ID " + confirmationId);
					} catch (TicketServiceException | IllegalArgumentException ex) {
						System.out.println(ex.getMessage());
					}
					break;
				case 4:
					break;
				default:
					System.out.println("Invalid Input");
				}
			} catch (TicketServiceException tse) {
				System.out.println(tse.getMessage());
			}

		}
		scanner.close();

	}
}
