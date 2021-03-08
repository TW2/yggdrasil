/*
 * Copyright (C) 2021 util2
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
package org.wingate.ygg.subs.ass.tool;

/**
 *
 * @author util2
 */
public class Ass64Char {
    
    private double width = 0;
    private double height = 0;
    private double descent = 0;
    private double extlead = 0;
    private String text = ""; 

    public Ass64Char() {
    }
    
    public static Ass64Char create(String text, double width, double height,
            double descent, double extlead){
        Ass64Char ac = new Ass64Char();
        
        ac.text = text;
        ac.width = width;
        ac.height = height;
        ac.descent = descent;
        ac.extlead = extlead;
        
        return ac;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDescent() {
        return descent;
    }

    public void setDescent(double descent) {
        this.descent = descent;
    }

    public double getExtlead() {
        return extlead;
    }

    public void setExtlead(double extlead) {
        this.extlead = extlead;
    }
}
