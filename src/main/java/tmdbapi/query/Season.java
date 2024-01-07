package tmdbapi.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mist
 */
public class Season {
  
  private final Connection con;
  
  private final int tmdbId;
  private final int num;
  private final String name;
  
  
  private static final HashMap<Integer,Season> cache = new HashMap<>();
  
  public static Season get(Connection con, int keyValue){
    return cache.get(keyValue);
  }
  
  public static void loadAll(Connection con) throws SQLException{
    cache.clear();
    PreparedStatement stmt = con.prepareStatement("SELECT * FROM Seasons");
    ResultSet rset = stmt.executeQuery();
    while(rset.next()){
      cache.put(rset.getInt("tmdbId"), new Season(con,
              rset.getInt("tmdbId"),
              rset.getInt("num"),
              rset.getString("name")));
    }
  }

  public Season(Connection con, int tmdbId, int num, String name) {
    this.con = con;
    this.tmdbId = tmdbId;
    this.num = num;
    this.name = name;
  }
  
  public String getName() throws SQLException{
    return name;
  }
  
  public int getNum() throws SQLException{
    return num;
  }
  
  private List<Episode> ep;
  
  public List<Episode> getEpisodes() throws SQLException{
    if(ep != null){
      return ep;
    }
    
    PreparedStatement stmt = con.prepareStatement("SELECT tmdbId FROM Episodes WHERE seasonId = ? ORDER BY num ASC");
    stmt.setInt(1, tmdbId);
    ResultSet rset = stmt.executeQuery();
    List<Episode> episodes = new ArrayList<>();
    while(rset.next()){
      int episodeId = rset.getInt(1);
      episodes.add(Episode.get(con, episodeId));
    }
    stmt.close();
    ep = episodes;
    return episodes;
  }
  
  public void setWatched(boolean watched) throws SQLException{
    for (Episode episode : getEpisodes()) {
      episode.setWatched(watched);
    }
  }
  
}
