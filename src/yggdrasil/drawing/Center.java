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
public class Center {
    
    private int xo = -1, yo = -1;
    private boolean set = false;
    private double rx = 0d, ry = 0d;
    private final List<AShape> mShapes = new ArrayList<>();
    
    public Center(){
        
    }
    
    public Center(int xo, int yo){
        this.xo = xo;
        this.yo = yo;
        if(xo != -1 && yo != -1){
            set = true;
        }
    }
    
    public void clear(){
        xo = -1;
        yo = -1;
        set = false;
        rx = 0d;
        ry = 0d;
        mShapes.clear();
    }
    
    public void setCenter(int xo, int yo){
        this.xo = xo;
        this.yo = yo;
        if(xo != -1 && yo != -1){
            set = true;
        }
    }
    
    public boolean isSet(){
        return set;
    }
    
    public int getX(){
        return xo;
    }
    
    public int getY(){
        return yo;
    }
    
    public void setRotation(int rx, int ry){
        this.rx = rx;
        this.ry = ry;
    }
    
    public double getRX(){
        return rx;
    }
    
    public double getRY(){
        return ry;
    }
    
    public void setRotationPreview(List<AShape> pshapes, double angle){
        if(angle==0d){
            angle = getAngle();
        }
        mShapes.clear();
        Point2D O, L; double xa, ya;
        for(AShape s : pshapes){
            if(s instanceof Move){ // ReStart
                Move m = (Move)s;
                if(m.isMoveM()){
                    // M
                    xa = m.getXa(); ya = m.getYa();
                    O = rotateWithPoint(xo, yo, xa, ya, angle);
                    mShapes.add(Move.create(O, true));
                }else{
                    // N
                    xa = m.getXa(); ya = m.getYa();
                    O = rotateWithPoint(xo, yo, xa, ya, angle);
                    mShapes.add(Move.create(O, false));
                }
            }else if(s instanceof Line){
                Line l = (Line)s;
                xa = l.getXa(); ya = l.getYa();
                O = rotateWithPoint(xo, yo, xa, ya, angle);
                xa = l.getXb(); ya = l.getYb();
                L = rotateWithPoint(xo, yo, xa, ya, angle);
                mShapes.add(Line.create(O, L));
            }else if(s instanceof Bezier){
                Bezier b = (Bezier)s;
                if(b.isCubic()){
                    // Bezier - cubic
                    xa = b.getXo(); ya = b.getYo();
                    O = rotateWithPoint(xo, yo, xa, ya, angle);
                    xa = b.getX(); ya = b.getY();
                    L = rotateWithPoint(xo, yo, xa, ya, angle);
                    xa = b.getCpxa(); ya = b.getCpya();
                    Point2D CP1 = rotateWithPoint(xo, yo, xa, ya, angle);
                    xa = b.getCpxb(); ya = b.getCpyb();
                    Point2D CP2 = rotateWithPoint(xo, yo, xa, ya, angle);
                    mShapes.add(Bezier.createCubic(O, CP1, CP2, L));
                }else if(b.isQuadratic()){
                    // Bezier - quadratic
                    xa = b.getXo(); ya = b.getYo();
                    O = rotateWithPoint(xo, yo, xa, ya, angle);
                    xa = b.getX(); ya = b.getY();
                    L = rotateWithPoint(xo, yo, xa, ya, angle);
                    xa = b.getCpx(); ya = b.getCpy();
                    Point2D CP = rotateWithPoint(xo, yo, xa, ya, angle);
                    mShapes.add(Bezier.createQuadratic(O, CP, L));
                }else{
                    // S BSpline
//                BSpline bs = (BSpline)s;                
//                xa = (int)bs.getOriginPoint().getX();
//                ya = (int)bs.getOriginPoint().getY();
//                java.awt.Point P = rotateWithPoint(xo, yo, xa, ya, angle);
//                BSpline nbs = new BSpline(P.x, P.y);
//                for(ControlPoint cp : bs.getControlPoints()){
//                    xa = (int)cp.getOriginPoint().getX();
//                    ya = (int)cp.getOriginPoint().getY();
//                    java.awt.Point CP1 = rotateWithPoint(xo, yo, xa, ya, angle);
//                    nbs.addPoint(CP1.x, CP1.y);
//                }
//                if(bs.isNextExist()){
//                    xa = (int)bs.getNextPoint().getX();
//                    ya = (int)bs.getNextPoint().getY();
//                    java.awt.Point NX = rotateWithPoint(xo, yo, xa, ya, angle);
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
    
    /** Rotation d'un point par rapport à un autre.
     * @param xo Centre en xo
     * @param yo Centre en yo
     * @param xa Abscisse du point
     * @param ya Ordonnée du point
     * @param angle Angle en degré (positif ou négatif)
     * @return Le point modifié avec la rotation */
    private Point2D rotateWithPoint(double xo, double yo, double xa, double ya, double angle){
        Point2D S = new Point2D.Double(xo, yo);
        Point2D P = new Point2D.Double(xa, ya);
        Point2D Pprime = P;
        double SP = Math.sqrt(Math.pow(P.getX()-S.getX(), 2)+Math.pow(P.getY()-S.getY(), 2));
        double tan_PSN = (P.getY() - S.getY()) / (P.getX() - S.getX());
        double angle_PSN = Math.toDegrees(Math.atan(tan_PSN));
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() >= 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN));
        }
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() < 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN)+2*Math.PI);
        }
        if(P.getX() - S.getX() < 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN)+Math.PI);
        }
        double xPprime = SP * Math.cos(Math.toRadians(angle+angle_PSN)) + S.getX();
        double yPprime = SP * Math.sin(Math.toRadians(angle+angle_PSN)) + S.getY();
        Pprime.setLocation(xPprime, yPprime);
        return Pprime;
    }
    
    public double getAngle(){
        java.awt.Point S = new java.awt.Point(xo, yo);
        java.awt.Point P = new java.awt.Point((int)rx, (int)ry);
        double tan_PSN = (P.getY() - S.getY()) / (P.getX() - S.getX());
        double angle_PSN = Math.toDegrees(Math.atan(tan_PSN));
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() >= 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN));
        }
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() < 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN)+2*Math.PI);
        }
        if(P.getX() - S.getX() < 0){
            angle_PSN = Math.toDegrees(Math.atan(tan_PSN)+Math.PI);
        }
        return angle_PSN;
    }
}
