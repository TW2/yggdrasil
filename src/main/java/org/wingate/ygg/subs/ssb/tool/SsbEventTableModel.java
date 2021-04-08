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
package org.wingate.ygg.subs.ssb.tool;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.ssb.SsbEvent;
import org.wingate.ygg.subs.ssb.SsbEventType;
import org.wingate.ygg.subs.ssb.SsbMacro;

/**
 *
 * @author util2
 */
public class SsbEventTableModel extends AbstractTableModel {
    
    List<SsbEvent> evts = new ArrayList<>();
    Language in = null;
    ISO_3166 get = null;

    public SsbEventTableModel(Language in, ISO_3166 get) {
        this.in = in;
        this.get = get;
    }

    @Override
    public int getRowCount() {
        return evts.size();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {        
        switch(columnIndex){
            case 0: return rowIndex + 1; // Index 0 = line number
            case 1: return evts.get(rowIndex).getType(); // Index 1 = type
            case 2:
                String event_or_time = evts.get(rowIndex).isUseId() ?
                        evts.get(rowIndex).getId() : 
                        evts.get(rowIndex).getStart().toProgramExtendedTime() + "%" +
                        evts.get(rowIndex).getEnd().toProgramExtendedTime();
                return event_or_time; // Index 2 = event_or_time
            case 3: return evts.get(rowIndex).getMacro(); // Index 3 = macro
            case 4: return evts.get(rowIndex).getNote(); // Index 4 = note
            case 5: return Help.getCPL(evts.get(rowIndex).getText()); // Index 11 = CPL
            case 6:
                Time start = evts.get(rowIndex).getStart();
                Time end = evts.get(rowIndex).getEnd();
                String text = evts.get(rowIndex).getText();
                return Help.getCPS(start, end, text); // Index 12 = CPS
            case 7: return evts.get(rowIndex).getText(); // Index 13 = text
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0: return Integer.class; // Index 0 = line number
            case 1: return SsbEventType.class; // Index 1 = type
            case 2: return String.class; // Index 2 = idt
            case 3: return SsbMacro.class; // Index 3 = macro
            case 4: return String.class; // Index 4 = note
            case 5: return Integer.class; // Index 5 = cpl
            case 6: return Integer.class; // Index 6 = cps
            case 7: return String.class; // Index 7 = text
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0: return "#"; // Index 0 = line number
            case 1: return in.getTranslated("SSB_Type", get, "Type"); // Index 1 = type
            case 2: return in.getTranslated("SSB_IDT", get, "Event ID or time"); // Index 2 = idt
            case 3: return in.getTranslated("SSB_Macro", get, "Macro"); // Index 3 = macro
            case 4: return in.getTranslated("SSB_Note", get, "Note"); // Index 4 = note
            case 5: return in.getTranslated("SSB_CPL", get, "CPL"); // Index 5 = cpl
            case 6: return in.getTranslated("SSB_CPS", get, "CPS"); // Index 6 = cps
            case 7: return in.getTranslated("SSB_Text", get, "Text"); // Index 7 = text
        }
        return "?";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void insertAll(List<SsbEvent> events){
        for(int i=0; i<events.size(); i++){
            evts.add(events.get(i));
        }
    }
    
    public void insertOne(SsbEvent ev){
        evts.add(ev);
    }
    
    public void insertOneAt(SsbEvent ev, int index){
        evts.add(index, ev);
    }
    
    public void removeAll(){
        evts.clear();
    }
    
    public void removeOne(SsbEvent ev){
        String wanted = SsbEvent.toEvent(ev);
        int lineINDEX = -1;
        for(int i=0; i< evts.size(); i++){
            String rawlineFromModel = SsbEvent.toEvent(evts.get(i));
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
    
    public SsbEvent getEventAt(int row){
        return evts.get(row);
    }
    
    public List<SsbEvent> getAllEvents(){
        return new ArrayList<>(evts);
    }
    
    public void changeEventAt(SsbEvent newEvent, int row){
        evts.add(row + 1, newEvent);
        evts.remove(row);
    }
    
    // Index 0 -> Line number
    public int getLineNumber(int row){
        return row + 1;
    }
    
    // Index 1 -> Type
    public SsbEventType getLineType(int row){
        return evts.get(row).getType();
    }
    
    // Index 2 -> Event ID or Time (Start-End)
    public String getIdOrTime(int row){
        return evts.get(row).isUseId() ?
                evts.get(row).getId() :
                evts.get(row).getStart().toProgramExtendedTime() + "%" +
                evts.get(row).getEnd().toProgramExtendedTime();
    }
    
    // Index 2 -> Event ID or Time (Start-End) >> Event ID
    public String getEventID(int row){
        return evts.get(row).getId();
    }
    
    // Index 2 -> Event ID or Time (Start-End) >> Start
    public Time getStartTime(int row){
        return evts.get(row).getStart();
    }
    
    // Index 2 -> Event ID or Time (Start-End) >> End
    public Time getEndTime(int row){
        return evts.get(row).getEnd();
    }
    
    // Index 3 -> Macro
    public SsbMacro getMacro(int row){
        return evts.get(row).getMacro();
    }
    
    // Index 4 -> Note
    public String getNote(int row){
        return evts.get(row).getNote();
    }
    
    // Index 5 -> CPL
    public int getHelpCPL(int row){
        return Help.getCPL(evts.get(row).getText());
    }
    
    // Index 6 -> CPS
    public int getHelpCPS(int row){
        Time start = evts.get(row).getStart();
        Time end = evts.get(row).getEnd();
        String text = evts.get(row).getText();
        return Help.getCPS(start, end, text);
    }
    
    // Index 7 -> Text
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
