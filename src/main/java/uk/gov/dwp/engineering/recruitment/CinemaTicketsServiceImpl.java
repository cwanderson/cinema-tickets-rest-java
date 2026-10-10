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
  private static final String MAX_TICKET_COUNT_EXCEEDED = "ticketCount must not be greater than " + MAX_TICKET_COUNT;
  private static final String MAX_TOTAL_TICKET_COUNT_EXCEEDED = "total ticketCount must not be greater than " + MAX_TICKET_COUNT;
  private static final long ZERO = 0L;
  private static final String ACCOUNT_ID_IS_NULL = "accountId must not be null";
  private static final String ACCOUNT_ID_LESS_THAN_ZERO = "accountId must be greater than zero";
  private static final String TICKET_REQUESTS_IS_NULL = "ticketRequests must not be null";
  private static final String TICKET_REQUESTS_IS_EMPTY = "ticketRequests must not be empty";
  private static final String TICKET_REQUEST_IS_NULL = "ticketRequest must not be null";
  private static final String TICKET_TYPE_IS_NULL = "ticketType must not be null";
  private static final String TICKET_COUNT_LESS_THAN_ZERO = "ticketCount must not be less than zero";

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
      throw new InvalidBookingException(ACCOUNT_ID_IS_NULL);
    }
    if (accountId <= ZERO) {
      throw new InvalidBookingException(ACCOUNT_ID_LESS_THAN_ZERO);
    }
    if (ticketRequests == null) {
      throw new InvalidBookingException(TICKET_REQUESTS_IS_NULL);
    }
    if (ticketRequests.length == 0) {
      throw new InvalidBookingException(TICKET_REQUESTS_IS_EMPTY);
    }
    int totalTicketCount = 0;
    for (var ticketRequest : ticketRequests) {
      validateTicketRequest(ticketRequest);

      totalTicketCount += ticketRequest.ticketCount();
      if (totalTicketCount > MAX_TICKET_COUNT) {
        throw new InvalidBookingException(MAX_TOTAL_TICKET_COUNT_EXCEEDED);
      }
    }
    return null;
  }

  private void validateTicketRequest(TicketRequest ticketRequest) {
    if (ticketRequest == null) {
      throw new InvalidBookingException(TICKET_REQUEST_IS_NULL);
    }
    if (ticketRequest.type() == null) {
      throw new InvalidBookingException(TICKET_TYPE_IS_NULL);
    }
    if (ticketRequest.ticketCount() < ZERO) {
      throw new InvalidBookingException(TICKET_COUNT_LESS_THAN_ZERO);
    }
    if (ticketRequest.ticketCount() > MAX_TICKET_COUNT) {
      throw new InvalidBookingException(MAX_TICKET_COUNT_EXCEEDED);
    }
  }
}
