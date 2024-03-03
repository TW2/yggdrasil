/*
 * Copyright (C) 2024 util2
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
package org.wingate.ygg.ass;

import java.awt.image.BufferedImage;

/**
 *
 * @author util2
 */
public class SubtitlesRender {
    
    private final ASS ass;

    public SubtitlesRender(ASS ass) {
        this.ass = ass;
    }
    
    public BufferedImage get(AssTime t){
        int w = Integer.parseInt(ass.getResX());
        int h = Integer.parseInt(ass.getResY());
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        
        
        return output;
    }
    
    public static BufferedImage get(String text, AssStyle style, int w, int h){
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        
        
        return output;
    }
}
