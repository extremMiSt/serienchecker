package tmdb;

import java.io.IOException;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import serienchecker.Main;

public class TmdbInfo {

  private String tmdbID;

  public TmdbInfo(String id) {
    tmdbID = id;
  }

  public String getSeriesName() {
    try {
      Document doc = Jsoup.connect("https://www.themoviedb.org/tv/" + tmdbID + "/seasons").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Elements elem = doc.getElementsByClass("title");
      String title = elem.get(0).child(0).child(0).html();
      return title;
    } catch (IOException ex) {
      Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex1) {
        Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex1);
      }
      return getSeriesName();
    }
  }

  public String[] getSeasonNames() {
    try {
      Document doc = Jsoup.connect("https://www.themoviedb.org/tv/" + tmdbID + "/seasons").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Elements seasons = doc.getElementsByClass("season");
      
      String[] names = new String[seasons.size()];
      for (int i = 0; i < seasons.size(); i++) {
        String href = seasons.get(i).child(0).attr("href");
        names[i] = href.substring(href.lastIndexOf("/")+1);
      }
      System.out.println(Arrays.toString(names));
      return names;
    } catch (IOException ex) {
      Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex1) {
        Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex1);
      }
      return getSeasonNames();
    }
  }
  
  public Season getSeason(String s) throws IOException {
    try {
      Season season = new Season(s);
      Document doc = Jsoup.connect("https://www.themoviedb.org/tv/" + tmdbID + "/season/" + s).maxBodySize(Integer.MAX_VALUE).timeout(90000).get();
      Elements episodes = doc.getElementsByClass("episode_number");
      System.out.println("elem episodes" + episodes.size());
      for (int i = 0; i < episodes.size(); i++) {
        Element episode = episodes.get(i);
        
        Integer eNum = Integer.valueOf(episode.html());
        String name = episode.nextElementSibling().child(0).text();
        String id = tmdbID + "$" + s + "$" + eNum;
        season.addEpisode(new Episode(id, name, season, eNum));
      }
      return season;
    } catch (IOException ex) {
      Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
            try {
        Thread.sleep(10000);
      } catch (InterruptedException ex1) {
        Logger.getLogger(TmdbInfo.class.getName()).log(Level.SEVERE, null, ex1);
      }
      return getSeason(s);
    }
  }

}
