package restfulbooker.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import restfulbooker.TestBase;
import restfulbooker.entities.BookingDatesReq;
import restfulbooker.entities.BookingReq;
import restfulbooker.entities.BookingResp;
import restfulbooker.entities.GetBookingIdsResp;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import static common.Constants.AVG_TIMEOUT;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;

class RestfulBookerE2ETests extends TestBase {
    private GetBookingIdsResp[] bookingIds;
    private SecureRandom random;
    private BookingResp booking;

    @BeforeAll
    void preconditions() {
        RestAssured.basePath = "/booking";
        random = new SecureRandom();
    }

    @Test
    @Order(1)
    void itShouldBeAbleToCreateBooking() {
        var zoneId = ZoneId.systemDefault();
        var checkIn = LocalDate.now().plusDays(5L);
        var checkOut = LocalDate.now().plusWeeks(2L);
        var bookingDates = BookingDatesReq.builder()
            .checkIn(Date.from(checkIn.atStartOfDay(zoneId).toInstant()))
            .checkOut(Date.from(checkOut.atStartOfDay(zoneId).toInstant()))
            .build();
        var bookingReq = BookingReq.builder()
            .totalPrice(random.nextInt(100, 500))
            .depositPaid(random.nextBoolean()).bookingDates(bookingDates)
            .additionalNeeds(random.nextBoolean() ? "All inclusive" : "Breakfast")
            .build();
        var response = RestAssured.given().contentType(JSON).body(bookingReq).post();
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        booking = response.as(BookingResp.class);
        softly.then(booking.getBookingId()).isNotNull().isInstanceOf(Number.class);
    }

    @Test
    @Order(2)
    void bookingIdsShouldContainAlreadyCreatedBookingId() {
        var response = RestAssured.get();
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        bookingIds = response.as(GetBookingIdsResp[].class);
        softly.then(bookingIds.length).isGreaterThan(0);
        var optionalBookingId = Arrays.stream(bookingIds).filter(bookingId ->
            booking.getBookingId().equals(bookingId.getBookingId())).findAny();
        softly.then(optionalBookingId.isPresent()).isTrue();
    }

    @Test
    @Order(2)
    void itShouldReturnBookingByItsId() {
        var response = RestAssured.get("/" + booking.getBookingId());
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        var actual = response.as(BookingReq.class);
        var expected = booking.getBooking();
        softly.then(actual.getFirstName()).isEqualTo(expected.getFirstName());
        softly.then(actual.getLastName()).isEqualTo(expected.getLastName());
        softly.then(actual.getTotalPrice()).isEqualTo(expected.getTotalPrice());
        softly.then(actual.isDepositPaid()).isEqualTo(expected.isDepositPaid());
        softly.then(actual.getBookingDates().getCheckIn()).isEqualTo(expected.getBookingDates().getCheckIn());
        softly.then(actual.getBookingDates().getCheckOut()).isEqualTo(expected.getBookingDates().getCheckOut());
        softly.then(actual.getAdditionalNeeds()).isEqualTo(expected.getAdditionalNeeds());
    }

    @Test
    @Order(3)
    void itShouldBeAbleToDeleteBooking() {
        var response = RestAssured.given().contentType(JSON)
            .cookie("token", tokenizer.getToken())
            .delete("/" + booking.getBookingId().intValue());
        softly.then(response.statusCode()).isEqualTo(SC_CREATED);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
    }
}
