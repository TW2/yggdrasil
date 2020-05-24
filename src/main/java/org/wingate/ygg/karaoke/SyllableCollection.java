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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class SyllableCollection {
    
    private List<Syllable> syllables = new ArrayList<>();
    private Time startTime = Time.create(0L);

    public SyllableCollection() {
    }

    public List<Syllable> getSyllables() {
        return syllables;
    }

    public void setSyllables(List<Syllable> syllables) {
        this.syllables = syllables;
    }
    
    public int getSyllableCount(){
        return syllables.size();
    }
    
    public void changeSyllable(int index, Syllable syllable){
        syllables.set(index, syllable);
    }
    
    public static SyllableCollection create(Event event, KaraokeLanguage language){
        SyllableCollection sc = new SyllableCollection();
        
        if(event.getText().contains("\\k") == true){
            Pattern p = Pattern.compile("\\{\\\\k(\\d+)\\}([^\\{]*)");
            Matcher m = p.matcher(event.getText());
            long t_ms = Time.toMillisecondsTime(event.getStartTime());
            while(m.find()){
                long duration = Integer.parseInt(m.group(1)) * 10;
                Time startSyl = Time.create(t_ms);
                Time endSyl = Time.create(duration);
                sc.syllables.add(Syllable.create(m.group(2), startSyl, endSyl));
                t_ms += duration;
            }
        }else{
            switch(language){
                case Romaji: sc.syllables = Roumaji.getLimits(event.getText()); break;
                default: break;
            }
        }
        
        return sc;
    }
    
    public String getSentence(boolean karaoke){
        String sentence = "";
        
        if(karaoke == true){
            for(int i=0; i<syllables.size(); i++){
                Syllable syl = syllables.get(i);
                sentence = sentence
                        .concat("{\\k")
                        .concat(Integer.toString((int)(Time.toMillisecondsTime(syl.getDuration()) / 10)))
                        .concat("}")
                        .concat(syl.getContent());
            }
        }else{
            for(int i=0; i<syllables.size(); i++){
                sentence += syllables.get(i).getContent();
            }
        }
        
        return sentence;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }
    
    public Time getDurationRange(int from, int to){
        Time t = syllables.get(from).getDuration();
        for (int i=from+1; i<to; i++){
            t = Time.addition(t, syllables.get(i).getDuration());
        }
        return t;
    }
    
    public void setDurationAt(int index, Time startArea, Time current){
        Time lastSylDuration = index == 0 ? Time.create(0L) : syllables.get(index - 1).getDuration();
        Time fromStart = Time.addition(startArea, current);
        Time diff = Time.substract(fromStart, lastSylDuration);
        syllables.get(index).setDuration(diff);
        System.out.println("Durée : " + diff.toDisplayTime());
    }
}
