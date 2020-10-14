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
package org.wingate.ygg.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author util2
 */
public class DrawingPoint extends AShape {
    
    private Color pointColor = Color.blue;
    private Color controlPointColor = Color.orange;
    private double x = 0f;
    private double y = 0f;
    
    private double size = 10;
    
    private boolean controlPoint = false;

    public DrawingPoint() {
        
    }

    @Override
    public void draw(Graphics2D g) {
        Color oldColor = g.getColor();
        
        if(controlPoint == true){
            g.setColor(controlPointColor);
            g.fill(new Ellipse2D.Double(x - size/2, y - size/2, size, size));
        }else{
            g.setColor(pointColor);
            g.fill(new Rectangle2D.Double(x - size/2, y - size/2, size, size));
        }
        
        g.setColor(oldColor);
    }

    @Override
    public void drawOperations(Graphics2D g) {
        Color oldColor = g.getColor();
        
        if(controlPoint == true){
            g.setColor(Color.magenta);
            g.fill(new Ellipse2D.Double(x - size/2, y - size/2, size, size));
        }else{
            g.setColor(Color.magenta);
            g.fill(new Rectangle2D.Double(x - size/2, y - size/2, size, size));
        }
        
        g.setColor(oldColor);
    }
    
    public static DrawingPoint create(double x, double y, boolean controlPoint){
        DrawingPoint drawingPoint = new DrawingPoint();
        
        drawingPoint.x = x;
        drawingPoint.y = y;
        drawingPoint.controlPoint = controlPoint;
        
        return drawingPoint;
    }
    
    public static DrawingPoint create(Point2D p, boolean controlPoint){
        DrawingPoint drawingPoint = new DrawingPoint();
        
        drawingPoint.x = p.getX();
        drawingPoint.y = p.getY();
        drawingPoint.controlPoint = controlPoint;
        
        return drawingPoint;
    }
    
    public Point2D getPoint2D(){
        return new Point2D.Double(x, y);
    }
    
    public void setPoint2D(Point2D p){
        this.x = p.getX();
        this.y = p.getY();
    }

    public Color getPointColor() {
        return pointColor;
    }

    public void setPointColor(Color pointColor) {
        this.pointColor = pointColor;
    }

    public Color getControlPointColor() {
        return controlPointColor;
    }

    public void setControlPointColor(Color controlPointColor) {
        this.controlPointColor = controlPointColor;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isControlPoint() {
        return controlPoint;
    }

    public void setControlPoint(boolean controlPoint) {
        this.controlPoint = controlPoint;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
    
}
