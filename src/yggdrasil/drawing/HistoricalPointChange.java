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

import java.awt.geom.Point2D;

/**
 *
 * @author util2
 */
public class HistoricalPointChange {
    
    private AShape shape;
    private AShape anotherShape = null;
    private Point2D oldLocation;
    private Point2D newLocation;

    public HistoricalPointChange() {
    }

    public HistoricalPointChange(AShape shape, Point2D oldLocation) {
        this.shape = shape;
        this.oldLocation = oldLocation;
    }

    public HistoricalPointChange(AShape shape, Point2D oldLocation, Point2D newLocation) {
        this.shape = shape;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    public HistoricalPointChange(AShape shape, AShape another, Point2D oldLocation) {
        this.shape = shape;
        this.anotherShape = another;
        this.oldLocation = oldLocation;
    }

    public HistoricalPointChange(AShape shape, AShape another, Point2D oldLocation, Point2D newLocation) {
        this.shape = shape;
        this.anotherShape = another;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }
    
    public boolean hasSecondShape(){
        return anotherShape != null;
    }

    public AShape getShape() {
        return shape;
    }

    public void setShape(AShape shape) {
        this.shape = shape;
    }

    public AShape getAnotherShape() {
        return anotherShape;
    }

    public void setAnotherShape(AShape anotherShape) {
        this.anotherShape = anotherShape;
    }

    public Point2D getOldLocation() {
        return oldLocation;
    }

    public void setOldLocation(Point2D oldLocation) {
        this.oldLocation = oldLocation;
    }

    public Point2D getNewLocation() {
        return newLocation;
    }

    public void setNewLocation(Point2D newLocation) {
        this.newLocation = newLocation;
    }
    
}
