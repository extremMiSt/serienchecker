package tmdbapi.database;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import tmdbapi.TMDB_env;

/**
 *
 * @author mist
 */
public class TmdbHelper {
  
  public static JSONObject getSeriesData(int seriesId) throws IOException, InterruptedException{
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.themoviedb.org/3/tv/"+seriesId+"?language=en-UK"))
        .header("accept", "application/json")
        .header("Authorization", "Bearer " + TMDB_env.TMDB_TOKEN)
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return new JSONObject(response.body());
  }
  
  public static JSONObject getSeasonData(int seriesId, int seasonNum) throws IOException, InterruptedException{
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://api.themoviedb.org/3/tv/"+seriesId+"/season/"+seasonNum+"?language=en-US"))
        .header("accept", "application/json")
        .header("Authorization", "Bearer " + TMDB_env.TMDB_TOKEN)
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return new JSONObject(response.body());
  }
  
}
