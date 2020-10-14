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
package org.wingate.ygg.drawing.layers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author util2
 */
public class LayersGroup {
    
    private String name;
    private int index;
    private Color color;
    private List<Layer> layers = new ArrayList<>();

    public LayersGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
    
    public void addLayer(Layer layer){
        boolean add = true;
        for(Layer l : layers){
            if(l.getUID() == layer.getUID()){
                add = false;
                break;
            }
        }
        if(add == true){
            layers.add(layer);
        }
    }
    
    public void removeLayer(Layer layer){
        int id = -1;
        for(int i=0; i<layers.size(); i++){
            Layer l = layers.get(i);
            if(l.getUID() == layer.getUID()){
                id = i;
                break;
            }
        }
        if(id != -1){
            layers.remove(id);
        }
    }
}
