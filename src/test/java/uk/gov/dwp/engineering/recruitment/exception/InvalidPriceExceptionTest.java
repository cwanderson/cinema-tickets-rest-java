package uk.gov.dwp.engineering.recruitment.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvalidPriceExceptionTest {

  private InvalidPriceException underTest;

  @Test
  void shouldReturnExpectedMessage_whenExceptionCreated() {
    underTest = new InvalidPriceException("message");

    assertEquals("message", underTest.getMessage());
  }
}
