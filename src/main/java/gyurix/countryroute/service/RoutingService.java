package gyurix.countryroute.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gyurix.countryroute.dto.CountryData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparingInt;


@Service
public class RoutingService {
  private final ResponseStatusException BAD_REQUEST = new ResponseStatusException(HttpStatus.BAD_REQUEST);
  private final Map<String, Integer> countryIds = new HashMap<>();
  private CountryData[] countries;
  private int[][] countryBorders;
  private int countryCount;

  public RoutingService(@Value("classpath:countries.json") Resource countriesJson) throws IOException {
    loadCountries(countriesJson);
  }

  public int[] dijkstra(int from, int to) {
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

  public void loadCountries(Resource countriesJson) throws IOException {
    countries = new ObjectMapper().readerForArrayOf(CountryData.class).readValue(countriesJson.getURL());
    countryCount = countries.length;

    for (int i = 0; i < countryCount; ++i)
      countryIds.put(countries[i].getName(), i);

    countryBorders = new int[countryCount][];
    for (int i = 0; i < countryCount; ++i) {
      CountryData cd = countries[i];
      String[] borderNames = cd.getBorders();
      int borderCount = borderNames.length;

      countryBorders[i] = new int[borderCount];
      for (int j = 0; j < borderCount; ++j)
        countryBorders[i][j] = countryIds.get(borderNames[j]);
    }
  }

  public List<String> route(String fromName, String toName) {
    int from = countryIds.getOrDefault(fromName, -1);
    if (from == -1)
      throw BAD_REQUEST;

    int to = countryIds.getOrDefault(toName, -1);
    if (to == -1)
      throw BAD_REQUEST;

    int[] prev = dijkstra(from,to);
    if (from != to && prev[to] == 0)
      throw BAD_REQUEST;

    LinkedList<String> route = new LinkedList<>();
    while (to != -1) {
      route.addFirst(countries[to].getName());
      to = prev[to] - 1;
    }
    return route;
  }
}
