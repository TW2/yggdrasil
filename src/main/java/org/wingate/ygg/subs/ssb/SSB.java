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
import java.util.List;
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
    
    private List<SsbMacro> macros = new ArrayList<>();
    private List<SsbEvent> events = new ArrayList<>();
    private List<SsbResource> resources = new ArrayList<>();
    
    private String subInfoTitle = "";
    private String subInfoAuthor = "";
    private String subInfoVersion = "";
    private String subInfoDescription = "";
    
    private int subTargetWidth = 1280;
    private int subTargetHeight = 720;
    private int subTargetDepth = 1000;
    private String subTargetView = "perspective";

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
                    if(line.startsWith("Title: ")) ssb.subInfoTitle = line.substring("Title: ".length());
                    if(line.startsWith("Author: ")) ssb.subInfoAuthor = line.substring("Author: ".length());
                    if(line.startsWith("Version: ")) ssb.subInfoVersion = line.substring("Version: ".length());
                    if(line.startsWith("Description: ")) ssb.subInfoDescription = line.substring("Description: ".length());
                }
                
                // Elements     (Target)
                if(area == Area.Target){
                    if(line.startsWith("Width: ")) ssb.subTargetWidth = Integer.parseInt(line.substring("Width: ".length()));
                    if(line.startsWith("Height: ")) ssb.subTargetHeight = Integer.parseInt(line.substring("Height: ".length()));
                    if(line.startsWith("Depth: ")) ssb.subTargetDepth = Integer.parseInt(line.substring("Depth: ".length()));
                    if(line.startsWith("View: ")) ssb.subTargetView = line.substring("View: ".length());
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
            pw.println("Title: " + ssb.subInfoTitle);
            pw.println("Author: " + ssb.subInfoAuthor);
            pw.println("Version: " + ssb.subInfoVersion);
            pw.println("Description: " + ssb.subInfoDescription);
            
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
            pw.println("Width: " + Integer.toString(ssb.subTargetWidth));
            pw.println("Height: " + Integer.toString(ssb.subTargetHeight));
            pw.println("Depth: " + Integer.toString(ssb.subTargetDepth));
            pw.println("View: " + ssb.subTargetView);
            
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

    public String getSubInfoTitle() {
        return subInfoTitle;
    }

    public void setSubInfoTitle(String subInfoTitle) {
        this.subInfoTitle = subInfoTitle;
    }

    public String getSubInfoAuthor() {
        return subInfoAuthor;
    }

    public void setSubInfoAuthor(String subInfoAuthor) {
        this.subInfoAuthor = subInfoAuthor;
    }

    public String getSubInfoVersion() {
        return subInfoVersion;
    }

    public void setSubInfoVersion(String subInfoVersion) {
        this.subInfoVersion = subInfoVersion;
    }

    public String getSubInfoDescription() {
        return subInfoDescription;
    }

    public void setSubInfoDescription(String subInfoDescription) {
        this.subInfoDescription = subInfoDescription;
    }

    public int getSubTargetWidth() {
        return subTargetWidth;
    }

    public void setSubTargetWidth(int subTargetWidth) {
        this.subTargetWidth = subTargetWidth;
    }

    public int getSubTargetHeight() {
        return subTargetHeight;
    }

    public void setSubTargetHeight(int subTargetHeight) {
        this.subTargetHeight = subTargetHeight;
    }

    public int getSubTargetDepth() {
        return subTargetDepth;
    }

    public void setSubTargetDepth(int subTargetDepth) {
        this.subTargetDepth = subTargetDepth;
    }

    public String getSubTargetView() {
        return subTargetView;
    }

    public void setSubTargetView(String subTargetView) {
        this.subTargetView = subTargetView;
    }
    
}
