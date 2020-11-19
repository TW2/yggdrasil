/*
 * Copyright (C) 2019 util2
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
package yggdrasil.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import yggdrasil.util.subtitle.ass.Event;
import yggdrasil.util.subtitle.ass.Event.LineType;
import yggdrasil.util.subtitle.ass.Style;
import yggdrasil.languages.ISO_3166;
import yggdrasil.languages.Language;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class AssEventTableModel extends AbstractTableModel {
    
    List<Event> evts = new ArrayList<>();
    Language in = null;
    ISO_3166 get = null;

    public AssEventTableModel(Language in, ISO_3166 get) {
        this.in = in;
        this.get = get;
    }

    @Override
    public int getRowCount() {
        return evts.size();
    }

    @Override
    public int getColumnCount() {
        return 14;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {        
        switch(columnIndex){
            case 0: return rowIndex + 1; // Index 0 = line number
            case 1: return evts.get(rowIndex).getLineType(); // Index 1 = line type
            case 2: return evts.get(rowIndex).getLayer(); // Index 2 = layer
            case 3: return evts.get(rowIndex).getStartTime(); // Index 3 = start time
            case 4: return evts.get(rowIndex).getEndTime(); // Index 4 = end time
            case 5: return evts.get(rowIndex).getMarginL(); // Index 5 = margin l
            case 6: return evts.get(rowIndex).getMarginR(); // Index 6 = margin r
            case 7: return evts.get(rowIndex).getMarginV(); // Index 7 = margin v
            case 8: return evts.get(rowIndex).getStyle(); // Index 8 = style
            case 9: return evts.get(rowIndex).getName(); // Index 9 = name
            case 10: return evts.get(rowIndex).getEffect(); // Index 10 = effect
            case 11:
                return Help.getCPL(evts.get(rowIndex).getText()); // Index 11 = CPL
            case 12:
                Time start = evts.get(rowIndex).getStartTime();
                Time end = evts.get(rowIndex).getEndTime();
                String text = evts.get(rowIndex).getText();
                return Help.getCPS(start, end, text); // Index 12 = CPS
            case 13: return evts.get(rowIndex).getText(); // Index 13 = text
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0: return Integer.class; // Index 0 = line number
            case 1: return LineType.class; // Index 1 = line type
            case 2: return Integer.class; // Index 2 = layer
            case 3: return Time.class; // Index 3 = start time
            case 4: return Time.class; // Index 4 = end time
            case 5: return Integer.class; // Index 5 = margin l
            case 6: return Integer.class; // Index 6 = margin r
            case 7: return Integer.class; // Index 7 = margin v
            case 8: return Style.class; // Index 8 = style
            case 9: return String.class; // Index 9 = name
            case 10: return String.class; // Index 10 = effect
            case 11: return Integer.class; // Index 11 = CPL
            case 12: return Integer.class; // Index 12 = CPS
            case 13: return String.class; // Index 13 = text
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0: return "#"; // Index 0 = line number
            case 1: return in.getTranslated("Type", get, "Type"); // Index 1 = line type
            case 2: return in.getTranslated("Layer", get, "Layer"); // Index 2 = layer
            case 3: return in.getTranslated("Start", get, "Start"); // Index 3 = start time
            case 4: return in.getTranslated("End", get, "End"); // Index 4 = end time
            case 5: return in.getTranslated("Margin_Left", get, "ML"); // Index 5 = margin l
            case 6: return in.getTranslated("Margin_Right", get, "MR"); // Index 6 = margin r
            case 7: return in.getTranslated("Vertical_Margin", get, "MV"); // Index 7 = margin v
            case 8: return in.getTranslated("Style", get, "Style"); // Index 8 = style
            case 9: return in.getTranslated("Name", get, "Name"); // Index 9 = name
            case 10: return in.getTranslated("Effect", get, "Effect"); // Index 10 = effect
            case 11: return in.getTranslated("CPL", get, "CPL"); // Index 11 = CPL
            case 12: return in.getTranslated("CPS", get, "CPS"); // Index 12 = CPS
            case 13: return in.getTranslated("Text", get, "Text"); // Index 13 = text
        }
        return "?";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void insertAll(List<Event> events){
        for(int i=0; i<events.size(); i++){
            evts.add(events.get(i));
        }
    }
    
    public void insertOne(Event ev){
        evts.add(ev);
    }
    
    public void insertOneAt(Event ev, int index){
        evts.add(index, ev);
    }
    
    public void removeAll(){
        evts.clear();
    }
    
    public void removeOne(Event ev){
        String wanted = Event.getAssEventLine(ev);
        int lineINDEX = -1;
        for(int i=0; i< evts.size(); i++){
            String rawlineFromModel = Event.getAssEventLine(evts.get(i));
            if(rawlineFromModel.equals(wanted) == true){
                lineINDEX = i;
                break;
            }
        }
        if(lineINDEX != -1){
            evts.remove(lineINDEX);
        }
    }
    
    public void removeOne(int index){
        if(index < getRowCount() && index > -1){
            evts.remove(index);
        }
    }
    
    public Event getEventAt(int row){
        return evts.get(row);
    }
    
    public List<Event> getAllEvents(){
        return new ArrayList<>(evts);
    }
    
    public void changeEventAt(Event newEvent, int row){
        evts.add(row + 1, newEvent);
        evts.remove(row);
    }
    
    // Index 0 -> Line number
    public int getLineNumber(int row){
        return row + 1;
    }
    
    // Index 1 -> Line type
    public LineType getLineType(int row){
        return evts.get(row).getLineType();
    }
    
    // Index 2 -> Layer
    public int getLayer(int row){
        return evts.get(row).getLayer();
    }
    
    // Index 3 -> Start time
    public Time getStartTime(int row){
        return evts.get(row).getStartTime();
    }
    
    // Index 4 -> End time
    public Time getEndTime(int row){
        return evts.get(row).getEndTime();
    }
    
    // Index 5 -> Margin L
    public int getMarginL(int row){
        return evts.get(row).getMarginL();
    }
    
    // Index 6 -> Margin R
    public int getMarginR(int row){
        return evts.get(row).getMarginR();
    }
    
    // Index 7 -> Margin V
    public int getMarginV(int row){
        return evts.get(row).getMarginV();
    }
    
    // Index 8 -> Style name
    public Style getStyle(int row){        
        return evts.get(row).getStyle();
    }
    
    // Index 9 -> Name or Actor
    public String getName(int row){
        return evts.get(row).getName();
    }
    
    // Index 10 -> Effect
    public String getEffect(int row){
        return evts.get(row).getEffect();
    }
    
    // Index 11 -> CPL
    public int getHelpCPL(int row){
        return Help.getCPL(evts.get(row).getText());
    }
    
    // Index 12 -> CPS
    public int getHelpCPS(int row){
        Time start = evts.get(row).getStartTime();
        Time end = evts.get(row).getEndTime();
        String text = evts.get(row).getText();
        return Help.getCPS(start, end, text);
    }
    
    // Index 13 -> Text
    public String getText(int row){
        return evts.get(row).getText();
    }
    
    static class Help {        
        public static int getCPL(String text){
            String[] pieces = text.split("\\\\N");
            int max = -1;
            for(String piece : pieces){
                max = Math.max(max, piece.length());
            }
            return max;
        }
        
        public static int getCPS(Time s, Time e, String text){
            Time dur = Time.substract(s, e);
            String ref = text.replaceAll("\\\\N", "").replaceAll(" ", "");
            int duration = (int)Time.getLengthInSeconds(dur);
            return duration == 0 ? ref.length() : ref.length() / duration;
        }
    }
}
