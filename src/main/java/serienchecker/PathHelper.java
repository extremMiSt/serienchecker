/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serienchecker;

import java.io.File;
import java.security.CodeSource;

/**
 *
 * @author mist
 */
public class PathHelper {
  public static String getJarPath(){
    //not a problem under windows
    if(System.getProperty("os.version").toLowerCase().startsWith("w")){
      return "";
    }
    
    CodeSource codeSource = PathHelper.class.getProtectionDomain().getCodeSource();
    File jarFile = new File(codeSource.getLocation().getPath());
    String jarDir = jarFile.getParentFile().getAbsolutePath();
    int jar = jarDir.indexOf(".jar");
    if(jar >= 0) { //we still are in a jar (probably oneJar)
      jarDir = (new File(jarDir.substring(0,jar+3))).getParentFile().getAbsolutePath();
    }
    int file = jarDir.lastIndexOf("file:");
    if(file >= 0) { //onejar fucks up
      jarDir = jarDir.substring(file+5, jarDir.length());
    }
    return jarDir + File.separatorChar;
  }
}
