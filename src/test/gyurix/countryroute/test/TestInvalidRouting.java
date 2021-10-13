package gyurix.countryroute.test;

import org.junit.Before;
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
public class TestInvalidRouting extends AbstractTest {

  @Before
  public void loadCountries() throws Exception {
    routingService.loadCountries().get();
  }

  @Test
  public void testInvalidDestination() throws Exception {
    mvc.perform(get("/routing/svk/inv"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.error").value("400 BAD_REQUEST"))
      .andExpect(jsonPath("$.reason").value("The given destination country (INV) was not found"));
  }

  @Test
  public void testInvalidOrigin() throws Exception {
    mvc.perform(get("/routing/inv/svk"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.error").value("400 BAD_REQUEST"))
      .andExpect(jsonPath("$.reason").value("The given origin country (INV) was not found"));
  }

  @Test
  public void testInvalidRoute() throws Exception {
    mvc.perform(get("/routing/svk/abw"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.error").value("400 BAD_REQUEST"))
      .andExpect(jsonPath("$.reason").value("No route can be found between countries SVK and ABW"));
  }
}
