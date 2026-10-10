package uk.gov.dwp.engineering.recruitment.thirdparty;

import org.junit.jupiter.api.BeforeEach;

class TicketPriceServiceImplTest {

  private TicketPriceService underTest;

  @BeforeEach
  void setup() {
    underTest = new TicketPriceServiceImpl();
  }
}
