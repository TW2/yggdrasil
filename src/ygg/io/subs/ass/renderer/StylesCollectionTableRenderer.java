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
package ygg.io.subs.ass.renderer;

import java.awt.Component;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import ygg.io.subs.ass.Style;
import ygg.io.subs.ass.collection.StylesCollection;

/**
 *
 * @author util2
 */
public class StylesCollectionTableRenderer extends JLabel implements TableCellRenderer {
    
    private StylesCollection stylesCollection = new StylesCollection();

    public StylesCollectionTableRenderer() {
        setOpaque(true);
    }

    public StylesCollection getStylesCollection() {
        return stylesCollection;
    }

    public void setStylesCollection(StylesCollection stylesCollection) {
        this.stylesCollection = stylesCollection;        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        if(value instanceof Style){
            Style style = (Style)value;            
            setText(getStyleName(style));
        }
        
        return this;
    }
    
    private String getStyleName(Style style){
        for(Map.Entry<String, Style> entry : stylesCollection.getStyles().entrySet()){
            if(entry.getKey().equals(style.getName()) == true){
                return entry.getKey();
            }
        }
        return "?";
    }
    
}
