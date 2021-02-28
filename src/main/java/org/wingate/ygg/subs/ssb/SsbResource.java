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

/**
 *
 * @author util2
 */
public class SsbResource {
    /*
    RESOURCES section

    The RESOURCES section describes all resources that will be used within the subtitle format. This includes images and fonts.
    
    Texture

    Texture: TEXTURE_ID,data|url,base_64_or_url
    TEXTURE_ID: can be any character but must not contain comma.
    data|url: You need to tell if the data after this is actual data or an url
    base_64_or_url: base64 encoded string or an url as an absolute path to the file on the file system.
    
    Fonts

    Font: FONT_FAMILY,style,data|url,base_64_or_url
    FONT_FAMILY: can be any character but must not contain comma.
    style: regular, bold, italic, bold-italic
    data|url: You need to tell if the data after this is actual data or an url
    base_64_or_url: base64 encoded string or a url as an absolute path to the file on the file system.
    
    */
    
    public enum Style{
        Plain("regular"),
        Bold("bold"),
        Italic("italic"),
        BoldItalic("bold-italic");
        
        String type;        
        
        private Style(String type){
            this.type = type;
        }

        public String getType() {
            return type;
        }
        
        public static Style get(String entry){
            Style s = Plain;
            
            for(Style style : values()){
                if(style.getType().equalsIgnoreCase(entry)){
                    s = style;
                    break;
                }
            }
            
            return s;
        }
    }
    
    private SsbResourceType type = SsbResourceType.Font;
    private boolean dataUsed = true;
    private String name = "Arial";
    private Style style = Style.Plain;
    private String content = "";
    
    public SsbResource() {
        
    }
    
    public static SsbResource create(String line){
        SsbResource r = new SsbResource();
        // Texture: TEXTURE_ID,data|url,base_64_or_url
        // Font: FONT_FAMILY,style,data|url,base_64_or_url
        
        switch(SsbResourceType.get(line)){
            case Texture -> {
                r.type = SsbResourceType.Texture;
                String s = line.substring("Texture: ".length());
                String[] t = s.split(",", 2);
                r.name = t[0];                
                r.dataUsed = t[1].equalsIgnoreCase("data");
                r.content = t[2];
            }
            case Font -> {
                r.type = SsbResourceType.Font;
                String s = line.substring("Font: ".length());
                String[] t = s.split(",", 3);
                r.name = t[0];
                r.style = Style.get(t[1]);
                r.dataUsed = t[2].equalsIgnoreCase("data");
                r.content = t[2];
            }
        }
        
        return r;
    }
    
    public static String getResourceLine(SsbResource res){
        // Texture: TEXTURE_ID,data|url,base_64_or_url
        // Font: FONT_FAMILY,style,data|url,base_64_or_url
        
        switch(res.getType()){
            case Texture -> {
                return "Texture: " + res.getName() + ","
                        + (res.isDataUsed() ? "data" : "url") + "," 
                        + res.getContent();
            }
            case Font -> {
                return "Font: " + res.getName() + ","
                        + res.getStyle().getType() + ","
                        + (res.isDataUsed() ? "data" : "url") + "," 
                        + res.getContent();
            }
        }
        
        return "";
    }

    public SsbResourceType getType() {
        return type;
    }

    public void setType(SsbResourceType type) {
        this.type = type;
    }

    public boolean isDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(boolean dataUsed) {
        this.dataUsed = dataUsed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
