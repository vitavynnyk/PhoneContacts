package phoneApp.service.phoneBook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ContactDto(
        @JsonProperty("id")
       String id,
       @JsonProperty("first_name")
       String firstName,
       @JsonProperty("last_name")
       String lastName,
       @JsonProperty("emails")
       List<String> emails,
       @JsonProperty("phone_numbers")
       List<String> phoneNumbers) {
}
