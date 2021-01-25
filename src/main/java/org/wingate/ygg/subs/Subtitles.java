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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public enum Subtitles {
    SSA("ssa"),
    ASS("ass"),
    SRT("srt"),
    VES("ves"),
    WebVTT("vtt"),
    SSB("ssb"),
    SMI("smi"),
    SUB("sub"),
    SUP("sup");
    
    String extension;
    
    private Subtitles(String extension){
        this.extension  = extension;
    }
    
    public static BufferedImage getSubs(File sub, Time t, int width, int height){
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        if(sub == null){
            return output;
        }
        
        switch(getSubtitlesType(sub.getName())){
            case ASS -> {
                try {
                    output = AssYggyApply.getSubsImage(sub.getPath(), t);
                } catch (Exception ex) {
                    Logger.getLogger(Subtitles.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return output;
    }

    public String getExtension() {
        return extension;
    }
    
    public static Subtitles getSubtitlesType(String filenameOrPath){
        Subtitles output = ASS;
        
        int lastIndex = filenameOrPath.lastIndexOf(".") + 1;
        String ext = filenameOrPath.substring(lastIndex);
        
        for(Subtitles choice : values()){
            if(choice.getExtension().equalsIgnoreCase(ext) == true){
                output = choice;
                break;
            }
        }
        
        return output;
    }
}
