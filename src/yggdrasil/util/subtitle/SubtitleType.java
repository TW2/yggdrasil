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
package yggdrasil.util.subtitle;

/**
 *
 * @author util2
 */
public enum SubtitleType {
    NONE("UKW", "Undefined"),
    ASS("ASS", "Advanced Sub Station"),
    YVES("VES", "Yggdrasil, Vectors, Enhanced Subtitles"),
    SSB("SSB", "Sub Station Beta");
    
    String name;
    String definition;
    
    private SubtitleType(String name, String definition){
        this.name = name;
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "(*." + name.toLowerCase() + ") " + definition + " files";
    }
    
    public static SubtitleType get(String search){
        SubtitleType type = NONE;
        
        for(SubtitleType st : values()){
            if(st.getName().equalsIgnoreCase(search) == true){
                type = st;
                break;
            }
            if(st.getDefinition().equalsIgnoreCase(search) == true){
                type = st;
                break;
            }
        }
        
        return type;
    }
}
