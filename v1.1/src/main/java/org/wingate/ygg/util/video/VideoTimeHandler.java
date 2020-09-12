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
package org.wingate.ygg.util.video;

import javax.swing.event.EventListenerList;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class VideoTimeHandler implements Runnable {

    private Thread displayTH = null;
    private volatile boolean running = false;
    private long current = 0L;
    private long duration = 0L;
    private long requiredStop = -1L;
    
    private long msAtStart = -1L;

    public VideoTimeHandler() {
        init();
    }
    
    private void init(){        
        displayTH = new Thread(this);
        displayTH.start();
    }
    
    public void playVTH(){
        running = true;
        msAtStart = System.currentTimeMillis();
    }
    
    public void pauseVTH(){
        running = !running;
        if(running == true){
            msAtStart = System.currentTimeMillis();
        }else{
            current = current + System.currentTimeMillis() - msAtStart;
        }
    }
    
    public void stopVTH(){
       running = false;
    }
    
    public void resetVTH(Time t){
        current = Time.toMillisecondsTime(t);
        duration = 0L;
        requiredStop = -1L;
    }
    
    public void setStopTime(Time t){
        requiredStop = Time.toMillisecondsTime(t);
    }
    
    public void setDuration(Time t){
        duration = Time.toMillisecondsTime(t);
    }
    
    private void changeTime(){
        try {
            Thread.sleep(1);
            long ms = current + System.currentTimeMillis() - msAtStart;
            fireTimeChanged(new VideoTimeEvent(Time.create(ms)));
            if(requiredStop != -1L && ms >= requiredStop){
                fireTimeReached(new VideoTimeEvent(Time.create(ms)));
            }
            if(duration != 0L && ms >= duration){
                fireTimeEnded(new VideoTimeEvent(Time.create(ms)));
                stopVTH();
            }
        } catch (InterruptedException ex) {
            // Silent error stop while in sleep
        }
    }
    
    @Override
    public void run() {
        while(true){
            if(running == true){
                changeTime();
            }
        }
    }
    
    //==========================================================================
    // Events
    //==========================================================================
    
    private final EventListenerList listeners = new EventListenerList();
    
    public void addVideoTimeListener(IVideoTimeListener listener){
        listeners.add(VideoTimeListener.class, (VideoTimeListener)listener);
    }
    
    public void removeVideoTimeListener(IVideoTimeListener listener){
        listeners.remove(VideoTimeListener.class, (VideoTimeListener)listener);
    }
    
    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireTimeChanged(VideoTimeEvent event) {
        for(Object o : getListeners()){
            if(o instanceof VideoTimeListener){
                VideoTimeListener listen = (VideoTimeListener)o;
                listen.timeChanged(event);
                break;
            }
        }
    }
    
    protected void fireTimeReached(VideoTimeEvent event) {
        for(Object o : getListeners()){
            if(o instanceof VideoTimeListener){
                VideoTimeListener listen = (VideoTimeListener)o;
                listen.timeReached(event);
                break;
            }
        }
    }
    
    protected void fireTimeEnded(VideoTimeEvent event) {
        for(Object o : getListeners()){
            if(o instanceof VideoTimeListener){
                VideoTimeListener listen = (VideoTimeListener)o;
                listen.timeEnded(event);
                break;
            }
        }
    }
}
