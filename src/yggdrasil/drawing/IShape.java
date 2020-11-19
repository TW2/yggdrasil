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
package yggdrasil.drawing;

import yggdrasil.drawing.layers.Layer;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author util2
 */
public interface IShape {
    
    public String getShapeName();
    public void setShapeName(String shapeName);
    
    public Layer getLayer();
    public void setLayer(Layer layer);
    
    public void draw(Graphics2D g);
    
    public void drawOperations(Graphics2D g);
    
    public boolean quickModify(Point2D oldXY, Point2D newXY);
    
}
