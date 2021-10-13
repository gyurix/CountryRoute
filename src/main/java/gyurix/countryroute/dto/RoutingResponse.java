package gyurix.countryroute.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.LinkedList;

@Getter
@ToString
public class RoutingResponse {
  private final LinkedList<String> route = new LinkedList<>();

  public void addPrev(String country) {
    route.addFirst(country);
  }
}
