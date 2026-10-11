package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import uk.gov.dwp.engineering.recruitment.domain.Booking;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;

class CinemaTicketsControllerTest {

  private final CinemaTicketsService cinemaTicketsService = mock(CinemaTicketsService.class);

  private CinemaTicketsController underTest;

  @BeforeEach
  void setup() {
    underTest = new CinemaTicketsController(cinemaTicketsService);
  }

  @Test
  void shouldHaveAStatusOfCreatedInResponse_whenABookingIsSuccessfullyMade() {
    var accountId = 1L;
    var ticketRequest = new TicketRequest(TicketType.ADULT, 1);
    var bookingConfirmation = new BookingConfirmation(accountId);
    when(cinemaTicketsService.purchaseTickets(accountId, ticketRequest))
            .thenReturn(bookingConfirmation);
    var booking = new Booking(accountId, ticketRequest);

    var response = underTest.makeBooking(booking);

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
  }

  @Test
  void shouldHaveABookingConfirmationInResponse_whenABookingIsSuccessfullyMade() {
    var accountId = 1L;
    var ticketRequest = new TicketRequest(TicketType.ADULT, 1);
    var bookingConfirmation = new BookingConfirmation(accountId);
    when(cinemaTicketsService.purchaseTickets(accountId, ticketRequest))
            .thenReturn(bookingConfirmation);
    var booking = new Booking(accountId, ticketRequest);

    var response = underTest.makeBooking(booking);

    assertEquals(bookingConfirmation, response.getBody());
  }

}
