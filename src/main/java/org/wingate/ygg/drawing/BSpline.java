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
import org.wingate.ygg.drawing.bspline.BSplineCurve;
import org.wingate.ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class BSpline extends AShape {
    
    private Color bezierSplinesColor = DrawColor.burly_wood.getColor();
    private float curveWidth = 1f;
    private float dashedWidth = 1f;
    
    private BSplineCurve bsc = null;

    public BSpline() {
    }

    @Override
    public void draw(Graphics2D g) {
        if(bsc != null){
            Stroke oldStroke = g.getStroke();
            Color oldColor = g.getColor();

            // Draw dashed control lines
            g.setStroke(new BasicStroke(
                    dashedWidth,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    10f,
                    new float[]{2f, 2f},
                    2f
            ));

            g.setColor(Color.gray);
            
            if(bsc.getControlPoints().size() > 1){
                Point2D previous = bsc.getControlPoints().get(0);
                for(int i=1; i < bsc.getControlPoints().size(); i++){
                    Point2D last = bsc.getControlPoints().get(i);
                    g.draw(new Line2D.Double(
                            new Point2D.Double(previous.getX(), previous.getY()),
                            new Point2D.Double(last.getX(), last.getY())
                    ));
                    previous = last;
                }                
            }
            
            // Draw curve
            g.setColor(bezierSplinesColor);
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            bsc.paintCurve(g, 5, true);
            
            g.setStroke(oldStroke);
            g.setColor(oldColor);
            
            for(int i=0; i < bsc.getControlPoints().size(); i++){
                Point2D p = bsc.getControlPoints().get(i);
                if(i == 0 | i == bsc.getControlPoints().size() - 1){
                    // Premier OU dernier point :
                    DrawingPoint.create(p.getX(), p.getY(), false).draw(g); // Origin or Last
                }else{
                    DrawingPoint.create(p.getX(), p.getY(), true).draw(g); // Control points
                }
            }
        }
    }

    @Override
    public void drawOperations(Graphics2D g) {
        if(bsc != null){
            Stroke oldStroke = g.getStroke();
            Color oldColor = g.getColor();
            
            g.setColor(Color.magenta);

            // Draw dashed control lines
            g.setStroke(new BasicStroke(
                    dashedWidth,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    10f,
                    new float[]{2f, 2f},
                    2f
            ));
            
            if(bsc.getControlPoints().size() > 1){
                Point2D previous = bsc.getControlPoints().get(0);
                for(int i=1; i < bsc.getControlPoints().size(); i++){
                    Point2D last = bsc.getControlPoints().get(i);
                    g.draw(new Line2D.Double(
                            new Point2D.Double(previous.getX(), previous.getY()),
                            new Point2D.Double(last.getX(), last.getY())
                    ));
                    previous = last;
                }                
            }
            
            // Draw curve
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            bsc.paintCurve(g, 5, true);
            
            g.setStroke(oldStroke);
            g.setColor(oldColor);
            
            for(int i=0; i < bsc.getControlPoints().size(); i++){
                Point2D p = bsc.getControlPoints().get(i);
                if(i == 0 | i == bsc.getControlPoints().size() - 1){
                    // Premier OU dernier point :
                    DrawingPoint.create(p.getX(), p.getY(), false).drawOperations(g); // Origin or Last
                }else{
                    DrawingPoint.create(p.getX(), p.getY(), true).drawOperations(g); // Control points
                }
            }
        }
    }

    public Color getBezierSplinesColor() {
        return bezierSplinesColor;
    }

    public void setBezierSplinesColor(Color bezierSplinesColor) {
        this.bezierSplinesColor = bezierSplinesColor;
    }

    public float getCurveWidth() {
        return curveWidth;
    }

    public void setCurveWidth(float curveWidth) {
        this.curveWidth = curveWidth;
    }

    public float getDashedWidth() {
        return dashedWidth;
    }

    public void setDashedWidth(float dashedWidth) {
        this.dashedWidth = dashedWidth;
    }

    public BSplineCurve getBSplineCurve() {
        return bsc;
    }
    
    public void addPoint(Point2D p){
        if(bsc == null){
            // Si l'objet spline n'existe pas :
            bsc = new BSplineCurve(p);
        }else{
            // Si l'objet spline existe :
            bsc.addControlPoint(p);
        }
    }
    
    public void removePoint(Point2D p){
        if(bsc != null){
            if(bsc.getControlPoints().size() > 1){
                bsc.removeControlPoint(p);
            }else if(bsc.getControlPoints().size() == 1){
                bsc = null;
            }
        }        
    }
    
    @Override
    public boolean quickModify(Point2D oldXY, Point2D newXY) {
        if (bsc != null){
            for(int i=0; i<bsc.getControlPoints().size(); i++){
                Point2D p = bsc.getControlPoints().get(i);
                if(p.distance(oldXY) < 2d){
                    bsc.getControlPoints().get(i).setLocation(newXY);
                    return true;
                }
            }
        }
        return false;
    }
}
