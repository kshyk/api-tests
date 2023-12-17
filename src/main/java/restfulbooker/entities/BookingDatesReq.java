package restfulbooker.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Value
@Builder
@Jacksonized
public class BookingDatesReq {
    @JsonProperty(value = "checkin")
    LocalDate checkIn;
    @JsonProperty(value = "checkout")
    LocalDate checkOut;
}
