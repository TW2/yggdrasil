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
package org.wingate.ygg.subs.srt;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;

/**
 *
 * @author util2
 */
public class SrtEventTableModel extends AbstractTableModel {
    
    List<SrtEvent> evts = new ArrayList<>();
    Language in = null;
    ISO_3166 get = null;

    public SrtEventTableModel(Language in, ISO_3166 get) {
        this.in = in;
        this.get = get;
    }

    @Override
    public int getRowCount() {
        return evts.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0 -> { return rowIndex + 1; } // Index 0 = line number 
            case 1 -> { return evts.get(rowIndex).getStart(); }// Index 1 = start time            
            case 2 -> { return evts.get(rowIndex).getEnd(); }// Index 2 = end time            
            case 3 -> { return Help.getCPL(evts.get(rowIndex).getText()); }// Index 3 = CPL            
            case 4 -> {
                Time start = evts.get(rowIndex).getStart();
                Time end = evts.get(rowIndex).getEnd();
                String text = evts.get(rowIndex).getText();
                return Help.getCPS(start, end, text); // Index 4 = CPS
            }
            case 5 -> { return evts.get(rowIndex).getText(); }// Index 5 = text
            
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0 -> { return Integer.class; }// Index 0 = line number            
            case 1 -> { return Time.class; }// Index 3 = start time            
            case 2 -> { return Time.class; }// Index 4 = end time            
            case 3 -> { return Integer.class; }// Index 11 = CPL            
            case 4 -> { return Integer.class; }// Index 12 = CPS            
            case 5 -> { return String.class; }// Index 13 = text            
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0 -> { return "#"; }// Index 0 = line number            
            case 1 -> { return in.getTranslated("Start", get, "Start"); }// Index 3 = start time            
            case 2 -> { return in.getTranslated("End", get, "End"); }// Index 4 = end time            
            case 3 -> { return in.getTranslated("CPL", get, "CPL"); }// Index 11 = CPL            
            case 4 -> { return in.getTranslated("CPS", get, "CPS"); }// Index 12 = CPS            
            case 5 -> { return in.getTranslated("Text", get, "Text"); }// Index 13 = text            
        }
        return "?";
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void insertAll(List<SrtEvent> events){
        for(int i=0; i<events.size(); i++){
            evts.add(events.get(i));
        }
    }
    
    public void insertOne(SrtEvent ev){
        evts.add(ev);
    }
    
    public void insertOneAt(SrtEvent ev, int index){
        evts.add(index, ev);
    }
    
    public void removeAll(){
        evts.clear();
    }
    
    public void removeOne(SrtEvent ev){
        String wanted = SrtEvent.getSrtEventLine(ev);
        int lineINDEX = -1;
        for(int i=0; i< evts.size(); i++){
            String rawlineFromModel = SrtEvent.getSrtEventLine(evts.get(i));
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
    
    public SrtEvent getEventAt(int row){
        return evts.get(row);
    }
    
    public List<SrtEvent> getAllEvents(){
        return new ArrayList<>(evts);
    }
    
    public void changeEventAt(SrtEvent newEvent, int row){
        evts.add(row + 1, newEvent);
        evts.remove(row);
    }
    
    // Index 0 -> Line number
    public int getLineNumber(int row){
        return row + 1;
    }
    
    // Index 3 -> Start time
    public Time getStartTime(int row){
        return evts.get(row).getStart();
    }
    
    // Index 4 -> End time
    public Time getEndTime(int row){
        return evts.get(row).getEnd();
    }
    
    // Index 11 -> CPL
    public int getHelpCPL(int row){
        return Help.getCPL(evts.get(row).getText());
    }
    
    // Index 12 -> CPS
    public int getHelpCPS(int row){
        Time start = evts.get(row).getStart();
        Time end = evts.get(row).getEnd();
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
