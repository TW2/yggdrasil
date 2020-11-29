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
package yggdrasil.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import yggdrasil.drawing.layers.Layer;
import yggdrasil.drawing.layers.LayersGroup;

/**
 *
 * @author util2
 */
public class LayersGroupTreeNodeRenderer extends DefaultTreeCellRenderer {
    
    public LayersGroupTreeNodeRenderer() {
        setDoubleBuffered(true);
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        
        if(isRoot(value)){
            setText("Groups");
            return this;
        }
        
        setIcon(getIcon(value));
        setText(getText(value));

        return this;
    }
    
    protected boolean isRoot(Object value){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return node.isRoot();
    }
    
    protected boolean isLayerTreeNode(Object value){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return node.getUserObject() instanceof Layer;
    }
    
    protected boolean isGroupTreeNode(Object value){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        return node.getUserObject() instanceof LayersGroup;
    }
    
    protected ImageIcon getIcon(Object value){
        Color color = Color.red;
        boolean lock = false;
        boolean visible = true;
        
        if(isLayerTreeNode(value)){
            Layer layer = (Layer)((DefaultMutableTreeNode) value).getUserObject();
            color = layer.getColor();
            lock = layer.isLock();
            visible = layer.isVisible();
        }else if(isGroupTreeNode(value)){
            LayersGroup group = (LayersGroup)((DefaultMutableTreeNode) value).getUserObject();
            color = group.getColor();
            lock = group.isLock();
            visible = group.isVisible();
        }
        
        BufferedImage img = new BufferedImage(3*30, 30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Color
        g2d.setColor(color);
        g2d.fillRect(5, 5, 20, 20);
        g2d.setColor(Color.black);
        g2d.drawRect(5, 5, 20, 20);
        
        // Lock
        ImageIcon iiLock;
        if(lock == true){
            iiLock = new ImageIcon(getClass().getResource("/documents/images/20_linkOK.png"));
        }else{
            iiLock = new ImageIcon(getClass().getResource("/documents/images/20_linkNOT.png"));
        }
        g2d.drawImage(iiLock.getImage(), 1*30+5, 5, null);
        
        // Visibility
        ImageIcon iiVisible;
        if(visible == true){
            iiVisible = new ImageIcon(getClass().getResource("/documents/images/red_and_green-green-cross.png"));
        }else{
            iiVisible = new ImageIcon(getClass().getResource("/documents/images/red_and_green-red-cross.png"));
        }
        g2d.drawImage(iiVisible.getImage(), 2*30+5, 5, null);
                
        ImageIcon icon = new ImageIcon(img);
        g2d.dispose();
        
        return icon;
    }
    
    protected String getText(Object value){
        String name = "Default?";
        
        if(isLayerTreeNode(value)){
            Layer layer = (Layer)((DefaultMutableTreeNode) value).getUserObject();
            name = layer.getName();
        }else if(isGroupTreeNode(value)){
            LayersGroup group = (LayersGroup)((DefaultMutableTreeNode) value).getUserObject();
            name = group.getName();
        }
        
        return name;
    }
}
