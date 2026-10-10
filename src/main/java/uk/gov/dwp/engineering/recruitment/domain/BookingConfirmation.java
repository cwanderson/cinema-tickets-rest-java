package uk.gov.dwp.engineering.recruitment.domain;

public record BookingConfirmation(Long accountId, Long seatCount) {

  @Deprecated
  public BookingConfirmation(Long accountId) {
    this(accountId, null);
  }
}
