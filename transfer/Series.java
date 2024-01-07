package transfer.tmdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Series implements Comparable<Series> {
  
  private String tmdbID;
  private String name;
  private List<Season> seasons = new ArrayList<Season>();
  private String text;
  
/*  public Series(String id) throws IOException {
    tmdbID = id;
    TmdbInfo info = new TmdbInfo(tmdbID);
    name = info.getSeriesName();
    text = "";
    String[] seasonNames = info.getSeasonNames();
    for (int i = 0; i < seasonNames.length; i++) {
      Season s = info.getSeason(seasonNames[i]);
      seasons.add(s);
    }
  }*/
  
  public String getName() {
    return name;
  }
  
  public String getText() {
    return text;
  }
  
  public synchronized void setText(String s) {
    text = s;
  }
  
  public Season getSeason(int num) {
    return seasons.get(num);
  }
  
  public String getTmdbID() {
    return tmdbID;
  }
  
  public int numSeasons() {
    return seasons.size();
  }
  
  public Episode lastEpisode() {
    for (int i = numSeasons() - 1; i >= 0; i--) {
      Season s = this.getSeason(i);
      for (int j = s.getNumEpisodes() - 1; j >= 0; j--) {
        if (s.getEpisode(j).isWatched()) {
          return s.getEpisode(j);
        }
      }
    }
    return null;
  }
  
  public int watchedEpisodesCount() {
    int count = 0;
    for (int i = numSeasons() - 1; i >= 0; i--) {
      Season s = this.getSeason(i);
      for (int j = s.getNumEpisodes() - 1; j >= 0; j--) {
        if (s.getEpisode(j).isWatched()) {
          count++;
        }
      }
    }
    return count;
  }
  
  public int status() {
    int stat = 0;
    boolean missed = false; //hab ne ungesehene episode gefunden
    for (int i = 0; i < numSeasons(); i++) {
      Season s = this.getSeason(i);
      if (s.getNum().equals("0")) {
        continue;
      }
      for (int j = 0; j < s.getNumEpisodes(); j++) {
        Episode e = s.getEpisode(j);
        if (e.isWatched()) {
          if (missed) { // nach ner lücke was neues gefunden
            return 1;
          }
          if (j + 1 == s.getNumEpisodes() && i + 1 == numSeasons()) { //ende staffel, ende Serie
            stat = 3;
          } else if (j + 1 == s.getNumEpisodes()) { //ende staffel
            stat = 2;
          } else {
            stat = 0;
          }
          
        } else {
          missed = true;
        }
      }
    }
    return stat;
  }

//    public Episode getEpisode(String season, int episode, String name){
//        for (int i = 0; i < numSeasons(); i++) {
//            Season s = this.getSeason(i);
//            for (int j = 0; j < s.getNumEpisodes(); j++) {
//                Episode e = s.getEpisode(j);
//                if(s.getNum().equals("-1")){
//                    if(e.getName().equals(name) && s.getNum().equals(season)){
//                        return e;
//                    }
//                }else{
//                    if(e.getNum() == episode && s.getNum().equals(season)){
//                        return e;
//                    }
//                }
//            }
//        }
//        return null;
//    }
  public Episode getEpisode(String imdbID) {
    for (int i = 0; i < numSeasons(); i++) {
      Season s = this.getSeason(i);
      for (int j = 0; j < s.getNumEpisodes(); j++) {
        Episode e = s.getEpisode(j);
        if (e.getTmdbID().equals(imdbID)) {
          return e;
        }
      }
    }
    return null;
  }
  
//  public void setWatched(){
//    for (Season season : seasons) {
//      season.setWatched(true);
//    }
//  }
  
  @Override
  public int compareTo(Series s) {
    int i = ((Integer) status()).compareTo(s.status());
    if (i == 0) {
      return this.name.toLowerCase().compareTo(s.getName().toLowerCase());
    }
    return i;
  }
  
  public void sort() {
    for (int i = 0; i < seasons.size(); i++) {
      seasons.get(i).sort();
    }
    Collections.sort(seasons);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Series other = (Series) obj;
    if ((this.tmdbID == null) ? (other.tmdbID != null) : !this.tmdbID.equals(other.tmdbID)) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + (this.tmdbID != null ? this.tmdbID.hashCode() : 0);
    return hash;
  }
  
}
