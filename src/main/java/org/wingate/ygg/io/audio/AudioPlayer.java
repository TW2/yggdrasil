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
package org.wingate.ygg.io.audio;

import java.awt.Color;
import java.awt.Shape;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;

/**
 *
 * @author util2
 */
public class AudioPlayer extends JPanel implements Runnable {

    // <editor-fold defaultstate="collapsed" desc="Variables">
    
    public enum Action {
        None, Ready, Play, Pause, Stop;
    }
    
    private volatile FFmpegFrameGrabber grabber = null;
    private volatile SourceDataLine soundLine = null;
    private volatile ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private volatile Action action = Action.None;
    
    private Thread thAct = null;
    
    private final int waveHeight = 100;
    private Map<Double, Double> xy = new HashMap<>();
    
    // Informations
    FFStuffs ffss = null;
    
    // Pour info :
    // https://www.liveabout.com/convert-milliseconds-to-samples-1817867
    // ms x sample rate = samples
    // samples / sample rate = ms
    
    //==========================================================================
    //--------------------------------------------------------------------------
    File videoFilePath;
    Color backColor = DrawColor.white_smoke.getColor();
    Color centerColor = DrawColor.corn_flower_blue.getColor();
    Color curveColor = Color.blue.brighter();
    Color vSecondsColor = Color.red;
    Color keyFrameColor = Color.black;
    Shape wholeCurveShape = null;
    Shape centerShape = null;
    //--------------------------------------------------------------------------
    
    //==========================================================================
    //--------------------------------------------------------------------------
    final String ffmpegPath = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
    final String ffprobePath = Loader.load(org.bytedeco.ffmpeg.ffprobe.class);
    //--------------------------------------------------------------------------
    
    //==========================================================================
    //--------------------------------------------------------------------------
    boolean hasFilePath = false;
    Thread thread = null;
    volatile boolean playing = false, enableSound = false;
    Time timeStart = new Time(), timeEnd = new Time();
    Time oldTimeStart = new Time(), oldTimeEnd = new Time();
    // For play area
    long msAreaStart = 0L, msAreaStop = 0L;
    Time msCurrent = Time.create(0L), oldMsCurrent = Time.create(0L);
    // For the movement of waveform
    int offset = 0;
    // To play sound
    File generatedWAV = null, oldSelectedWAV = null;
    float samplesPerPixel = 1000f;
    List<Integer> keyframes = new ArrayList<>();
    //--------------------------------------------------------------------------
    
    //==========================================================================
    //--------------------------------------------------------------------------
    Clip clipPlaying = null; 
    AudioInputStream audioInputStreamPlaying = null;
    //--------------------------------------------------------------------------
    
    //==========================================================================
    //--------------------------------------------------------------------------
    int[] startAreaKaraPixels = null, stopAreaKaraPixels = null;
    //--------------------------------------------------------------------------
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Evénements">
    
    private final EventListenerList listeners = new EventListenerList();
    
    public void addVideoListener(IAudioListener listener) {
        listeners.add(AudioListener.class, (AudioListener)listener);
    }

    public void removeVideoListener(IAudioListener listener) {
        listeners.remove(AudioListener.class, (AudioListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireProgress(AudioEvent event) {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.displayProgress(event);
                break;
            }
        }
    }
    
    // </editor-fold>
    
    public AudioPlayer() {
        init();
    }
    
    private void init(){
        startThread();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Lancement et arrêt de tâche">
    
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Play Pause Stop">
    
    public void playAudio(){        
        switch(action){
            case Ready -> action = Action.Play;
            case Pause -> action = Action.Play;
            case Stop -> action = Action.Play;
        }
    }
    
    public void pauseAudio(){
        switch(action){
            case Play -> action = Action.Pause;
            case Pause -> action = Action.Play;
            case Stop -> action = Action.Play;
        }
    }
    
    public void stopAudio(){
        switch(action){
            case Play -> action = Action.Stop;
            case Pause -> action = Action.Stop;
            case Ready -> action = Action.Stop;
        }
    }
    
    // </editor-fold>
    
    private void endOfFile(){
        try {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setFile(File video) throws FrameGrabber.Exception, LineUnavailableException {
        if(video.exists() == false){
            action = Action.None;
            return;
        }        
        
        if(soundLine != null){
            soundLine.stop();
            soundLine.flush();
            soundLine.drain();
            soundLine.close();
            soundLine = null;
        }        
        if(grabber != null){
            grabber.stop();
            grabber.release();
        }
        grabber = new FFmpegFrameGrabber(video);
        grabber.start();
        
        AudioFormat audioFormat = new AudioFormat(
                grabber.getSampleRate(), 
                16, 
                grabber.getAudioChannels(), 
                true, 
                true
        );
        
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        soundLine = (SourceDataLine) AudioSystem.getLine(info);
        soundLine.open(audioFormat);
        soundLine.start();
        
        action = Action.Ready;
    }
    
    @Override
    public void run() {
        while(true){
            if(action == Action.Play){
                try {
                    Frame frame = grabber.grab();
                    
                    if (frame == null) {
                        action = Action.None;
                        continue;
                    }
                    
                    if (frame.samples != null) {
                        ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                        channelSamplesShortBuffer.rewind();

                        ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

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
                        } catch (InterruptedException | ExecutionException ex) {
                            thAct.interrupt();
                            stopThread();
                            action = Action.None;
                            endOfFile();
                        }
                    }
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public int getSamplesFromFrame(int frame){
        // On a des frames, on veut des samples
        // 1. Avoir des milliseconds
        Time ref = Time.getTimeFromFrame(frame, ffss.getFps());
        // 2. Avoir des samples
        return (int)Math.round(Time.toMillisecondsTime(ref) * ffss.getSampleRate());
    }
    
    
}
