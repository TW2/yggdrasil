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
package ygg.io.subs.ass.collection;

import java.util.HashMap;
import java.util.Map;
import ygg.io.subs.ass.Style;

/**
 *
 * @author util2
 */
public class StylesCollection {
    
    private String collectionName = "Default collection name";
    private Map<String, Style> styles = new HashMap<>();
        
    private Map<Style, Boolean> enableStyles = new HashMap<>();

    public StylesCollection() {
    }
    
    public static StylesCollection create(String collectionName, Map<String, Style> styles){
        StylesCollection sc = new StylesCollection();
        
        sc.collectionName = collectionName;
        sc.styles = styles;
        
        for(Map.Entry<String, Style> entry : styles.entrySet()){
            sc.enableStyles.put(entry.getValue(), Boolean.TRUE);
        }
        
        return sc;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<String, Style> getStyles() {
        return styles;
    }

    public void setStyles(Map<String, Style> styles) {
        this.styles = styles;
    }

    public Map<Style, Boolean> getEnableStyles() {
        return enableStyles;
    }

    public void setEnableStyles(Map<Style, Boolean> enableStyles) {
        this.enableStyles = enableStyles;
    }
    
}
