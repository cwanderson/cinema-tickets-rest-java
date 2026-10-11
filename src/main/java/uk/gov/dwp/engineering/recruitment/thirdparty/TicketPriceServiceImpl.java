package uk.gov.dwp.engineering.recruitment.thirdparty;

import java.math.BigDecimal;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidPriceException;

public class TicketPriceServiceImpl implements TicketPriceService {

  @Override
  public BigDecimal getPrice(TicketType ticketType) {
    if (ticketType == null) {
      throw new InvalidPriceException("ticket type must not be null");
    }
    return switch (ticketType) {
      case ADULT -> new BigDecimal("25.99");
      case CHILD -> new BigDecimal("17.50");
      case INFANT -> new BigDecimal("0.00");
      default -> throw new InvalidPriceException("cannot determine price for unknown ticket type");
    };
  }
}
