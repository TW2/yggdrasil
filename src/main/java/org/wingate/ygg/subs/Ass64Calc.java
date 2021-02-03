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
package org.wingate.ygg.subs;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/Aegisub/Aegisub/blob/6f546951b4f004da16ce19ba638bf3eedefb9f31/src/auto4_lua.cpp
 * @author util2
 */
public class Ass64Calc {
    
    public Ass64Calc() {
        
    }
    
    public static List<Ass64Char> measure(AssEvent event){
        List<Ass64Char> chs = new ArrayList<>();
        
        float fontsize = event.getStyle().getFontsize() * 64;
        float spacing = event.getStyle().getSpacing() * 64;
        
        double width, height = 0, descent = 0, extlead = 0;
        
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ctx = image.createGraphics();
        
        Font font = event.getStyle().getFont().deriveFont(fontsize);
        FontMetrics metrics = ctx.getFontMetrics(font);
        
        for(char c : event.getText().toCharArray()){
            // Get values from context - WIDTH
            float w = metrics.charWidth(c);
            
            // Get values from context - HEIGHT
            float h = metrics.getHeight();
            
            // Get values from context - DESCENT
            float d = metrics.getDescent();
            
            // Get values from context - EXTLEAD
            float e = metrics.getLeading();
            
            double scaling = fontsize / (double)(h > 0 ? h : 1);
            width = (w + spacing) * scaling;
            height = h > height ? h*scaling : height;
            descent = d > descent ? d*scaling : descent;
            extlead = e > extlead ? e*scaling : extlead;
            
            Ass64Char ac = Ass64Char.create(
                    Character.toString(c),
                    event.getStyle().getScaleX() / 100d * width / 64d, 
                    event.getStyle().getScaleY() / 100d * height / 64d, 
                    event.getStyle().getScaleY() / 100d * descent / 64d, 
                    event.getStyle().getScaleY() / 100d * extlead / 64d);
            chs.add(ac);
        }
        
        float wt = metrics.stringWidth(event.getText());
        float ht = metrics.stringWidth(event.getText());
        double scaling = fontsize / (double)(ht > 0 ? ht : 1);
        
        width = (wt + spacing * event.getText().length()) * scaling;
        double cr = event.getStyle().getScaleX() / 100d * (width / event.getText().length()) / 64d;
        
        chs.forEach(ch -> {
            ch.setWidth(ch.getWidth() + cr);
        });
        
        ctx.dispose();
        
        return chs;
    }
    
}
