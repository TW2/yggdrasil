/*
 * Copyright (C) 2018 util2
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
package org.wingate.ygg.ass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class ASS {
    
    // File
    private static File assFile = null;
    
    private static List<Event> events = new ArrayList<>();
    private static Map<String, Style> styles = new HashMap<>();
    private static List<String> names = new ArrayList<>();
    
    //[Script Info]
    private static String title = "Default Aegisub file";                       // Title
    private static String scriptType = "v4.00+";                                // ScriptType: v4.00+
    private static String wrapStyle = "2";                                      // WrapStyle: 2
    private static String scaled = "yes";                                       // ScaledBorderAndShadow: yes
    private static String matrix = "TV.601";                                    // YCbCr Matrix: TV.601
    private static String resX = "1920";                                        // PlayResX: 1920
    private static String resY = "1080";                                        // PlayResY: 1080
    
    //[Aegisub Project Garbage]
    private static String aegisLastStyleStorage = "Default";                    // Last Style Storage: Default
    private static String aegisAudioFile = "";                                  // Audio File: ../relative/path/to/file.extension
    private static String aegisVideoFile = "";                                  // Video File: ../relative/path/to/file.extension
    private static String aegisVideoARMode = "4";                               // Video AR Mode: 4
    private static String aegisVideoARValue = "1.777778";                       // Video AR Value: 1.777778
    private static String aegisVideoZoomPercent = "1.000000";                   // Video Zoom Percent: 1.000000
    private static String aegisActiveLine = "1";                                // Active Line: 27
    private static String aegisVideoPosition = "0";                             // Video Position: 24

    public ASS() {
        
    }
    
    public void clearData(){
        events.clear();
        styles.clear();
        names.clear();
    }
    
    public static ASS NoFileToLoad(){
        ASS ass = new ASS();
        
        ass.getStyles().put("Default", Style.getDefault());
        
        return ass;
    }
    
    public static ASS Read(String path){
        assFile = new File(path);        
        ASS ass = new ASS();
        ass.clearData();
        // Read file by line
        try(FileInputStream fis = new FileInputStream(path); 
                InputStreamReader isr = new InputStreamReader(fis, Common.detectCharset(path)); 
                BufferedReader br = new BufferedReader(isr);){
            String line;
            while((line = br.readLine()) != null){
                //[Script Info]
                if(line.startsWith("Title")){ title = line.substring("Title: ".length()); }
                if(line.startsWith("ScriptType")){ scriptType = line.substring("ScriptType: ".length()); }
                if(line.startsWith("WrapStyle")){ wrapStyle = line.substring("WrapStyle: ".length()); }
                if(line.startsWith("ScaledBorderAndShadow")){ scaled = line.substring("ScaledBorderAndShadow: ".length()); }
                if(line.startsWith("YCbCr Matrix")){ matrix = line.substring("YCbCr Matrix: ".length()); }
                if(line.startsWith("PlayResX")){ resX = line.substring("PlayResX: ".length()); }
                if(line.startsWith("PlayResY")){ resY = line.substring("PlayResY: ".length()); }
                //[Aegisub Project Garbage]
                if(line.startsWith("Last Style Storage")){ aegisLastStyleStorage = line.substring("Last Style Storage: ".length()); }
                if(line.startsWith("Audio File")){ aegisAudioFile = line.substring("Audio File: ".length()); }
                if(line.startsWith("Video File")){ aegisVideoFile = line.substring("Video File: ".length()); }
                if(line.startsWith("Video AR Mode")){ aegisVideoARMode = line.substring("Video AR Mode: ".length()); }
                if(line.startsWith("Video AR Value")){ aegisVideoARValue = line.substring("Video AR Value: ".length()); }
                if(line.startsWith("Video Zoom Percent")){ aegisVideoZoomPercent = line.substring("Video Zoom Percent: ".length()); }
                if(line.startsWith("Active Line")){ aegisActiveLine = line.substring("Active Line: ".length()); }
                if(line.startsWith("Video Position")){ aegisVideoPosition = line.substring("Video Position: ".length()); }
                //[V4+ Styles]                
                if(line.startsWith("Style")){
                    Style style = Style.create(line);
                    styles.put(style.getName(), style); 
                }
                //[Events]
                if(line.startsWith("Comment") | line.startsWith("Dialogue") | line.startsWith("#Proposal") | line.startsWith("#Request")){
                    events.add(Event.createFromASS(line, styles));
                    String[] array = line.split(",", 9);
                    if(array[4].isEmpty() == false){ names.add(array[4]); }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ASS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ASS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ass;
    }
    
    public static void Save(String path, ASS ass){
        try(PrintWriter pw = new PrintWriter(path, StandardCharsets.UTF_8)){
            //[Script Info]
            pw.println("[Script Info]");
            pw.println("; This is an Advanced Sub Station (ASS) file generated by Yggdrasil!");
            pw.println("; Feel free to look at: https://github.com/TW2");
            pw.println("Title: " + title);
            pw.println("ScriptType: " + scriptType);
            pw.println("WrapStyle: " + wrapStyle);
//            pw.println("ScaledBorderAndShadow: " + scaled);
//            pw.println("YCbCr Matrix: " + matrix);
            pw.println("PlayResX: " + resX);
            pw.println("PlayResY: " + resY);
            pw.println("");
            //[Aegisub Project Garbage]
//            pw.println("[Aegisub Project Garbage]");
//            pw.println("Last Style Storage: " + aegisLastStyleStorage);
//            pw.println("Audio File: " + aegisAudioFile);
//            pw.println("Video File: " + aegisVideoFile);
//            pw.println("Video AR Mode: " + aegisVideoARMode);
//            pw.println("Video AR Value: " + aegisVideoARValue);
//            pw.println("Video Zoom Percent: " + aegisVideoZoomPercent);
//            pw.println("Active Line: " + aegisActiveLine);
//            pw.println("Video Position: " + aegisVideoPosition);
//            pw.println("");
            //[V4+ Styles]
            pw.println("[V4+ Styles]");
            pw.println("Format: Name, Fontname, Fontsize, "
                    + "PrimaryColour, SecondaryColour, OutlineColour, BackColour, "
                    + "Bold, Italic, Underline, StrikeOut, "
                    + "ScaleX, ScaleY, Spacing, Angle, "
                    + "BorderStyle, Outline, Shadow, "
                    + "Alignment, MarginL, MarginR, MarginV, Encoding");            
            if(styles.isEmpty() == true){
                pw.println(Style.getStyle());
            }else{
                styles.entrySet().forEach((entry) -> {
                    pw.println(Style.toAssStyleLine(entry.getValue()));
                });
            }
            pw.println("");
            //[Events]
            pw.println("[Events]");
            pw.println("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text");
            events.forEach((ev) -> {
                pw.println(Event.getAssEventLine(ev));
            });
            pw.println("");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ASS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ASS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String Memory(ASS ass){
        StringBuilder sb = new StringBuilder();
        
        //[Script Info]
        sb.append("[Script Info]").append("\n");
        sb.append("; This is an Advanced Sub Station (ASS) file generated by Yggdrasil!").append("\n");
        sb.append("; Feel free to look at: https://github.com/TW2").append("\n");
        sb.append("Title: ").append(title).append("\n");
        sb.append("ScriptType: ").append(scriptType).append("\n");
        sb.append("WrapStyle: ").append(wrapStyle).append("\n");
        sb.append("ScaledBorderAndShadow: ").append(scaled).append("\n");
        sb.append("YCbCr Matrix: ").append(matrix).append("\n");
        sb.append("PlayResX: ").append(resX).append("\n");
        sb.append("PlayResY: ").append(resY).append("\n");
        sb.append("").append("\n");
        //[Aegisub Project Garbage]
        sb.append("[Aegisub Project Garbage]").append("\n");
        sb.append("Last Style Storage: ").append(aegisLastStyleStorage).append("\n");
        sb.append("Audio File: ").append(aegisAudioFile).append("\n");
        sb.append("Video File: ").append(aegisVideoFile).append("\n");
        sb.append("Video AR Mode: ").append(aegisVideoARMode).append("\n");
        sb.append("Video AR Value: ").append(aegisVideoARValue).append("\n");
        sb.append("Video Zoom Percent: ").append(aegisVideoZoomPercent).append("\n");
        sb.append("Active Line: ").append(aegisActiveLine).append("\n");
        sb.append("Video Position: ").append(aegisVideoPosition).append("\n");
        sb.append("").append("\n");
        //[V4+ Styles]
        sb.append("[V4+ Styles]").append("\n");
        sb.append("Format: Name, Fontname, Fontsize, "
                + "PrimaryColour, SecondaryColour, OutlineColour, BackColour, "
                + "Bold, Italic, Underline, StrikeOut, "
                + "ScaleX, ScaleY, Spacing, Angle, "
                + "BorderStyle, Outline, Shadow, "
                + "Alignment, MarginL, MarginR, MarginV, Encoding").append("\n");
        if(styles.isEmpty() == true){
            sb.append(Style.getStyle()).append("\n");
        }else{
            styles.entrySet().forEach((entry) -> {
                sb.append(Style.toAssStyleLine(entry.getValue())).append("\n");
            });
        }
        sb.append("").append("\n");
        //[Events]
        sb.append("[Events]").append("\n");
        sb.append("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text").append("\n");
        events.forEach((ev) -> {
            sb.append(Event.getAssEventLine(ev)).append("\n");
        });
        sb.append("").append("\n");
        
        return sb.toString();
    }

    //--------------------------------------------------------------------------
    
    public void setTitle(String title) {
        ASS.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setScriptType(String scriptType) {
        ASS.scriptType = scriptType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setWrapStyle(String wrapStyle) {
        ASS.wrapStyle = wrapStyle;
    }

    public String getWrapStyle() {
        return wrapStyle;
    }

    public void setScaled(String scaled) {
        ASS.scaled = scaled;
    }

    public String getScaled() {
        return scaled;
    }

    public void setMatrix(String matrix) {
        ASS.matrix = matrix;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setResX(String resX) {
        ASS.resX = resX;
    }

    public String getResX() {
        return resX;
    }

    public void setResY(String resY) {
        ASS.resY = resY;
    }

    public String getResY() {
        return resY;
    }

    public void setAegisLastStyleStorage(String aegisLastStyleStorage) {
        ASS.aegisLastStyleStorage = aegisLastStyleStorage;
    }

    public String getAegisLastStyleStorage() {
        return aegisLastStyleStorage;
    }

    public void setAegisAudioFile(String aegisAudioFile) {
        ASS.aegisAudioFile = aegisAudioFile;
    }

    public String getAegisAudioFile() {
        return aegisAudioFile;
    }

    public void setAegisVideoFile(String aegisVideoFile) {
        ASS.aegisVideoFile = aegisVideoFile;
    }

    public String getAegisVideoFile() {
        return aegisVideoFile;
    }

    public void setAegisVideoARMode(String aegisVideoARMode) {
        ASS.aegisVideoARMode = aegisVideoARMode;
    }

    public String getAegisVideoARMode() {
        return aegisVideoARMode;
    }

    public void setAegisVideoARValue(String aegisVideoARValue) {
        ASS.aegisVideoARValue = aegisVideoARValue;
    }

    public String getAegisVideoARValue() {
        return aegisVideoARValue;
    }

    public void setAegisVideoZoomPercent(String aegisVideoZoomPercent) {
        ASS.aegisVideoZoomPercent = aegisVideoZoomPercent;
    }

    public String getAegisVideoZoomPercent() {
        return aegisVideoZoomPercent;
    }

    public void setAegisActiveLine(String aegisActiveLine) {
        ASS.aegisActiveLine = aegisActiveLine;
    }

    public String getAegisActiveLine() {
        return aegisActiveLine;
    }

    public void setAegisVideoPosition(String aegisVideoPosition) {
        ASS.aegisVideoPosition = aegisVideoPosition;
    }

    public String getAegisVideoPosition() {
        return aegisVideoPosition;
    }

    public void setEvents(List<Event> events) {
        ASS.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setStyles(Map<String, Style> styles) {
        ASS.styles = styles;
    }

    public Map<String, Style> getStyles() {
        return styles;
    }

    public void setNames(List<String> names) {
        ASS.names = names;
    }

    public List<String> getNames() {
        return names;
    }

    public File getAssFile() {
        return assFile;
    }
    
}
