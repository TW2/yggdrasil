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
package yggdrasil.videoplayer;

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
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;

/**
 *
 * @author util2
 */
public class AudioPlayer implements Runnable {
    
    private File audioVideoFile = null;
    private volatile Thread audioVideoThread = null;
    private volatile boolean running = false;
    
    private volatile Time start = Time.create(0L);
    private volatile Time stop = Time.create(0L);
    
    private volatile long pause, offset, limit, microStart, microElapsed;    
    private volatile boolean paused = false;

    public AudioPlayer() {
        init();        
    }
    
    private void init(){
        audioVideoThread = new Thread(this);
        audioVideoThread.start();
    }

    @Override
    public void run() {
        while(true){
            if(running == true){
                process();
            }
        }
    }
    
    private void process(){
        boolean useStart = !Time.isEqual(Time.create(0L), start);
        boolean useStop = !Time.isEqual(Time.create(0L), stop);
        if(audioVideoFile != null){
            try {
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(audioVideoFile);
                grabber.start();
                
                if(useStart == true){
                    grabber.setTimestamp(Time.toMillisecondsTime(start), true);
                }                
                
                AudioFormat audioFormat = new AudioFormat(
                        grabber.getSampleRate(), 
                        16, 
                        grabber.getAudioChannels(), 
                        true, 
                        true);
                
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                
                while (!Thread.interrupted() && paused == false) {                    
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        break;
                    }
                    if (frame.samples != null) {
                        ShortBuffer channelSamplesFloatBuffer = (ShortBuffer) frame.samples[0];
                        channelSamplesFloatBuffer.rewind();

                        ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesFloatBuffer.capacity() * 2);

                        for (int i = 0; i < channelSamplesFloatBuffer.capacity(); i++) {
                            short val = channelSamplesFloatBuffer.get(i);
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
                        } catch (InterruptedException ex) {
                            audioVideoThread.interrupt();
                        } catch (ExecutionException ex) {
                            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
                soundLine.stop();
                grabber.stop();
                grabber.release();
            } catch (FrameGrabber.Exception | LineUnavailableException | InterruptedException ex) {
                Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void setPauseTime(){
        if(paused == true){
            pause = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
        }
    }
    
    public void audioPlay(){
        running = true;
        paused = false;
    }
    
    public void audioPause(){
        paused = !paused;
        setPauseTime();
    }
    
    public void audioStop(){
        running = false;
        paused = true;
        setPauseTime();
    }
    
    public File getAudioVideoFile() {
        return audioVideoFile;
    }

    public void setAudioVideoFile(File audioVideoFile) {
        this.audioVideoFile = audioVideoFile;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getStop() {
        return stop;
    }

    public void setStop(Time stop) {
        this.stop = stop;
    }
}
