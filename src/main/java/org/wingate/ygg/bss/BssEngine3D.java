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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author util2
 */
public class BssEngine3D {
    
    private BufferedImage scene;
    private Graphics2D g;
    private int sceneWidth;
    private int sceneHeight;
    
    private BssEngine3D(){
        scene = null;
        g = null;
        sceneWidth = -1;
        sceneHeight = -1;
    }
    
    public static BssEngine3D createScene(int width, int height, boolean isBSS){
        BssEngine3D be3D = new BssEngine3D();
        
        be3D.sceneWidth = width;
        be3D.sceneHeight = height;
        be3D.scene = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        be3D.g = be3D.scene.createGraphics();
        
        return be3D;
    }
    
    public void dispose(){
        g.dispose();
    }

    public BufferedImage getScene() {
        return scene;
    }

    public int getSceneWidth() {
        return sceneWidth;
    }

    public int getSceneHeight() {
        return sceneHeight;
    }
    
    public void apply(List<BssShape> shapes){
        BufferedImage textLayerImage = new BufferedImage(sceneWidth, sceneHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage karaLayerImage = new BufferedImage(sceneWidth, sceneHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage bordLayerImage = new BufferedImage(sceneWidth, sceneHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage shadLayerImage = new BufferedImage(sceneWidth, sceneHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gText = textLayerImage.createGraphics();
        Graphics2D gKara = karaLayerImage.createGraphics();
        Graphics2D gBord = bordLayerImage.createGraphics();
        Graphics2D gShad = shadLayerImage.createGraphics();
        for(BssShape bsh : shapes){
            AffineTransform tr = new AffineTransform();
            GeneralPath sText = bsh.getGeneralPath();            
            GeneralPath sKara = bsh.getGeneralPath();            
            GeneralPath sBord = bsh.getGeneralPath();            
            GeneralPath sShad = bsh.getGeneralPath();
            for(Tag tag : Tag.values()){
                Object o = bsh.getTags().get(tag.getStartsWith());
                switch(tag){
                    case OldAlignment, Alignment, Position, Movement, WrapStyle,
                            FontName, FontSize, Spacing -> {
                        /* Ne rien faire car déjà traité */ 
                    }
                    case Alpha, TextColor, TextAlpha, KaraokeColor, KaraokeAlpha,
                            OutlineColor, OutlineAlpha, ShadowColor, ShadowAlpha,
                            Origin, OldTextColor -> {
                        /* Ne rien faire car traité après */ 
                    }
                    case ScaleXY -> {
                        if(o != null && o instanceof Integer v){
                            tr.scale(v, v);
                            sText.createTransformedShape(tr);
                            sKara.createTransformedShape(tr);
                            sBord.createTransformedShape(tr);
                            sShad.createTransformedShape(tr);
                        }
                        
                    }
                    case ScaleX -> {
                        if(o != null && o instanceof Integer w){
                            tr.scale(w, 1);
                            sText.createTransformedShape(tr);
                            sKara.createTransformedShape(tr);
                            sBord.createTransformedShape(tr);
                            sShad.createTransformedShape(tr);
                        }
                    }
                    case ScaleY -> {
                        if(o != null && o instanceof Integer h){
                            tr.scale(1, h);
                            sText.createTransformedShape(tr);
                            sKara.createTransformedShape(tr);
                            sBord.createTransformedShape(tr);
                            sShad.createTransformedShape(tr);
                        }
                    }
                    case Rotation, RotationZ -> {
                        if(o != null && o instanceof Double rz){
                            Point2D org = (Point2D)bsh.getTags().get("\\org");
                            if(org != null){
                                tr.rotate(rz, org.getX(), org.getY());
                                sText.createTransformedShape(tr);
                                sKara.createTransformedShape(tr);
                                sBord.createTransformedShape(tr);
                                sShad.createTransformedShape(tr);
                            }else{
                                //tr.rotate(rz, org.getX(), org.getY()); TODO Calcul posX posY sans size
                            }
                        }
                    }
                    case RotationX -> {
                        // 3D
                        if(o != null && o instanceof Double rx){
                            System.out.println("TODO frx" + rx);
                        }
                    }
                    case RotationY -> {
                        // 3D
                        if(o != null && o instanceof Double ry){
                            System.out.println("TODO fry" + ry);
                        }
                    }
                    case ShearX -> {
                        if(o != null && o instanceof Double sx){
                            tr.shear(sx, 1);
                            sText.createTransformedShape(tr);
                            sKara.createTransformedShape(tr);
                            sBord.createTransformedShape(tr);
                            sShad.createTransformedShape(tr);
                        }
                    }
                    case ShearY -> {
                        if(o != null && o instanceof Double sy){
                            tr.shear(1, sy);
                            sText.createTransformedShape(tr);
                            sKara.createTransformedShape(tr);
                            sBord.createTransformedShape(tr);
                            sShad.createTransformedShape(tr);
                        }
                    }
                    case Border -> {
                        
                    }
                    case XBorder -> {
                        
                    }
                    case YBorder -> {
                        
                    }
                    case Shadow -> {
                        
                    }
                    case XShadow -> {
                        
                    }
                    case YShadow -> {
                        
                    }
                    case Karaoke -> {
                        
                    }
                    case KaraokeFill, OldKaraokeFill -> {
                        
                    }
                    case KaraokeOutline -> {
                        
                    }
                    
                }
            }
            
            int alpha = bsh.getTags().get("\\alpha") == null ? -1 : 255 - (int)bsh.getTags().get("\\alpha");
            
            // Text
            Color cText = (Color)(bsh.getTags().get("\\c") != null ?
                    bsh.getTags().get("\\c") : bsh.getTags().get("\\1c"));
            int aText = bsh.getTags().get("\\1a") == null ? alpha : 255 - (int)bsh.getTags().get("\\1a");
            cText = new Color(cText.getRed(), cText.getGreen(), cText.getBlue(), (aText == -1 ? 0 : aText));
            gText.setColor(cText);
            gText.fill(sText);
            
            // Kara
            Color cKara = (Color)bsh.getTags().get("\\2c");
            int aKara = bsh.getTags().get("\\2a") == null ? alpha : 255 - (int)bsh.getTags().get("\\2a");
            if(aKara != -1){
                cKara = new Color(cKara.getRed(), cKara.getGreen(), cKara.getBlue(), aKara);
            }
            gKara.setColor(cKara);
            gKara.fill(sKara);
            
            // Border
            Color cBord = (Color)bsh.getTags().get("\\3c");
            int aBord = bsh.getTags().get("\\3a") == null ? alpha : 255 - (int)bsh.getTags().get("\\3a");
            if(aBord != -1){
                cBord = new Color(cBord.getRed(), cBord.getGreen(), cBord.getBlue(), aBord);
            }
            gBord.setColor(cBord);
            gBord.fill(sBord);
            
            // Shadow
            Color cShad = (Color)bsh.getTags().get("\\4c");
            int aShad = bsh.getTags().get("\\4a") == null ? alpha : 255 - (int)bsh.getTags().get("\\4a");
            if(aShad != -1){
                cShad = new Color(cShad.getRed(), cShad.getGreen(), cShad.getBlue(), aShad);
            }
            gShad.setColor(cShad);
            gShad.fill(sShad);
        }
        
        // Debug marks
        gText.setColor(Color.green);
        gKara.setColor(Color.green);
        gBord.setColor(Color.green);
        gShad.setColor(Color.green);
        // En haut à gauche
        gText.fillRect(0, 0, 50, 5);
        gText.fillRect(0, 0, 5, 50);
        // En haut à droite
        gText.fillRect(sceneWidth-50, 0, 50, 5);
        gText.fillRect(sceneWidth-5, 0, 5, 50);
        // En bas à gauche
        gText.fillRect(0, sceneHeight-5, 50, 5);
        gText.fillRect(0, sceneHeight-50, 5, 50);
        // En bas à droite
        gText.fillRect(sceneWidth-50, sceneHeight-5, 50, 5);
        gText.fillRect(sceneWidth-5, sceneHeight-50, 5, 50);
        // Layer existence
        gText.drawString("text", 15, 30);
        gKara.drawString("kara", 15, 50);
        gBord.drawString("bord", 15, 70);
        gShad.drawString("shad", 15, 90);
        // Size
        gText.drawString(String.format("%dx%d", sceneWidth, sceneHeight), 15, 110);
        // Margins
        gText.setColor(Color.magenta);
        // En haut à gauche
        gText.fillRect(0, 0, 25, 5);
        gText.fillRect(0, 0, 5, 25);
        // En haut à droite
        gText.fillRect(sceneWidth-25, 0, 25, 5);
        gText.fillRect(sceneWidth-5, 0, 5, 25);
        // En bas à gauche
        gText.fillRect(0, sceneHeight-5, 25, 5);
        gText.fillRect(0, sceneHeight-25, 5, 25);
        // En bas à droite
        gText.fillRect(sceneWidth-25, sceneHeight-5, 25, 5);
        gText.fillRect(sceneWidth-5, sceneHeight-25, 5, 25);
        
        // Blend layers
        g.drawImage(shadLayerImage, 0, 0, null);
        g.drawImage(karaLayerImage, 0, 0, null);
        g.drawImage(bordLayerImage, 0, 0, null);
        g.drawImage(textLayerImage, 0, 0, null);
        
        // Dispose layers
        gText.dispose();
        gKara.dispose();
        gBord.dispose();
        gShad.dispose();
    }
}
