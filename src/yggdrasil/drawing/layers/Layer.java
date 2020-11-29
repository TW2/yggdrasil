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
package yggdrasil.drawing.layers;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import yggdrasil.MainFrame;
import yggdrasil.drawing.AShape;
import yggdrasil.drawing.HistoricalPointChange;
import yggdrasil.drawing.Memories;
import yggdrasil.drawing.Move;

/**
 *
 * @author util2
 */
public class Layer {
    
    private Color color = Color.green;
    private boolean lock = false;
    private boolean visible = true;
    private String name = "Default";
    
    private LayerDirection layerDirection = LayerDirection.Forward;    
    private List<Memories<?>> memoriesArray = new ArrayList<>();
    private float generalPathAlpha = 0.2f;
    private float scale = 1f;
    
    public Layer() {
        
    }
    
    public static Layer create(String name, Color color){
        Layer layer = new Layer();
        
        layer.name = name;
        layer.color = color;
        
        return layer;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LayerDirection getLayerDirection() {
        return layerDirection;
    }

    public void setLayerDirection(LayerDirection layerDirection) {
        this.layerDirection = layerDirection;
    }

    public List<Memories<?>> getMemoriesArray() {
        return memoriesArray;
    }

    public void setMemoriesArray(List<Memories<?>> memoriesArray) {
        this.memoriesArray = memoriesArray;
    }

    public float getGeneralPathAlpha() {
        return generalPathAlpha;
    }

    public void setGeneralPathAlpha(float generalPathAlpha) {
        this.generalPathAlpha = generalPathAlpha;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    
    // Opérations sur les couches :
    // 1. Génération de la forme
    
    /**
     * Reroute (to do before addElement)
     * @see addElement
     */
    public Point rerouteElement(MainFrame mf, Point lastClicked){
        //======================================================================
        // Check about undo
        //----------------------------------------------------------------------
        int index = -1;
        // Get MemoriesArray from actual Layer
        for(int i=0; i<memoriesArray.size(); i++){
            if(memoriesArray.get(i).isUndo()){
                index = i;
                break;
            }
        }
        if(index != -1){
            for(int i=memoriesArray.size() - 1; i >= 0; i--){
                if(i >= index){
                    if(memoriesArray.get(i).getObject() instanceof AShape){
                        Point2D p = (Point2D)memoriesArray.get(i).getOldState();
                        lastClicked = new Point((int)p.getX(),(int)p.getY());
                    }
                    memoriesArray.remove(i);
                    mf.removeLastFromHistoric();
                }                
            }
        }
        return lastClicked;
    }
    
    public void addElement(Point2D oldPoint, Point2D newPoint, Class cls, Object obj, MainFrame mf){
        //======================================================================
        // Add element
        //----------------------------------------------------------------------
        Memories mms = new Memories();
        mms.setOldState(oldPoint);
        mms.setNewState(newPoint);
        mms.setObjectClass(cls);
        mms.setObject(obj);
        
        if(obj instanceof AShape){
            // Get MemoriesArray from actual Layer
            if(memoriesArray.isEmpty()){
                // Any AShape replace by a Move (M)                    
                mms.setObjectClass(Move.class);
                mms.setObject(Move.create(newPoint, true));
                memoriesArray.add(mms);
            }else{
                memoriesArray.add(mms);
            }
        }else if(obj instanceof HistoricalPointChange){
            // Get MemoriesArray from actual Layer
            if(memoriesArray.isEmpty() == false){
                memoriesArray.add(mms);
            }
        }
        
        // Mise à jour de l'historique
        mf.addToHistoric(mms);
    }
}
