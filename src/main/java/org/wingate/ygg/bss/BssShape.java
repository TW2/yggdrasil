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
package org.wingate.ygg.bss;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Map;

/**
 *
 * @author util2
 */
public class BssShape {
    
    private final int index;
    
    private final double x, y, width, height;
    private final GeneralPath generalPath = new GeneralPath();
    private final Map<String, Object> tags;

    public BssShape(Map<String, Object> tags, Shape shape, int index,
            double x, double y, double width, double height) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.tags = tags;
        
        // Swing path
        if(shape != null){
            generalPath.append(shape, false);
        }        
    }

    public int getIndex() {
        return index;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public GeneralPath getGeneralPath() {
        return generalPath;
    }
    
}
