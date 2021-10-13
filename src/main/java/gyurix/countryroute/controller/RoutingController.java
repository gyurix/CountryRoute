package gyurix.countryroute.controller;

import gyurix.countryroute.config.AsyncConfig;
import gyurix.countryroute.dto.RoutingResponse;
import gyurix.countryroute.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static gyurix.countryroute.service.RoutingService.LOADING_DATA;

@RestController
public class RoutingController {
  @Autowired
  private AsyncConfig asyncConfig;

  @Autowired
  private RoutingService routingService;

  @GetMapping(value = "/routing/{from}/{to}", produces = "application/json")
  public RoutingResponse endpointRouting(@PathVariable String from, @PathVariable String to) {
    if (routingService.shouldFetchData()) {
      routingService.loadCountries();
      throw LOADING_DATA;
    }

    return routingService.route(from.toUpperCase(), to.toUpperCase());
  }
}
