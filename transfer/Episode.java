package transfer.tmdb;

import serienchecker.Main;

public class Episode implements Comparable<Episode> {
    
    private String tmdbID;
    private String name;
    private Season season;
    private int number;
    private boolean watched = false;
    
    public Episode(String imdbID, String name, Season season, int number) {
        this.tmdbID = imdbID;
        this.name = name;
        this.season = season;
        this.number = number;
    }
    
//    public Episode(String imdbID, String name, Season season, int number, boolean watched) {
//        this.imdbID = imdbID;
//        this.name = name;
//        this.season = season;
//        this.number = number;
//        //this.watched = watched;
//    }
    
    public String getTmdbID(){
        return tmdbID;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return number;
    }

    public Season getSeason() {
        return season;
    }

    public boolean isWatched() {
        return (Transfer.dataOld.watched != null &&Transfer.dataOld.watched.contains(tmdbID));
    }

/*    public void setWatched(boolean watched) {
        this.watched = watched;
        if(watched){
          Main.data.watched.add(tmdbID);
        }else{
          Main.data.watched.remove(tmdbID);
        }
        
    }*/

    @Override
    public int compareTo(Episode s) {
      return ((Integer)this.getNum()).compareTo(s.getNum());
    }

  @Override
  public String toString() {
    return "Episode{" + "imdbID=" + tmdbID + ", name=" + name + ", number=" + number + ", watched=" + watched + '}';
  }
    
}
