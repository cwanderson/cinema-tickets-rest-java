# Cinema Tickets Code Test

## Contents

-   [Supplied Objective](#objective)
    -   [Business Rules](#business-rules)
    -   [Constraints](#constraints)
    -   [Assumptions](#assumptions)
    -   [Your Task](#your-task)
-   [My Solution](#my-solution)
    -   [My Assumptions](#my-assumptions)
    -   [Validation Rules](#validation-rules)
    -   [Approach Taken](#approach-taken)
    -   [Running the Service](#running-the-service)

## Objective

This is a coding exercise which will allow you to demonstrate how you code and your approach to a given problem.

You will be assessed on:

-   Your ability to write clean, well-tested and reusable code.
-   How you have understood, interpreted the business rules and ensured they are correctly met.
-   Identifying the ambiguity within the business rules and handle accordingly.

[Back to top](#cinema-tickets-code-test)

### Business Rules

-   There are 3 types of tickets i.e. INFANT, CHILD, and ADULT.
-   The ticket prices are based on the type of ticket (see table below).
-   The ticket purchaser declares how many and what type of tickets they want to buy.
-   Multiple tickets can be purchased at any given time.
-   Only a maximum of 25 tickets that can be purchased at a time.
-   An INFANT does not pay for a ticket and are not allocated a seat. They will be sitting on an ADULT lap.
-   CHILD and INFANT tickets cannot be purchased without purchasing an ADULT ticket.

| Ticket Type | Price  |
| ----------- | ------ |
| INFANT      | £0     |
| CHILD       | £17.50 |
| ADULT       | £25.99 |

-   There is an existing `PaymentService` responsible for taking payments.
-   There is an existing `SeatReservationService` responsible for reserving seats.

[Back to top](#cinema-tickets-code-test)

### Constraints

-   The `CinemaTicketsService` interface MUST not be modified.
-   The code in `uk.gov.dwp.engineering.recruitment.domain` package MUST not be modified with the exception of BookingConfirmation, which may be extended in a non-breaking manner if you choose to do so.

[Back to top](#cinema-tickets-code-test)

### Assumptions

You can assume:

-   All accounts with an id greater than zero are valid. They also have sufficient funds to pay for any number of tickets.
-   The `PaymentService` implementation is an external provider with no defects.
-   You do not need to worry about how the actual payment happens or integrating that service.
-   The payment will always go through once a payment request has been made to the `PaymentService`.
-   The `SeatReservationService` implementation is an external provider with no defects.
-   You do not need to worry about how the seat reservation algorithm works or integrating that service.
-   The seats will always be reserved once a reservation request has been made to the `SeatReservationService`.

Any assumptions outside of the list above should be correctly and thoroughly documented.

[Back to top](#cinema-tickets-code-test)

### Your Task

Provide a working implementation of `CinemaTicketsServiceImpl` that:

-   Considers the above objective, business rules, constraints & assumptions.
-   Calculates the correct amount for the requested tickets and makes a payment request to the `PaymentService`.
-   Calculates the correct number of seats to reserve and makes a seat reservation request to the `SeatReservationService`.
-   Rejects any invalid ticket purchase requests.
-   It is up to you to identify what should be deemed as an invalid purchase request.
-   Please ensure you record your approach and any assumptions you make in the `README.md` of your final solution and within your submitted repository.
-   Ensure appropriate error handling and testing is implemented for the 'CinemaTicketsController' referring to all business rules and rejected ticket requests.

[Back to top](#cinema-tickets-code-test)

## My Solution

### My Assumptions

I have made the following assumptions:

-   An INFANT must sit on an ADULT lap.
-   An ADULT can only have one INFANT sitting on their lap.
-   The supplied business rules state that `an INFANT does not pay for a ticket` and that `CHILD and INFANT tickets cannot be purchased without purchasing an ADULT ticket`. To me, these statements are ambiguous as it could just mean that
as the INFANT ticket price is currently £0.00 then the INFANT ticket is not paid for but is still purchased. With that in mind, I have assumed that as an INFANT will be sitting on an ADULT lap:
    -   any INFANT tickets purchased will be included in the cost calculation.
    -   any INFANT tickets purchased will count towards the number of tickets purchased.
-   There are no limits placed on the ratio of CHILD to ADULT tickets, so it will be possible to purchase one ADULT tickets and twenty-four CHILD tickets.
-   The `PaymentService` is called before the `SeatReservationService`.
-   No error checking/recovery is required for the `PaymentService`.
-   No error checking/recovery is required for the `SeatReservationService`.
-   The ticket prices are not dynamic, but fixed at `£25.99` for an `ADULT` ticket `£17.50` for a `CHILD` ticket and `£0.00` for an `INFANT` ticket.

Under normal circumstances I would seek clarification of these assumptions at a story refinement or three amigos meeting.

[Back to top](#cinema-tickets-code-test)

### Validation Rules

I have applied the following validation rules taking into consideration the stated business rules and assumptions, the already defined contract specification, and my own assumptions about the requirements:

-   The accountId must not be null.
-   The accountId must be greater than zero.
-   The ticketRequests array must not be null.
-   The ticketRequests array must not contain a null ticketRequest.
-   The ticketRequests array must not be empty.
-   A ticketRequest must not have a null ticketType.
-   A ticketRequest ticketType must be one of `ADULT`, `CHILD` or `INFANT`.
-   A ticketRequest must not have a ticketCount of less than zero. This is based on the contract specification.
-   The total adult ticket count must be greater than zero.
-   The total infant ticket count must not be greater than the total adult ticket count.
-   The total ticket count must not be less than one.
-   The total ticket count must not exceed 25.
-   No validation required on the response from the `PaymentService`.
-   No validation required on the response from the `SeatReservationService`.

[Back to top](#cinema-tickets-code-test)

### Approach Taken

-   Code/test quality (`Checkstyle`;`PMD`;`PiTest`;`SpotBugs`) and test coverage (`Jacoco`) tools were added to the project and configured to run on every `mvn clean verify`.
-   All code was written using a TDD approach.
-   Baseline tests were written for the code that didn't have any coverage before implementing any of the requirements.
-   Validation scenarios were added first covering all the validation rules.
-   With the validation scenarios covered I moved on the ticket cost calculation and then to the seat count calculation.
-   The ticket cost and seat count calculations are kept within the `CinemaTicketsServiceImpl` class as they are fairly simple and don't make the class feel too bloated ast this stage.
-   Instead of hard-coding the ticket prices in the `CinemaTicketsServiceImpl` class I extract this out to a `TicketPriceService`. This was after noticing that the code I had written
for the cost calculation wasn't counting any `INFANT` tickets. This was because they are priced at `£0.00` and the unit tests that checked the prices all passed as it wouldn't make any
difference if they were being counted or not. By mocking the `TicketPriceService` I could alter the `INFANT` ticket price in the `CinemaTicketsServiceImpl` unit tests and prove
that `INFANT` tickets were now being included in the ticket cost calculation.
-   Finally, the `BookingConfirmation` was tested.
-   The basic logic of the `CinemaTicketsController` was tested in unit test as part of the baseline tests.
-   With the unit tests all green and the service implementation completed I moved on to writing a component test to exercise the validation and business rules against the `CinemaTicketsController`.
The component test, `CinemaTicketsComponentTest`, runs using the `@SpringBootTest` annotation, instantiating a real `CinemaTicketsController`, injecting a real `CinemaTicketsServiceImpl` and
injecting mocks of the `PaymentService`, `SeatReservationService` and `TicketPriceService` into the `CinemaTicketsServiceImpl`. A `MockMvc` object is autowired to allow HTTP requests to be
made to the `CinemaTicketsController` and the resulting responses to then be asserted against.

[Back to top](#cinema-tickets-code-test)

### Running the Service

Requires JDK 21 and Maven 3.10+.

This assumes that you already have the code repository cloned to a local directory.

```shell
mvn clean verify
mvn spring-boot:run
```

This will start the service and will be available at <http://localhost:8080>

Example successful booking request:

```shell
curl -X 'POST' \
  'http://localhost:8080/cinema/bookings' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "accountId": 1234,
  "ticketRequests": [
    {
      "type": "ADULT",
      "ticketCount": 1
    }
  ]
}'
```

Example successful response:

```shell
{
  "accountId": 1234,
  "seatCount": 1,
  "totalCost": 25.99
}
```

Example invalid booking request:

```shell
curl -X 'POST' \
  'http://localhost:8080/cinema/bookings' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "accountId": 1234,
  "ticketRequests": [
    {
      "type": "ADULT",
      "ticketCount": 0
    }
  ]
}'
```

Example invalid booking response:

```shell
{
  "detail":"adult ticketCount must be greater than zero",
  "instance":"/cinema/bookings",
  "status":400,
  "title":"Bad Request"
}
```

[Back to top](#cinema-tickets-code-test)
