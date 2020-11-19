/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package yggdrasil.drawing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author The Wingate 2940
 */
public class Translation {
    
    private Point2D T = null;
    private boolean set = false;
    private double dx = 0d, dy = 0d;
    private List<AShape> mShapes = new ArrayList<>();
    
    public Translation(){
        
    }
    
    public Translation(Point2D T){
        this.T = T;
        if(T != null){
            set = true;
        }
    }
    
    public void clear(){
        T = null;
        set = false;
        dx = 0d;
        dy = 0d;
        mShapes.clear();
    }
    
    public void setTranslation(Point2D T){
        this.T = T;
        if(T != null){
            set = true;
        }
    }
    
    public boolean isSet(){
        return set;
    }
    
    public double getX(){
        return T.getX();
    }
    
    public double getY(){
        return T.getY();
    }
    
    public void setDistance(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }
    
    public double getTX(){
        return dx;
    }
    
    public double getTY(){
        return dy;
    }
    
    public double getDX(){
        return dx-T.getX();
    }
    
    public double getDY(){
        return dy-T.getY();
    }
    
    public void setTranslatonPreview(List<AShape> pshapes){        
        mShapes.clear();
        Point2D O, L; double xa, ya;
        for(AShape s : pshapes){
            if(s instanceof Move){ // ReStart
                Move m = (Move)s;
                if(m.isMoveM()){
                    // M
                    xa = m.getXa(); ya = m.getYa();
                    O = translateWithPoint(getDX(), getDY(), xa, ya);
                    mShapes.add(Move.create(O, true));
                }else{
                    // N
                    xa = m.getXa(); ya = m.getYa();
                    O = translateWithPoint(getDX(), getDY(), xa, ya);
                    mShapes.add(Move.create(O, false));
                }
            }else if(s instanceof Line){
                Line l = (Line)s;
                xa = l.getXa(); ya = l.getYa();
                O = translateWithPoint(getDX(), getDY(), xa, ya);
                xa = l.getXb(); ya = l.getYb();
                L = translateWithPoint(getDX(), getDY(), xa, ya);
                mShapes.add(Line.create(O, L));
            }else if(s instanceof Bezier){
                Bezier b = (Bezier)s;
                if(b.isCubic()){
                    // Bezier - cubic
                    xa = b.getXo(); ya = b.getYo();
                    O = translateWithPoint(getDX(), getDY(), xa, ya);
                    xa = b.getX(); ya = b.getY();
                    L = translateWithPoint(getDX(), getDY(), xa, ya);
                    xa = b.getCpxa(); ya = b.getCpya();
                    Point2D CP1 = translateWithPoint(getDX(), getDY(), xa, ya);
                    xa = b.getCpxb(); ya = b.getCpyb();
                    Point2D CP2 = translateWithPoint(getDX(), getDY(), xa, ya);
                    mShapes.add(Bezier.createCubic(O, CP1, CP2, L));
                }else if(b.isQuadratic()){
                    // Bezier - quadratic
                    xa = b.getXo(); ya = b.getYo();
                    O = translateWithPoint(getDX(), getDY(), xa, ya);
                    xa = b.getX(); ya = b.getY();
                    L = translateWithPoint(getDX(), getDY(), xa, ya);
                    xa = b.getCpx(); ya = b.getCpy();
                    Point2D CP = translateWithPoint(getDX(), getDY(), xa, ya);
                    mShapes.add(Bezier.createQuadratic(O, CP, L));
                }else{
                    // S BSpline
//                BSpline bs = (BSpline)s;                
//                xa = (int)bs.getStart().getX();
//                ya = (int)bs.getStart().getY();
//                Point2D P = translateWithPoint(getDX(), getDY(), xa, ya);
//                BSpline nbs = new BSpline(P.x, P.y);
//                for(ControlPoint cp : bs.getControlPoints()){
//                    xa = (int)cp.getStart().getX();
//                    ya = (int)cp.getStart().getY();
//                    Point2D CP1 = translateWithPoint(getDX(), getDY(), xa, ya);
//                    nbs.addPoint(CP1.x, CP1.y);
//                }
//                if(bs.isNextExist()){
//                    xa = (int)bs.getNextPoint().getX();
//                    ya = (int)bs.getNextPoint().getY();
//                    Point2D NX = translateWithPoint(getDX(), getDY(), xa, ya);
//                    nbs.setNextPoint(NX.x, NX.y);
//                }
//                mShapes.add(nbs);                    
                }
            }
        }
    }
    
    public List<AShape> getPreviewShapes(){
        return mShapes;
    }
    
    private Point2D translateWithPoint(double rdx, double rdy, double xa, double ya){
        Point2D P = new Point2D.Double(xa, ya);
        Point2D Pprime = P;
        double xPprime = P.getX() + rdx;
        double yPprime = P.getY() + rdy;
        Pprime.setLocation(xPprime, yPprime);
        return Pprime;
    }
}
