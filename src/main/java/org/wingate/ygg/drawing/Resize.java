/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wingate.ygg.drawing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author The Wingate 2940
 */
public class Resize {
    
    private int xo = -1, yo = -1;
    private boolean set = false;
    private double dx = 0d, dy = 0d;
    private final List<AShape> mShapes = new ArrayList<>();
    
    public Resize(){
        
    }
    
    public Resize(int xo, int yo){
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
        dx = 0d;
        dy = 0d;
        mShapes.clear();
    }
    
    public void setResize(int xo, int yo){
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
    
    public void setDistance(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }
    
    public double getRX(){
        return dx;
    }
    
    public double getRY(){
        return dy;
    }
    
    public void setResizePreview(List<AShape> pshapes, double percent){
        if(percent==0d){
            percent = getPercent();
        }
        mShapes.clear();
        Point2D O, L, M = getReStartPoint(pshapes); double xa, ya;
        for(AShape s : pshapes){
            if(s instanceof Move){
                Move m = (Move)s;
                if(m.isMoveM()){
                    // M
                    xa = m.getXa(); ya = m.getYa();
                    O = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    mShapes.add(Move.create(O, true));
                }else{
                    // N
                    xa = m.getXa(); ya = m.getYa();
                    O = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    mShapes.add(Move.create(O, false));
                }
            }else if(s instanceof Line){
                Line l = (Line)s;
                xa = l.getXa(); ya = l.getYa();
                O = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                xa = l.getXb(); ya = l.getYb();
                L = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                mShapes.add(Line.create(O, L));
            }else if(s instanceof Bezier){
                Bezier b = (Bezier)s;
                if(b.isCubic()){
                    // Bezier - cubic
                    xa = b.getXo(); ya = b.getYo();
                    O = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    xa = b.getX(); ya = b.getY();
                    L = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    xa = b.getCpxa(); ya = b.getCpya();
                    Point2D CP1 = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    xa = b.getCpxb(); ya = b.getCpyb();
                    Point2D CP2 = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    mShapes.add(Bezier.createCubic(O, CP1, CP2, L));
                }else if(b.isQuadratic()){
                    // Bezier - quadratic
                    xa = b.getXo(); ya = b.getYo();
                    O = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    xa = b.getX(); ya = b.getY();
                    L = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    xa = b.getCpx(); ya = b.getCpy();
                    Point2D CP = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
                    mShapes.add(Bezier.createQuadratic(O, CP, L));
                }else{
                    // S BSpline
//                BSpline bs = (BSpline)s;                
//                xa = (int)bs.getStart().getX();
//                ya = (int)bs.getStart().getY();
//                Point2D P = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
//                BSpline nbs = new BSpline(P.x, P.y);
//                for(ControlPoint cp : bs.getControlPoints()){
//                    xa = (int)cp.getStart().getX();
//                    ya = (int)cp.getStart().getY();
//                    Point2D CP1 = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
//                    nbs.addPoint(CP1.x, CP1.y);
//                }
//                if(bs.isNextExist()){
//                    xa = (int)bs.getNextPoint().getX();
//                    ya = (int)bs.getNextPoint().getY();
//                    Point2D NX = resizeWithPoint(M.getX(), M.getY(), xa, ya, percent);
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
    private Point2D resizeWithPoint(double x, double y, double xa, double ya, double percent){
        //S est le point issu d'un ReStart, c'est donc le point de référence
        //P est le point d'insertion (pour lequel on veut calculer P')
        Point2D S = new Point2D.Double(x, y);
        Point2D P = new Point2D.Double(xa, ya);
        Point2D Pprime = P;
        //Si P est égal à S alors, on n'a pas besoin de faire le calcule on retourne le même point.
        if(P.equals(S)){ return P; }
        //On veut que le point S soit toujours l'origine
        //On donc calcule la distance de S à P pour en sortir une distance en fonction du pourcentage
        double SP = Point2D.distance(S.getX(), S.getY(), P.getX(), P.getY());
        double SPprime = SP*percent/100;
        //On calcule l'angle S afin de savoir où resituer le point P'
        double tanS = (P.getY() - S.getY()) / (P.getX() - S.getX());
        double angleS = Math.toDegrees(Math.atan(tanS));
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() >= 0){
            angleS = Math.toDegrees(Math.atan(tanS));
        }
        if(P.getX() - S.getX() > 0 && P.getY() - S.getY() < 0){
            angleS = Math.toDegrees(Math.atan(tanS)+2*Math.PI);
        }
        if(P.getX() - S.getX() < 0){
            angleS = Math.toDegrees(Math.atan(tanS)+Math.PI);
        }
        //La distance en fonction du pourcentage vient s'ajouter aux coordonnées de S avec l'angle S.
        double xPprime = SPprime * Math.cos(Math.toRadians(angleS)) + S.getX();
        double yPprime = SPprime * Math.sin(Math.toRadians(angleS)) + S.getY();
        //S.x-(S.x-xo+dx-xo)+(P.getX()-dx)*percent/100, S.y-(S.y-yo+dy-yo)+(P.getY()-dy)*percent/100
        //S.x-(S.x-dx)+(P.getX()-dx)*percent/100, S.y-(S.y-dy)+(P.getY()-dy)*percent/100
        Pprime.setLocation(xPprime,yPprime);
        return Pprime;
    }
    
    public double getPercent(){
        //100% = xo
        //?% = dx
        //?% = 100*dx/xo
        double percent = 100d*dx/xo;
        return percent;
    }
    
    public Point2D getReStartPoint(List<AShape> shapes){
        for(AShape s : shapes){
            if(s instanceof Move){ // ReStart (M)
                Move m = (Move)s;
                if(m.isMoveM()){
                    return m.getPointA();
                }                
            }
        }
        return new Point2D.Double(0, 0);
    }
}
