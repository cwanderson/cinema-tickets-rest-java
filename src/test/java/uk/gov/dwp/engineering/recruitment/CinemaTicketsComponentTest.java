package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import uk.gov.dwp.engineering.recruitment.domain.Booking;
import uk.gov.dwp.engineering.recruitment.domain.BookingConfirmation;
import uk.gov.dwp.engineering.recruitment.domain.TicketRequest;
import uk.gov.dwp.engineering.recruitment.domain.TicketType;
import uk.gov.dwp.engineering.recruitment.thirdparty.PaymentService;
import uk.gov.dwp.engineering.recruitment.thirdparty.SeatReservationService;
import uk.gov.dwp.engineering.recruitment.thirdparty.TicketPriceService;

@SpringBootTest
@AutoConfigureMockMvc
class CinemaTicketsComponentTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockitoBean
  PaymentService paymentService;
  @MockitoBean
  SeatReservationService seatReservationService;
  @MockitoBean
  TicketPriceService ticketPriceService;

  @BeforeEach
  void setup() {
    when(ticketPriceService.getPrice(TicketType.ADULT)).thenReturn(new BigDecimal("25.99"));
    when(ticketPriceService.getPrice(TicketType.CHILD)).thenReturn(new BigDecimal("17.50"));
    when(ticketPriceService.getPrice(TicketType.INFANT)).thenReturn(new BigDecimal("0.00"));
  }

  @Test
  void givenABookingIsMadeForOneAdultTicket_whenTheBookingIsRequested_thenTheBookingConfirmationIsReturned()
          throws Exception {
    var expected = new BookingConfirmation(1L, 1L, new BigDecimal("25.99"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(expected, bookingConfirmation);

    verify(paymentService).debitAccount(1L, new BigDecimal("25.99"));
    verify(seatReservationService).reserveSeats(1L, 1L);
  }

  @Test
  void givenABookingForOneOfEachTypeOfTicket_whenTheBookingIsRequested_thenTheBookingConfirmationIsReturned() throws Exception {
    var expected = new BookingConfirmation(1L, 2L, new BigDecimal("43.49"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 1);
    var infantTicketRequest = new TicketRequest(TicketType.INFANT, 1);
    var booking = new Booking(1L, adultTicketRequest, childTicketRequest, infantTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(expected, bookingConfirmation);

    verify(paymentService).debitAccount(1L, new BigDecimal("43.49"));
    verify(seatReservationService).reserveSeats(1L, 2L);
  }

  @Test
  void givenABookingWithMultipleTicketRequests_whenTheBookingIsRequested_thenTheBookingConfirmationIsReturned() throws Exception {
    var expected = new BookingConfirmation(1L, 6L, new BigDecimal("138.96"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 1);
    var infantTicketRequest = new TicketRequest(TicketType.INFANT, 1);
    var booking = new Booking(1L, adultTicketRequest,
            childTicketRequest,
            infantTicketRequest,
            adultTicketRequest,
            adultTicketRequest,
            childTicketRequest,
            adultTicketRequest,
            infantTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(expected, bookingConfirmation);

    verify(paymentService).debitAccount(1L, new BigDecimal("138.96"));
    verify(seatReservationService).reserveSeats(1L, 6L);
  }

  @Test
  void givenABookingWithTheMaximumNumberOfTickets_whenTheBookingIsRequested_thenTheBookingConfirmationIsReturned() throws Exception {
    var expected = new BookingConfirmation(1L, 25L, new BigDecimal("649.75"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 25);
    var booking = new Booking(1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(expected, bookingConfirmation);

    verify(paymentService).debitAccount(1L, new BigDecimal("649.75"));
    verify(seatReservationService).reserveSeats(1L, 25L);
  }

  @Test
  void givenABookingWithAZeroTicketRequest_whenTheBookingIsRequested_thenTheBookingConfirmationIsReturned() throws Exception {
    var expected = new BookingConfirmation(1L, 1L, new BigDecimal("25.99"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 0);
    var booking = new Booking(1L, adultTicketRequest, childTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(expected, bookingConfirmation);

    verify(paymentService).debitAccount(1L, new BigDecimal("25.99"));
    verify(seatReservationService).reserveSeats(1L, 1L);
  }

  @Test
  void givenABookingForAnInfantTicketWithoutAnAdultTicket_whenTheBookingIsRequested_thenAnErrorResponseIsReturned()
          throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "adult ticketCount must be greater than zero");
    expected.setInstance(new URI("/cinema/bookings"));
    var infantTicketRequest = new TicketRequest(TicketType.INFANT, 1);
    var booking = new Booking(1L, infantTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingForAChildTicketWithoutAnAdultTicket_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "adult ticketCount must be greater than zero");
    expected.setInstance(new URI("/cinema/bookings"));
    var childTicketRequest = new TicketRequest(TicketType.CHILD, 1);
    var booking = new Booking(1L, childTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingHasMoreInfantTicketsThanThereAreAdultTickets_whenTheBookingIsRequested_thenAnErrorResponseIsReturned()
          throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "infant ticket count must not exceed adult ticket count");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var infantTicketRequest = new TicketRequest(TicketType.INFANT, 1);
    var booking = new Booking(1L, infantTicketRequest, adultTicketRequest, infantTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingIsMadeWithAnUnknownTicketType_whenTheBookingIsRequested_thenAnErrorResponseIsReturned()
          throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Failed to read request");
    expected.setInstance(new URI("/cinema/bookings"));
    var ticketRequest = objectMapper.createObjectNode()
            .put("type", "UNKNOWN")
            .put("ticketCount", 1);
    var ticketRequests = objectMapper.createArrayNode()
            .add(ticketRequest);
    var booking = objectMapper.createObjectNode()
            .put("accountId", 1L)
            .set("ticketRequests", ticketRequests);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(booking.toString()))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingExceedsTheMaximumNumberOfTicketsInASingleRequest_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketCount must not be greater than 25");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 26);
    var booking = new Booking(1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingExceedsTheMaximumNumberOfTicketsAcrossAllRequests_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "total ticketCount must not be greater than 25");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 13);
    var childTicketRequest = new TicketRequest(TicketType.ADULT, 13);
    var booking = new Booking(1L, adultTicketRequest, childTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANegativeCount_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketCount must not be less than zero");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var childTicketRequest = new TicketRequest(TicketType.CHILD, -1);
    var booking = new Booking(1L, adultTicketRequest, childTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithAnAccountIdOfZero_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "accountId must be greater than zero");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(0L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANegativeAccountId_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "accountId must be greater than zero");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(-1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANullAccountId_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "accountId must not be null");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(null, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithNoTicketRequests_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketRequests must not be null");
    expected.setInstance(new URI("/cinema/bookings"));
    var booking = new Booking(1L, (TicketRequest[]) null);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANullTicketType_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketType must not be null");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(null, 1);
    var booking = new Booking(1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANullTicketCount_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Failed to read request");
    expected.setInstance(new URI("/cinema/bookings"));
    var ticketRequest = objectMapper.createObjectNode()
            .put("type", "ADULT")
            .set("ticketCount", null);
    var ticketRequests = objectMapper.createArrayNode()
            .add(ticketRequest);
    var booking = objectMapper.createObjectNode()
            .put("accountId", 1L)
            .set("ticketRequests", ticketRequests);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithANullTicketRequest_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketRequest must not be null");
    expected.setInstance(new URI("/cinema/bookings"));
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(1L, adultTicketRequest, null);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }

  @Test
  void givenABookingWithAnEmptyTicketRequest_whenTheBookingIsRequested_thenAnErrorResponseIsReturned() throws Exception {
    var expected = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "ticketRequests must not be empty");
    expected.setInstance(new URI("/cinema/bookings"));
    var booking = new Booking(1L);

    var response = mockMvc.perform(post("/cinema/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(booking)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var problemDetail = objectMapper.readValue(contentAsString, ProblemDetail.class);
    assertEquals(expected, problemDetail);

    verifyNoInteractions(paymentService);
    verifyNoInteractions(seatReservationService);
  }
}
