package uk.gov.dwp.engineering.recruitment.thirdparty;

import java.math.BigDecimal;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.exception.InvalidPriceException;

public class TicketPriceServiceImpl implements TicketPriceService {

  @Override
  public BigDecimal getPrice(TicketType ticketType) {
    if (ticketType == null) {
      throw new InvalidPriceException("");
    }
    return null;
  }
}
