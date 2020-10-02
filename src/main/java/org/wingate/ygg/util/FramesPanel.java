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
package org.wingate.ygg.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JPanel;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class FramesPanel extends JPanel {
    
    private FFStuffs ffss;
    private boolean dark = false;
    
    private Map<Integer, Time> IFrames;
    private Map<Integer, Time> PFrames;
    private Map<Integer, Time> BFrames;
    
    private int frameCount = 0;
    private Time currentTime = Time.create(0L);
    
    private Time areaStartTime = Time.create(0L);
    private Time areaEndTime = Time.create(0L);

    public FramesPanel(boolean dark) {
        this.dark = dark;
        init();
    }
    
    private void init(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch(e.getButton()){
                    case MouseEvent.BUTTON1 -> {
                        areaStartTime = Time.getTimeFromFrame(e.getX() * frameCount / getWidth(), ffss.getFps());
                        updateArea(areaStartTime, areaEndTime);
                    }
                    case MouseEvent.BUTTON3 -> {
                        areaEndTime = Time.getTimeFromFrame(e.getX() * frameCount / getWidth(), ffss.getFps());
                        updateArea(areaStartTime, areaEndTime);
                    }
                    case MouseEvent.BUTTON2 -> {
                        currentTime = Time.getTimeFromFrame(e.getX() * frameCount / getWidth(), ffss.getFps());
                        updatePosition(currentTime);
                    }
                }
            }
        });
    }
    
    public void configure(FFStuffs ffss){
        this.ffss = ffss;
        this.IFrames = ffss.getIFrames();
        this.PFrames = ffss.getPFrames();
        this.BFrames = ffss.getBFrames(); 
        frameCount = Time.getFrame(ffss.getDuration(), ffss.getFps());
    }

    @Override
    public void paint(Graphics g) {        
        // On peint l'arrière-plan
        g.setColor(FramesColors.Background.getColor(dark));
        g.fillRect(0, 0, getWidth(), getHeight());
                
        if(frameCount > 0){
            // On peint les I frames
            g.setColor(FramesColors.KeyFrame.getColor(dark));
            for(Map.Entry<Integer, Time> entry : IFrames.entrySet()){
                int x = Math.round((float)entry.getKey() * getWidth() / frameCount);
                g.drawLine(x, 0, x, getHeight());
            }
            // On peint le temps du début
            g.setColor(FramesColors.StartArea.getColor(dark));
            int xStart = Math.round(Time.getFrame(areaStartTime, ffss.getFps()) * getWidth() / frameCount);
            g.drawLine(xStart, 0, xStart, getHeight());

            // On peint le temps de fin
            g.setColor(FramesColors.StopArea.getColor(dark));
            int xStop = Math.round(Time.getFrame(areaEndTime, ffss.getFps()) * getWidth() / frameCount);
            g.drawLine(xStop, 0, xStop, getHeight());
            
            // On peint la zone
            g.setColor(FramesColors.Area.getColor(dark));
            g.fillRect(xStart, 0, xStop - xStart, getHeight());
            
            // On peint la progression
            g.setColor(FramesColors.Progress.getColor(dark));
            int x1 = Math.round(Time.getFrame(currentTime, ffss.getFps()) * getWidth() / frameCount);
            g.drawLine(x1, 0, x1, getHeight());
        }
    }
    
    public void updatePosition(Time time){
        currentTime = time;
        repaint();
    }
    
    public void updateArea(Time start, Time end){
        areaStartTime = start;
        areaEndTime = end;
        repaint();
    }
    
    public FFStuffs getFfss() {
        return ffss;
    }

    public Time getCurrentTime() {
        return currentTime;
    }

    public Time getAreaStartTime() {
        return areaStartTime;
    }

    public Time getAreaEndTime() {
        return areaEndTime;
    }
    
    public enum FramesColors{
        Background("Background", DrawColor.white_smoke.getColor(), new Color(71, 75, 76)),
        KeyFrame("Key frames", Color.black, Color.white),
        StartArea("Start", DrawColor.dark_green.getColor(), DrawColor.lime.getColor()),
        StopArea("Stop", DrawColor.dark_red.getColor(), DrawColor.orange_red.getColor()),
        Area("Area", DrawColor.green_yellow.getColor(0.5f), DrawColor.green_yellow.getColor(0.5f)),
        Progress("Progress", DrawColor.violet.getColor(), DrawColor.violet.getColor()),
        Pointer("Pointer", Color.pink, Color.pink);
        
        
        String name;
        Color light;
        Color dark;
        
        private FramesColors(String name, Color light, Color dark){
            this.name = name;
            this.light = light;
            this.dark = dark;
        }

        public String getName() {
            return name;
        }

        public Color getColor(boolean isDark) {
            return isDark == false ? light : dark;
        }
    }
    
}
