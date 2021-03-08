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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssEvent;

/**
 *
 * @author util2
 */
public class AssRender {
    
    /*
    Balise RESET : r<style>
    Balise POSITION : pos(<x>,<y>)
    Balise MOUVEMENT : move(<x1>,<y1>,<x2>,<y2>,<t1>,<t2>)
    Balise ITALIQUE (italic en anglais) : i<v>
    Balise GRAS (bold en anglais) : b<v>
    Balise SOULIGNE (underline en anglais) : u<v>
    Balise BARRE (strike-out en anglais) : s<v>
    Balise BORDURE (border en anglais) : bord<v>
    Balise BORDURE sur X : xbord<v>
    Balise BORDURE sur Y : ybord<v>
    Balise OMBRE (shadow en anglais) : shad<v>
    Balise OMBRE sur X : xshad<v>
    Balise OMBRE sur Y : yshad<v>
    Balise FLOU de génération 1 (blur edge an anglais) : be<v>
    Balise FLOU de génération 2 (blur an anglais) : blur<v>
    Balise NOM de la POLICE (font name en anglais) : fn<v>
    Balise TAILLE de la POLICE (font size en anglais) : fs<v>
    Balise ÉCHELLE de la POLICE sur X (font scale en anglais) : fscx<v>
    Balise ÉCHELLE de la POLICE sur Y (font scale en anglais) : fscy<v>
    Balise ESPACEMENT (font spacing en anglais) : fsp<v>
    Balise ROTATION sur X : frx<v>
    Balise ROTATION sur Y : fry<v>
    Balise ROTATION sur Z : frz<v> ou fr<v>
    Balise CISEAU sur X (shearing en anglais) : fax<v> entre -2 et 2
    Balise CISEAU sur Y (shearing en anglais) : fay<v> entre -2 et 2
    Balise COULEUR du TEXTE : c&H<bb><gg><rr>& ou 1c&H<bb><gg><rr>&
    Balise COULEUR du KARAOKE : 2c&H<bb><gg><rr>&
    Balise COULEUR de la BORDURE : 3c&H<bb><gg><rr>&
    Balise COULEUR de l'OMBRE : 4c&H<bb><gg><rr>&
    Balise TRANSPARENCE générale : alpha&H<vv>&
    Balise TRANSPARENCE du TEXTE : 1a&H<vv>&
    Balise TRANSPARENCE du KARAOKE : 2a&H<vv>&
    Balise TRANSPARENCE de la BORDURE : 3a&H<vv>&
    Balise TRANSPARENCE de l'OMBRE : 4a&H<vv>&
    Balise ALIGNEMENT : an<v>
    Balise KARAOKÉ : k<v>, K<v>, kf<v> et ko<v>
    Balise STYLE D'ENVELOPPE (wrap style en anglais) : q<v>
    Balise ORIGINE : org(<x>,<y>)
    Balise FONDU simple : fad(<fadein>,<fadeout>)
    Balise FONDU complexe : fade(<a1>,<a2>,<a3>,<t1>,<t2>,<t3>,<t4>)
    Balise ANIMATION : 
    t(<modificateurs>),
    t(<accélération>,<modificateurs>),
    t(<t1>,<t2>,<modificateurs>)
    t(<t1>,<t2>,<accélération>,<modificateurs>)
    Balise CLIP sans mode dessin : 
    clip(<x1>,<y1>,<x2>,<y2>) et 
    iclip(<x1>,<y1>,<x2>,<y2>)
    Balise CLIP avec mode dessin :
    clip(<mode dessin>), 
    clip(<échelle>,<mode dessin>),
    iclip(<mode dessin>) et 
    iclip(<échelle>,<mode dessin>)
    Balise DESSIN : p<v>
    */
    
    private BufferedImage image = null;
    private Graphics2D g2d = null;
    private ASS ass = null;
    
    private double eventSizeX = 0d, eventsizeY = 0d;
    private Font font = null;
    
    public AssRender(){
        
    }
    
    private AffineTransform startPosition = new AffineTransform();
    private AffineTransform finalPosition = new AffineTransform();
    
    private void applyAll(AssEvent event, List<Ass64Char> chars){
        if(ass == null) return;
        int width = Integer.parseInt(ass.getResX());
        int height = Integer.parseInt(ass.getResX());        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        
        applyAlignment(event);                  // 1er cause image et marges
        applyPosition(event);                   // la position ne comprend pas les marges
        applyMovement(event);                   // le mouvement ne comprend pas les marges
        font = event.getStyle().getFont();
        applyFontname(event);
        applyFontsize(event);
        
        // Application de l'événement
        float x = (float)startPosition.getTranslateX();
        float y = (float)startPosition.getTranslateY();
        for(Ass64Char c : chars){
            g2d.drawString(c.getText(), x, y);
            x += c.getWidth();
        }
        
        g2d.dispose();
    }

