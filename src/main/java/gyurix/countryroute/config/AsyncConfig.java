package gyurix.countryroute.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AsyncConfig {
  private final int asyncQueueSize;
  private final int maxPoolSize;
  private final int minPoolSize;

  public AsyncConfig(@Value("${async.queue_size}") int asyncQueueSize, @Value("${async.min_pool_size}") int minPoolSize, @Value("${async.max_pool_size}") int maxPoolSize) {
    this.asyncQueueSize = asyncQueueSize;
    this.maxPoolSize = maxPoolSize;
    this.minPoolSize = minPoolSize;
  }
}
