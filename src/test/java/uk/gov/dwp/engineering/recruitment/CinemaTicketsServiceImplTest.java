package uk.gov.dwp.engineering.recruitment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class CinemaTicketsServiceImplTest {

  private final PaymentService paymentService = mock(PaymentService.class);
  private final SeatReservationService seatReservationService = mock(SeatReservationService.class);

  private CinemaTicketsServiceImpl underTest;

  @BeforeEach
  void setup() {
   underTest = new CinemaTicketsServiceImpl(paymentService, seatReservationService);
  }

  @Nested
  class AccountIdValidationTests {

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

    @Test
    void shouldNotCallDownstreamServices_whenAccountIdIsANegativeValue_andExceptionThrown() {
      try {
        underTest.purchaseTickets(-1L);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }
  }

  @Test
  void shouldThrowInvalidBookingException_whenTicketRequestIsNull() {
    assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, (TicketRequest[]) null));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenTicketRequestsIsNull() {
    try {
      underTest.purchaseTickets(1L, (TicketRequest[]) null);
    } catch (InvalidBookingException e) {
      assertEquals("ticketRequests must not be null", e.getMessage());
    }
  }
}
