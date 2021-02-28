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
package org.wingate.ygg.util;

import java.awt.image.BufferedImage;
import java.io.File;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.subs.ass.AssYggyApply;

/**
 *
 * @author util2
 */
public class SubtitlesChoice {
    
    private File subs = null;

    public SubtitlesChoice() {
    }
    
    public void setSubtitlesFile(File subs){
        this.subs = subs;
    }
    
    public BufferedImage getAtTime(Time t){
        BufferedImage img = null;
        
        if(subs != null && subs.exists() == true){
            if(subs.getName().toLowerCase().endsWith(".ass") == true){
                try { img = AssYggyApply.getSubsImage(subs.getPath(), t); } catch (Exception ex) { }
            }
        }        
        
        return img;
    }
}
