package gyurix.countryroute.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CountryData {
  private String[] borders;
  @JsonProperty("cca3")
  private String name;
}
