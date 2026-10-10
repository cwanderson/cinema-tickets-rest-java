package uk.gov.dwp.engineering.recruitment.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestExceptionHandlerTest {

  private RestExceptionHandler underTest;

  @BeforeEach
  void setup() {
    underTest = new RestExceptionHandler();
  }

  @Test
  void shouldReturnStatusOfNotImplemented_whenInvalidBookingExceptionIsHandled() {
    var problemDetail = underTest.handleInvalidBookingException(new InvalidBookingException("Not implemented yet."));

    assertEquals(501, problemDetail.getStatus());
  }

  @Test
  void shouldIncludeMessageFromInvalidBookingException_whenInvalidBookingExceptionIsHandled() {
    var problemDetail = underTest.handleInvalidBookingException(new InvalidBookingException("Not implemented yet."));

    assertEquals("Not implemented yet.", problemDetail.getDetail());
  }
}
