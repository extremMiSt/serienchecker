package tmdbapi.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mist
 */
public class Series implements Comparable<Series>{
  
  private final Connection con;
  
  private final int tmdbId;
  private final String name;
  private String comment;
  
  private static final HashMap<Integer,Series> cache = new HashMap<>();
  
  public static Series get(Connection con, int keyValue){
    return cache.get(keyValue);
  }
  
  public static void loadAll(Connection con) throws SQLException{
    cache.clear();
    PreparedStatement stmt = con.prepareStatement("SELECT * FROM Added LEFT JOIN (SELECT name, tmdbId as db FROM Series) ON tmdbId=db");
    ResultSet rset = stmt.executeQuery();
    while(rset.next()){
      cache.put(rset.getInt("tmdbId"), new Series(con,
              rset.getInt("tmdbId"),
              rset.getString("comment"),
              rset.getString("name")));
    }
  }
  
  private Series(Connection con, int tmdbId, String comment, String name){
    this.con = con;
    this.tmdbId = tmdbId;
    this.comment = comment;
    this.name = name;
    
  }
  
  public String getComment() throws SQLException{
    return comment;
  }
  
  public void setComment(String com) throws SQLException{
    PreparedStatement stmt = con.prepareStatement("UPDATE Added SET comment = ? WHERE tmdbId = ?");
    stmt.setString(1, com);
    stmt.setInt(2, tmdbId);
    stmt.executeUpdate();
    stmt.close();
    con.commit();
    comment = com;
  }

  public String getName() throws SQLException {
    return name;
  }

  public int getTmdbId() {
    return tmdbId;
  }
  
  
  
  public List<Season> getSeasons() throws SQLException{
    PreparedStatement stmt = con.prepareStatement("SELECT tmdbId FROM Seasons WHERE seriesId = ? ORDER BY num ASC");
    stmt.setInt(1, tmdbId);
    ResultSet rset = stmt.executeQuery();
    List<Season> seasons = new ArrayList<>();
    while(rset.next()){
      int seasonId = rset.getInt(1);
      seasons.add(Season.get(con, seasonId));
    }
    stmt.close();
    return seasons;
  }
  
  public Episode lastEpisode() throws SQLException {
    List<Season> seasons = getSeasons();
    for (int i = seasons.size() - 1; i >= 0; i--) {
      Season s = seasons.get(i);
      List<Episode> episodes = s.getEpisodes();
      for (int j = episodes.size() - 1; j >= 0; j--) {
        if (episodes.get(j).isWatched()) {
          return episodes.get(j);
        }
      }
    }
    return null;
  }
  
  public int watchedEpisodesCount() throws SQLException {
    int count = 0;
    List<Season> seasons = getSeasons();
    for (int i = seasons.size() - 1; i >= 0; i--) {
      Season s = seasons.get(i);
      List<Episode> episodes = s.getEpisodes();
      for (int j = episodes.size() - 1; j >= 0; j--) {
        if (episodes.get(j).isWatched()) {
          count++;
        }
      }
    }
    return count;
  }
  
  public int status() throws SQLException {
    int stat = 0;
    boolean missed = false; //hab ne ungesehene episode gefunden
    List<Season> seasons = getSeasons();
    for (int i = 0; i < seasons.size(); i++) {
      Season s = seasons.get(i);
      if (s.getNum() == 0) {
        continue;
      }
      List<Episode> episodes = s.getEpisodes();
      for (int j = 0; j < episodes.size(); j++) {
        Episode e = episodes.get(j);
        if (e.isWatched()) {
          if (missed) { // nach ner lücke was neues gefunden
            return 1;
          }
          if (j + 1 == episodes.size() && i + 1 == seasons.size()) { //ende staffel, ende Serie
            stat = 3;
          } else if (j + 1 == episodes.size()) { //ende staffel
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
  
  @Override
  public int compareTo(Series s){
    try {
      int i = ((Integer) status()).compareTo(s.status());
      if (i == 0) {
        System.out.println(this.tmdbId + " " + this.getName() + " " + s.getName());
        return this.getName().toLowerCase().compareTo(s.getName().toLowerCase());
      }
      return i;
    } catch (SQLException ex) {
      Logger.getLogger(Series.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }
}
