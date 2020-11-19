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
import java.util.ArrayList;
import java.util.List;
import yggdrasil.drawing.Memories;

/**
 *
 * @author util2
 */
public class Layer {
    
    private Color color = Color.green;
    private boolean lock = false;
    private boolean visible = true;
    private String name = "Default";
    
    private int layer = 0;
    private LayerDirection layerDirection = LayerDirection.Forward;
    
    private List<Memories<?>> memoriesArray = new ArrayList<>();
    
    private int UID = 0;

    public Layer() {
        
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

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
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

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }
    
}
