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

import java.awt.Point;
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
    private int syllableIndex = -1;
    
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
            
            Time t = event.getStartTime();
            
            while(m.find()){
                Time duration = Time.create((long)Integer.parseInt(m.group(1)) * 10L);
                Time startSyl = t; // Start time
                t = Time.addition(t, duration); // End time -> start + duration
                Time endSyl = t; // End time
                sc.syllables.add(Syllable.create(m.group(2), startSyl, endSyl));
            }
        }else{
            switch(language){
                case Romaji: sc.syllables = Roumaji.getLimits(event.getText()); break;
                default: break;
            }
            if(sc.syllables.isEmpty() == false){
                Time startOfSentence = event.getStartTime();
                Time endOfSentence = event.getEndTime();
                Time durationOfSentence = Time.substract(startOfSentence, endOfSentence);
                
                Time partOfKaraoke = Time.divide(durationOfSentence, Time.create((long)sc.getSyllableCount()));
                Time renewStart = event.getStartTime();
                
                for(Syllable syl : sc.syllables){
                    syl.setStart(renewStart);
                    renewStart = Time.addition(renewStart, partOfKaraoke);
                    syl.setEnd(renewStart);
                    syl.setDuration(partOfKaraoke);
                }
            }
        }
        
        if(sc.syllables.isEmpty() == false){
            sc.syllableIndex = 0;            
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
        return syllables.isEmpty() ? Time.create(0L) : syllables.get(0).getStart();
    }
    
    public Time getEndTime() {
        return syllables.isEmpty() ? Time.create(0L) : syllables.get(syllables.size() - 1).getEnd();
    }
    
    public Time getStartTimeAt(int index) {
        return syllables.isEmpty() | syllables.size() - 1 < index ? Time.create(0L) : syllables.get(index).getStart();
    }
    
    public Time getEndTimeAt(int index) {
        return syllables.isEmpty() | syllables.size() - 1 < index ? Time.create(0L) : syllables.get(index).getEnd();
    }

    public int getSyllableIndex() {
        return syllableIndex;
    }

    public void setSyllableIndex(int syllableIndex) {
        this.syllableIndex = syllableIndex;
    }
    
    public Point getStartPointAt(int index){
        return syllables.get(index).getStartPoint();
    }
    
    public Point getEndPointAt(int index){
        return syllables.get(index).getEndPoint();
    }
    
    public Syllable getSyllableAt(int index){
        return syllables.get(index);
    }
}