    public BufferedImage getImage() {
        return image;
    }
    
    // -------------------------------------------------------------------------
    // Balise RESET : r<style>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise POSITION : pos(<x>,<y>)
    // ---
    /**
     * La position est fonction de la surcharge pos si spécifiée
     * @param event Evénement sur lequel intervenir
     */
    private void applyPosition(AssEvent event){
        // Cherche le dernier paramètres
        String txt = event.getText();
        if(txt.contains("\\pos")){
            Pattern p = Pattern.compile("pos\\((\\d+),(\\d+)\\)");
            Matcher m = p.matcher(txt);
            if(m.find()){
                int x = Integer.parseInt(m.group(1));
                int y = Integer.parseInt(m.group(2));
                
                // On réunit ici les marges afin de les enlever
                // La position se faisant sans marges
                int mL = event.getMarginL();
//                int mR = event.getMarginR();
                int mV = event.getMarginV();
                
                startPosition.translate(x - mL, y - mV);
            }
        }
    }
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise MOUVEMENT : move(<x1>,<y1>,<x2>,<y2>,<t1>,<t2>)
    // ---
    /**
     * Le mouvement est fonction de la surcharge pos si spécifiée
     * @param event Evénement sur lequel intervenir
     */
    private void applyMovement(AssEvent event){
        // Cherche le dernier paramètres
        String txt = event.getText();
        if(txt.contains("\\move")){
            Pattern p = Pattern.compile("move\\((\\d+),(\\d+),(\\d+),(\\d+).*(\\d*).*(\\d*)\\)");
            Matcher m = p.matcher(txt);
            if(m.find()){
                int x1 = Integer.parseInt(m.group(1));
                int y1 = Integer.parseInt(m.group(2));
                int x2 = Integer.parseInt(m.group(3));
                int y2 = Integer.parseInt(m.group(4));
                
                // On réunit ici les marges afin de les enlever
                // La position se faisant sans marges
                int mL = event.getMarginL();
//                int mR = event.getMarginR();
                int mV = event.getMarginV();
                
                startPosition.translate(x1 - mL, y1 - mV);
                finalPosition.translate(x2, y2);
                
                // TODO: Time t1 t2
            }
        }
    }
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ITALIQUE (italic en anglais) : i<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise GRAS (bold en anglais) : b<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise SOULIGNE (underline en anglais) : u<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise BARRE (strike-out en anglais) : s<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise BORDURE (border en anglais) : bord<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise BORDURE sur X : xbord<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise BORDURE sur Y : ybord<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise OMBRE (shadow en anglais) : shad<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise OMBRE sur X : xshad<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise OMBRE sur Y : yshad<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise FLOU de génération 1 (blur edge an anglais) : be<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise FLOU de génération 2 (blur an anglais) : blur<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise NOM de la POLICE (font name en anglais) : fn<v>
    // ---
    private void applyFontname(AssEvent event){
        String fontname = event.getStyle().getFontname();
        // On vérifie si dans le texte il y a des 'fn'
        String txt = event.getText();
        Pattern p = Pattern.compile("fn([A-Za-z0-9]+)");
        Matcher m = p.matcher(txt);
        if(m.find()){
            fontname = m.group(1);            
        }
        font = new Font(fontname, font.getStyle(), font.getSize());
        g2d.setFont(font);
    }
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TAILLE de la POLICE (font size en anglais) : fs<v>
    // ---
    private void applyFontsize(AssEvent event){
        float fontsize = event.getStyle().getFontsize();
        // On vérifie si dans le texte il y a des 'fn'
        String txt = event.getText();
        Pattern p = Pattern.compile("fs([A-Za-z0-9]+)");
        Matcher m = p.matcher(txt);
        if(m.find()){
            fontsize = Integer.parseInt(m.group(1));            
        }
        font = font.deriveFont(fontsize);
        g2d.setFont(font);
    }
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ÉCHELLE de la POLICE sur X (font scale en anglais) : fscx<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ÉCHELLE de la POLICE sur Y (font scale en anglais) : fscy<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ESPACEMENT (font spacing en anglais) : fsp<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ROTATION sur X : frx<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ROTATION sur Y : fry<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ROTATION sur Z : frz<v> ou fr<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise CISEAU sur X (shearing en anglais) : fax<v> entre -2 et 2
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise CISEAU sur Y (shearing en anglais) : fay<v> entre -2 et 2
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise COULEUR du TEXTE : c&H<bb><gg><rr>& ou 1c&H<bb><gg><rr>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise COULEUR du KARAOKE : 2c&H<bb><gg><rr>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise COULEUR de la BORDURE : 3c&H<bb><gg><rr>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise COULEUR de l'OMBRE : 4c&H<bb><gg><rr>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TRANSPARENCE générale : alpha&H<vv>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TRANSPARENCE du TEXTE : 1a&H<vv>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TRANSPARENCE du KARAOKE : 2a&H<vv>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TRANSPARENCE de la BORDURE : 3a&H<vv>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise TRANSPARENCE de l'OMBRE : 4a&H<vv>&
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ALIGNEMENT : an<v>
    // ---    
    /**
     * La position est fonction du style et de la surcharge an si spécifiée
     * @param event Evénement sur lequel intervenir
     */
    private void applyAlignment(AssEvent event){
        // On réunit la taille de l'image
        int w = image.getWidth();
        int h = image.getHeight();
        
        // On réunit ici les marges
        int mL = event.getMarginL();
        int mR = event.getMarginR();
        int mV = event.getMarginV();
        
        // Cherche le dernier paramètres
        String txt = event.getText();
        int align = txt.contains("\\an") ?
                Integer.parseInt(txt.substring(txt.indexOf("\\an"), txt.indexOf("\\an") + 1)) : 
                event.getStyle().getAlignment();
        System.out.println("align " + align);
        // On définit les alignements avec les marges
        switch(align){
            case 1 -> {
                startPosition.translate(
                        mL, 
                        h - mV - eventsizeY);
            }
            case 2 -> {
                startPosition.translate(
                        (w - eventSizeX) / 2,
                        120d);
//                        h - mV - eventsizeY); 
            }
            case 3 -> {
                startPosition.translate(
                        w - mR - eventSizeX,
                        h - mV - eventsizeY); 
            }
            case 4 -> {
                startPosition.translate(
                        mL,
                        (h - eventsizeY) / 2);
            }
            case 5 -> {
                startPosition.translate(
                        (w - eventSizeX) / 2,
                        (h - eventsizeY) / 2); 
            }
            case 6 -> { 
                startPosition.translate(
                        w - mR - eventSizeX,
                        (h - eventsizeY) / 2);
            }
            case 7 -> {
                startPosition.translate(
                        mL,
                        mV); 
            }
            case 8 -> { 
                startPosition.translate(
                        (w - eventSizeX) / 2, 
                        mV); 
            }
            case 9 -> {
                startPosition.translate(
                        w - mR - eventSizeX, 
                        mV - eventsizeY);
            }
        }
    }
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise KARAOKÉ : k<v>, K<v>, kf<v> et ko<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise STYLE D'ENVELOPPE (wrap style en anglais) : q<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ORIGINE : org(<x>,<y>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise FONDU simple : fad(<fadein>,<fadeout>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise FONDU complexe : fade(<a1>,<a2>,<a3>,<t1>,<t2>,<t3>,<t4>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise ANIMATION : 
    // t(<modificateurs>),
    // t(<accélération>,<modificateurs>),
    // t(<t1>,<t2>,<modificateurs>)
    // t(<t1>,<t2>,<accélération>,<modificateurs>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise CLIP sans mode dessin : 
    // clip(<x1>,<y1>,<x2>,<y2>) et 
    // iclip(<x1>,<y1>,<x2>,<y2>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise CLIP avec mode dessin :
    // clip(<mode dessin>), 
    // clip(<échelle>,<mode dessin>),
    // iclip(<mode dessin>) et 
    // iclip(<échelle>,<mode dessin>)
    // ---
    
