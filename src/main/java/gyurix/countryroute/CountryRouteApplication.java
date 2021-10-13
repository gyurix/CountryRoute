package gyurix.countryroute;

import gyurix.countryroute.config.AsyncConfig;
import gyurix.countryroute.config.RoutingConfig;
import gyurix.countryroute.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class CountryRouteApplication implements CommandLineRunner {
  @Autowired
  private AsyncConfig asyncConfig;

  @Autowired
  private RoutingConfig routingConfig;

  @Autowired
  private RoutingService routingService;

  public static void main(String[] args) {
    SpringApplication.run(CountryRouteApplication.class, args);
  }

  @Bean("asyncExecutor")
  public TaskExecutor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(1000);
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.initialize();
    return executor;
  }

  @Override
  public void run(String... args) {
    if (routingConfig.isLoadAtStartup())
      routingService.loadCountries();
  }
}
