package gyurix.countryroute.test;

import gyurix.countryroute.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class AbstractTest {
  @Autowired
  protected MockMvc mvc;
  @Autowired
  protected RoutingService routingService;

}
