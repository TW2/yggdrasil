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
package org.wingate.ygg.util;

import java.awt.Color;

/**
 *
 * @author util2
 */
public class ColorUtils {
    
    private final Color startColor = DrawColor.lime.getColor();
    private final Color endColor = DrawColor.purple.getColor();

    public ColorUtils() {
    }
    
    public static Color getCurrentColor(int startFrame, int endFrame, int currentFrame){
        ColorUtils cu = new ColorUtils();
        
        float percent = (float)endFrame - (float)currentFrame;
        
        float diffRED = (float)cu.endColor.getRed() - (float)cu.startColor.getRed();
        float diffGREEN = (float)cu.endColor.getGreen() - (float)cu.startColor.getGreen();
        float diffBLUE = (float)cu.endColor.getBlue()- (float)cu.startColor.getBlue();
        
        int newRED = Math.round((float)cu.startColor.getRed() + diffRED * percent);
        int newGREEN = Math.round((float)cu.startColor.getGreen() + diffGREEN * percent);
        int newBLUE = Math.round((float)cu.startColor.getBlue()+ diffBLUE * percent);
        
        newRED = newRED > 255 ? 255 : (newRED < 0 ? 0 : newRED);
        newGREEN = newGREEN > 255 ? 255 : (newGREEN < 0 ? 0 : newGREEN);
        newBLUE = newBLUE > 255 ? 255 : (newBLUE < 0 ? 0 : newBLUE);
        
        return new Color(newRED, newGREEN, newBLUE);
    }
}
