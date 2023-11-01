/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serienchecker;

import gui.MainGui;
import tmdb.Data;
import java.io.IOException;

/**
 *
 * @author mist
 */
public class Main {
    
    public static Data data;
    public static MainGui gui;
    public static int x = 0;
    public static int y = 0;
    
    public static void main(String args[]) throws IOException{
        data = Data.deSerialize();
        gui = new MainGui();
    }
    
}
