package imdb;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import serienchecker.Main;

public class ImdbInfo {

  private String imdbID;

  public ImdbInfo(String id) {
    imdbID = id;
  }

  public String getSeriesName() {
    try {
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Elements elem = doc.getElementsByAttributeValue("property", "og:title");
      String title = elem.get(0).attr("content");
      return title.substring(0, title.indexOf("(") - 1);
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getSeriesName();
    }
  }

  public boolean isSeasonsWorking() {
    try {
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes?season=1").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Element seasons = doc.getElementById("bySeason");
      return true;
    } catch (IOException ex) {
      return false;
    }
  }
  
  public String[] getSeasonNames() {
    try {
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Element seasons = doc.getElementById("bySeason");
      if(seasons == null){
        return new String[0];
      }
      Elements options = seasons.getElementsByTag("option");
      String[] names = new String[options.size()];
      for (int i = 0; i < options.size(); i++) {
        names[i] = options.get(i).attr("value").trim();
      }
      return names;
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getSeasonNames();
    }
  }
  
  public String[] getYears() {
    try {
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes").maxBodySize(Integer.MAX_VALUE).timeout(60000).get();
      Elements options = doc.getElementById("byYear").getElementsByTag("option");
      String[] names = new String[options.size()];
      for (int i = 0; i < options.size(); i++) {
        names[i] = options.get(i).attr("value").trim();
      }
      return names;
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getSeasonNames();
    }
  }
  
  public Season getSeason(String s) throws IOException {
    try {
      Season season = new Season(s);
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes?season=" + s).maxBodySize(Integer.MAX_VALUE).timeout(90000).get();
      Elements episodelist = doc.getElementsByAttributeValue("class", "list detail eplist");
      Elements episodes = episodelist.get(0).children();
      System.out.println("elem episodes" + episodes.size());
      for (int i = 0; i < episodes.size(); i++) {
        Element episode = episodes.get(i);
        int eNum = Integer.parseInt(episode.getElementsByAttributeValue("itemprop", "episodeNumber").get(0).attr("content"));
        Element nameTag = episode.getElementsByAttributeValue("itemprop", "name").get(0);
        String name = nameTag.ownText();
        String id = nameTag.attr("href");
        id = id.substring(id.indexOf("/", 1) + 1, id.lastIndexOf("/"));
        season.addEpisode(new Episode(id, name, season, eNum));
      }
      return season;
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getSeason(s);
    }
  }
  
    public Season getYear(String s) {
    try {
      Season season = new Season(s);
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes?year=" + s).maxBodySize(Integer.MAX_VALUE).timeout(90000).get();
      Elements episodelist = doc.getElementsByAttributeValue("class", "list detail eplist");
      Elements episodes = episodelist.get(0).children();
      for (int i = 0; i < episodes.size(); i++) {
        Element episode = episodes.get(i);
        int eNum = Integer.parseInt(episode.getElementsByAttributeValue("itemprop", "episodeNumber").get(0).attr("content").replaceAll(",", ""));
        Element nameTag = episode.getElementsByAttributeValue("itemprop", "name").get(0);
        String name = nameTag.ownText();
        String id = nameTag.attr("href");
        id = id.substring(id.indexOf("/", 1) + 1, id.lastIndexOf("/"));
        season.addEpisode(new Episode(id, name, season, eNum));
      }
      return season;
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getAll();
    }
  }
  
  public Season getAll(){
    try {
      Season season = new Season("all");
      Document doc = Jsoup.connect("http://www.imdb.com/title/" + imdbID + "/episodes").maxBodySize(Integer.MAX_VALUE).timeout(90000).get();
      Elements episodelist = doc.getElementsByAttributeValue("class", "list detail eplist");
      Elements episodes = episodelist.get(0).children();
      for (int i = 0; i < episodes.size(); i++) {
        Element episode = episodes.get(i);
        int eNum = Integer.parseInt(episode.getElementsByAttributeValue("itemprop", "episodeNumber").get(0).attr("content"));
        Element nameTag = episode.getElementsByAttributeValue("itemprop", "name").get(0);
        String name = nameTag.ownText();
        String id = nameTag.attr("href");
        id = id.substring(id.indexOf("/", 1) + 1, id.lastIndexOf("/"));
        //System.out.println(id);
        season.addEpisode(new Episode(id, name, season, eNum));
      }
      return season;
    } catch (IOException ex) {
      Logger.getLogger(ImdbInfo.class.getName()).log(Level.SEVERE, null, ex);
      Main.data.errorOccured();
      return getAll();
    }
  }

}
