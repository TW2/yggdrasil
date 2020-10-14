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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author util2
 */
public class HistoricalLayersComboRenderer extends JPanel implements ListCellRenderer {

    private final JLabel lblColor = new JLabel("N");
    private final JLabel lblLocker = new JLabel("N");
    private final JLabel lblVisible = new JLabel("N");
    private final JLabel lblName = new JLabel("N");
    
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
        lblColor.setPreferredSize(new Dimension(10, getBounds().height));
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
            
        }
        
        return this;
    }
    
}
