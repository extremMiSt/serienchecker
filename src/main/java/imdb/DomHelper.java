/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb;

import org.jsoup.nodes.Document;

/**
 *
 * @author mist
 */
public class DomHelper {
  
  public static void getByAttribute(Document doc, String name, String value){
    System.out.println(doc.getAllElements().size());
  }
  
}
