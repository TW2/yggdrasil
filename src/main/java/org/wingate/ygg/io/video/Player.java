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
package org.wingate.ygg.io.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
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
public class Player {
    
    private enum Controller{
        Stop, Play;
    }
    
    private volatile Controller ctrl = Controller.Stop;
    private volatile Time start = Time.create(0L);
    private volatile Time stop = Time.create(0L);

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
    
    private volatile Thread playThread;
    private volatile File video = null;
    
    public Player(File video) {
        this.video = video;
    }
    
    public void playMedia(Time start, Time end){
        ctrl = Controller.Play;
        try {
            start();
        } catch (Exception ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void playMedia(){
        playMedia(Time.create(0L), Time.create(0L));
    }
    
    public void pauseMedia(){
        switch(ctrl){
            case Play -> { ctrl = Controller.Stop; }
            case Stop -> { ctrl = Controller.Play; }
        }
    }
    
    public void stopMedia(){
        ctrl = Controller.Stop;
        try {
            stop();
        } catch (Exception ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void start() throws Exception {
        playThread = new Thread(() -> {
            try {
                final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video);
                grabber.start();
                
                final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);
                
                final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();
                
                final Java2DFrameConverter converter = new Java2DFrameConverter();
                
                ExecutorService executor = Executors.newSingleThreadExecutor();
                
                while (!Thread.interrupted()) {
                    if(ctrl == Controller.Play){
                        Frame frame = grabber.grab();
                    
                        if (frame == null) {
                            break;
                        }

                        // On met à jour le temps de la vidéo
                        double milliseconds = frame.timestamp / 1000d;

                        // On met à jour le numéro d'image
                        int frameNumber = grabber.getFrameNumber();


                        if (frame.image != null) {
                            // Video >> frame.image != null
                            // On transmet les éléments à l'event
                            VideoEvent event = new VideoEvent(
                                    converter.convert(frame), milliseconds, frameNumber);
                            fireVideo(event);
                        } else if (frame.samples != null) {
                            // Audio >> frame.samples != null
                            final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                            channelSamplesShortBuffer.rewind();

                            final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                            for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                                short val = channelSamplesShortBuffer.get(i);
                                outBuffer.putShort(val);
                            }

                            /**
                             * We need this because soundLine.write ignores
                             * interruptions during writing.
                             */
                            try {
                                executor.submit(() -> {
                                    soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                    outBuffer.clear();
                                }).get();
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
                soundLine.stop();
                grabber.stop();
                grabber.release();
            } catch (ExecutionException | LineUnavailableException | FrameGrabber.Exception | InterruptedException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        playThread.start();
    }

    private void stop() throws Exception {
        playThread.interrupt();
    }
}
