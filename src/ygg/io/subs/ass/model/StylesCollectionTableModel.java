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
package ygg.io.subs.ass.model;

import java.util.Map;
import javax.swing.table.AbstractTableModel;
import ygg.io.language.ISO_3166;
import ygg.io.language.Language;
import ygg.io.subs.ass.Style;
import ygg.io.subs.ass.collection.StylesCollection;

/**
 *
 * @author util2
 */
public class StylesCollectionTableModel extends AbstractTableModel {

    private StylesCollection stylesCollection = new StylesCollection();
    Language in = null;
    ISO_3166 get = null;
    
    public StylesCollectionTableModel(Language in, ISO_3166 get) {
        this.in = in;
        this.get = get;
    }

    //==========================================================================
    //--------------------------------------------------------------------------
    public StylesCollection getStylesCollection() {
        return stylesCollection;
    }

    public void setStylesCollection(StylesCollection stylesCollection) {
        this.stylesCollection = stylesCollection;
    }
    //--------------------------------------------------------------------------
    //==========================================================================

    @Override
    public int getRowCount() {
        return stylesCollection.getStyles().size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0 -> {
                // (0) Boolean >> Enable style in ASS script                
                return getStyleEnableValue(rowIndex);
            }
            case 1 -> {
                // (1) String >> Style name
                return getStyle(rowIndex);
            }
        }
        return null;
    }
    
    private Style getStyle(int row){
        int count = 0;
        for(Map.Entry<Style, Boolean> entry : stylesCollection.getEnableStyles().entrySet()){
            if(count == row){
                return entry.getKey();
            }
            count++;
        }
        return Style.getDefault();
    }
    
    private boolean getStyleEnableValue(int row){
        int count = 0;
        for(Map.Entry<Style, Boolean> entry : stylesCollection.getEnableStyles().entrySet()){
            if(count == row){
                return entry.getValue();
            }
            count++;
        }
        return false;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 0 -> {
                return Boolean.class;
            }
            case 1 -> {
                return Style.class;
            }
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0 -> {
                return in.getTranslated("InScript", get, "In script");
            }
            case 1 -> {
                return in.getTranslated("StyleName", get, "Style name");
            }
        }
        return "?";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0 -> {
                return true;
            }
            case 1 -> {
                return false;
            }
        }
        return false;
    }
}
