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

import java.awt.image.BufferedImage;
import java.io.File;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.Subtitles;

/**
 *
 * @author util2
 */
public class PlaySubtitles implements Runnable {
    
    private Thread thAct = null;
    private volatile File subsFile = null;
    private volatile BufferedImage subsImage = null;
    private volatile long ms = 0L;
    private volatile boolean doAct = false;

    public PlaySubtitles() {
        init();
    }
    
    private void init(){
        startThread();
    }
    
    public void startThread(){
        stopThread();
        thAct = new Thread(this);
        thAct.start();
    }
    
    public void stopThread(){
        if(thAct != null){
            thAct.interrupt();
            thAct = null;
        }
    }
    
    public void changeMilliseconds(long ms){
        this.ms = ms;
        doAct = true;
    }

    @Override
    public void run() {
        while(true){
            if(doAct == true){
                if(subsFile != null){
                    subsImage = Subtitles.getSubs(subsFile, Time.create(ms), 1920, 1080);
                    MainFrame.getVideoFrame().updateSubtitlesImage(subsImage);
                }
                doAct = false;
            }
        }
    }

    public void setSubsFile(File subsFile) {
        this.subsFile = subsFile;
    }
    
}
