package uk.gov.dwp.engineering.recruitment.exception;

public class InvalidPriceException extends RuntimeException {

  public InvalidPriceException(String message) {
    super(message);
  }
}
