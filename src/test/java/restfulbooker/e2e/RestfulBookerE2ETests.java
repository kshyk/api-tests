package restfulbooker.e2e;

import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
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
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(SC_OK);
            softly.assertThat(response.time()).isLessThanOrEqualTo(1000L);
            booking = response.as(BookingResp.class);
            softly.assertThat(booking.getBookingId()).isNotNull().isInstanceOf(Number.class);
        });
    }

    @Test
    @Order(2)
    void bookingIdsShouldContainAlreadyCreatedBookingId() {
        var response = RestAssured.get();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(SC_OK);
            softly.assertThat(response.time()).isLessThanOrEqualTo(1000L);
            bookingIds = response.as(GetBookingIdsResp[].class);
            softly.assertThat(bookingIds.length).isGreaterThan(0);
            var optionalBookingId = Arrays.stream(bookingIds).filter(bookingId ->
                booking.getBookingId().equals(bookingId.getBookingId())).findAny();
            softly.assertThat(optionalBookingId.isPresent()).isTrue();
        });
    }

    @Test
    @Order(2)
    void itShouldReturnBookingByItsId() {
        SoftAssertions.assertSoftly(softly -> {
            var response = RestAssured.get("/" + booking.getBookingId());
            softly.assertThat(response.statusCode()).isEqualTo(SC_OK);
            softly.assertThat(response.time()).isLessThanOrEqualTo(1000L);
            var actual = response.as(BookingReq.class);
            var expected = booking.getBooking();
            softly.assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
            softly.assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
            softly.assertThat(actual.getTotalPrice()).isEqualTo(expected.getTotalPrice());
            softly.assertThat(actual.isDepositPaid()).isEqualTo(expected.isDepositPaid());
            softly.assertThat(actual.getBookingDates().getCheckIn()).isEqualTo(expected.getBookingDates().getCheckIn());
            softly.assertThat(actual.getBookingDates().getCheckOut()).isEqualTo(expected.getBookingDates().getCheckOut());
            softly.assertThat(actual.getAdditionalNeeds()).isEqualTo(expected.getAdditionalNeeds());
        });
    }

    @Test
    @Order(3)
    void itShouldBeAbleToDeleteBooking() {
        var response = RestAssured.given().contentType(JSON)
            .cookie("token", tokenizer.getToken())
            .delete("/" + booking.getBookingId().intValue());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(SC_CREATED);
            softly.assertThat(response.time()).isLessThanOrEqualTo(1000L);
        });
    }
}
