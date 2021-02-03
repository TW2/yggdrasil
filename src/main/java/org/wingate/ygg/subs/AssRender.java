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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author util2
 */
public class AssRender {
    
    public AssRender(){
        init();
    }
    
    private void init(){
        String text = "This is yggdrasil, a video and audio tool!";
        AssEvent event = new AssEvent();
        AssStyle style = AssStyle.getDefault();
        
        style.setFontname("Rosario Unique");
        style.setFontsize(50);
        
        event.setStyle(style);
        event.setLineType(AssEvent.LineType.Dialogue);
        event.setText(text);
        
        List<Ass64Char> chars = Ass64Calc.measure(event);
        
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setColor(Color.red);
        g2d.setFont(event.getStyle().getFont());
        
        float x = 50f;
        float y = 50f;
        for(Ass64Char c : chars){
            g2d.drawString(c.getText(), x, y);
            x += c.getWidth();
        }
        
        g2d.dispose();
        try {
            ImageIO.write(image, "png", new File("D:\\test.png"));
        } catch (IOException ex) {
            Logger.getLogger(AssRender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
