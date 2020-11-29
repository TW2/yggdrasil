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
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import yggdrasil.drawing.layers.Layer;

/**
 *
 * @author util2
 */
public class HistoricalLayersComboRenderer extends JPanel implements ListCellRenderer {

    private final ImageIcon lockOK = new ImageIcon(getClass().getResource("/documents/images/20_linkOK.png"));
    private final ImageIcon lockNOT = new ImageIcon(getClass().getResource("/documents/images/20_linkNOT.png"));
    
    private final ImageIcon vtrue = new ImageIcon(getClass().getResource("/documents/images/red_and_green-green-cross.png"));
    private final ImageIcon vnot = new ImageIcon(getClass().getResource("/documents/images/red_and_green-red-cross.png"));
    
    Color c = Color.green;
    boolean lock = false;
    boolean visible = true;
    String name = "Error";
        
    public HistoricalLayersComboRenderer() {
        init();
    }
    
    private void init(){
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(300, 30));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        // Color
        g.setColor(c);
        g.fillRect(1, 5, 20, 20);
        g.setColor(Color.black);
        g.drawRect(1, 5, 20, 20);
        
        // Lock        
        g.drawImage(lock ? lockOK.getImage() : lockNOT.getImage(), 1*20+4, 5, null);
        
        // Visibility
        g.drawImage(visible ? vtrue.getImage() : vnot.getImage(), 2*20+8, 5, null);
        
        // Name
        g.drawString(name, 3*20+12, 20);
    }
    
    

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        if(value instanceof Layer){
            Layer layer = (Layer)value;
            
            c = layer.getColor();
            lock = layer.isLock();
            visible = layer.isVisible();
            name = layer.getName();
            
            repaint();
        }
        
        return this;
    }
    
}
