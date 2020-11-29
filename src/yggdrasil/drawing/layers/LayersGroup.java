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
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author util2
 */
public class LayersGroup {
    
    private String name = "Default group";
    private int index = 0;
    private Color color = Color.green;
    private boolean lock = false;
    private boolean visible = true;
    private List<Layer> layers = new ArrayList<>();
    private DefaultMutableTreeNode node;

    public LayersGroup() {
    }
    
    public static LayersGroup create(String name){
        LayersGroup layersGroup = new LayersGroup();
        
        layersGroup.name = name;
        layersGroup.node = new DefaultMutableTreeNode(name);
        
        return layersGroup;
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

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
    
    public void addLayer(Layer layer){
        layers.add(layer);
    }
    
    public void removeLayer(Layer layer){
        layers.remove(layer);
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }
}
