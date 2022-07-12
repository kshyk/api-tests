package restfulbooker.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GetBookingIdsResp {
    @JsonProperty(value = "bookingid")
    Number bookingId;
}
