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
import org.bytedeco.javacv.FrameGrabber;

/**
 *
 * @author util2
 */
public class PlayAudio implements Runnable {
    
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
    
    // <editor-fold defaultstate="collapsed" desc="Lecture audio">
    
    private volatile FFmpegFrameGrabber grabber = null;
    private volatile SourceDataLine soundLine = null;
    private volatile ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile Action action = Action.None;

    private Thread thAct = null;

    private boolean area = false;
    
    private long msAreaStart = 0L, msAreaStop = 5000L;

    public PlayAudio() {
        init();
    }

    private void init(){
        startThread();
    }

    public void setAudio(File audio) throws FrameGrabber.Exception, LineUnavailableException{
        // Vérification de la présence de fichier existant
        if(audio.exists() == false){
            action = Action.None;
            return;
        }

        // Clôture de la ligne du son si elle existe (lors d'un précédent passage ici)
        if(soundLine != null){
            soundLine.stop();
            soundLine.flush();
            soundLine.drain();
            soundLine.close();
            soundLine = null;
        }

        // Clôture du composant FFMpeg s'il existe (lors d'un précédent passage ici)
        if(grabber != null){
            grabber.stop();
            grabber.release();
            grabber = null;
        }

        // Lancement du composant FFMpeg (nouveau)
        grabber = new FFmpegFrameGrabber(audio);
        grabber.start();

        // Définition du format audio pour lire le son
        AudioFormat audioFormat = new AudioFormat(
                grabber.getSampleRate(), 
                16, 
                grabber.getAudioChannels(), 
                true, 
                true
        );

        // Ouverture d'un processus audio pour le son
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        soundLine = (SourceDataLine) AudioSystem.getLine(info);
        soundLine.open(audioFormat);
        soundLine.start();

        // Renseignement de l'état de l'action (état de lecture)
        action = Action.Ready;
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

    private void endOfFile(){
        try {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(PlayAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while(true){
            if(action == Action.Play){
                try {
                    // Si pour le média on demande un début différent de zéro
                    if(msAreaStart != 0L && area == true){
                        grabber.setAudioTimestamp(msAreaStart * 1000L);
                        area = false;
                    }

                    // On obtient la frame en cours
                    org.bytedeco.javacv.Frame frame = grabber.grab();

                    // Si le média n'a plus de frame lisible (EOF)
                    if (frame == null) {
                        action = Action.None;
                        continue;
                    }
                    
                    // Si le média a dépassé la limite spécifiée
                    if(msAreaStop != 0L && frame.timestamp / 1000L >= msAreaStop){
                        action = Action.Ready;
                        msAreaStart = 0L;
                        msAreaStop = 0L;
                        continue;
                    }

                    // Si le média a un paquet audio
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
                    Logger.getLogger(PlayAudio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void playStopAudio(){
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
    
    // </editor-fold>
    
}
