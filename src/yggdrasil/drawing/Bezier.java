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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 *
 * @author util2
 */
public class Bezier extends AShape {
    
    private Color cubicColor = Color.magenta;
    private Color quadraticColor = Color.cyan;
    
    private double xo = 0f;
    private double yo = 0f;
    private double cpxa = 0f;
    private double cpya = 0f;
    private double cpxb = 0f;
    private double cpyb = 0f;
    private double cpx = 0f;
    private double cpy = 0f;
    private double x = 0f;
    private double y = 0f;
    
    private boolean cubic = true;
    
    private float curveWidth = 1f;
    private float dashedWidth = 1f;

    public Bezier() {
        
    }

    @Override
    public void draw(Graphics2D g) {
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
        
        if(cubic == true){
            g.draw(new Line2D.Double(new Point2D.Double(xo, yo), new Point2D.Double(cpxa, cpya)));
            g.draw(new Line2D.Double(new Point2D.Double(cpxa, cpya), new Point2D.Double(cpxb, cpyb)));
            g.draw(new Line2D.Double(new Point2D.Double(cpxb, cpyb), new Point2D.Double(x, y)));
        }else{
            g.draw(new Line2D.Double(new Point2D.Double(xo, yo), new Point2D.Double(cpx, cpy)));
            g.draw(new Line2D.Double(new Point2D.Double(cpx, cpy), new Point2D.Double(x, y)));
        }
        
        // Draw curve
        if(cubic == true){
            g.setColor(cubicColor);
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new CubicCurve2D.Double(xo, yo, cpxa, cpya, cpxb, cpyb, x, y));
        }else{
            g.setColor(quadraticColor);
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new QuadCurve2D.Double(xo, yo, cpx, cpy, x, y));
        }
        
        g.setStroke(oldStroke);
        g.setColor(oldColor);
        
        // Draw points
        if(cubic == true){
            DrawingPoint.create(xo, yo, false).draw(g); // Origin
            DrawingPoint.create(cpxa, cpya, true).draw(g); // Control point 1
            DrawingPoint.create(cpxb, cpyb, true).draw(g); // Control point 2
            DrawingPoint.create(x, y, false).draw(g); // Last point
        }else{
            DrawingPoint.create(xo, yo, false).draw(g); // Origin
            DrawingPoint.create(cpx, cpy, true).draw(g); // Control point
            DrawingPoint.create(x, y, false).draw(g); // Last point
        }
        
        
    }
    
    @Override
    public void drawOperations(Graphics2D g) {
        Stroke oldStroke = g.getStroke();
        Color oldColor = g.getColor();
        
        // Draw curve
        if(cubic == true){
            g.setColor(Color.magenta);
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.fill(new CubicCurve2D.Double(xo, yo, cpxa, cpya, cpxb, cpyb, x, y));
        }else{
            g.setColor(Color.magenta);
            g.setStroke(new BasicStroke(curveWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.fill(new QuadCurve2D.Double(xo, yo, cpx, cpy, x, y));
        }
        
        // Draw dashed control lines
        g.setStroke(new BasicStroke(
                dashedWidth,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                10f,
                new float[]{2f, 2f},
                2f
        ));
        
        g.setColor(Color.magenta);
        
        if(cubic == true){
            g.draw(new Line2D.Double(new Point2D.Double(xo, yo), new Point2D.Double(cpxa, cpya)));
            g.draw(new Line2D.Double(new Point2D.Double(cpxa, cpya), new Point2D.Double(cpxb, cpyb)));
            g.draw(new Line2D.Double(new Point2D.Double(cpxb, cpyb), new Point2D.Double(x, y)));
        }else{
            g.draw(new Line2D.Double(new Point2D.Double(xo, yo), new Point2D.Double(cpx, cpy)));
            g.draw(new Line2D.Double(new Point2D.Double(cpx, cpy), new Point2D.Double(x, y)));
        }
        
        g.setStroke(oldStroke);
        g.setColor(oldColor);
        
        // Draw points
        if(cubic == true){
            // Origin
            DrawingPoint d1 = DrawingPoint.create(xo, yo, false);
            d1.setPointColor(Color.magenta);
            d1.draw(g);
            // Control point 1
            DrawingPoint d2 = DrawingPoint.create(cpxa, cpya, true);
            d2.setPointColor(Color.magenta);
            d2.draw(g);
            // Control point 2
            DrawingPoint d3 = DrawingPoint.create(cpxb, cpyb, true);
            d3.setPointColor(Color.magenta);
            d3.draw(g);
            // Last point
            DrawingPoint d4 = DrawingPoint.create(x, y, false);
            d4.setPointColor(Color.magenta);
            d4.draw(g);
        }else{
            // Origin
            DrawingPoint d1 = DrawingPoint.create(xo, yo, false);
            d1.setPointColor(Color.magenta);
            d1.draw(g);
            // Control point
            DrawingPoint d2 = DrawingPoint.create(cpx, cpy, true);
            d2.setPointColor(Color.magenta);
            d2.draw(g);
            // Last point
            DrawingPoint d3 = DrawingPoint.create(x, y, false);
            d3.setPointColor(Color.magenta);
            d3.draw(g);
        }
    }
    
    public static Bezier createCubic(
            double xo, double yo, 
            double cpxa, double cpya, double cpxb, double cpyb,
            double x, double y){
        Bezier bezier = new Bezier();
        
        bezier.xo = xo;
        bezier.yo = yo;
        bezier.cpxa = cpxa;
        bezier.cpya = cpya;
        bezier.cpxb = cpxb;
        bezier.cpyb = cpyb;
        bezier.x = x;
        bezier.y = y;
        
        return bezier;
    }
    
    public static Bezier createCubic(Point2D o, Point2D cpa, Point2D cpb, Point2D p){
        return createCubic(
                o.getX(), o.getY(),
                cpa.getX(), cpa.getY(), cpb.getX(), cpb.getY(),
                p.getX(), p.getY());
    }
    
    public static Bezier createCubic(Point2D o, Point2D p){
        double xdiff = p.getX() - o.getX();
        double ydiff = p.getY() - o.getY();

        double x1_3 = o.getX() + xdiff/3d;
        double x2_3 = o.getX() + xdiff*2d/3d;
        double y1_3 = o.getY() + ydiff/3d;
        double y2_3 = o.getY() + ydiff*2d/3d;
        
        return createCubic(
                o.getX(), o.getY(),
                x1_3, y1_3, x2_3, y2_3,
                p.getX(), p.getY());
    }
    
    public static Bezier createQuadratic(
            double xo, double yo, 
            double cpx, double cpy,
            double x, double y){
        Bezier bezier = new Bezier();
        
        bezier.cubic = false;
        
        bezier.xo = xo;
        bezier.yo = yo;
        bezier.cpx = cpx;
        bezier.cpy = cpy;
        bezier.x = x;
        bezier.y = y;
        
        return bezier;
    }
    
    public static Bezier createQuadratic(Point2D o, Point2D cp, Point2D p){
        return createQuadratic(
                o.getX(), o.getY(),
                cp.getX(), cp.getY(),
                p.getX(), p.getY());
    }
    
    public static Bezier createQuadratic(Point2D o, Point2D p){        
        // Calculation of control points
        //   Point2D cp = new Point2D.Double(
        //          (end.getX() - start.getX()) * 1 / 2 + start.getX(),
        //          (end.getY() - start.getY()) * 1 / 2 + start.getY()
        //   );
        Point2D cp = new Point2D.Double(
                (p.getX() - o.getX()) * 1 / 2 + o.getX(),
                (p.getY() - o.getY()) * 1 / 2 + o.getY()
        );
        
        return createQuadratic(
                o.getX(), o.getY(),
                cp.getX(), cp.getY(),
                p.getX(), p.getY());
    }

    public boolean isCubic() {
        return cubic;
    }

    public void setCubic(boolean cubic) {
        this.cubic = cubic;
    }
    
    public boolean isQuadratic() {
        return !cubic;
    }

    public void setQuadratic(boolean quadratic) {
        this.cubic = !quadratic;
    }

    public double getXo() {
        return xo;
    }

    public void setXo(double xo) {
        this.xo = xo;
    }

    public double getYo() {
        return yo;
    }

    public void setYo(double yo) {
        this.yo = yo;
    }

    public double getCpxa() {
        return cpxa;
    }

    public void setCpxa(double cpxa) {
        this.cpxa = cpxa;
    }

    public double getCpya() {
        return cpya;
    }

    public void setCpya(double cpya) {
        this.cpya = cpya;
    }

    public double getCpxb() {
        return cpxb;
    }

    public void setCpxb(double cpxb) {
        this.cpxb = cpxb;
    }

    public double getCpyb() {
        return cpyb;
    }

    public void setCpyb(double cpyb) {
        this.cpyb = cpyb;
    }

    public double getCpx() {
        return cpx;
    }

    public void setCpx(double cpx) {
        this.cpx = cpx;
    }

    public double getCpy() {
        return cpy;
    }

    public void setCpy(double cpy) {
        this.cpy = cpy;
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

    public Color getCubicColor() {
        return cubicColor;
    }

    public void setCubicColor(Color cubicColor) {
        this.cubicColor = cubicColor;
    }

    public Color getQuadraticColor() {
        return quadraticColor;
    }

    public void setQuadraticColor(Color quadraticColor) {
        this.quadraticColor = quadraticColor;
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
    
    public double[] toCubic(){
        double QP0x = xo;
        double QP0y = yo;
        double QP1x = cpx;
        double QP1y = cpy;
        double QP2x = x;
        double QP2y = y;
        
        double CP0x = QP0x;
        double CP0y = QP0y;
        double CP1x = QP0x + (2d/3d*(QP1x-QP0x));
        double CP1y = QP0y + (2d/3d*(QP1y-QP0y));
        double CP2x = QP2x + (2d/3d*(QP1x-QP2x));
        double CP2y = QP2y + (2d/3d*(QP1y-QP2y));
        double CP3x = QP2x;
        double CP3y = QP2y;
        
        double[] points = new double[8];
        points[0] = CP0x; // xo
        points[1] = CP0y; // yo
        points[2] = CP1x; // cpxa
        points[3] = CP1y; // cpya
        points[4] = CP2x; // cpxb
        points[5] = CP2y; // cpyb
        points[6] = CP3x; // x
        points[7] = CP3y; // y
        
        return points;
    }
    
    @Override
    public boolean quickModify(Point2D oldXY, Point2D newXY) {
        Point2D pointO = new Point2D.Double(xo, yo);
        Point2D pointCPA = new Point2D.Double(cpxa, cpya);
        Point2D pointCPB = new Point2D.Double(cpxb, cpyb);
        Point2D pointCP = new Point2D.Double(cpx, cpy);
        Point2D point = new Point2D.Double(x, y);
        
        if(pointO.distance(oldXY) < 2d){
            setXo(newXY.getX());
            setYo(newXY.getY());
            return true;
        }
        if(pointCPA.distance(oldXY) < 2d){
            setCpxa(newXY.getX());
            setCpya(newXY.getY());
            return true;
        }
        if(pointCPB.distance(oldXY) < 2d){
            setCpxb(newXY.getX());
            setCpyb(newXY.getY());
            return true;
        }
        if(pointCP.distance(oldXY) < 2d){
            setCpx(newXY.getX());
            setCpy(newXY.getY());
            return true;
        }
        if(point.distance(oldXY) < 2d){
            setX(newXY.getX());
            setY(newXY.getY());
            return true;
        }
        return false;
    }
    
}
