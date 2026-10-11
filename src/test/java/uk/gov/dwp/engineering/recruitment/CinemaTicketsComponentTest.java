package uk.gov.dwp.engineering.recruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
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
    var adultTicketRequest = new TicketRequest(TicketType.ADULT, 1);
    var booking = new Booking(1L, adultTicketRequest);

    var response = mockMvc.perform(post("/cinema/bookings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(booking)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    var contentAsString = response.getResponse().getContentAsString();
    var bookingConfirmation = objectMapper.readValue(contentAsString, BookingConfirmation.class);
    assertEquals(1L, bookingConfirmation.accountId());
    assertEquals(1L, bookingConfirmation.seatCount());
    assertEquals(new BigDecimal("25.99"), bookingConfirmation.totalCost());
  }
}
