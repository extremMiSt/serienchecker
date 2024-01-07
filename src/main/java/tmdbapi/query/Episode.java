package tmdbapi.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author mist
 */
public class Episode{
  
  private static final HashMap<Integer,Episode> cache = new HashMap<>();
  
  private Connection con;

  private final int tmdbId;
  private final String name;
  private final int num;
  private final int seasonId;
  
  private Boolean watched;
    
  public static Episode get(Connection con, int tmdbId) throws SQLException{
    return cache.get(tmdbId);
  }
  
  public static void loadAll(Connection con) throws SQLException{
    cache.clear();
    PreparedStatement stmt = con.prepareStatement("SELECT * FROM Episodes LEFT JOIN (SELECT 1, tmdbId as db FROM Watched) ON tmdbId=db");
    ResultSet rset = stmt.executeQuery();
    while(rset.next()){
      cache.put(rset.getInt("tmdbId"), new Episode(con,
              rset.getString("name"),
              rset.getInt("num"),
              rset.getInt("seasonId"),
              rset.getObject("1") != null,
              rset.getInt("tmdbId")));
    }
  }
  
  private Episode(Connection con, String name, int num, int seasonId, boolean watched, int tmdbId) throws SQLException {
    this.con = con;
    this.tmdbId = tmdbId;
    this.name = name;
    this.num = num;
    this.seasonId= seasonId;
    this.watched = watched;
  }
  
  public String getName() throws SQLException{
    return name;
  }
  
  public int getNum() throws SQLException{
    return num;
  }
  
  public Season getSeason() throws SQLException{
    return Season.get(con, seasonId);
  }
  
  public void setWatched(boolean watched) throws SQLException{
    if(watched){
      PreparedStatement stmt = con.prepareStatement("INSERT INTO Watched VALUES(?)");
      stmt.setInt(1, this.tmdbId);
      stmt.executeUpdate();
      stmt.close();
    }else{
      PreparedStatement stmt = con.prepareStatement("DELETE FROM Watched WHERE tmdbId = ?");
      stmt.setInt(1, this.tmdbId);
      stmt.executeUpdate();
      stmt.close();
    }
    this.watched = watched;
  }
  
  public boolean isWatched() throws SQLException{
    return watched;
  }
  
}
