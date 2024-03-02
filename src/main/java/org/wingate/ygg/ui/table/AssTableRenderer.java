/*
 * Copyright (C) 2024 util2
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
package org.wingate.ygg.ui.table;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.wingate.ygg.ass.AssEvent;
import org.wingate.ygg.ass.AssTime;
import org.wingate.ygg.helper.DrawColor;

/**
 *
 * @author util2
 */
public class AssTableRenderer extends DefaultTableCellRenderer {

    public AssTableRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        setBackground(Color.white);
        setForeground(Color.black);
        
        switch (value) {
            case Integer x -> setText(Integer.toString(x));
            case AssTime x -> setText(x.toASSTime());
            case AssEvent.LineType x -> setText(x.toString());
            case String x -> setText(x);
            default -> {
            }
        }
        
        if(((AssEvent.LineType)table.getValueAt(row, 1)).compareTo(AssEvent.LineType.Comment) == 0){
            setBackground(DrawColor.green.getColor(0.2f));
            setForeground(DrawColor.green.getColor());
        }
        
        if(isSelected){
            setBackground(DrawColor.corn_flower_blue.getColor());
            setForeground(Color.white);
        }
        
        return this;
    }
}
