package imdb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import serienchecker.Main;
import serienchecker.PathHelper;

public class Data {

  public List<Series> series = Collections.synchronizedList(new ArrayList<Series>());
  public Set<String> watched = new HashSet<String>();

  public volatile int updated;
  public volatile int errors;

  public int size() {
    return series.size();
  }

  public Series get(int arg0) {
    return series.get(arg0);
  }

  public boolean add(Series arg0) {
    if (!(series.contains(arg0))) {
      return series.add(arg0);
    } else {
      Logger.getLogger(Data.class.getName()).log(Level.INFO, "{0} schon vorhanden", arg0.getImdbID());
      return false;
    }
  }

  public void update() throws IOException {
    Main.gui.setTitle("!! 0 Serienchecker 0/" + this.size() + "!!");
    ThreadGroup group = new ThreadGroup("update");
    updated = 0;
    errors = 0;
    for (int i = 0; i < this.size(); i++) {
      final int b = i;
      while (group.activeCount() > 5) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ex) {
        }
      }
      Thread t = new Thread(group, b + " " + get(b).getName()) {
        @Override
        public void run() {
          try {
            Series se = get(b);
            Series se2 = new Series(se.getImdbID());
            for (int j = 0; j < se.numSeasons(); j++) {
              Season s = se.getSeason(j);
              for (int k = 0; k < s.getNumEpisodes(); k++) {
                Episode e = s.getEpisode(k);
                if (e.isWatched()) {
                  Episode e2 = se2.getEpisode(e.getImdbID());
                  if (e2 != null) {
                    e2.setWatched(true);
                  }
                }
              }
            }
            incrementUpdated();
            se2.setText(se.getText());
            series.set(series.lastIndexOf(se), se2);
          } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      };
      t.start();
    }
    while (group.activeCount() > 0) {
    }
    Data.serialize(this);
    Main.gui.reInit();
    Main.gui.setTitle("Serienchecker");
  }

  public synchronized void incrementUpdated() {
    updated = updated + 1;
    Main.gui.setTitle("!! " + errors + " Serienchecker " + updated + "/" + size() + "!!");
  }

  public synchronized void errorOccured() {
    errors = errors + 1;
    Main.gui.setTitle("!! " + errors + " Serienchecker " + updated + "/" + size() + "!!");
  }

  public void sort() {
    for (int i = 0; i < series.size(); i++) {
      series.get(i).sort();
    }
    Collections.sort(series);
  }

  public static void serialize(Data d) throws FileNotFoundException, IOException {
    d.sort();
    XStream xStream = new XStream(new DomDriver());
    xStream.setMode(XStream.ID_REFERENCES);
    xStream.allowTypes(new Class[]{Data.class, Series.class, Season.class, Episode.class});
    xStream.alias("x", String.class);
    xStream.alias("a", Data.class);
    xStream.aliasField("aa", Data.class, "series");
    xStream.aliasField("ab", Data.class, "watched");
    xStream.alias("b", Series.class);
    xStream.aliasField("ba", Series.class, "imdbID");
    xStream.aliasField("bb", Series.class, "name");
    xStream.aliasField("bc", Series.class, "seasons");
    xStream.aliasField("bd", Series.class, "text");
    xStream.useAttributeFor(Series.class, "imdbID");
    xStream.useAttributeFor(Series.class, "name");
    xStream.useAttributeFor(Series.class, "text");
    xStream.alias("c", Season.class);
    xStream.aliasField("ca", Season.class, "num");
    xStream.aliasField("cb", Season.class, "episodes");
    xStream.useAttributeFor(Season.class, "num");
    xStream.alias("d", Episode.class);
    xStream.aliasField("da", Episode.class, "imdbID");
    xStream.aliasField("db", Episode.class, "name");
    xStream.aliasField("dc", Episode.class, "season");
    xStream.aliasField("dd", Episode.class, "number");
    xStream.aliasField("de", Episode.class, "watched");
    xStream.useAttributeFor(Episode.class, "imdbID");
    xStream.useAttributeFor(Episode.class, "name");
    xStream.useAttributeFor(Episode.class, "number");
    xStream.useAttributeFor(Episode.class, "watched");
    xStream.aliasAttribute("r", "reference");
    File out = new File(PathHelper.getJarPath() + "data.xml.gz");
    FileOutputStream os = new FileOutputStream(out);
    GZIPOutputStream gzos = new GZIPOutputStream(os);
    xStream.toXML(d, gzos);
    gzos.close();
    os.close();
  }

  public static Data deSerialize() throws IOException {
    File in = new File(PathHelper.getJarPath() + "data.xml.gz");
    if (in.exists()) {
      XStream xStream = new XStream(new StaxDriver());
      xStream.setMode(XStream.ID_REFERENCES);
      xStream.allowTypes(new Class[]{Data.class, Series.class, Season.class, Episode.class});
      xStream.alias("x", String.class);
      xStream.alias("a", Data.class);
      xStream.aliasField("aa", Data.class, "series");
      xStream.aliasField("ab", Data.class, "watched");
      xStream.alias("b", Series.class);
      xStream.aliasField("ba", Series.class, "imdbID");
      xStream.aliasField("bb", Series.class, "name");
      xStream.aliasField("bc", Series.class, "seasons");
      xStream.aliasField("bd", Series.class, "text");
      xStream.useAttributeFor(Series.class, "imdbID");
      xStream.useAttributeFor(Series.class, "name");
      xStream.useAttributeFor(Series.class, "text");
      xStream.alias("c", Season.class);
      xStream.aliasField("ca", Season.class, "num");
      xStream.aliasField("cb", Season.class, "episodes");
      xStream.useAttributeFor(Season.class, "num");
      xStream.alias("d", Episode.class);
      xStream.aliasField("da", Episode.class, "imdbID");
      xStream.aliasField("db", Episode.class, "name");
      xStream.aliasField("dc", Episode.class, "season");
      xStream.aliasField("dd", Episode.class, "number");
      xStream.aliasField("de", Episode.class, "watched");
      xStream.useAttributeFor(Episode.class, "imdbID");
      xStream.useAttributeFor(Episode.class, "name");
      xStream.useAttributeFor(Episode.class, "number");
      xStream.useAttributeFor(Episode.class, "watched");
      xStream.aliasAttribute("r", "reference");
      xStream.ignoreUnknownElements();                                    //COMMENT THIS!!
      xStream.omitField(Episode.class, "watched");
      FileInputStream is = new FileInputStream(in);
      GZIPInputStream gzis = new GZIPInputStream(is);
      Data data = (Data) xStream.fromXML(gzis);
      gzis.close();
      is.close();
      if(data.watched == null) data.watched = new HashSet<String>();
      return data;
    } else {
      return new Data();
    }
  }

//  /**
//   * Gibt den Pfad zur Jar zurück, da das bei meinem linux nicht mit einem einfachen leeren pfad tut.
//   * Liefert für windoof den leeren pfad. Liefert bei meiner linux distro (sonnst nichts getestet) den pfad zur jar mit abschließendem Trennzeichen zurück
//   * @return Pfad zur jar
//   */
//  public static String getDataDir() {
//    if(System.getProperty("os.version").toLowerCase().startsWith("w")){
//      return "";
//    }
//    String jar = System.getProperty("java.class.path");
//    jar = jar.substring(jar.lastIndexOf(":") + 1, jar.lastIndexOf(File.separatorChar));
//    System.out.println(jar + File.separatorChar);
//    return jar + File.separatorChar;
//  }

}
