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
package test;

import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class SSBaio {
    
    public enum View {
        Perspective("Perspective"),
        Orthogonal("Orthogonal");
        
        String parameter;
        
        private View(String parameter){
            this.parameter = parameter;
        }
        
        public String getParameter(){
            return parameter;
        }
    }
    
    public enum EventType {
        Note, Text, Proposal, Request;
    }
    
    public static class Macro {

        private Macro() {
        }
        
        public static Macro createDefault(){
            Macro m = new Macro();
            
            return m;
        }
    }
    
    public static class MacroDatabase {
        
    }
    
    public static class Event {
        
        private EventType eventType = EventType.Text;
        private boolean idEnabled = false;
        private String id = "";
        private Time start = Time.create(0L);
        private Time end = Time.create(1000L);
        private Macro macro = Macro.createDefault();
        private String note = "";
        private String text = "";

        public Event() {
        }

        public EventType getEventType() {
            return eventType;
        }

        public void setEventType(EventType eventType) {
            this.eventType = eventType;
        }

        public boolean isIdEnabled() {
            return idEnabled;
        }

        public void setIdEnabled(boolean idEnabled) {
            this.idEnabled = idEnabled;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public Macro getMacro() {
            return macro;
        }

        public void setMacro(Macro macro) {
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
        
        
    } 
    
    // ##### INFO #####
    private String infoTitle = "";
    private String infoAuthor = "";
    private String infoVersion = "";
    private String infoDescription = "";
    
    // ##### TARGET #####
    private int targetWidth = 1280;
    private int targetHeight = 720;
    private int targetDepth = 1000;
    private View targetView = View.Perspective;
    
    // ##### EVENTS #####
    
}
