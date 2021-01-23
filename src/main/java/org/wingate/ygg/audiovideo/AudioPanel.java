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
package org.wingate.ygg.audiovideo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.ui.IfrWave;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.SignalData;

/**
 *
 * @author util2
 */
public class AudioPanel extends JPanel {
    
    private AVInfo info = null;
    private AudioLink audioLink = null;
    
    private IfrWave wave = null;
    
    private long offset = 0L;
    private long msPerImage = 8000L;
    private float msPerPixel = 0.25f;
    
    private boolean showSpectrum = false;
    
    private Time start = Time.create(0L);
    private Time end = Time.create(0L);
    
    private long startCursor = -1;
    private long stopCursor = -1;

    public AudioPanel() {
        init();
    }
    
    private void init(){
        setDoubleBuffered(true);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> {
                        //LEFT
                        startCursor = e.getX() + Math.abs(offset);
                    }
                    case MouseEvent.BUTTON3 -> {
                        //RIGHT
                        stopCursor = e.getX() + Math.abs(offset);
                    }
                    case MouseEvent.BUTTON2 -> {
                        //CENTER
                    }
                }
                repaint();
            }
        });
    }

    public void setInfo(AVInfo info, int scrollBarTotal, int imageSize) {
        this.info = info;
        audioLink = AudioLink.create(scrollBarTotal, imageSize, info.getDuration() / 1000L);
    }

    public void setWave(IfrWave wave) {
        this.wave = wave;
    }
    
    public BufferedImage getImage(long ms, boolean spectrum){
        return SignalData.getImage(ms, spectrum);
    }
    
    public void setCurrentScrolledMilliseconds(int value){
        if(audioLink != null){
            // Cherche le décalage la forme d'onde
            offset = audioLink.getOffset(value, msPerImage, msPerPixel);
            repaint();
        }
    }

    public boolean isShowSpectrum() {
        return showSpectrum;
    }

    public void setShowSpectrum(boolean showSpectrum) {
        this.showSpectrum = showSpectrum;
        repaint();
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public float getMsPerPixel() {
        return msPerPixel;
    }

    public void setMsPerPixel(float msPerPixel) {
        this.msPerPixel = msPerPixel;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        if(showSpectrum == false & info != null & audioLink != null){
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la forme d'onde
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            double r = Math.abs(offset) / getWidth();
            long floor = Math.round(Math.floor(r) * msPerImage * msPerPixel);
            long ceil = Math.round(Math.ceil(r + 0.5) * msPerImage * msPerPixel);
            
            // a = combien de fois on a getWidth() dans offset
            int a = (int)Math.round(Math.floor(r));
            // x = le reste
            int x = (int)(offset + a * getWidth());
            
//            System.out.println("floor = " + floor + "; ceil = " + ceil + " | " + x);
            
            if(SignalData.isImageExists(floor, false)){
                g.drawImage(getImage(floor, false), x, 0, null);
            }

            if(SignalData.isImageExists(ceil, false)){
                g.drawImage(getImage(ceil, false), x + getWidth(), 0, null);
            }
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la ligne du zéro
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            g.setColor(Color.magenta);
            g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine le repère
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            g.setColor(Color.red);
            for(int xa = (int)offset; xa<getWidth(); xa+=getWidth()/2){
                g.drawLine(xa, 0, xa, getHeight());
            }
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la zone sélectionnée
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            int xa = (int)(offset + startCursor);
            int xb = (int)(offset + stopCursor);
            
            // Zone (area)
            if(xb > xa){
                g.setColor(DrawColor.lime.getColor(0.3f));
                g.fillRect(xa, 0, xb-xa, getHeight());
            }
            
            // Début (start)
            g.setColor(DrawColor.dark_khaki.getColor(0.5f));
            g.drawLine(xa-1, 0, xa-1, getHeight());
            g.setColor(DrawColor.dark_khaki.getColor());
            g.drawLine(xa, 0, xa, getHeight());
            g.setColor(DrawColor.dark_khaki.getColor(0.5f));
            g.drawLine(xa+1, 0, xa+1, getHeight());
            
            // Fin (stop)
            g.setColor(DrawColor.chocolate.getColor(0.5f));
            g.drawLine(xb-1, 0, xb-1, getHeight());
            g.setColor(DrawColor.chocolate.getColor());
            g.drawLine(xb, 0, xb, getHeight());
            g.setColor(DrawColor.chocolate.getColor(0.5f));
            g.drawLine(xb+1, 0, xb+1, getHeight());            
            //------------------------------------------------------------------
        }
        
        if(showSpectrum == true & info != null & audioLink != null){
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la forme d'onde
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            double r = Math.abs(offset) / getWidth();
            long floor = Math.round(Math.floor(r) * msPerImage * msPerPixel);
            long ceil = Math.round(Math.ceil(r + 0.5) * msPerImage * msPerPixel);
            
            // a = combien de fois on a getWidth() dans offset
            int a = (int)Math.round(Math.floor(r));
            // x = le reste
            int x = (int)(offset + a * getWidth());
            
            if(SignalData.isImageExists(floor, true)){
                g.drawImage(getImage(floor, true), x, 0, null);
            }

            if(SignalData.isImageExists(ceil, true)){
                g.drawImage(getImage(ceil, true), x + getWidth(), 0, null);
            }
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine le repère
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            g.setColor(Color.red);
            for(int xa = (int)offset; xa<getWidth(); xa+=getWidth()/2){
                g.drawLine(xa, 0, xa, getHeight());
            }
            //------------------------------------------------------------------
                        
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la zone sélectionnée
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            int xa = (int)(offset + startCursor);
            int xb = (int)(offset + stopCursor);
            
            // Zone (area)
            if(xb > xa){
                g.setColor(DrawColor.cyan.getColor(0.3f));
                g.fillRect(xa, 0, xb-xa, getHeight());
            }
            
            // Début (start)
            g.setColor(DrawColor.lime.getColor(0.5f));
            g.drawLine(xa-1, 0, xa-1, getHeight());
            g.setColor(DrawColor.lime.getColor());
            g.drawLine(xa, 0, xa, getHeight());
            g.setColor(DrawColor.lime.getColor(0.5f));
            g.drawLine(xa+1, 0, xa+1, getHeight());
            
            // Fin (stop)
            g.setColor(DrawColor.yellow.getColor(0.5f));
            g.drawLine(xb-1, 0, xb-1, getHeight());
            g.setColor(DrawColor.yellow.getColor());
            g.drawLine(xb, 0, xb, getHeight());
            g.setColor(DrawColor.yellow.getColor(0.5f));
            g.drawLine(xb+1, 0, xb+1, getHeight());            
            //------------------------------------------------------------------
        }
        
    }
    
}
