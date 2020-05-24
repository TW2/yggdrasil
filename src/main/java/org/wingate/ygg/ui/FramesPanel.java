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
package org.wingate.ygg.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import javax.swing.JPanel;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class FramesPanel extends JPanel {
    
    private FFStuffs ffss;
    
    private Color backgroundColor = DrawColor.white_smoke.getColor();
    private final Color IFrameColor = DrawColor.blue_violet.getColor();
    private final Color startColor = DrawColor.lime.getColor();
    private final Color endColor = DrawColor.red.getColor();
    private final Color currentTrackerColor = DrawColor.pink.getColor();
    
    private Map<Integer, Time> IFrames;
    private Map<Integer, Time> PFrames;
    private Map<Integer, Time> BFrames;
    
    private int frameCount = 0;
    private Time currentTime = Time.create(0L);    

    public FramesPanel() {      
        
    }
    
    public void configure(FFStuffs ffss, boolean dark){
        this.ffss = ffss;
        this.IFrames = ffss.getIFrames();
        this.PFrames = ffss.getPFrames();
        this.BFrames = ffss.getBFrames(); 
        frameCount = Time.getFrame(ffss.getDuration(), ffss.getFps());
        
        if(dark == true){
            backgroundColor = DrawColor.chocolate.getColor();
        }
    }

    @Override
    public void paint(Graphics g) {        
        // On peint l'arrière-plan
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());
                
        if(frameCount > 0){
            // On peint les I frames
            g.setColor(IFrameColor);
            for(Map.Entry<Integer, Time> entry : IFrames.entrySet()){
                int x = Math.round((float)entry.getKey() * getWidth() / frameCount);
                g.drawLine(x, 0, x, getHeight());
            }
            
            // On peint la progression
            g.setColor(currentTrackerColor);
            int x1 = Math.round(Time.getFrame(currentTime, ffss.getFps()) * getWidth() / frameCount);
            g.drawLine(x1, 0, x1, getHeight());
        }
    }
    
    public void updatePosition(Time time){
        currentTime = time;
        repaint();
    }

    public FFStuffs getFfss() {
        return ffss;
    }
    
}
