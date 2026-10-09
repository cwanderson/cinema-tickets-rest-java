package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class CinemaTicketsServiceImplTest {

  public final PaymentService paymentService = mock(PaymentService.class);
  public final SeatReservationService seatReservationService = mock(SeatReservationService.class);

  public CinemaTicketsServiceImpl underTest;

  @BeforeEach
  void setup() {
   underTest = new CinemaTicketsServiceImpl(paymentService, seatReservationService);
  }

  @Test
  void shouldThrowInvalidBookingException_whenAccountIdIsNull() {
    assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(null, new TicketRequest[]{}));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenAccountIdIsNull() {
    try {
      underTest.purchaseTickets(null);
    } catch (InvalidBookingException e) {
      assertEquals("accountId must not be null", e.getMessage());
    }
  }
}
