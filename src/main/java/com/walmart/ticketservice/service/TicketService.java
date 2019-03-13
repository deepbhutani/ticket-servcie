package com.walmart.ticketservice.service;

import com.walmart.ticketservice.util.TicketServiceException;
import com.walmart.ticketservice.venue.booking.Booking;


/**
 * Ticket Service interface.
 *
 */
public interface TicketService {
    /**
     *
     * @return the number of tickets available in the venue that are neither held nor reserved
     */
    int seatsAvailable();
    
    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a Booking object identifying the specific seats and related
    information
     * @throws Exception 
     */
    Booking findAndHoldAvailableSeats(int numSeats, String customerEmail) throws Exception;
    
    /**
     * Reserver and Commit seats held for a specific customer
     *
     * @param bookingId the seat hold identifier
     * @param customerEmail the email id
     * @return reservation confirmation code
     * @throws TicketServiceException 
     */
    String reserveSeats(String bookingId, String customerEmail) throws TicketServiceException;
}