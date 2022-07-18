package restfulbooker.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BookingResp {
    @JsonProperty(value = "bookingid")
    Number bookingId;
    BookingReq booking;
}
