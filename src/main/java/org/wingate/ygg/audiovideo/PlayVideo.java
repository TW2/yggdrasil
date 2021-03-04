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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.event.EventListenerList;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssRender;
import org.wingate.ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class PlayVideo implements Runnable {
    
    // Lecture vidéo
    private final VideoPanel videoPanel = new VideoPanel();
    
    public enum Action {
        None, Ready, Play, Pause, Stop;
    }

    public long getMsAreaStart() {
        return msAreaStart;
    }

    public void setMsAreaStart(long msAreaStart) {
        this.msAreaStart = msAreaStart;
    }

    public long getMsAreaStop() {
        return msAreaStop;
    }

    public void setMsAreaStop(long msAreaStop) {
        this.msAreaStop = msAreaStop;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Lecture vidéo">
    
    private volatile FFmpegFrameGrabber grabber = null;

    private volatile Action action = Action.None;

    private Thread thAct = null;

    private boolean area = false;
    
    private volatile long msAreaStart = 0L, msAreaStop = 0L, msCurrent = 0L;
    private volatile long msFromAudio = 0L;
    private volatile boolean msFromAudioUsed = false;

    private final Java2DFrameConverter converter = new Java2DFrameConverter();
    
    private volatile ASS ass = null;

    public PlayVideo() {
        init();
    }

    private void init(){
        startThread();
    }

    public void setVideo(File video) throws FrameGrabber.Exception, LineUnavailableException{
        // Vérification de la présence de fichier existant
        if(video.exists() == false){
            action = Action.None;
            return;
        }

        // Clôture du composant FFMpeg s'il existe (lors d'un précédent passage ici)
        if(grabber != null){
            grabber.stop();
            grabber.release();
            grabber = null;
        }

        // Lancement du composant FFMpeg (nouveau)
        grabber = new FFmpegFrameGrabber(video);
        grabber.start();

        // Renseignement de l'état de l'action (état de lecture)
        action = Action.Ready;
        
        System.out.println("Video ready for playback!");
    }
    
    public void setupAudioSync(PlayAudio pa){
        pa.addAudioListener(new AudioListener() {
            @Override
            public void getMillisecondsTime(long ms) {
                System.out.println("OKKO");
                action = Action.Ready;
                msFromAudio = ms;
                msFromAudioUsed = true;
                action = Action.Play;
            }
        });
    }

    public void startThread(){
        stopThread();
        thAct = new Thread(this);
        thAct.start();
    }

    public void stopThread(){
        if(thAct != null && thAct.isInterrupted() == false){
            thAct.interrupt();
            thAct = null;
        }else if(thAct != null && thAct.isInterrupted() == true){
            thAct = null;
        }
    }
    
    long timeMicrosCounter = 0L;
    long timeMicros_before = 0L;
    long timeMicros_after = 0L;

    @Override
    public void run() {
        while(true){
            if(action == Action.Play){
                try {
                    if(msAreaStart != 0L && area == true){
                        grabber.setVideoTimestamp(msAreaStart * 1000L);
                        timeMicrosCounter = msAreaStart * 1000L;
                        area = false;
                    }
                    
                    org.bytedeco.javacv.Frame frame = grabber.grab();

                    if (frame == null) {
                        action = Action.None;
                        continue;
                    }

                    if(frame.image != null){
                        // We have a timestamp with pts.
                        // That timestamp is in microseconds.
                        // We must do available a picture at pts
                        // but we can't access it directly.
                        // ---
                        // Here is the timestamp in microseconds :
                        long timestamp = frame.timestamp;
                        // We have a counter of last timestamp :
                        // long timeMicrosCounter = 0L;
                        // ---
                        // Now calculate a target :
                        long target = timestamp - timeMicrosCounter;
                        // We have to reach the target
                        // ---
                        // Here is a thing to reach it :
                        // We waiting for a good time
                        try{
                            if(target != 0L){
                                timeMicros_after = System.nanoTime() / 1000L;
                                TimeUnit.MICROSECONDS.sleep(
                                        target - (timeMicros_after - timeMicros_before));
                            }                                
                        }catch(InterruptedException exc){
                            // Quit the loop if error
                            break;
                        }
                        timeMicros_before = System.nanoTime() / 1000L;
                        // Now we can display and wait for another image next
                        // Fire to BufferedImage (converter.convert(frame))
                        fireVideo(converter.convert(frame));
                        
                        msCurrent = frame.timestamp / 1000L;
                        fireTime(msCurrent);
                        fireFrameNumber(grabber.getFrameNumber());
                        
                        timeMicrosCounter = timestamp;
                    }

                    if(msAreaStop != 0L && frame.timestamp / 1000L >= msAreaStop){
                        action = Action.Ready;
                        msAreaStart = 0L;
                        msAreaStop = 0L;
                    }
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(PlayVideo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void playStopVideo(){
        area = true;
        switch(action){
            case Ready -> action = Action.Play;
            case Play -> action = Action.Pause;
            case Pause -> action = Action.Play;
            case Stop -> action = Action.Play;
        }
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void updateImage(BufferedImage image){
        videoPanel.updateImage(image);
    }

    public void updateSubtitlesImage(BufferedImage subs){
        videoPanel.updateImage(subs);
    }
    
    public VideoPanel getVideoPanel(){
        return videoPanel;
    }

    public void setAss(ASS ass) {
        this.ass = ass;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Composant vidéo">
    
    public class VideoPanel extends javax.swing.JPanel {

        private BufferedImage img = null;
        
        public VideoPanel() {            
            init();
        }
        
        private void init(){
            setDoubleBuffered(true);
        }
        
        public void updateImage(BufferedImage img){
            this.img = img;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            if(img != null){
                g.setColor(MainFrame.isDark() ? Color.gray : DrawColor.alice_blue.getColor());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                Dimension dim = getScaledDimension(
                        new Dimension(img.getWidth(), img.getHeight()), 
                        new Dimension(getWidth(), getHeight())
                );

                int x = (getWidth() - dim.width) / 2;
                int y = (getHeight() - dim.height) / 2;
                g.drawImage(img, x, y, dim.width, dim.height, null);
                
                if(ass != null){
                    AssRender ar = new AssRender();
                    ar.render(ass, msCurrent);
                    BufferedImage subs = ar.getImage();
                    if(subs != null){
                        System.out.println("(x,y) -> {" + x + "," + y + "}");
                        g.drawImage(subs, x, y, dim.width, dim.height, null);
                    }                    
                }
            }
        }
        
        private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

            int original_width = imgSize.width;
            int original_height = imgSize.height;
            int bound_width = boundary.width;
            int bound_height = boundary.height;
            int new_width = original_width;
            int new_height = original_height;

            // first check if we need to scale width
            if (original_width > bound_width) {
                //scale width to fit
                new_width = bound_width;
                //scale height to maintain aspect ratio
                new_height = (new_width * original_height) / original_width;
            }

            // then check if we need to scale even with the new height
            if (new_height > bound_height) {
                //scale height to fit instead
                new_height = bound_height;
                //scale width to maintain aspect ratio
                new_width = (new_height * original_width) / original_height;
            }

            return new Dimension(new_width, new_height);
        }
        
    }
    
    // </editor-fold>
    
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
    
    protected void fireVideo(BufferedImage image) {
        for(Object o : getListeners()){
            if(o instanceof VideoListener){
                VideoListener listen = (VideoListener)o;
                listen.getImage(image);
                break;
            }
        }
    }
    
    protected void fireTime(long ms) {
        for(Object o : getListeners()){
            if(o instanceof VideoListener){
                VideoListener listen = (VideoListener)o;
                listen.getTime(ms);
                break;
            }
        }
    }
    
    protected void fireFrameNumber(int frame) {
        for(Object o : getListeners()){
            if(o instanceof VideoListener){
                VideoListener listen = (VideoListener)o;
                listen.getFrameNumber(frame);
                break;
            }
        }
    }
    
    // </editor-fold>
}
