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
package org.wingate.ygg.subs;

import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.AssStyle;
import org.wingate.ygg.subs.AssStylesCollection;

/**
 *
 * @author util2
 */
public class AssStylesCollectionTableModel extends AbstractTableModel {

    private AssStylesCollection stylesCollection = new AssStylesCollection();
    Language in = null;
    ISO_3166 get = null;
    
    public AssStylesCollectionTableModel(Language in, ISO_3166 get) {
        this.in = in;
        this.get = get;
    }

    //==========================================================================
    //--------------------------------------------------------------------------
    public AssStylesCollection getStylesCollection() {
        return stylesCollection;
    }

    public void setStylesCollection(AssStylesCollection stylesCollection) {
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
    
    private AssStyle getStyle(int row){
        int count = 0;
        for(Map.Entry<AssStyle, Boolean> entry : stylesCollection.getEnableStyles().entrySet()){
            if(count == row){
                return entry.getKey();
            }
            count++;
        }
        return AssStyle.getDefault();
    }
    
    private boolean getStyleEnableValue(int row){
        int count = 0;
        for(Map.Entry<AssStyle, Boolean> entry : stylesCollection.getEnableStyles().entrySet()){
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
                return AssStyle.class;
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
