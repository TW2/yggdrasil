/*
 * Copyright (C) 2021 util2
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
package org.wingate.ygg.subs.ssb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author util2
 */
public class SSB {
    
    // File
    private File ssbFile = null;
    
    /*
    #INFO
    Title: My new project
    Author: Youka
    Version: 16.06.2012
    Description: First concept of a new render format.

    #TARGET
    Width: 1280
    Height: 720
    Depth: 1000
    View: perspective

    #MACROS
    Default: [bold=y]
    Mine: [bold=n;color=FF0000]
    Another: [Mine;position=100,200,-1;rotate-z=180]I'm a

    #EVENTS
    //0-2.0|||This line is a comment over 2 seconds!
    2.0-5:0.0|Another|Hello, i'm a note!|red,    rotated\ntext over multiple lines.
    5:0.0-2:5:0.0|Mine|Draw sth.|[mode=shape;texture=RAMEN]m 0 0 l 50.5 0 50.5 20.125 0 20.125
    10:0.0-10:50:0.0||${Another}Lets scale some text to double its size!|[animate=500, 1000, [scale=2]]This text is getting huge
    20.0.0-21.0.0|||[font=MaterialIcon]some_circle_ligature
    'show-something'|Default||This will only be shown when the event id is given

    #RESOURCES
    Texture: RAMEN,url,../ramen.tga
    // Will we support ligaturs? Pretty important for icon fonts
    Font: MaterialIcon,regular,data,AAEAAAAKAIAAAwAgT1MvMnwMf9s...
    */
    
    private Map<SsbSubInfo, String> subInfos = new HashMap<>();
    private Map<SsbTargetSubInfo, String> targetInfos = new HashMap<>();
    private List<SsbMacro> macros = new ArrayList<>();
    private List<SsbEvent> events = new ArrayList<>();
    private List<SsbResource> resources = new ArrayList<>();

    public SSB() {
    }
    
    public static SSB NoFileToLoad(){
        SSB ssb = new SSB();
        
        ssb.macros.add(SsbMacro.getDefaultMacro());
        
        return ssb;
    }
    
