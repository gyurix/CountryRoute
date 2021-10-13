package gyurix.countryroute.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyurix.countryroute.config.RoutingConfig;
import gyurix.countryroute.dto.CountryData;
import gyurix.countryroute.dto.RoutingResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;

import static java.util.Comparator.comparingInt;

@Service
public class RoutingService {
  public static final ResponseStatusException LOADING_DATA = new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Still loading country border data, try again later");

  private final Logger log = LoggerFactory.getLogger(RoutingService.class);
  private final ObjectMapper mapper = new ObjectMapper();

  private int[][] countryBorders;
  private CountryData[] countryData;
  private Map<String, Integer> countryIds;

  private long lastTry = System.currentTimeMillis();
  @Autowired
  private RoutingConfig routingConfig;
  @Getter
  private boolean succeed;

  private int[] dijkstra(int from, int to) {
    int countryCount = countryData.length;
    int[] dist = new int[countryCount];
    int[] prev = new int[countryCount];
    for (int i = 0; i < countryCount; ++i)
      dist[i] = 1000000;
    dist[from] = 0;
    PriorityQueue<Integer> pq = new PriorityQueue<>(comparingInt(a -> dist[a]));
    pq.offer(from);

    while (!pq.isEmpty()) {
      int i = pq.poll();
      int d = dist[i] + 1;
      for (int j : countryBorders[i]) {
        if (d < dist[j]) {
          dist[j] = d;
          pq.remove(j);
          pq.offer(j);
          prev[j] = i + 1;
          if (j == to)
            return prev;
        }
      }
    }
    return prev;
  }

  @Async("asyncExecutor")
  public CompletableFuture<Boolean> loadCountries() {
    try {
      log.info("Loading countries...");
      countryData = mapper.readerForArrayOf(CountryData.class).readValue(routingConfig.getCountriesUrl());
      int countryCount = countryData.length;

      countryIds = new HashMap<>();
      for (int i = 0; i < countryCount; ++i)
        countryIds.put(countryData[i].getName(), i);

      countryBorders = new int[countryCount][];
      for (int i = 0; i < countryCount; ++i) {
        CountryData cd = countryData[i];
        String[] borderNames = cd.getBorders();
        int borderCount = borderNames.length;

        countryBorders[i] = new int[borderCount];
        for (int j = 0; j < borderCount; ++j)
          countryBorders[i][j] = countryIds.get(borderNames[j]);
      }
      succeed = true;
      log.info("Loaded countries successfully");
      return CompletableFuture.completedFuture(true);
    } catch (IOException err) {
      log.error("Failed to load countries", err);
    }
    return CompletableFuture.completedFuture(false);
  }

  public RoutingResponse route(String fromName, String toName) {
    log.info("Calculating route between countries {} -> {}...", fromName, toName);

    if (!succeed)
      throw LOADING_DATA;

    int from = countryIds.getOrDefault(fromName, -1);
    if (from == -1)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The given origin country (%s) was not found", fromName));

    int to = countryIds.getOrDefault(toName, -1);
    if (to == -1)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The given destination country (%s) was not found", toName));

    int[] prev = dijkstra(from, to);
    if (from != to && prev[to] == 0)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("No route can be found between countries %s and %s", fromName, toName));

    RoutingResponse response = new RoutingResponse();
    while (to != -1) {
      response.addPrev(countryData[to].getName());
      to = prev[to] - 1;
    }

    log.info("Found route: {}", response.getRoute());
    return response;
  }

  public boolean shouldFetchData() {
    if (succeed)
      return false;

    long time = System.currentTimeMillis();
    if (lastTry + routingConfig.getRetryMs() < time) {
      lastTry = time;
      return true;
    }

    return false;
  }
}
