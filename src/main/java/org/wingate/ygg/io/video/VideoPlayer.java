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
package org.wingate.ygg.io.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class VideoPlayer implements Runnable {
    
    private enum Controller{
        Stop, Play;
    }
    
    private volatile Controller ctrl = Controller.Stop;
    private volatile Time start = Time.create(0L);
    private volatile Time stop = Time.create(0L);
    private volatile long oldNanos = 0L;
    private Thread th = null;
    private File video = null;
    private FFmpegFrameGrabber g = null;
    
    // Valeurs à renvoyer par fire event
    private double milliseconds = 0d;
    private BufferedImage bi = null;
    private int frameNumber = 0;
    
    // Audio/Vidéo
    Java2DFrameConverter converter = new Java2DFrameConverter();

    public VideoPlayer() {
        init();
    }
    
    private void init(){        
        startThread();        
    }
    
    public void setVideo(File video) throws FrameGrabber.Exception {
        if(video != null && g != null){
            g.stop();
            g = null;
        }
        // Vidéo
        this.video = video;
        
        // Entrée Audio/Vidéo
        g = new FFmpegFrameGrabber(video);
        
        // Go
        g.start();
    }
    
    public void startThread(){
        if(th == null){
            th = new Thread(this);
            th.start();
        }        
    }
    
    public void stopThread(){
        if(th != null && th.isAlive()){
            th.interrupt();
            th = null;
        }
    }
    
    public void playMedia(Time start, Time stop){
        this.start = start;
        this.stop = stop;
        if(video != null){
            if(g.hasVideo() == true){
                try {
                    g.setVideoTimestamp(Time.toMillisecondsTime(start));
                    oldNanos = Time.toMillisecondsTime(start) * 1000000L;
                } catch (FrameGrabber.Exception ex) {
                    return;
                }
            }
            if(ctrl == Controller.Stop){     
                ctrl = Controller.Play;
            }
        }
    }
    
    public void playMedia(){
        playMedia(Time.create(0L), Time.create(0L));
    }
    
    public void pauseMedia(){
        if(video != null){
            switch(ctrl){
                case Play -> { ctrl = Controller.Stop; }
                case Stop -> { ctrl = Controller.Play; }
            }
        }
    }
    
    public void stopMedia(){
        if(video != null){
            if(ctrl == Controller.Play){
                ctrl = Controller.Stop;
            }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Evénements">
    
    private final EventListenerList listeners = new EventListenerList();
    
    public void addVideoListener(IVideo listener) {
        listeners.add(VideoListener.class, (VideoListener)listener);
    }

    public void removeVideoListener(IVideo listener) {
        listeners.remove(VideoListener.class, (VideoListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireVideo(VideoEvent event) {
        for(Object o : getListeners()){
            if(o instanceof VideoListener){
                VideoListener listen = (VideoListener)o;
                listen.getImage(event);
                break;
            }
        }
    }
    
    // </editor-fold>
    
    @Override
    public void run() {
        while(true){
            if(ctrl == Controller.Play){
                try {
                    // On obtient un nouveau type de frame (DATA ou AUDIO ou VIDEO)
                    Frame frame = g.grab();
                    
                    // On vérifie si on n'a pas atteint la limite
                    // si le temps n'est pas le même pour start et stop
                    if(Time.isEqual(start, stop) == false){
                        // Si start et stop sont différents
                        if(frame.timestamp >= Time.toMillisecondsTime(stop)){
                            // Alors si on a atteint la limite
                            stopMedia();
                        }
                    }
                    
                    // Video
                    if(frame.image != null){
                    
                        // On met à jour le temps de la vidéo
                        milliseconds = frame.timestamp / 1000d;

                        // On met à jour le numéro d'image
                        frameNumber = g.getFrameNumber();

                        try{
                            bi = converter.convert(frame);
                        }catch(Exception exc){
                            // This is not an image
                        }
                        
                        // On transmet les éléments à l'event
                        VideoEvent event = new VideoEvent(bi, milliseconds, frameNumber);
                        fireVideo(event);


                        // On prend le temps à l'instant T                        
                        long before = System.nanoTime();
                        long timestamp = frame.timestamp * 1000L;
                        long calculatedTimestampNanos = oldNanos + System.nanoTime() - before;

                        // On attend le temps requis entre deux frames
                        while(timestamp > calculatedTimestampNanos){
                            calculatedTimestampNanos = oldNanos + System.nanoTime() - before;
                        }

                        oldNanos = calculatedTimestampNanos;
                    }
                    
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
