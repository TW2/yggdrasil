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

import java.util.ArrayList;
import java.util.List;
import org.wingate.ygg.util.Time;
import java.util.Map;

/**
 *
 * @author util2
 */
public class Event {
    
    public enum LineType{
        Dialogue("Dialogue", "Dialogue"),
        Comment("Comment", "Comment"),
        Proposal("#Proposal", "#Proposal"),
        Request("#Request", "#Request"),
        Sound("#ASX_Sound", "Sound"),
        Movie("#ASX_Movie", "Movie"),
        Picture("#ASX_Picture", "Picture"),
        Commands("#ASX_Commands", "Command");
        
        String type;
        String origin;
        
        private LineType(String type, String origin){
            this.type = type;
            this.origin = origin;
        }

        @Override
        public String toString() {
            return type;
        }
        
        public static LineType from(String description){
            LineType lineType = Dialogue;
            for(LineType lt : values()){
                if(lt.toString().equalsIgnoreCase(description) == true){
                    lineType = lt;
                    break;
                }
                if(lt.getOrigin().equalsIgnoreCase(description) == true){
                    lineType = lt;
                    break;
                }
            }
            return lineType;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getOrigin() {
            return origin;
        }
        
    }
    
    private LineType lineType = LineType.Comment;
    private int layer = 0;
    private int marginL = 0;
    private int marginR = 0;
    private int marginV = 0; 
    private Time startTime = Time.create(0L);
    private Time endTime = Time.create(1000L);
    private Style style = Style.getDefault();
    private String name = "";
    private String effect = "";
    private String text = "";
    private List<String> splitKaraoke = new ArrayList<>();

    public Event() {
        
    }
    
    /**
     * Get an Event object from an ASS event line
     * Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
     * @param ASS an ASS line
     * @param styles
     * @return An Event object
     */
    public static Event createFromASS(String ASS, Map<String, Style> styles){
        Event ev = new Event();
        String[] array = ASS.split(",", 10);
        if(array.length == 10){
            // Line type (Format)
            switch(array[0].substring(0, array[0].indexOf(":"))){
                case "Dialogue": ev.setLineType(LineType.Dialogue); break;
                case "Comment": ev.setLineType(LineType.Comment); break;
                case "#Proposal": ev.setLineType(LineType.Proposal); break;
                case "#Request": ev.setLineType(LineType.Request); break;
                default: ev.setLineType(LineType.Comment); break;
            }
            // Layer
            ev.setLayer(Integer.parseInt(array[0].substring(array[0].lastIndexOf(":") + 2)));
            // Start - End
            ev.setStartTime(Time.create(array[1]));
            ev.setEndTime(Time.create(array[2]));
            // Style
            ev.setStyle(styles.get(array[3]));
            // Name
            ev.setName(array[4]);
            // Margins LRV
            ev.setMarginL(Integer.parseInt(array[5]));
            ev.setMarginR(Integer.parseInt(array[6]));
            ev.setMarginV(Integer.parseInt(array[7]));
            // Effect
            ev.setEffect(array[8]);
            // Text
            ev.setText(array[9]);
        }
        return ev;
    }
    
    /**
     * Get an Event object from an ASS event line
     * Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
     * @param ASS an ASS line
     * @return An Event object
     */
    public static Event createFromASS(String ASS){
        Event ev = new Event();
        String[] array = ASS.split(",", 10);
        if(array.length == 10){
            // Line type (Format)
            switch(array[0].substring(0, array[0].indexOf(":"))){
                case "Dialogue": ev.setLineType(LineType.Dialogue); break;
                case "Comment": ev.setLineType(LineType.Comment); break;
                case "#Proposal": ev.setLineType(LineType.Proposal); break;
                case "#Request": ev.setLineType(LineType.Request); break;
                default: ev.setLineType(LineType.Comment); break;
            }
            // Layer
            ev.setLayer(Integer.parseInt(array[0].substring(array[0].lastIndexOf(":") + 2)));
            // Start - End
            ev.setStartTime(Time.create(array[1]));
            ev.setEndTime(Time.create(array[2]));
            // Style
            ev.setStyle(Style.getDefault());
            // Name
            ev.setName(array[4]);
            // Margins LRV
            ev.setMarginL(Integer.parseInt(array[5]));
            ev.setMarginR(Integer.parseInt(array[6]));
            ev.setMarginV(Integer.parseInt(array[7]));
            // Effect
            ev.setEffect(array[8]);
            // Text
            ev.setText(array[9]);
        }
        return ev;
    }
    
    /**
     * Get an ASS line for this event
     * Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
     * @param ev An event
     * @return An ASS formatted line
     */
    public static String getAssEventLine(Event ev){
        String line = "";
        line += ev.getLineType().toString().concat(": ");
        line += Integer.toString(ev.getLayer()).concat(",");
        line += ev.getStartTime().toASSTime().concat(",");
        line += ev.getEndTime().toASSTime().concat(",");
        line += ev.getStyle().getName().concat(",");
        line += ev.getName().concat(",");
        line += Integer.toString(ev.getMarginL()).concat(",");
        line += Integer.toString(ev.getMarginR()).concat(",");
        line += Integer.toString(ev.getMarginV()).concat(",");
        line += ev.getEffect().concat(",");
        line += ev.getText();
        return line;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public void setMarginL(int marginL) {
        this.marginL = marginL;
    }

    public int getMarginL() {
        return marginL;
    }

    public void setMarginR(int marginR) {
        this.marginR = marginR;
    }

    public int getMarginR() {
        return marginR;
    }

    public void setMarginV(int marginV) {
        this.marginV = marginV;
    }

    public int getMarginV() {
        return marginV;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getEffect() {
        return effect;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public List<String> getSplitKaraoke() {
        return splitKaraoke;
    }

    public void setSplitKaraoke(List<String> splitKaraoke) {
        this.splitKaraoke = splitKaraoke;
    }
    
    public Event getCopy(){
        Event nv = new Event();
        
        nv.setLineType(lineType);
        nv.setLayer(layer);
        nv.setStartTime(startTime);
        nv.setEndTime(endTime);
        nv.setStyle(style);
        nv.setName(name);
        nv.setMarginL(marginL);
        nv.setMarginR(marginR);
        nv.setMarginV(marginV);
        nv.setEffect(effect);
        nv.setText(text);
        
        return nv;
    }
}
