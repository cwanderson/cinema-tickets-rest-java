package uk.gov.dwp.engineering.recruitment;

import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {

  private static final int MAX_TICKET_COUNT = 25;
  private static final long ZERO = 0L;

  private final PaymentService paymentService;

  private final SeatReservationService seatReservationService;

  public CinemaTicketsServiceImpl(PaymentService paymentService,
      SeatReservationService seatReservationService) {
    this.paymentService = paymentService;
    this.seatReservationService = seatReservationService;
  }

  @Override
  public BookingConfirmation purchaseTickets(final Long accountId,
      final TicketRequest... ticketRequests)
      throws InvalidBookingException {
    if (accountId == null) {
      throw new InvalidBookingException("accountId must not be null");
    }
    if (accountId <= ZERO) {
      throw new InvalidBookingException("accountId must be greater than zero");
    }
    if (ticketRequests == null) {
      throw new InvalidBookingException("ticketRequests must not be null");
    }
    if (ticketRequests.length == 0) {
      throw new InvalidBookingException("ticketRequests must not be empty");
    }
    int totalTicketCount = 0;
    for (var ticketRequest : ticketRequests) {
      validateTicketRequest(ticketRequest);

      totalTicketCount += ticketRequest.ticketCount();
      if (totalTicketCount > MAX_TICKET_COUNT) {
        throw new InvalidBookingException(
                "total ticketCount must not be greater than " + MAX_TICKET_COUNT);
      }
    }
    return null;
  }

  private void validateTicketRequest(TicketRequest ticketRequest) {
    if (ticketRequest == null) {
      throw new InvalidBookingException("ticketRequest must not be null");
    }
    if (ticketRequest.type() == null) {
      throw new InvalidBookingException("ticketType must not be null");
    }
    if (ticketRequest.ticketCount() < ZERO) {
      throw new InvalidBookingException("ticketCount must not be less than zero");
    }
    if (ticketRequest.ticketCount() > MAX_TICKET_COUNT) {
      throw new InvalidBookingException("ticketCount must not be greater than " + MAX_TICKET_COUNT);
    }
  }
}
