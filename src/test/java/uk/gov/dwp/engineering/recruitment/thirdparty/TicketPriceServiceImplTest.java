package uk.gov.dwp.engineering.recruitment.thirdparty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidPriceException;

class TicketPriceServiceImplTest {

  private TicketPriceService underTest;

  @BeforeEach
  void setup() {
    underTest = new TicketPriceServiceImpl();
  }

  @Test
  void shouldThrowInvalidPriceException_whenThePriceOfANullTicketTypeIsRequested() {
    assertThrows(InvalidPriceException.class, () -> underTest.getPrice(null));
  }

  @Test
  void shouldIncludeDescriptiveMessageInException_whenThePriceOfANullTicketTypeIsRequested() {
    try {
      underTest.getPrice(null);
    } catch (InvalidPriceException e) {
      assertEquals("ticket type must not be null", e.getMessage());
    }
  }

  @Test
  void shouldReturnCorrectPrice_whenPriceForAdultTicketTypeRequested() {
    var actual = underTest.getPrice(TicketType.ADULT);

    assertEquals(new BigDecimal("25.99"), actual);
  }

  @Test
  void shouldReturnCorrectPrice_whenPriceForChildTicketTypeRequested() {
    var actual = underTest.getPrice(TicketType.CHILD);

    assertEquals(new BigDecimal("17.50"), actual);
  }

  @Test
  void shouldReturnCorrectPrice_whenPriceForInfantTicketTypeRequested() {
    var actual = underTest.getPrice(TicketType.INFANT);

    assertEquals(new BigDecimal("0.00"), actual);
  }
}
