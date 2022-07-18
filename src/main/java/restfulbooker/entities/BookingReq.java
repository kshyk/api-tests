package restfulbooker.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BookingReq {
    @JsonProperty(value = "firstname")
    @Builder.Default
    String firstName = "Mike";
    @JsonProperty(value = "lastname")
    @Builder.Default
    String lastName = "Myers";
    @JsonProperty(value = "totalprice")
    Number totalPrice;
    @JsonProperty(value = "depositpaid")
    boolean depositPaid;
    @JsonProperty(value = "bookingdates")
    BookingDatesReq bookingDates;
    @JsonProperty(value = "additionalneeds")
    String additionalNeeds;
}
