package restfulbooker.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Value
@Builder
@Jacksonized
public class BookingDatesReq {
    @JsonProperty(value = "checkin")
    Date checkIn;
    @JsonProperty(value = "checkout")
    Date checkOut;
}