    // -------------------------------------------------------------------------
    
    // -------------------------------------------------------------------------
    // Balise DESSIN : p<v>
    // ---
    
    // -------------------------------------------------------------------------
    
    @SuppressWarnings("Convert2Lambda")
    public void render(ASS ass, long currentTime){
        this.ass = ass;
        
        List<AssEvent> evts = new ArrayList<>();
        
        ass.getEvents().forEach(event -> {
            long start = Time.toMillisecondsTime(event.getStartTime());
            long stop = Time.toMillisecondsTime(event.getEndTime());
            if (event.getLineType() == AssEvent.LineType.Dialogue
                    && start <= currentTime && currentTime < stop) {
                evts.add(event);
            }
        });
        
        Collections.sort(evts, new Comparator<AssEvent>() {
            @Override
            public int compare(AssEvent o1, AssEvent o2) {
                return Integer.compare(o1.getLayer(), o2.getLayer());
            }
        });
        
        evts.forEach(event -> {
            try{
                List<Ass64Char> list = Ass64Calc.measure(event);
                eventSizeX = 0d;
                eventsizeY = 0d;
                String[] t = event.getText().split("\\\\N");
                for(Ass64Char c : list){
                    if(eventsizeY == 0d){
                        eventsizeY = (t.length > 0 ? t.length : 1) * c.getExtlead();
                    }
                    eventSizeX += c.getWidth();
                }
                applyAll(event, list);
            }catch(Exception ex){
            }
        });
    }
    
    private Shape getOutline(String s, Font f){
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout layout = new TextLayout(s, f, frc);
        return layout.getOutline(null);
    }
    
}
