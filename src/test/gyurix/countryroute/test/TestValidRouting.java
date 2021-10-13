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
public class TestValidRouting extends AbstractTest {

  @Before
  public void loadCountries() throws Exception {
    routingService.loadCountries().get();
  }

  @Test
  public void testValidRouteCzeIta() throws Exception {
    mvc.perform(get("/routing/cze/ita"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.route.length()").value(3))
      .andExpect(jsonPath("$.route[0]").value("CZE"))
      .andExpect(jsonPath("$.route[1]").value("AUT"))
      .andExpect(jsonPath("$.route[2]").value("ITA"));
  }

  @Test
  public void testValidRouteSsdHun() throws Exception {
    mvc.perform(get("/routing/ssd/hun"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.route.length()").value(9))
      .andExpect(jsonPath("$.route[0]").value("SSD"))
      .andExpect(jsonPath("$.route[1]").value("SDN"))
      .andExpect(jsonPath("$.route[2]").value("EGY"))
      .andExpect(jsonPath("$.route[3]").value("ISR"))
      .andExpect(jsonPath("$.route[4]").value("SYR"))
      .andExpect(jsonPath("$.route[5]").value("TUR"))
      .andExpect(jsonPath("$.route[6]").value("BGR"))
      .andExpect(jsonPath("$.route[7]").value("SRB"))
      .andExpect(jsonPath("$.route[8]").value("HUN"));
  }

  @Test
  public void testValidRouteSyrUkr() throws Exception {
    mvc.perform(get("/routing/SYR/UKR"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.route.length()").value(5))
      .andExpect(jsonPath("$.route[0]").value("SYR"))
      .andExpect(jsonPath("$.route[1]").value("TUR"))
      .andExpect(jsonPath("$.route[2]").value("BGR"))
      .andExpect(jsonPath("$.route[3]").value("ROU"))
      .andExpect(jsonPath("$.route[4]").value("UKR"));
  }

  @Test
  public void testValidSameCountry() throws Exception {
    mvc.perform(get("/routing/svk/SvK"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.route.length()").value(1))
      .andExpect(jsonPath("$.route[0]").value("SVK"));
  }
}
