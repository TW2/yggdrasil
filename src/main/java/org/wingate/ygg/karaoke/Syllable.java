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

import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class Syllable {
    
    private String content = "";
    private Time duration = Time.create(0L);
    private Time start = Time.create(0L);
    private Time end = Time.create(0L);
    
    public Syllable() {
    }
    
    public static Syllable create(String content){
        Syllable syl = new Syllable();
        
        syl.content = content;
        syl.duration = Time.create(0L);
        
        return syl;
    }
    
    public static Syllable create(String content, Time start, Time end){
        Syllable syl = new Syllable();
        
        syl.content = content;
        syl.duration = Time.substract(start, end);
        syl.start = start;
        syl.end = end;
        
        return syl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
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
    
    public void updateTime(Time start, Time end){
        this.start = start;
        this.end = end;
        this.duration = Time.substract(start, end);
    }
}
