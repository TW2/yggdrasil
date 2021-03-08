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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class SsbEvent {
    
    /*
    The EVENTS section describes what to render at all.
    Every source block tells the renderer: “when render what”.
    Format of source blocks is...
    
    start-end | macro | note | text
    
    start-end
    ---------
    Start and end are the times when to start and end rendering.
    Times have one of the following formats...
    
    Time Based
    
    [[[hours:]minutes:]seconds.]milliseconds
    
    Event Based
    
    'event-id'
    
    You will be able to pass an array of event-id's to the frame render 
    function of SSB which will allow you to control when lines should be shown 
    from the outside.
    
    macro
    -----
    macro is the name of a macro defined in the MACROS section.
    Content of it will be inserted to the beginning of source block text.
    
    note
    ----
    Note is just an information beside, renderers ignore it.
        
    text
    ----
    Text is a mix of style tags and geometries.
    Everything what should be rendered is written here.
    */
    
    private SsbEventType type = SsbEventType.Dialogue;
    
    private Time start = Time.create(0L);
    private Time end = Time.create(0L);
    
    private String id = null;
    private boolean useId = false;
    
    private SsbMacro macro = null;
    private String note = null;
    private String text = null;

    public SsbEvent() {
    }
    
    public static SsbEvent create(Time start, Time end, SsbMacro macro, String note, String text){
        SsbEvent ev = new SsbEvent();
        
        ev.start = start;
        ev.end = end;
        
        ev.macro = macro;
        ev.note = note;
        ev.text = text;
        
        return ev;
    }
    
    public static SsbEvent create(String id, SsbMacro macro, String note, String text){
        SsbEvent ev = new SsbEvent();
        
        ev.id = id;
        ev.useId = true;
        
        ev.macro = macro;
        ev.note = note;
        ev.text = text;
        
        return ev;
    }

    public SsbEventType getType() {
        return type;
    }

    public void setType(SsbEventType type) {
        this.type = type;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUseId() {
        return useId;
    }

    public void setUseId(boolean useId) {
        this.useId = useId;
    }

    public SsbMacro getMacro() {
        return macro;
    }

    public void setMacro(SsbMacro macro) {
        this.macro = macro;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String toEvent(){
        // start-end | macro | note | text
        StringBuilder event = new StringBuilder();
        event.append(useId == true ? "'" + getId() + "'" : getEventTime());
        event.append("|");
        event.append(getMacro());
        event.append("|");
        event.append(getNote());
        event.append("|");
        event.append(getText());
        
        return event.toString();
    }
    
    public static String toEvent(SsbEvent ev){
        // start-end | macro | note | text
        StringBuilder event = new StringBuilder();
        event.append(ev.isUseId() == true ? "'" + ev.getId() + "'" : ev.getEventTime());
        event.append("|");
        event.append(ev.getMacro());
        event.append("|");
        event.append(ev.getNote());
        event.append("|");
        event.append(ev.getText());
        
        return event.toString();
    }
    
    private String getEventTime(){
        // [[[hours:]minutes:]seconds.]milliseconds
        
        /* Start */
        int h_start = start.getHours();
        int m_start = start.getMinutes();
        int s_start = start.getSeconds();
        int ms_start = start.getMilliseconds();
        
        String startValue = "";
        startValue += h_start > 0 ? h_start + ":" : "";
        startValue += m_start > 0 | !startValue.isEmpty() ? m_start + ":" : "";
        startValue += s_start > 0 | !startValue.isEmpty() ? s_start + "." : "";
        startValue += Integer.toString(ms_start);
        
        /* End */
        int h_end = end.getHours();
        int m_end = end.getMinutes();
        int s_end = end.getSeconds();
        int ms_end = end.getMilliseconds();
        
        String endValue = "";
        endValue += h_end > 0 ? h_end + ":" : "";
        endValue += m_end > 0 | !endValue.isEmpty() ? m_end + ":" : "";
        endValue += s_end > 0 | !endValue.isEmpty() ? s_end + "." : "";
        endValue += Integer.toString(ms_end);
        
        // start-end
        return startValue + "-" + endValue;
    }
    
    /**
     * From start-end
     * @param entry
     * @return start time + end time
     */
    public static Time[] getStartEnd(String entry){
        Time[] ts = new Time[2];
        
        String[] t = entry.split("-");
        
        Pattern p = Pattern.compile("(?<h>\\d*).*(?<m>\\d*).*(?<s>\\d*).*(?<ms>\\d+)");
        Matcher m = p.matcher(t[0]);
        
        if(m.find()){
            Time start = Time.create(0L);
            if(m.groupCount() > 4) start.setHours(Integer.parseInt(m.group("h")));
            if(m.groupCount() > 3) start.setMinutes(Integer.parseInt(m.group("m")));
            if(m.groupCount() > 2) start.setSeconds(Integer.parseInt(m.group("s")));
            if(m.groupCount() > 1) start.setMilliseconds(Integer.parseInt(m.group("ms")));
            ts[0] = start;
        }
        
        m = p.matcher(t[1]);
        
        if(m.find()){
            Time end = Time.create(0L);
            if(m.groupCount() > 4) end.setHours(Integer.parseInt(m.group("h")));
            if(m.groupCount() > 3) end.setMinutes(Integer.parseInt(m.group("m")));
            if(m.groupCount() > 2) end.setSeconds(Integer.parseInt(m.group("s")));
            if(m.groupCount() > 1) end.setMilliseconds(Integer.parseInt(m.group("ms")));
            ts[1] = end;
        }
        
        return ts;
    }
    
    public static SsbEvent fromLine(String line){
        String[] t = line.split("|");
        if(t[0].matches("'[^']+'")){
            return create(
                    t[0],                               // id
                    SsbMacro.setMacroFromLine(t[1]),    // macros
                    t[2],                               // note
                    t[3]                                // text
            );
        }else{
            Time[] ts = getStartEnd(t[0]);
            return create(
                    ts[0], 
                    ts[1], 
                    SsbMacro.setMacroFromLine(t[1]),    // macros
                    t[2],                               // note
                    t[3]                                // text
            );
        }
    }
}
