package gyurix.countryroute.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gyurix.countryroute.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoutingController {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private RoutingService routingService;

  @GetMapping(value = "/routing/{from}/{to}", produces = "application/json")
  public String endpointRouting(@PathVariable String from, @PathVariable String to) throws JsonProcessingException {
    return "{\"route\":" + objectMapper.writeValueAsString(routingService.route(from, to)) + "}";
  }
}
