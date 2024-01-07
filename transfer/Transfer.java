package transfer.tmdb;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import serienchecker.PathHelper;
import tmdbapi.database.Database;
import tmdbapi.query.Series;

/**
 *
 * @author mist
 */
public class Transfer {
  
  public static final String FILE = PathHelper.getJarPath()+"transfer.db";
  
  public static Data dataOld; 
  public static Database dataNew; 
  
  public static void main(String[] args) throws IOException, SQLException, InterruptedException{
    dataOld = Data.deSerialize();
    boolean fresh = !(new File(FILE)).exists();
    dataNew = new Database(FILE);
    if (fresh) {
      dataNew.create();
    }
    System.out.println("adding all series");
    for (int i = 0; i < dataOld.size(); i++) {
      transfer.tmdb.Series s = dataOld.get(i);
      dataNew.addTMDB(Integer.parseInt(s.getTmdbID()));
    }
    
    System.out.println("updating series in new database");
    dataNew.update();
    
    System.out.println("updating episode watch status");
    for (Series added : dataNew.getAdded()) {
      for (tmdbapi.query.Season season : added.getSeasons()) {
        for (tmdbapi.query.Episode episode : season.getEpisodes()) {
          String oldId = added.getTmdbId()+ "$" + season.getNum() + "$" + episode.getNum();
          if(dataOld.watched.contains(oldId)){
            episode.setWatched(true);
          }
        }
      }
    }
    dataNew.close();
    
  }
  
}
