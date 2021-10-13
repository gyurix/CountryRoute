package gyurix.countryroute.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@Getter
public class RoutingConfig {
  @Value("${routing.countries_url}")
  private URL countriesUrl;

  @Value("${routing.load_at_startup}")
  private boolean loadAtStartup;

  @Value("${routing.retry_ms}")
  private long retryMs;
}
