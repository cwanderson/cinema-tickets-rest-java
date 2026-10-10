package uk.gov.dwp.engineering.recruitment.thirdparty;

import java.math.BigDecimal;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;

public interface TicketPriceService {

  BigDecimal getPrice(TicketType ticketType);
}
