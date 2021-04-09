/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wingate.ygg.chat;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author Naruto
 */
public class Emoticons {

    private final Map<String, ImageIcon> icons = new HashMap<>();
    
    public Emoticons() {
        init();
    }
    
    private void init(){        
        //01 - :D
        icons.put(":D", new ImageIcon(getClass().getResource("/smiley/m01.png")));
        //02 - :)
        icons.put(":)", new ImageIcon(getClass().getResource("/smiley/m02.png")));
        //03 - ^_^
        icons.put("^_^", new ImageIcon(getClass().getResource("/smiley/m03.png")));
        //04 - ^o^
        icons.put("^o^", new ImageIcon(getClass().getResource("/smiley/m04.png")));
        
        //05 - :p ou :P
        icons.put(":p", new ImageIcon(getClass().getResource("/smiley/m05.png")));
        icons.put(":P", new ImageIcon(getClass().getResource("/smiley/m05.png")));
        //06 - :love:
        icons.put(":love:", new ImageIcon(getClass().getResource("/smiley/m06.png")));
        //07 - >_< ou xD
        icons.put(">_<", new ImageIcon(getClass().getResource("/smiley/m07.png")));
        icons.put("xD", new ImageIcon(getClass().getResource("/smiley/m07.png")));
        //08 - =) ou =D
        icons.put("=)", new ImageIcon(getClass().getResource("/smiley/m08.png")));
        icons.put("=D", new ImageIcon(getClass().getResource("/smiley/m08.png")));
        
        //09 - >_<' ou >_<" ou XD
        icons.put(">_<'", new ImageIcon(getClass().getResource("/smiley/m09.png")));
        icons.put(">_<\"", new ImageIcon(getClass().getResource("/smiley/m09.png")));
        icons.put("XD", new ImageIcon(getClass().getResource("/smiley/m09.png")));
        //10 - =(
        icons.put("=(", new ImageIcon(getClass().getResource("/smiley/m10.png")));
        //11 - x( ou X(
        icons.put("x(", new ImageIcon(getClass().getResource("/smiley/m11.png")));
        icons.put("X(", new ImageIcon(getClass().getResource("/smiley/m11.png")));
        //12 - :(
        icons.put(":(", new ImageIcon(getClass().getResource("/smiley/m12.png")));
        
        //13 - p) ou P)
        icons.put("p)", new ImageIcon(getClass().getResource("/smiley/m13.png")));
        icons.put("P)", new ImageIcon(getClass().getResource("/smiley/m13.png")));
        //14 - -_-
        icons.put("-_-", new ImageIcon(getClass().getResource("/smiley/m14.png")));
        //15 - :/ ou :\
        icons.put(":/", new ImageIcon(getClass().getResource("/smiley/m15.png")));
        icons.put(":\\", new ImageIcon(getClass().getResource("/smiley/m15.png")));
        //16 - :[
        icons.put(":[", new ImageIcon(getClass().getResource("/smiley/m16.png")));
        
        //17 - ;)
        icons.put(";)", new ImageIcon(getClass().getResource("/smiley/m17.png")));
    }
    
    private BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public ImageIcon getIcon(String smiley) {
        if(icons.containsKey(smiley)){
            return icons.get(smiley);
        }
        return null;
    }
    
    public List<String> getKeys(){
        return new ArrayList<>(icons.keySet());
    }
}
