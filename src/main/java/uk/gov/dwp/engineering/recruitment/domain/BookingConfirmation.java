package uk.gov.dwp.engineering.recruitment.domain;

import java.math.BigDecimal;

public record BookingConfirmation(Long accountId, Long seatCount, BigDecimal totalCost) {

  @Deprecated
  public BookingConfirmation(Long accountId) {
    this(accountId, null, null);
  }
}
