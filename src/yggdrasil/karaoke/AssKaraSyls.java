/*
 * Copyright (C) 2020 util2
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
package yggdrasil.karaoke;

import org.wingate.timelibrary.Time;
import yggdrasil.util.subtitle.ass.Event;

/**
 *
 * @author util2
 */
public class AssKaraSyls {
    private Time start;
    private Time end;
    private String syllable;
    private Event parentEvent;
    private int index;

    public AssKaraSyls() {
    }
    
    public static AssKaraSyls create(Time start, Time end, String syllable, Event parent, int index){
        AssKaraSyls ks = new AssKaraSyls();
        
        ks.start = start;
        ks.end = end;
        ks.syllable = syllable;
        ks.parentEvent = parent;
        ks.index = index;
        
        return ks;
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

    public String getSyllable() {
        return syllable;
    }

    public void setSyllable(String syllable) {
        this.syllable = syllable;
    }

    public Event getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(Event parentEvent) {
        this.parentEvent = parentEvent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public Time getMsLength(){
        return Time.substract(start, end);
    }
}
