package tmdbapi.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sqlite.SQLiteConfig;
import serienchecker.Main;
import tmdbapi.query.Episode;
import tmdbapi.query.Season;
import tmdbapi.query.Series;

/**
 *
 * @author mist
 */
public class Database{
  
  private final Connection con;
  
  public Database(String file) throws SQLException{
    String url = "jdbc:sqlite:"+file;
    SQLiteConfig config = new SQLiteConfig();
    config.setJournalMode(SQLiteConfig.JournalMode.MEMORY);
    config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
    con =  config.createConnection(url);
    con.setAutoCommit(false);
    
  }
  
  public Connection getCon(){
    return con;
  }
  
  public void close() throws SQLException{
    con.commit();
    con.close();
  }
  
  public void loadAll() throws SQLException{
    Series.loadAll(con);
    Season.loadAll(con);
    Episode.loadAll(con);
  }
  
  public void create() throws SQLException{
    PreparedStatement addedCreate = con.prepareStatement("CREATE TABLE Added (tmdbId, comment)");
    addedCreate.execute();
    addedCreate.close();
    PreparedStatement watchedCreate = con.prepareStatement("CREATE TABLE Watched (tmdbId)");
    watchedCreate.execute();
    watchedCreate.close();
    PreparedStatement seriesCreate = con.prepareStatement("CREATE TABLE Series (tmdbId, name)");
    seriesCreate.execute();
    seriesCreate.close();
    PreparedStatement seasonCreate = con.prepareStatement("CREATE TABLE Seasons (tmdbId, num, name, seriesId)");
    seasonCreate.execute();
    seasonCreate.close();
    PreparedStatement episodeCreate = con.prepareStatement("CREATE TABLE Episodes (tmdbId, num, name, seasonId)");
    episodeCreate.execute();
    episodeCreate.close();
    con.commit();
  }
  
  public void update() throws SQLException, IOException, InterruptedException{
    PreparedStatement seriesDrop = con.prepareStatement("DROP TABLE Series");
    seriesDrop.execute();
    seriesDrop.close();
    PreparedStatement seasonDrop = con.prepareStatement("DROP TABLE Seasons");
    seasonDrop.execute();
    seasonDrop.close();
    PreparedStatement episodeDrop = con.prepareStatement("DROP TABLE Episodes");
    episodeDrop.execute();
    episodeDrop.close();
    
    PreparedStatement seriesCreate = con.prepareStatement("CREATE TABLE Series (tmdbId, name)");
    seriesCreate.execute();
    seriesCreate.close();
    PreparedStatement seasonCreate = con.prepareStatement("CREATE TABLE Seasons (tmdbId, num, name, seriesId)");
    seasonCreate.execute();
    seasonCreate.close();
    PreparedStatement episodeCreate = con.prepareStatement("CREATE TABLE Episodes (tmdbId, num, name, seasonId)");
    episodeCreate.execute();
    episodeCreate.close();
    
    List<Integer> added = getAddedRaw();
    int updated = 0;
    Main.gui.setTitle("!! Serienchecker " + updated + "/" + added.size() + "!!");
    for (Integer series : added) {
      addTMDBInfo(series);
      updated ++;
      Main.gui.setTitle("!! Serienchecker " + updated + "/" + added.size() + "!!");
    }
    loadAll();
    con.commit();
  }
  
  public boolean addTMDB(int seriesId) throws SQLException{
    PreparedStatement seriesCheck = con.prepareStatement("SELECT * FROM Added WHERE tmdbID = ?");
    seriesCheck.setInt(1, seriesId);
    ResultSet rset = seriesCheck.executeQuery();
    if(rset.next()){
      seriesCheck.close();
      return false;
    }
    seriesCheck.close();
    PreparedStatement seriesAdd = con.prepareStatement("INSERT INTO Added VALUES(?,?)");
    seriesAdd.setInt(1, seriesId);
    seriesAdd.setString(2, "");
    seriesAdd.execute();
    seriesAdd.close();
    con.commit();
    addedCache = null;
    return true;
  }
  
  private List<Series> addedCache = null;
  
  public List<Series> getAdded() throws SQLException{
    if(addedCache != null){
      return addedCache;
    }
    
    PreparedStatement addedQuery = con.prepareStatement("SELECT tmdbID FROM Added");
    ArrayList<Series> result = new ArrayList<>();
    ResultSet rset = addedQuery.executeQuery();
    while(rset.next()){
      result.add(Series.get(con, rset.getInt("tmdbId")));
    }
    addedQuery.close();
    addedCache = result;
    return result;
  }
  
  public List<Integer> getAddedRaw() throws SQLException{
    PreparedStatement addedQuery = con.prepareStatement("SELECT tmdbID FROM Added");
    ArrayList<Integer> result = new ArrayList<>();
    ResultSet rset = addedQuery.executeQuery();
    while(rset.next()){
      result.add(rset.getInt("tmdbId"));
    }
    addedQuery.close();
    return result;
  }
  
  public void addTMDBInfo(int seriesId) throws SQLException, IOException, InterruptedException{
    PreparedStatement seriesInsert = con.prepareStatement("INSERT INTO Series VALUES(?,?)");
    PreparedStatement seasonInsert = con.prepareStatement("INSERT INTO Seasons VALUES(?,?,?,?)");
    PreparedStatement episodeInsert = con.prepareStatement("INSERT INTO Episodes VALUES(?,?,?,?)");
    
    JSONObject seriesData = TmdbHelper.getSeriesData(seriesId);
    String name = seriesData.getString("name");
    seriesInsert.setInt(1, seriesId);
    seriesInsert.setString(2, name);
    seriesInsert.execute();
    seriesInsert.clearParameters();
    
    JSONArray arr = seriesData.getJSONArray("seasons");
    for (int i = 0; i < arr.length(); i++) {
      int seasonNum = arr.getJSONObject(i).getInt("season_number");
      int seasonId = arr.getJSONObject(i).getInt("id");
      String seasonName = arr.getJSONObject(i).getString("name");
      seasonInsert.setInt(1, seasonId);
      seasonInsert.setInt(2, seasonNum);
      seasonInsert.setString(3, seasonName);
      seasonInsert.setInt(4, seriesId);
      seasonInsert.execute();
      seasonInsert.clearParameters();
      
      JSONObject seasonData = TmdbHelper.getSeasonData(seriesId, seasonNum);
      JSONArray arr1 = seasonData.getJSONArray("episodes");
      for (int j = 0; j < arr1.length(); j++) {
        int episodeNum = arr1.getJSONObject(j).getInt("episode_number");
        int episodeId = arr1.getJSONObject(j).getInt("id");
        String episodeName = arr1.getJSONObject(j).getString("name");
        episodeInsert.setInt(1, episodeId);
        episodeInsert.setInt(2, episodeNum);
        episodeInsert.setString(3, episodeName);
        episodeInsert.setInt(4, seasonId);
        episodeInsert.execute();
        episodeInsert.clearParameters();
      }
    }
    
    seriesInsert.close();
    seasonInsert.close();
    episodeInsert.close();
    con.commit();
  }
  
}
