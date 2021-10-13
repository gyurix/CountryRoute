package gyurix.countryroute.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "routing.load_at_startup=false")
public class TestCountryLoading extends AbstractTest {
  @Test
  public void testCountryLoading() throws Exception {
    assert routingService.loadCountries().get();
  }

  @Test
  public void testServiceUnavailable() throws Exception {
    mvc.perform(get("/routing/svk/deu"))
      .andDo(print())
      .andExpect(status().isServiceUnavailable())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.error").value("503 SERVICE_UNAVAILABLE"))
      .andExpect(jsonPath("$.reason").value("Still loading country border data, try again later"));
  }
}
