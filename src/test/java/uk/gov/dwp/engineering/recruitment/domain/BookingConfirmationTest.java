package uk.gov.dwp.engineering.recruitment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BookingConfirmationTest {

  private BookingConfirmation underTest;

  @Test
  void givenBookingConfirmation_whenGetAccount_thenExpectedValueReturned() {
    final BookingConfirmation bookingConfirmation = new BookingConfirmation(99L);
    assertEquals(99L, bookingConfirmation.accountId());
  }

  @Test
  void shouldReturnNullSeatCount_whenBookingConfirmationUsesDeprecatedConstructor() {
    underTest = new BookingConfirmation(99L);

    assertNull(underTest.seatCount());
  }

  @Test
  void shouldReturnValueForSeatCount_whenSeatCountSetInConstructor() {
    underTest = new BookingConfirmation(99L, 1L, null);

    assertEquals(1L, underTest.seatCount());
  }

  @Test
  void shouldReturnNullTotalCost_whenBookingConfirmationUsesDeprecatedConstructor() {
    underTest = new BookingConfirmation(99L);

    assertNull(underTest.totalCost());
  }

  @Test
  void shouldReturnValueForTotalCost_whenTotalCostSetInConstructor() {
    underTest = new BookingConfirmation(99L, 1L, new BigDecimal("25.99"));

    assertEquals(new BigDecimal("25.99"), underTest.totalCost());
  }
}
