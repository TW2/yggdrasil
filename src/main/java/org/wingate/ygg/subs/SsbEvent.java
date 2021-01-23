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
package org.wingate.ygg.subs;

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
    
    private Time start = Time.create(0L);
    private Time end = Time.create(0L);
    
    private SsbMacro macro = null;
    private String note = null;
    private String text = null;

    public SsbEvent() {
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
        StringBuilder event = new StringBuilder();
        event.append(getEventTime());
        event.append("|");
        
        
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
        startValue += s_start > 0 | !startValue.isEmpty() ? s_start + ":" : "";
        startValue += Integer.toString(ms_start);
        
        /* End */
        int h_end = end.getHours();
        int m_end = end.getMinutes();
        int s_end = end.getSeconds();
        int ms_end = end.getMilliseconds();
        
        String endValue = "";
        endValue += h_end > 0 ? h_end + ":" : "";
        endValue += m_end > 0 | !endValue.isEmpty() ? m_end + ":" : "";
        endValue += s_end > 0 | !endValue.isEmpty() ? s_end + ":" : "";
        endValue += Integer.toString(ms_end);
        
        // start-end
        return startValue + "-" + endValue;
    }
}
