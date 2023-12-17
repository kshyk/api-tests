package restfulbooker.e2e;

import io.restassured.RestAssured;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import restfulbooker.core.Tokenizer;
import restfulbooker.entities.BookingDatesReq;
import restfulbooker.entities.BookingReq;
import restfulbooker.entities.BookingResp;
import restfulbooker.entities.GetBookingIdsResp;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static common.Constants.Numbers.AVG_TIMEOUT;
import static common.Constants.Strings.*;
import static io.restassured.http.ContentType.JSON;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assumptions.assumeThat;
import static restfulbooker.core.EnvHolder.RESTFUL_BOOKER_PASSWORD;
import static restfulbooker.core.EnvHolder.RESTFUL_BOOKER_USERNAME;

@ExtendWith(SoftAssertionsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestfulBookerE2ETests {
    private final SecureRandom random = new SecureRandom();
    @InjectSoftAssertions
    private BDDSoftAssertions softly;
    private Tokenizer tokenizer;
    private BookingResp booking;

    @BeforeAll
    void preconditions() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        tokenizer = Tokenizer.INSTANCE;
        var body = Map.of(
            "username", RESTFUL_BOOKER_USERNAME,
            "password", RESTFUL_BOOKER_PASSWORD
        );
        var authResponse = RestAssured.given().contentType(JSON).body(body).post("/auth");
        assumeThat(authResponse.statusCode())
            .as("Unable to create token. Skipping tests...")
            .isEqualTo(SC_OK);
        tokenizer.setToken(authResponse.jsonPath().getString("token"));
        RestAssured.basePath = "/booking";
    }

    @Test
    @Order(1)
    void itShouldBeAbleToCreateBooking() {
        var bookingReq = BookingReq.builder()
            .totalPrice(random.nextInt(100, 500))
            .depositPaid(random.nextBoolean()).bookingDates(
                BookingDatesReq.builder()
                    .checkIn(LocalDate.now().plusDays(5L))
                    .checkOut(LocalDate.now().plusWeeks(2L))
                    .build())
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
    void bookingIdsShouldContainAlreadyCreatedBooking() {
        var response = RestAssured.get();
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        var bookingIds = response.as(GetBookingIdsResp[].class);
        softly.then(bookingIds.length).as("There are no bookings").isGreaterThanOrEqualTo(1);
        var optionalBookingId = Arrays.stream(bookingIds).filter(bookingId ->
            booking.getBookingId().equals(bookingId.getBookingId())).findAny();
        softly.then(optionalBookingId.isPresent()).as("Created booking ID is not present").isTrue();
    }

    @Test
    @Order(2)
    void itShouldReturnBookingByItsId() {
        var response = RestAssured.get("/" + booking.getBookingId());
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        var actual = response.as(BookingReq.class);
        var expected = booking.getBooking();
        softly.then(actual.getFirstName()).as(INCORRECT_FIRSTNAME).isEqualTo(expected.getFirstName());
        softly.then(actual.getLastName()).as(INCORRECT_LASTNAME).isEqualTo(expected.getLastName());
        softly.then(actual.getTotalPrice()).as(INCORRECT_TOTAL_PRICE).isEqualTo(expected.getTotalPrice());
        softly.then(actual.isDepositPaid()).as(INCORRECT_DEPOSIT_PAID_STATE).isEqualTo(expected.isDepositPaid());
        var actualBookingDates = actual.getBookingDates();
        var expectedBookingDates = expected.getBookingDates();
        softly.then(actualBookingDates.getCheckIn()).as(INCORRECT_CHECK_IN_DATE).isEqualTo(expectedBookingDates.getCheckIn());
        softly.then(actualBookingDates.getCheckOut()).as(INCORRECT_CHECK_OUT_DATE).isEqualTo(expectedBookingDates.getCheckOut());
        softly.then(actual.getAdditionalNeeds()).as(INCORRECT_ADDITIONAL_NEEDS).isEqualTo(expected.getAdditionalNeeds());
    }

    @Test
    @Order(3)
    void itShouldBePossibleToPartialBookingUpdate() {
        var expected = booking.getBooking().toBuilder()
            .firstName("Jason").lastName("Voorhees")
            .totalPrice(random.nextInt(501, 1000))
            .depositPaid(false)
            .additionalNeeds(random.nextBoolean() ? "Hockey mask" : "Machete")
            .build();
        var response = RestAssured.given().contentType(JSON)
            .cookie("token", tokenizer.getToken()).body(expected)
            .patch("/" + booking.getBookingId());
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        var actual = response.as(BookingReq.class);
        softly.then(actual.getFirstName()).as(INCORRECT_FIRSTNAME).isEqualTo(expected.getFirstName());
        softly.then(actual.getLastName()).as(INCORRECT_LASTNAME).isEqualTo(expected.getLastName());
        softly.then(actual.getTotalPrice()).as(INCORRECT_TOTAL_PRICE).isEqualTo(expected.getTotalPrice());
        softly.then(actual.isDepositPaid()).as(INCORRECT_DEPOSIT_PAID_STATE).isEqualTo(expected.isDepositPaid());
        var actualBookingDates = actual.getBookingDates();
        var expectedBookingDates = expected.getBookingDates();
        softly.then(actualBookingDates.getCheckIn()).as(INCORRECT_CHECK_IN_DATE).isEqualTo(expectedBookingDates.getCheckIn());
        softly.then(actualBookingDates.getCheckOut()).as(INCORRECT_CHECK_OUT_DATE).isEqualTo(expectedBookingDates.getCheckOut());
        softly.then(actual.getAdditionalNeeds()).as(INCORRECT_ADDITIONAL_NEEDS).isEqualTo(expected.getAdditionalNeeds());
    }

    @Test
    @Order(4)
    void itShouldBePossibleToUpdateEntireBooking() {
        var expected = BookingReq.builder()
            .firstName("Darth").lastName("Vader")
            .totalPrice(random.nextInt(1001, 2000))
            .depositPaid(true)
            .bookingDates(BookingDatesReq.builder()
                .checkIn(LocalDate.now().plusDays(11L))
                .checkOut(LocalDate.now().plusDays(13L))
                .build())
            .additionalNeeds(random.nextBoolean() ? "Light saber" : "Helmet")
            .build();
        var response = RestAssured.given().contentType(JSON)
            .cookie("token", tokenizer.getToken()).body(expected)
            .put("/" + booking.getBookingId());
        softly.then(response.statusCode()).isEqualTo(SC_OK);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
        var actual = response.as(BookingReq.class);
        softly.then(actual.getFirstName()).as(INCORRECT_FIRSTNAME).isEqualTo(expected.getFirstName());
        softly.then(actual.getLastName()).as(INCORRECT_LASTNAME).isEqualTo(expected.getLastName());
        softly.then(actual.getTotalPrice()).as(INCORRECT_TOTAL_PRICE).isEqualTo(expected.getTotalPrice());
        softly.then(actual.isDepositPaid()).as(INCORRECT_DEPOSIT_PAID_STATE).isEqualTo(expected.isDepositPaid());
        var actualBookingDates = actual.getBookingDates();
        var expectedBookingDates = expected.getBookingDates();
        softly.then(actualBookingDates.getCheckIn()).as(INCORRECT_CHECK_IN_DATE).isEqualTo(expectedBookingDates.getCheckIn());
        softly.then(actualBookingDates.getCheckOut()).as(INCORRECT_CHECK_OUT_DATE).isEqualTo(expectedBookingDates.getCheckOut());
        softly.then(actual.getAdditionalNeeds()).as(INCORRECT_ADDITIONAL_NEEDS).isEqualTo(expected.getAdditionalNeeds());
    }

    @Test
    @Order(5)
    void itShouldBeAbleToDeleteBooking() {
        var response = RestAssured.given().contentType(JSON)
            .cookie("token", tokenizer.getToken())
            .delete("/" + booking.getBookingId());
        softly.then(response.statusCode()).isEqualTo(SC_CREATED);
        softly.then(response.time()).isLessThanOrEqualTo(AVG_TIMEOUT);
    }
}
