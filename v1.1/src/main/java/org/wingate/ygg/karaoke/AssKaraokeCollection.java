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
package org.wingate.ygg.karaoke;

import java.util.ArrayList;
import java.util.List;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.ass.Event;

/**
 *
 * @author util2
 */
public class AssKaraokeCollection {
    private List<AssKaraSyls> syllables = new ArrayList<>();
    private Event event;

    private AssKaraokeCollection() {
    }
    
    public static AssKaraokeCollection create(Event ev, List<Integer> split){
        AssKaraokeCollection akc = new AssKaraokeCollection();
        
        if(split == null){
            split = new ArrayList<>();
        }
        
        akc.event = ev;
        
        Time sentenceLength = Time.substract(ev.getStartTime(), ev.getEndTime());
        long defaultMsForAll = Time.toMillisecondsTime(sentenceLength) / (!split.isEmpty() ? split.size() : 1);
        
        Time start = ev.getStartTime(), end = ev.getEndTime();
        String syl;
        
        if(split.isEmpty() == false){
            for(int i=0; i<split.size(); i++){
                if(i == 0){
                    end = Time.addition(ev.getStartTime(), Time.create(defaultMsForAll));
                    syl = ev.getText().substring(0, split.get(i));
                }else if(i == split.size() - 1){
                    start = end;
                    end = ev.getEndTime();
                    syl = ev.getText().substring(split.get(i-1)+1);
                }else{
                    start = end;
                    end = Time.addition(start, Time.create(defaultMsForAll));
                    syl = ev.getText().substring(split.get(i-1)+1, split.get(i));
                }
                akc.add(start, end, syl, ev, i);
            }
        }else{
            akc.add(start, end, ev.getText(), ev, 0);
        }
            
        return akc;
    }
    
    private void add(Time start, Time end, String syl, Event ev, int index){
        AssKaraSyls aks = AssKaraSyls.create(
                start, 
                end, 
                syl, 
                ev, 
                index);
        syllables.add(aks);
    }

    public List<AssKaraSyls> getSyllables() {
        return syllables;
    }

    public void setSyllables(List<AssKaraSyls> syllables) {
        this.syllables = syllables;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
}
