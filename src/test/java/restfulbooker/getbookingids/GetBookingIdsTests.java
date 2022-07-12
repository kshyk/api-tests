package restfulbooker.getbookingids;

import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import restfulbooker.TestBase;
import restfulbooker.entities.GetBookingIdsResp;

import static org.apache.http.HttpStatus.SC_OK;

class GetBookingIdsTests extends TestBase {
    @BeforeAll
    void preconditions() {
        RestAssured.basePath = "/booking";
    }

    @Test
    void apiShouldReturnAllExistingBookingIds() {
        var response = RestAssured.given().get();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(SC_OK);
            softly.assertThat(response.time()).isLessThanOrEqualTo(1000L);
            var bookingIds = response.as(GetBookingIdsResp[].class);
            softly.assertThat(bookingIds.length).isGreaterThan(0);
        });
    }
}
