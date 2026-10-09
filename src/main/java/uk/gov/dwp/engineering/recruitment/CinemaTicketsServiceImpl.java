package uk.gov.dwp.engineering.recruitment;

import org.springframework.stereotype.Service;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

@Service
public class CinemaTicketsServiceImpl implements CinemaTicketsService {

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
      throw new InvalidBookingException("");
    }
    return null;
  }
}
