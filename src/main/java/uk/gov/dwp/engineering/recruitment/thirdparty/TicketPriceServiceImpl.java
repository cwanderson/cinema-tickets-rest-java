package uk.gov.dwp.engineering.recruitment.thirdparty;

import java.math.BigDecimal;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidPriceException;

public class TicketPriceServiceImpl implements TicketPriceService {

  private static final BigDecimal ADULT_TICKET_PRICE = new BigDecimal("25.99");
  private static final BigDecimal CHILD_TICKET_PRICE = new BigDecimal("17.50");
  private static final BigDecimal INFANT_TICKET_PRICE = new BigDecimal("0.00");

  @Override
  public BigDecimal getPrice(TicketType ticketType) {
    if (ticketType == null) {
      throw new InvalidPriceException("ticket type must not be null");
    }
    return switch (ticketType) {
      case ADULT -> ADULT_TICKET_PRICE;
      case CHILD -> CHILD_TICKET_PRICE;
      case INFANT -> INFANT_TICKET_PRICE;
      default -> throw new InvalidPriceException("cannot determine price for unknown ticket type");
    };
  }
}
