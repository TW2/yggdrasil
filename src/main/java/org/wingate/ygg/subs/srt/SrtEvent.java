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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class SrtEvent {
    
    private int entryNumber = 0;
    private Time start = Time.create(0L);
    private Time end = Time.create(1000L);
    private String text = "";

    public SrtEvent() {
    }
    
    public static SrtEvent createFromSRT(String SRT){
        SrtEvent ev = new SrtEvent();
        
        try(StringReader sr = new StringReader(SRT); 
                BufferedReader br = new BufferedReader(sr);){
            String line;
            int countType = 0;
            while((line = br.readLine()) != null){
                if(countType == 0){                    
                    ev.entryNumber = Integer.parseInt(line);
                }else if(countType == 1){
                    String[] t = line.split("-->");
                    ev.start = Time.create(t[0].trim());
                    ev.end = Time.create(t[1].trim());                    
                }else if(countType >= 2){
                    ev.text += ev.text.isEmpty() == false ? "\n" + line : line;
                }
                countType += 1;
            }
        } catch (IOException ex) {
            Logger.getLogger(SrtEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ev;
    }
    
    public static String getSrtEventLine(SrtEvent ev){
        StringBuilder sb = new StringBuilder(ev.getEntryNumber()).append("\n");
        sb.append(ev.getStart().toSRTTime());
        sb.append(" --> ").append(ev.getEnd().toSRTTime()).append("\n");
        sb.append(ev.getText()).append("\n\n");
        return sb.toString();
    }

    public int getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(int entryNumber) {
        this.entryNumber = entryNumber;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
