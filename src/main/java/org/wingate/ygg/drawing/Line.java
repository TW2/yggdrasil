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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author util2
 */
public class Line extends AShape {
    
    private Color lineColor = Color.red;
    private double xa = 0f;
    private double ya = 0f;
    private double xb = 0f;
    private double yb = 0f;
    
    private float width = 1f;

    public Line() {
        
    }

    @Override
    public void draw(Graphics2D g) {
        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();
        
        // Draw line
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(getLineAB());
        
        g.setStroke(oldStroke);
        g.setColor(oldColor);
        
        // Draw points
        DrawingPoint.create(xa, ya, false).draw(g); // Origin
        DrawingPoint.create(xb, yb, false).draw(g); // Last point
    }
    
    @Override
    public void drawOperations(Graphics2D g) {
        // Draw line
        // ---------
        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();
        
        // Draw line
        g.setColor(Color.magenta);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(getLineAB());
        
        g.setStroke(oldStroke);
        g.setColor(oldColor);
        
        // Draw points
        // -----------
        // Origin
        DrawingPoint origin = DrawingPoint.create(xa, ya, false);
        origin.setPointColor(Color.magenta);
        origin.draw(g);
        // Last point
        DrawingPoint last = DrawingPoint.create(xb, yb, false);
        last.setPointColor(Color.magenta);
        last.draw(g);
    }
    
    public static Line create(double xa, double ya, double xb, double yb){
        Line line = new Line();
        
        line.xa = xa;
        line.ya = ya;
        line.xb = xb;
        line.yb = yb;
        
        return line;
    }
    
    public static Line create(Point2D pa, Point2D pb){
        Line line = new Line();
        
        line.xa = pa.getX();
        line.ya = pa.getY();
        line.xb = pb.getX();
        line.yb = pb.getY();
        
        return line;
    }
    
    public static Line create(Line2D l){
        Line line = new Line();
        
        line.xa = l.getX1();
        line.ya = l.getY1();
        line.xb = l.getX2();
        line.yb = l.getY2();
        
        return line;
    }
    
    public Point2D getPointA(){
        return new Point2D.Double(xa, ya);
    }
    
    public Point2D getPointB(){
        return new Point2D.Double(xb, yb);
    }
    
    public Line2D getLineAB(){
        return new Line2D.Double(xa, ya, xb, yb);
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public double getXa() {
        return xa;
    }

    public void setXa(double xa) {
        this.xa = xa;
    }

    public double getYa() {
        return ya;
    }

    public void setYa(double ya) {
        this.ya = ya;
    }

    public double getXb() {
        return xb;
    }

    public void setXb(double xb) {
        this.xb = xb;
    }

    public double getYb() {
        return yb;
    }

    public void setYb(double yb) {
        this.yb = yb;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public boolean quickModify(Point2D oldXY, Point2D newXY) {
        if(getPointA().distance(oldXY) < 2d){
            setXa(newXY.getX());
            setYa(newXY.getY());
            return true;
        }
        if(getPointB().distance(oldXY) < 2d){
            setXb(newXY.getX());
            setYb(newXY.getY());
            return true;
        }
        return false;
    }
    
}
