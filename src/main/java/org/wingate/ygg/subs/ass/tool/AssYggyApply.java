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
package org.wingate.ygg.subs.ass.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssEvent;

/**
 *
 * @author util2
 */
public class AssYggyApply {
    
    public static BufferedImage getSubsImage(String asspath, Time t) throws Exception{
        
        ASS ass = ASS.Read(asspath);
        
        // Taille de l'image
        int w = Integer.parseInt(ass.getResX());
        int h = Integer.parseInt(ass.getResY());
        
        if(w == 0 | h == 0){
            throw new Exception("Video size can't accept zero!");
        }else if(w == 0){
            throw new Exception("Video size (width) can't accept zero!");
        }else if(h == 0){
            throw new Exception("Video size (height) can't accept zero!");
        }
        
        // On crée l'outil libass
        AssYGGY yggy = AssYGGY.create();
        
        // On récupère les évén ements les plus proches de t
        List<AssEvent> events = new ArrayList<>();
        for(AssEvent ev : ass.getEvents()){
            long msStart = Time.toMillisecondsTime(ev.getStartTime());
            long msStop = Time.toMillisecondsTime(ev.getEndTime());
            long time = Time.toMillisecondsTime(t);
            if(time >= msStart & time < msStop){
                events.add(ev);
            }
        }
        
        // On réécrit un ass allégé
        ASS newAss = ASS.NoFileToLoad();
        newAss.setResX(ass.getResX());
        newAss.setResY(ass.getResY());
        newAss.setWrapStyle(ass.getWrapStyle());
        newAss.setStyles(ass.getStyles());
        newAss.setEvents(events);
        String newPath = (new File("configuration", "light.ass")).getPath();
        ASS.Save(newPath, newAss);
        
        // On demande un résultat
        File folder = new File("configuration");
        File imgFile = new File(folder, "image.png");
        if(imgFile.exists()) imgFile.delete();
        int result = yggy.getYggy().executor(
                imgFile.getPath(),
                newPath,
                Double.toString(Time.getLengthInSeconds(t)),
                w,
                h
        );
        
        if(result != 0){
            throw new Exception("Error while generate bitmap!");
        }
        
        // On obtient une image avec les sous-titres à l'instant t        
        // On obtient une autre image
        BufferedImage img = makeColorTransparent(
                new ImageIcon(imgFile.getPath()).getImage(), new Color(63, 63, 63));
                
        return img;
    }
    
    private static BufferedImage makeColorTransparent(Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if (( rgb | 0xFF000000 ) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }else{
                    // nothing to do
                    return rgb;
                }
            }
        }; 

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        Image img = Toolkit.getDefaultToolkit().createImage(ip);
        ImageIcon icon = new ImageIcon(img);
        
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.dispose();
        
        return bi;
    }
}
