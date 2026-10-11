package uk.gov.dwp.engineering.recruitment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;
import uk.gov.dwp.engineering.recruitment.thirdparty.TicketPriceService;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {

  private static final int MAX_TICKET_COUNT = 25;
  private static final String MAX_TICKET_COUNT_EXCEEDED =
          "ticketCount must not be greater than " + MAX_TICKET_COUNT;
  private static final String MAX_TOTAL_TICKET_COUNT_EXCEEDED =
          "total ticketCount must not be greater than " + MAX_TICKET_COUNT;
  private static final long ZERO = 0L;
  private static final String ACCOUNT_ID_IS_NULL = "accountId must not be null";
  private static final String ACCOUNT_ID_LESS_THAN_ZERO = "accountId must be greater than zero";
  private static final String TICKET_REQUESTS_IS_NULL = "ticketRequests must not be null";
  private static final String TICKET_REQUESTS_IS_EMPTY = "ticketRequests must not be empty";
  private static final String TICKET_REQUEST_IS_NULL = "ticketRequest must not be null";
  private static final String TICKET_TYPE_IS_NULL = "ticketType must not be null";
  private static final String TICKET_COUNT_LESS_THAN_ZERO =
          "ticketCount must not be less than zero";
  private static final String ADULT_TICKET_COUNT_IS_ZERO =
          "adult ticketCount must be greater than zero";
  private static final String INFANT_TICKET_COUNT_EXCEEDS_ADULT_TICKET_COUNT =
          "infant ticket count must not exceed adult ticket count";

  private final PaymentService paymentService;

  private final SeatReservationService seatReservationService;

  private final TicketPriceService ticketPriceService;

  public CinemaTicketsServiceImpl(PaymentService paymentService,
                                  SeatReservationService seatReservationService,
                                  TicketPriceService ticketPriceService) {
    this.paymentService = paymentService;
    this.seatReservationService = seatReservationService;
    this.ticketPriceService = ticketPriceService;
  }

  @Override
  public BookingConfirmation purchaseTickets(final Long accountId,
      final TicketRequest... ticketRequests)
      throws InvalidBookingException {
    validateAccountId(accountId);

    validateTicketRequests(ticketRequests);

    var ticketCountsByType = countTicketsByType(ticketRequests);

    validateTicketCounts(ticketCountsByType);

    var paymentAmount = new BigDecimal("25.99")
            .multiply(new BigDecimal(ticketCountsByType.get(TicketType.ADULT)));
    paymentAmount = paymentAmount.add(new BigDecimal("17.50")
            .multiply(new BigDecimal(ticketCountsByType.get(TicketType.CHILD))));
    paymentService.debitAccount(accountId, paymentAmount);

    var seatCount = ticketCountsByType.get(TicketType.ADULT).longValue();
    seatCount += ticketCountsByType.get(TicketType.CHILD).longValue();
    seatReservationService.reserveSeats(accountId, seatCount);

    return new BookingConfirmation(accountId, seatCount, paymentAmount);
  }

  private void validateAccountId(Long accountId) {
    if (accountId == null) {
      throw new InvalidBookingException(ACCOUNT_ID_IS_NULL);
    }
    if (accountId <= ZERO) {
      throw new InvalidBookingException(ACCOUNT_ID_LESS_THAN_ZERO);
    }
  }

  private void validateTicketRequests(TicketRequest... ticketRequests) {
    if (ticketRequests == null) {
      throw new InvalidBookingException(TICKET_REQUESTS_IS_NULL);
    }
    if (ticketRequests.length == 0) {
      throw new InvalidBookingException(TICKET_REQUESTS_IS_EMPTY);
    }
    for (var ticketRequest : ticketRequests) {
      validateTicketRequest(ticketRequest);
    }
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

  private Map<TicketType, Integer> countTicketsByType(TicketRequest... ticketRequests) {
    int adultTicketCount = 0;
    int childTicketCount = 0;
    int infantTicketCount = 0;
    for (var ticketRequest : ticketRequests) {
      switch (ticketRequest.type()) {
        case ADULT -> adultTicketCount += ticketRequest.ticketCount();
        case CHILD -> childTicketCount += ticketRequest.ticketCount();
        case INFANT -> infantTicketCount += ticketRequest.ticketCount();
        case null, default -> { }
      }
    }
    return Map.of(TicketType.ADULT, adultTicketCount,
            TicketType.CHILD, childTicketCount,
            TicketType.INFANT, infantTicketCount);
  }

  private void validateTicketCounts(Map<TicketType, Integer> ticketCountsByType) {
    var totalTicketCount = new AtomicInteger();
    ticketCountsByType.forEach((t, i) -> totalTicketCount.addAndGet(i));
    if (totalTicketCount.get() > MAX_TICKET_COUNT) {
      throw new InvalidBookingException(MAX_TOTAL_TICKET_COUNT_EXCEEDED);
    }
    if (ticketCountsByType.get(TicketType.ADULT) <= 0) {
      throw new InvalidBookingException(ADULT_TICKET_COUNT_IS_ZERO);
    }
    if (ticketCountsByType.get(TicketType.INFANT) > ticketCountsByType.get(TicketType.ADULT)) {
      throw new InvalidBookingException(INFANT_TICKET_COUNT_EXCEEDS_ADULT_TICKET_COUNT);
    }
  }
}
