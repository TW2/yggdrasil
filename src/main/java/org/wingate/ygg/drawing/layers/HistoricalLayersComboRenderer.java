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
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author util2
 */
public class HistoricalLayersComboRenderer extends JPanel implements ListCellRenderer {

    private final ImageIcon lockOK = new ImageIcon(getClass().getResource("/images/20_linkOK.png"));
    private final ImageIcon lockNOT = new ImageIcon(getClass().getResource("/images/20_linkNOT.png"));
    
    private final ImageIcon vtrue = new ImageIcon(getClass().getResource("/images/red_and_green-green-cross.png"));
    private final ImageIcon vnot = new ImageIcon(getClass().getResource("/images/red_and_green-red-cross.png"));
    
    private final JLabel lblColor = new JLabel();
    private final JLabel lblLocker = new JLabel();
    private final JLabel lblVisible = new JLabel();
    private final JLabel lblName = new JLabel();
    
    
    
    public HistoricalLayersComboRenderer() {
        init();
    }
    
    private void init(){
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(lblColor);
        add(lblLocker);
        add(lblVisible);
        add(lblName);
        lblColor.setOpaque(true);
        lblColor.setPreferredSize(new Dimension(10, 20));
        lblLocker.setOpaque(true);
        lblLocker.setPreferredSize(new Dimension(20, getBounds().height));
        lblVisible.setOpaque(true);
        lblVisible.setPreferredSize(new Dimension(20, getBounds().height));
        lblName.setOpaque(true);
        lblName.setPreferredSize(new Dimension(getBounds().width - 50, getBounds().height));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        if(value instanceof Layer){
            Layer layer = (Layer)value;
            
            Color c = layer.getColor();
            lblColor.setText("    ");
            lblColor.setBackground(c);
            
            boolean lock = layer.isLock();
            lblLocker.setText("");
            lblLocker.setIcon(lock ? lockOK : lockNOT);
            
            boolean visible = layer.isVisible();
            lblVisible.setText("");
            lblVisible.setIcon(visible ? vtrue : vnot);
            
            String name = layer.getName();
            lblName.setText(name == null | name != null && name.isEmpty() ? "   Default" : "   " + name);            
        }
        
        return this;
    }
    
}
