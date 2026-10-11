package uk.gov.dwp.engineering.recruitment.thirdparty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidPriceException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
  @Order(1)
  void shouldThrowInvalidPriceException_whenPriceForAnUnknownTicketTypeIsRequested() {
    try (var ticketTypeMockedStatic = mockStatic(TicketType.class)) {
      var unknownTicketType = mock(TicketType.class);
      doReturn(3).when(unknownTicketType).ordinal();
      ticketTypeMockedStatic.when(TicketType::values)
              .thenReturn(new TicketType[] {
                      TicketType.ADULT,
                      TicketType.CHILD,
                      TicketType.INFANT,
                      unknownTicketType });

      assertThrows(InvalidPriceException.class, () -> underTest.getPrice(unknownTicketType));
    }
  }

  @Test
  @Order(2)
  void shouldIncludeDescriptiveMessageInException_whenPriceForAnUnknownTicketTypeIsRequested() {
    try (var ticketTypeMockedStatic = mockStatic(TicketType.class)) {
      var unknownTicketType = mock(TicketType.class);
      doReturn(3).when(unknownTicketType).ordinal();
      ticketTypeMockedStatic.when(TicketType::values)
              .thenReturn(new TicketType[] {
                      TicketType.ADULT,
                      TicketType.CHILD,
                      TicketType.INFANT,
                      unknownTicketType });

      assertThrows(InvalidPriceException.class, () -> underTest.getPrice(unknownTicketType));
    } catch (InvalidPriceException e) {
      assertEquals("cannot determine price for unknown ticket type", e.getMessage());
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
