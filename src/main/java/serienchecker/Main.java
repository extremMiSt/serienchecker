/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serienchecker;

import gui.MainGui;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import tmdbapi.database.Database;

/**
 *
 * @author mist
 */
public class Main {
  
  public static final String FILE = PathHelper.getJarPath()+"data.db";

  public static Database data;
  public static MainGui gui;
  public static int x = 0;
  public static int y = 0;

  public static void main(String args[]) throws IOException, SQLException {
    boolean fresh = !(new File(FILE)).exists();
    data = new Database(FILE);
    if (fresh) {
      data.create();
    }
    data.loadAll();
    
    gui = new MainGui();
  }

}
