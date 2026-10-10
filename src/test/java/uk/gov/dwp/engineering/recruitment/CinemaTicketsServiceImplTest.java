package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidBookingException;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;

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

  @Nested
  class TicketRequestsValidationTests {

    @Test
    void shouldThrowInvalidBookingException_whenTicketRequestsIsNull() {
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

    @Test
    void shouldNotCallDownstreamServices_whenTicketRequestsIsNull_andExceptionThrown() {
      try {
        underTest.purchaseTickets(1L, (TicketRequest[]) null);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @Test
    void shouldThrowInvalidBookingException_whenTicketRequestsIsEmpty() {
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTicketRequestsIsEmpty() {
      try {
        underTest.purchaseTickets(1L);
      } catch (InvalidBookingException e) {
        assertEquals("ticketRequests must not be empty", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTicketRequestsIsEmpty_andExceptionThrown() {
      try {
        underTest.purchaseTickets(1L);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @Test
    void shouldThrowInvalidBookingException_whenTicketRequestsContainsANullTicketRequest() {
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, new TicketRequest[]{null}));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTicketRequestsContainsANullTicketRequest() {
      try {
        underTest.purchaseTickets(1L, new TicketRequest[]{null});
      } catch (InvalidBookingException e) {
        assertEquals("ticketRequest must not be null", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTicketRequestsContainsANullTicketRequest_andExceptionThrown() {
      try {
        underTest.purchaseTickets(1L, new TicketRequest[]{null});
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }
  }

  @Nested
  class TicketRequestValidationTests {

    @Test
    void shouldThrowInvalidBookingException_whenTicketTypeInTicketRequestIsNull() {
      var ticketRequest = new TicketRequest(null, 0);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, ticketRequest));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTicketTypeInTicketRequestIsNull() {
      var ticketRequest = new TicketRequest(null, 0);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("ticketType must not be null", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTicketTypeInTicketRequestIsNull_andExceptionThrown() {
      var ticketRequest = new TicketRequest(null, 0);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @Test
    void shouldThrowInvalidBookingException_whenTicketCountInTicketRequestIsLessThaZero() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, -1);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, ticketRequest));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTicketCountInTicketRequestIsLessThaZero() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, -1);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("ticketCount must not be less than zero", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTicketCountInTicketRequestIsLessThaZero_andExceptionThrown() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, -1);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @Test
    void shouldThrowInvalidBookingException_whenTicketCountInTicketRequestIsGreaterThanTwentyFive() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 26);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, ticketRequest));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTicketCountInTicketRequestIsGreaterThanTwentyFive() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 26);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("ticketCount must not be greater than 25", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTicketCountInTicketRequestIsGreaterThanTwentyFive_andExceptionThrown() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 26);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }
  }

  @Nested
  class TicketCountValidationTests {

    @Test
    void shouldThrowInvalidBookingException_whenTotalTicketCountRequestedIsGreaterThanTwentyFive() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 13);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, ticketRequest, ticketRequest));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenTotalTicketCountRequestedIsGreaterThanTwentyFive() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 13);
      try {
        underTest.purchaseTickets(1L, ticketRequest, ticketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("total ticketCount must not be greater than 25", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenTotalTicketCountRequestedIsGreaterThanTwentyFive_andExceptionThrown() {
      var ticketRequest = new TicketRequest(TicketType.ADULT, 13);
      try {
        underTest.purchaseTickets(1L, ticketRequest, ticketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @ParameterizedTest
    @EnumSource(names = {"CHILD", "INFANT"})
    void shouldThrowInvalidBookingException_whenNoAdultTicketIsRequested(TicketType ticketType) {
      var ticketRequest = new TicketRequest(ticketType, 1);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, ticketRequest));
    }

    @ParameterizedTest
    @EnumSource(names = {"CHILD", "INFANT"})
    void shouldIncludeDescriptiveMessageInException_whenNoAdultTicketIsRequested(TicketType ticketType) {
      var ticketRequest = new TicketRequest(ticketType, 1);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("adult ticketCount must be greater than zero", e.getMessage());
      }
    }

    @ParameterizedTest
    @EnumSource(names = {"CHILD", "INFANT"})
    void shouldNotCallDownstreamServices_whenNoAdultTicketIsRequested_andExceptionThrown(TicketType ticketType) {
      var ticketRequest = new TicketRequest(ticketType, 1);
      try {
        underTest.purchaseTickets(1L, ticketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }

    @Test
    void shouldThrowInvalidBookingException_whenInfantTicketsExceedsAdultTicketsRequested() {
      var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
      var infantTicketRequest = new TicketRequest(TicketType.INFANT, 2);
      assertThrows(InvalidBookingException.class, () -> underTest.purchaseTickets(1L, infantTicketRequest, adultTicketRequest));
    }

    @Test
    void shouldIncludeDescriptiveMessageInException_whenInfantTicketsExceedsAdultTicketsRequested() {
      var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
      var infantTicketRequest = new TicketRequest(TicketType.INFANT, 2);
      try {
        underTest.purchaseTickets(1L, infantTicketRequest, adultTicketRequest);
      } catch (InvalidBookingException e) {
        assertEquals("infant ticket count must not exceed adult ticket count", e.getMessage());
      }
    }

    @Test
    void shouldNotCallDownstreamServices_whenInfantTicketsExceedsAdultTicketsRequested_andExceptionThrown() {
      var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
      var infantTicketRequest = new TicketRequest(TicketType.INFANT, 2);
      try {
        underTest.purchaseTickets(1L, infantTicketRequest, adultTicketRequest);
      } catch (InvalidBookingException e) {
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
      }
    }
  }

  @Test
  void shouldCallPaymentServiceWithAccountIdAndCorrectAmount_whenOneAdultTicketIsRequested() {
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);

    underTest.purchaseTickets(1L, adultTicketRequest);

    verify(paymentService).debitAccount(1L, new BigDecimal("25.99"));
  }

  @Test
  void shouldCallPaymentServiceWithAccountIdAndCorrectAmount_whenOneAdultTicketAndOneChildTicketIsRequested() {
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 1);

    underTest.purchaseTickets(1L, adultTicketRequest, childTicketRequest);

    verify(paymentService).debitAccount(1L, new BigDecimal("43.49"));
  }

  @Test
  void shouldCallPaymentServiceWithAccountIdAndCorrectAmount_whenEightOfEachTypeOfTicketIsRequested() {
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 8);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 8);
    var infantTicketRequest = new TicketRequest(TicketType.INFANT, 8);
    var expectedAmount = new BigDecimal("43.49").multiply(new BigDecimal(8));

    underTest.purchaseTickets(1L, adultTicketRequest, childTicketRequest, infantTicketRequest);

    verify(paymentService).debitAccount(1L, expectedAmount);
  }

  @Test
  void shouldCallSeatReservationServiceWithAccountIdAndCorrectSeatCount_whenOneAdultTicketIsRequested() {
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);

    underTest.purchaseTickets(1L, adultTicketRequest);

    verify(seatReservationService).reserveSeats(1L, 1L);
  }
}
