package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

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
    assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(null));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenAccountIdIsNull() {
    try {
      underTest.purchaseTickets(null);
    } catch (InvalidBookingException e) {
      assertEquals("accountId must not be null", e.getMessage());
    }
  }

  @Test
  void shouldNotCallDownstreamServices_whenAccountIdIsNull_andExceptionThrown() {
    try {
      underTest.purchaseTickets(null);
    } catch (InvalidBookingException e) {
      verifyNoInteractions(paymentService);
      verifyNoInteractions(seatReservationService);
    }
  }

  @Test
  void shouldThrowInvalidBookingException_whenAccountIdIsZero() {
    assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(0L));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenAccountIdIsZero() {
    try {
      underTest.purchaseTickets(0L);
    } catch (InvalidBookingException e) {
      assertEquals("accountId must be greater than zero", e.getMessage());
    }
  }

  @Test
  void shouldNotCallDownstreamServices_whenAccountIdIsZero_andExceptionThrown() {
    try {
      underTest.purchaseTickets(0L);
    } catch (InvalidBookingException e) {
      verifyNoInteractions(paymentService);
      verifyNoInteractions(seatReservationService);
    }
  }

  @Test
  void shouldThrowInvalidBookingException_whenAccountIdIsANegativeValue() {
    assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(-1L));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenAccountIdIsANegativeValue() {
    try {
      underTest.purchaseTickets(-1L);
    } catch (InvalidBookingException e) {
      assertEquals("accountId must be greater than zero", e.getMessage());
    }
  }
}
