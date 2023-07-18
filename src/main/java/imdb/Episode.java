package imdb;

import serienchecker.Main;

public class Episode implements Comparable<Episode> {
    
    private String imdbID;
    private String name;
    private Season season;
    private int number;
    private boolean watched = false;

    public Episode(String imdbID, String name, Season season, int number) {
        this.imdbID = imdbID;
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
    
    public String getImdbID(){
        return imdbID;
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
        return (Main.data.watched != null && Main.data.watched.contains(imdbID));
    }

    public void setWatched(boolean watched) {
        //this.watched = watched;
        if(watched){
          Main.data.watched.add(imdbID);
        }else{
          Main.data.watched.remove(imdbID);
        }
        
    }

    @Override
    public int compareTo(Episode e) {
        return ((Integer)this.getNum()).compareTo(e.getNum());
    }

  @Override
  public String toString() {
    return "Episode{" + "imdbID=" + imdbID + ", name=" + name + ", number=" + number + ", watched=" + watched + '}';
  }
    
}
