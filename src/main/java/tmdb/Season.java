/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author mist
 */
public class Season implements Comparable<Season> {
    
    private String num;
    private List<Episode> episodes = new ArrayList<Episode>();
    
    public Season(String num){
        this.num = num;
    }

    public Episode getEpisode(int id) {
        return episodes.get(id);
    }

    public int getNumEpisodes(){
        return episodes.size();
    }

    public String getNum() {
        return num;
    }

    public void sort() {
        Collections.sort(episodes);
    }

    @Override
    public int compareTo(Season s) {
        try{ //wenns ne zahl ist zahlenmäßig sortieren
            Integer int1 = Integer.valueOf(this.getNum());
            Integer int2 = Integer.valueOf(s.getNum());
            return int1.compareTo(int2);
        }catch(NumberFormatException e){ //keine nummer
            return -this.getNum().compareTo(s.getNum());
        }
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }
    
    public void setWatched(boolean watch){
        for (int i = 0; i < episodes.size(); i++) {
            episodes.get(i).setWatched(watch);
        }
    }

  @Override
  public String toString() {
    return "Season{" + "num=" + num + ", episodes=" + episodes + '}';
  }
}
