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
package org.wingate.ygg.bss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.AssEvent;
import org.wingate.ygg.ass.AssStyle;
import org.wingate.ygg.ass.AssTime;

/**
 *
 * @author util2
 */
public class TryBSS {
    public static void main(String[] args) {        
        try{
            ASS ass = ASS.NoFileToLoad();
            
            AssStyle def = AssStyle.getDefault();
            def.setName("Default");
            def.setFontname("Calibri");
            def.setFontsize(90.25d);
            
            ass.getStyles().clear();
            ass.getStyles().put("Default", def);
            
            AssEvent ev01 = new AssEvent();
            ev01.setStartTime(AssTime.create(0L));
            ev01.setEndTime(AssTime.create(100L));
            ev01.setStyle(def);
            ev01.setText("{\\an5\\pos(500,50)\\fs120\\1c&H000F0F&}Bonjour !");
            
            ass.getEvents().add(ev01);
            
            AssEvent ev02 = new AssEvent();
            ev02.setStartTime(AssTime.create(0L));
            ev02.setEndTime(AssTime.create(100L));
            ev02.setStyle(def);
            ev02.setText("Hello world!");
            
            ass.getEvents().add(ev02);
            
            BufferedImage img = BssRenderer.getAssImage(ass, AssTime.create(50L), 1280, 720);
            ImageIO.write(img, "png", new File("C:\\Users\\util2\\Desktop\\trybss.png"));
        }catch(IOException exc){
            System.err.println("TryBSS: error when proceed!");
        }
    }
}
