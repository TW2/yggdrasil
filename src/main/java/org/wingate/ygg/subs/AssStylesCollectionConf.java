/*
 * Copyright (C) 2020 util2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wingate.ygg.subs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.ygg.subs.AssCommon;
import org.wingate.ygg.subs.AssStyle;

/**
 * StylesCollectionConf *.scc file per series/movies
 * @author util2
 */
public class AssStylesCollectionConf {

    private AssStylesCollectionConf() {
    }
    
    // Folder name
    private static final File FOLDER = new File("save");
    
    // Reader
    public static Map<String, AssStyle> read(String movieName){
        Map<String, AssStyle> styles = new HashMap<>();
        
        if(FOLDER.exists() == true){
            File movie = new File(FOLDER, movieName + ".scc");
            if(movie.exists() == false) return styles;
            try(FileInputStream fis = new FileInputStream(movie); 
                InputStreamReader isr = new InputStreamReader(fis, AssCommon.detectCharset(movie.getPath())); 
                BufferedReader br = new BufferedReader(isr);){
                
                String line;
                while((line = br.readLine()) != null){
                    if(line.startsWith("Style")){
                        AssStyle style = AssStyle.create(line);
                        styles.put(style.getName(), style); 
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(AssStylesCollectionConf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return styles;
    }
    
    // Writer
    public static void save(String movieName, Map<String, AssStyle> styles){
        if(FOLDER.exists() == false){
            FOLDER.mkdir();
        }
        File movie = new File(FOLDER, movieName + ".scc");
        try(PrintWriter pw = new PrintWriter(movie, StandardCharsets.UTF_8)){
            for(Map.Entry<String, AssStyle> entry : styles.entrySet()){
                pw.append(AssStyle.toAssStyleLine(entry.getValue()));
            }
        } catch (IOException ex) {
            Logger.getLogger(AssStylesCollectionConf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Reader (collection)
    public static Map<String, Map<String, AssStyle>> readCollection(){
        Map<String, Map<String, AssStyle>> collection = new HashMap<>();
        
        if(FOLDER.exists() == true){
            for(File file : FOLDER.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".scc");
                }
            })){
                Map<String, AssStyle> styles = new HashMap<>();
                try(FileInputStream fis = new FileInputStream(file); 
                    InputStreamReader isr = new InputStreamReader(fis, AssCommon.detectCharset(file.getPath())); 
                    BufferedReader br = new BufferedReader(isr);){

                    String line;
                    while((line = br.readLine()) != null){
                        if(line.startsWith("Style")){
                            AssStyle style = AssStyle.create(line);
                            styles.put(style.getName(), style); 
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AssStylesCollectionConf.class.getName()).log(Level.SEVERE, null, ex);
                }
                collection.put(file.getName().substring(file.getName().lastIndexOf(".")+1), styles);
            }
        }
        
        return collection;
    }
}
