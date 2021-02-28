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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
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
    private boolean msEventInitialized = false;
    private long currentMilliseconds = -1L;
    
    private long offset = 0L;
    private long msPerImage = 10000L;
    
    private boolean showSpectrum = false;
    
    private Time start = Time.create(0L);
    private Time end = Time.create(0L);
    
    private long startCursor = -1L;
    private long stopCursor = -1L;
    private int currentCursor = -1;
    
    private Map<Integer, Long> keyframes = new HashMap<>();

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
                        // 2000ms <> 920
                        // xms <> startCursor
                        start = Time.create(startCursor * msPerImage / 920);
                        if(MainFrame.getTableLinkFrame() != null){
                            MainFrame.getTableLinkFrame().setStartTime(start);
                        }
                        if(MainFrame.getVideoFrame() != null & info != null){
                            MainFrame.getVideoFrame().setStartTime(start, info.getFps());
                        }
                    }
                    case MouseEvent.BUTTON3 -> {
                        //RIGHT
                        stopCursor = e.getX() + Math.abs(offset);
                        // 2000ms <> 920
                        // xms <> stopCursor
                        end = Time.create(stopCursor * msPerImage / 920);
                        if(MainFrame.getTableLinkFrame() != null){
                            MainFrame.getTableLinkFrame().setEndTime(end);
                        }
                        if(MainFrame.getVideoFrame() != null & info != null){
                            MainFrame.getVideoFrame().setEndTime(end, info.getFps());
                        }
                    }
                    case MouseEvent.BUTTON2 -> {
                        //CENTER
                    }
                }
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                currentCursor = e.getX();
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
        if(msEventInitialized == false){
            wave.getPlayAudio().addAudioListener(new AudioListener() {
                @Override
                public void getMillisecondsTime(long ms) {
                    if(currentMilliseconds != ms){
                        currentMilliseconds = ms;
                        repaint();
                    }
                }
            });
            msEventInitialized = true;
        }
    }
    
    public BufferedImage getImage(long ms, boolean spectrum){
        return SignalData.getImage(ms, spectrum);
    }
    
    public void setCurrentScrolledMilliseconds(int value){
        if(audioLink != null){
            // Cherche le décalage la forme d'onde
            offset = audioLink.getOffset(value, msPerImage);
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

    public long getMsPerImage() {
        return msPerImage;
    }

    public void setMsPerImage(long msPerImage) {
        this.msPerImage = msPerImage;
    }

    public void setKeyframes(Map<Integer, Long> keyframes) {
        this.keyframes = keyframes;
        repaint();
    }
    
    public void setArea(Time start, Time end){
        startCursor = Time.toMillisecondsTime(start) * getWidth() / msPerImage;
        stopCursor = Time.toMillisecondsTime(end) * getWidth() / msPerImage;
        this.start = start;
        this.end = end;
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        if(showSpectrum == false & info != null & audioLink != null){
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la forme d'onde
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            double r = Math.abs(offset) / getWidth();
            long floor = Math.round(Math.floor(r) * msPerImage);
            long ceil = Math.round(Math.ceil(r + 0.5) * msPerImage);
            
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
            for(int xa = (int)offset; xa<getWidth(); xa+=getWidth()/(msPerImage/1000)){
                g.drawLine(xa, 0, xa, getHeight());
            }
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine les keyframes
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            for(Map.Entry<Integer, Long> entry : keyframes.entrySet()){
                long b = offset + entry.getValue() / 1000L * getWidth() / msPerImage;
                if(b > 0 && b < getWidth()){
                    int xc = (int)(b);
                    g.setColor(MainFrame.isDark() ? Color.white : Color.black);
                    g.drawLine(xc, 0, xc, getHeight());
                }
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
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine le curseur
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            g.setColor(Color.pink);
            g.drawLine(currentCursor, 0, currentCursor, getHeight());
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine l'indicateur de lecture
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            long b = offset + currentMilliseconds * getWidth() / msPerImage;
            if(b > 0 && b < getWidth()){
                int xc = (int)(b);
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(xc-1, 0, xc-1, getHeight());
                g.setColor(DrawColor.magenta.getColor());
                g.drawLine(xc, 0, xc, getHeight());
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(xc+1, 0, xc+1, getHeight());
            }
            //------------------------------------------------------------------
        }
        
        if(showSpectrum == true & info != null & audioLink != null){
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine la forme d'onde
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            double r = Math.abs(offset) / getWidth();
            long floor = Math.round(Math.floor(r) * msPerImage);
            long ceil = Math.round(Math.ceil(r + 0.5) * msPerImage);
            
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
            for(int xa = (int)offset; xa<getWidth(); xa+=getWidth()/(msPerImage/1000)){
                g.drawLine(xa, 0, xa, getHeight());
            }
            //------------------------------------------------------------------
                 
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine les keyframes
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            for(Map.Entry<Integer, Long> entry : keyframes.entrySet()){
                long b = offset + entry.getValue() / 1000L * getWidth() / msPerImage;
                if(b > 0 && b < getWidth()){
                    int xc = (int)(b);
                    g.setColor(DrawColor.white.getColor());
                    g.drawLine(xc, 0, xc, getHeight());
                }
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
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine le curseur
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            g.setColor(Color.pink);
            g.drawLine(currentCursor, 0, currentCursor, getHeight());
            //------------------------------------------------------------------
            
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // On dessine l'indicateur de lecture
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            long b = offset + currentMilliseconds * getWidth() / msPerImage;
            if(b > 0 && b < getWidth()){
                int xc = (int)(b);
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(xc-1, 0, xc-1, getHeight());
                g.setColor(DrawColor.magenta.getColor());
                g.drawLine(xc, 0, xc, getHeight());
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(xc+1, 0, xc+1, getHeight());
            }
            //------------------------------------------------------------------
        }
        
    }
    
}
