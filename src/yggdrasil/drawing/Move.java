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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author util2
 */
public class Move extends AShape {
    
    private double xa = 0f;
    private double ya = 0f;
    
    private float width = 10;
    
    private boolean moveM;

    public Move() {
        
    }

    @Override
    public void draw(Graphics2D g) {
        // Draw points
        DrawingPoint.create(xa, ya, false).draw(g); // Last point
    }

    @Override
    public void drawOperations(Graphics2D g) {
        // Draw points
        // -----------
        // Last point
        DrawingPoint origin = DrawingPoint.create(xa, ya, false);
        origin.setPointColor(Color.magenta);
        origin.draw(g);
    }
    
    public static Move create(double xa, double ya, boolean moveM){
        Move move = new Move();
        
        move.xa = xa;
        move.ya = ya;
        
        move.moveM = moveM;
        
        return move;
    }
    
    public static Move create(Point2D pa, boolean moveM){
        return create(pa.getX(), pa.getY(), moveM);
    }
    
    public Point2D getPointA(){
        return new Point2D.Double(xa, ya);
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isMoveM() {
        return moveM;
    }

    public void setMoveM(boolean moveM) {
        this.moveM = moveM;
    }
    
    @Override
    public boolean quickModify(Point2D oldXY, Point2D newXY) {
        if(getPointA().distance(oldXY) < 2d){
            setXa(newXY.getX());
            setYa(newXY.getY());
            return true;
        }
        return false;
    }
    
}