    public static SSB Read(String path){
        SSB ssb = new SSB();
        ssb.ssbFile = new File(path);
        
        try(FileReader fr = new FileReader(path, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(fr)){
            Area area = Area.Info;
            String line;
            
            while((line = br.readLine()) != null){
                // Parts
                if(line.startsWith("#INFO") == true) area = Area.Info;
                if(line.startsWith("#TARGET") == true) area = Area.Target;
                if(line.startsWith("#MACROS") == true) area = Area.Macros;
                if(line.startsWith("#EVENTS") == true) area = Area.Events;
                if(line.startsWith("#RESOURCES") == true) area = Area.Resources;
                
                // Elements     (Infos)
                if(area == Area.Info){
                    if(line.startsWith("Title: ")) ssb.subInfos.put(SsbSubInfo.Title, line.substring("Title: ".length()));
                    if(line.startsWith("Author: ")) ssb.subInfos.put(SsbSubInfo.Author, line.substring("Author: ".length()));
                    if(line.startsWith("Version: ")) ssb.subInfos.put(SsbSubInfo.Version, line.substring("Version: ".length()));
                    if(line.startsWith("Description: ")) ssb.subInfos.put(SsbSubInfo.Description, line.substring("Description: ".length()));
                }
                
                // Elements     (Target)
                if(area == Area.Target){
                    if(line.startsWith("Width: ")) ssb.targetInfos.put(SsbTargetSubInfo.Width, line.substring("Width: ".length()));
                    if(line.startsWith("Height: ")) ssb.targetInfos.put(SsbTargetSubInfo.Height, line.substring("Height: ".length()));
                    if(line.startsWith("Depth: ")) ssb.targetInfos.put(SsbTargetSubInfo.Depth, line.substring("Depth: ".length()));
                    if(line.startsWith("View: ")) ssb.targetInfos.put(SsbTargetSubInfo.View, line.substring("View: ".length()));
                }
                // Elements     (Macros)
                if(area == Area.Macros){
                    ssb.macros.add(SsbMacro.setMacroFromLine(line));
                }
                
                // Elements     (Events)
                if(area == Area.Events){
                    ssb.events.add(SsbEvent.fromLine(line));
                }
                
                // Elements     (Resources)
                if(area == Area.Resources){
                    ssb.resources.add(SsbResource.create(line));
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(SSB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ssb;
    }
    
    public static void Save(String path, SSB ssb){
        try(PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)){
            //------------------------------------------------------------------
            // Infos
            //------------------------------------------------------------------
            /*
                #INFO
                Title: My new project
                Author: Youka
                Version: 16.06.2012
                Description: First concept of a new render format.
            */
            pw.println("#INFO");
            pw.println("Title: " + ssb.subInfos.get(SsbSubInfo.Title));
            pw.println("Author: " + ssb.subInfos.get(SsbSubInfo.Author));
            pw.println("Version: " + ssb.subInfos.get(SsbSubInfo.Version));
            pw.println("Description: " + ssb.subInfos.get(SsbSubInfo.Description));
            
            pw.println("");
            
            //------------------------------------------------------------------
            // Target Infos
            //------------------------------------------------------------------
            /*
                #TARGET
                Width: 1280
                Height: 720
                Depth: 1000
                View: perspective
            */
            pw.println("#TARGET");
            pw.println("Width: " + ssb.targetInfos.get(SsbTargetSubInfo.Width));
            pw.println("Height: " + ssb.targetInfos.get(SsbTargetSubInfo.Height));
            pw.println("Depth: " + ssb.targetInfos.get(SsbTargetSubInfo.Depth));
            pw.println("View: " + ssb.targetInfos.get(SsbTargetSubInfo.View));
            
            pw.println("");
            
            //------------------------------------------------------------------
            // Macros
            //------------------------------------------------------------------
            /*
                #MACROS
                Default: [bold=y]
                Mine: [bold=n;color=FF0000]
                Another: [Mine;position=100,200,-1;rotate-z=180]I'm a
            */
            pw.println("#MACROS");
            ssb.macros.forEach(macro -> {
                pw.println(SsbMacro.getMacro(macro));
            });
            
            pw.println("");
            
            //------------------------------------------------------------------
            // Events
            //------------------------------------------------------------------
            /*
                #EVENTS
                //0-2.0|||This line is a comment over 2 seconds!
                2.0-5:0.0|Another|Hello, i'm a note!|red,    rotated\ntext over multiple lines.
                5:0.0-2:5:0.0|Mine|Draw sth.|[mode=shape;texture=RAMEN]m 0 0 l 50.5 0 50.5 20.125 0 20.125
                10:0.0-10:50:0.0||${Another}Lets scale some text to double its size!|[animate=500, 1000, [scale=2]]This text is getting huge
                20.0.0-21.0.0|||[font=MaterialIcon]some_circle_ligature
                'show-something'|Default||This will only be shown when the event id is given
            */
            pw.println("#EVENTS");
            ssb.events.forEach(ev -> {
                String newline = "";
                if(ev.getType() == SsbEventType.Comment){
                    newline += "//";
                }
                newline += ev.toEvent();
                pw.println(newline);
            });
            
            pw.println("");
            
            //------------------------------------------------------------------
            // Resources
            //------------------------------------------------------------------
            /*
                #RESOURCES
                Texture: RAMEN,url,../ramen.tga
                // Will we support ligaturs? Pretty important for icon fonts
                Font: MaterialIcon,regular,data,AAEAAAAKAIAAAwAgT1MvMnwMf9s...
            */
            pw.println("#RESOURCES");
            ssb.resources.forEach(res -> {
                pw.println(SsbResource.getResourceLine(res));
            });
        } catch (IOException ex) {
            Logger.getLogger(SSB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public enum Area{
        Info, Target, Macros, Events, Resources
    }

    public File getSsbFile() {
        return ssbFile;
    }

    public void setSsbFile(File ssbFile) {
        this.ssbFile = ssbFile;
    }

    public Map<SsbSubInfo, String> getSubInfos() {
        return subInfos;
    }

    public void setSubInfos(Map<SsbSubInfo, String> subInfos) {
        this.subInfos = subInfos;
    }

    public Map<SsbTargetSubInfo, String> getTargetInfos() {
        return targetInfos;
    }

    public void setTargetInfos(Map<SsbTargetSubInfo, String> targetInfos) {
        this.targetInfos = targetInfos;
    }

    public List<SsbMacro> getMacros() {
        return macros;
    }

    public void setMacros(List<SsbMacro> macros) {
        this.macros = macros;
    }

    public List<SsbEvent> getEvents() {
        return events;
    }

    public void setEvents(List<SsbEvent> events) {
        this.events = events;
    }

    public List<SsbResource> getResources() {
        return resources;
    }

    public void setResources(List<SsbResource> resources) {
        this.resources = resources;
    }
    
}
