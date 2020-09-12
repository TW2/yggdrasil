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
package org.wingate.ygg.util.audio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.AdjustmentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.EventListenerList;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;

/**
 *
 * @author util2
 */
public class AudioWave extends JPanel implements Runnable {
    
    // Informations
    FFStuffs ffss = null;
    
    // Composants
    AudioWavePanel wp = null;
    AudioWaveScale aws = null;    
    JScrollBar horizontalWaveBar = new JScrollBar(JScrollBar.HORIZONTAL);
    
    // Hauteur du composant de la courbe
    int waveHeight = 100;

    private AudioWave() {
        
    }
    
    public static AudioWave create(File file, FFStuffs ffss, boolean dark){
        // On crée un nouveau objet principal
        AudioWave aw = new AudioWave();
        
        // On définit le fichier pour l'affichage de la forme d'onde
        aw.setVideoFilePath(file);
        
        // On renseigne (on obtient les infos)
        if(ffss == null){
            aw.ffss = FFStuffs.create(file);            
        }else{
            aw.ffss = ffss;
        }
        
        // On lance les dépendances
        aw.aws = new AudioWaveScale();
        aw.wp = new AudioWavePanel(aw.ffss, aw.aws, aw, dark);
        
        // On lance l'initialisation
        aw.init();
        
        return aw;
    }
    
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
    final String ffmpegPath = getClass().getResource("/configuration/ffmpeg.exe").getPath();
    final String ffprobePath = getClass().getResource("/configuration/ffprobe.exe").getPath();
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
    
    private void init(){
        setLayout(new BorderLayout());
        
        add(wp, BorderLayout.CENTER);
        add(horizontalWaveBar, BorderLayout.SOUTH);
        
        horizontalWaveBar.addAdjustmentListener((AdjustmentEvent e) -> {
            // e.getValue() > frames
            // on raisonne en samples
            int samples = Math.round(getSamplesFromFrame(e.getValue()) / samplesPerPixel);
            updateDisplayWithOffset(-samples, timeStart, timeEnd);
        });
        
        try { 
            clipPlaying = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(AudioWave.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        addAudioListener(new AudioListener() {
            @Override
            public void endOfSoundReached() {
                try{
                    stop();
                }catch(Exception ex){
                    System.out.println("End of sound error!");
                }
            }

            @Override
            public void endOfAreaReached() {
                try{
                    stop();
                }catch(Exception ex){
                    System.out.println("End of area error!");
                }
            }

            @Override
            public void displayProgress(AudioEvent event) {
                try{
                    msCurrent = event.getCurrentPosition();
                    wp.updateView();
                }catch(Exception ex){
                    System.out.println("Progress error!");
                }
            }
        });
        
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        while(true){
            if(clipPlaying.isRunning() == true && clipPlaying.getMicrosecondPosition() >= msAreaStop * 1000 && msAreaStop != 0L){
                fireEndOfAreaReached();
            }
            if(clipPlaying.isRunning() == true){
                Time progression = Time.create(Math.round(clipPlaying.getFramePosition()/ffss.getSampleRate()));
                fireDisplayProgress(new AudioEvent(progression));
            }
            if(clipPlaying.isRunning() == true && clipPlaying.getFramePosition() >= clipPlaying.getFrameLength()){
                fireEndOfSoundReached();
            }
        }
    }
    
    public void enableSound(boolean value){
        enableSound = value;
    }
    
    public void play(Time msStart, Time msStop){
        msAreaStart = Time.toMillisecondsTime(msStart);
        msAreaStop = Time.toMillisecondsTime(msStop);
        if(clipPlaying.isOpen()){
            clipPlaying.stop();
            clipPlaying.close();
        }
        if(generatedWAV != null){
            resetAudioStream();
            clipPlaying.setMicrosecondPosition(msAreaStart * 1000);
            clipPlaying.start();
        }
    }
    
    public void stop(){
        if(generatedWAV != null){
            // Pense-bête -> generatedWAV est définit dans doWaveForm()
            clipPlaying.stop();
            clipPlaying.close();
        }
    }
    
    // Method to reset audio stream 
    private void resetAudioStream(){ 
        try {
            audioInputStreamPlaying = AudioSystem.getAudioInputStream(generatedWAV);
            clipPlaying.open(audioInputStreamPlaying);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
            Logger.getLogger(AudioWave.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public void updateDisplayWithOffset(int offset, Time from, Time to){
        if(hasFilePath == true){
            this.offset = offset;
            
            timeStart = oldTimeStart != from ? from : oldTimeStart;
            timeEnd = oldTimeEnd != to ? to : oldTimeEnd;
            
            if(oldTimeStart != from | oldTimeEnd != to){
                oldTimeStart = timeStart;
                oldTimeEnd = timeEnd;
                
                doWaveForm();
            }
            
            horizontalWaveBar.setMaximum(ffss.getFrameCount());
            
            if(generatedWAV != null && generatedWAV != oldSelectedWAV){
                oldSelectedWAV = generatedWAV;
            }            
            
            wp.updateView();
        }
    }
    
    public Time getTimeOfStartArea() {
        if(wp.startArea != null){
            // On raisonne en samples et on veut des millisecondes
            int frames = getFrameFromSamples(Math.round(wp.startArea.x * samplesPerPixel));
            return Time.getTimeFromFrame(frames, ffss.getFps());
            //return Time.fromMillisecondsTime(Math.round(wp.startArea.x / aws.pixels * aws.milliseconds));
        }
        return Time.create(0L);
    }

    public void setTimeOfStartArea(Time startAreaTime) {
        // On a des millisecondes on veut des samples
        int frames = Time.getFrame(startAreaTime, ffss.getFps());
        int x = Math.round(getSamplesFromFrame(frames) / samplesPerPixel);
//        int x = (int)(Time.toMillisecondsTime(startAreaTime) / aws.milliseconds * aws.pixels);
        wp.startArea = new Point(x, 0);
        wp.updateView();
    }

    public Time getTimeOfStopArea() {
        if(wp.stopArea != null){
            // On raisonne en samples et on veut des millisecondes
            int frames = getFrameFromSamples(Math.round(wp.stopArea.x * samplesPerPixel));
            return Time.getTimeFromFrame(frames, ffss.getFps());
//            return Time.fromMillisecondsTime(Math.round(wp.stopArea.x / aws.pixels * aws.milliseconds));
        }
        return Time.create(0L);
    }

    public void setTimeOfStopArea(Time stopAreaTime) {
        // On a des millisecondes on veut des samples
        int frames = Time.getFrame(stopAreaTime, ffss.getFps());
        int x = Math.round(getSamplesFromFrame(frames) / samplesPerPixel);
//        int x = (int)(Time.toMillisecondsTime(stopAreaTime) / aws.milliseconds * aws.pixels);
        wp.stopArea = new Point(x, 0);
        wp.updateView();
    }
    
    public Time getCurrentKaraokeStartTime(){
//        // On obtient la syllabe en cours
//        int pixels = wp.karaokeOverlay.getStartPointAt(wp.karaokeOverlay.getSyllableIndex()).x;
//        // On raisonne en samples et on veut des millisecondes
//        int frames = getFrameFromSamples(Math.round(pixels * samplesPerPixel));
//        return Time.getTimeFromFrame(frames, ffss.getFps());
        return Time.create(0L);
    }
    
    public Time getCurrentKaraokeStopTime(){
//        // On obtient la syllabe en cours
//        int pixels = wp.karaokeOverlay.getEndPointAt(wp.karaokeOverlay.getSyllableIndex()).x;
//        // On raisonne en samples et on veut des millisecondes
//        int frames = getFrameFromSamples(Math.round(pixels * samplesPerPixel));
//        return Time.getTimeFromFrame(frames, ffss.getFps());
        return Time.create(0L);
    }
    
    public int getSamplesFromFrame(int frame){
        // On a des frames, on veut des samples
        // 1. Avoir des milliseconds
        Time ref = Time.getTimeFromFrame(frame, ffss.getFps());
        // 2. Avoir des samples
        return (int)Math.round(Time.toMillisecondsTime(ref) * ffss.getSampleRate());
    }
    
    public int getFrameFromSamples(int samples){
        // On fait l'inverse de 'getSamplesFromFrame(frame)'
        long ref = Math.round(samples / ffss.getSampleRate());
        return Time.getFrame(Time.create(ref), ffss.getFps());
    }
    
    public AudioWavePanel getAudioWavePanel(){
        return wp;
    }

    public int[] getStartAreaKaraPixels() {
        return startAreaKaraPixels;
    }

    public void setStartAreaKaraPixels(int[] startAreaKaraPixels) {
        this.startAreaKaraPixels = startAreaKaraPixels;
    }

    public int[] getStopAreaKaraPixels() {
        return stopAreaKaraPixels;
    }

    public void setStopAreaKaraPixels(int[] stopAreaKaraPixels) {
        this.stopAreaKaraPixels = stopAreaKaraPixels;
    }

    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Waveform">
    
    private void doWaveForm(){        
        
        int w;
        // w > largeur totale
        // on veut des samples on a des milliseconds
        Time substractWhole = Time.substract(Time.create(0L), ffss.getDuration());
        Time substractArea = Time.substract(timeStart, timeEnd);
        
        if(Time.toMillisecondsTime(substractArea) != 0){
            w = Math.round(getSamplesFromFrame(Time.getFrame(substractArea, ffss.getFps())) / samplesPerPixel);
        }else{
            w = Math.round(getSamplesFromFrame(Time.getFrame(substractWhole, ffss.getFps())) / samplesPerPixel);
        }
        
        if(w == 0){
            w = 20000;
        }
        
        String file = videoFilePath.getPath();
        Process p = null; ProcessBuilder pb;
        String line;
        AudioFormat format;
        long msStart = Time.toMillisecondsTime(timeStart);
        long msStop = Time.toMillisecondsTime(timeEnd);
        
        // Conversion de la partie audio
        File temp = new File(file.substring(0, file.lastIndexOf(".")) + "-converted.wav");
//        if(temp.exists()) temp.delete();
        
        if(temp.exists() == false){
            try{
                pb = new ProcessBuilder(ffmpegPath, "-i", file, "-vn", "-sample_fmt", "s16", temp.getPath()); 
                pb.redirectErrorStream(true);
                p = pb.start();
            }catch(IOException ex){
                try {
                    pb = new ProcessBuilder(getApplicationDirectory() + "\\configuration\\ffmpeg.exe", "-i", file, "-vn", "-sample_fmt", "s16", temp.getPath());
                    pb.redirectErrorStream(true);
                    p = pb.start();
                } catch (IOException ex1) {
                    Logger.getLogger(AudioWave.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        
        if(p != null){
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))){
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }            
            } catch (IOException ex) {
                Logger.getLogger(AudioWave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }            
        
        if(p != null){
            p.destroy();
        }        
        
        
        // On souhaite maintenant lire les bytes afin de créer la courbe
        //----------------------------------------------------------------------
        File newFile = new File(file.substring(0, file.lastIndexOf(".")) + "-piece.wav");
//        if(newFile.exists()) newFile.delete();
        
        if(newFile.exists() == false){
            try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(temp)){
            
                if(msStart != msStop){
                    int fromSecond = (int)Math.round((double)msStart / 1000d);
                    int lengthSeconds = (int)Math.round((double)(msStop - msStart) / 1000d);
                    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(temp);
                    format = fileFormat.getFormat();

                    int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
                    originalStream.skip(fromSecond * bytesPerSecond);

                    long framesOfAudioToCopy = lengthSeconds * (int)format.getFrameRate();

                    try (AudioInputStream finalStream = new AudioInputStream(originalStream, format, framesOfAudioToCopy)) {                
                        AudioSystem.write(finalStream, fileFormat.getType(), newFile);
                    }
                }else{
                    newFile = temp;
                }

                generatedWAV = newFile;

                //=============================
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(newFile);

                //=============================
                format = audioInputStream.getFormat();

                byte[] audioBytes = new byte[(int)(audioInputStream.getFrameLength() * format.getFrameSize())];

                audioInputStream.read(audioBytes);

                if(audioBytes != null){

                    int[] audioData = null;

                    if (format.getSampleSizeInBits() == 16) {
                        int nlengthInSamples = audioBytes.length / 2;
                        audioData = new int[nlengthInSamples];
                        if (format.isBigEndian()) {
                            for (int i = 0; i < nlengthInSamples; i++) {
                                /* First byte is MSB (high order) */
                                int MSB = (int) audioBytes[2*i];
                                /* Second byte is LSB (low order) */
                                int LSB = (int) audioBytes[2*i+1];
                                audioData[i] = MSB << 8 | (255 & LSB);
                            }
                        }else{
                            for (int i = 0; i < nlengthInSamples; i++) {
                                /* First byte is LSB (low order) */
                                int LSB = (int) audioBytes[2*i];
                                /* Second byte is MSB (high order) */
                                int MSB = (int) audioBytes[2*i+1];
                                audioData[i] = MSB << 8 | (255 & LSB);
                            }
                        }
                    } else if (format.getSampleSizeInBits() == 8) {
                        int nlengthInSamples = audioBytes.length;
                        audioData = new int[nlengthInSamples];
                        if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                            for (int i = 0; i < audioBytes.length; i++) {
                                audioData[i] = audioBytes[i];
                            }
                        } else {
                            for (int i = 0; i < audioBytes.length; i++) {
                                audioData[i] = audioBytes[i] - 128;
                            }
                        }
                    }

                    int frames_per_pixel = audioBytes.length / format.getFrameSize()/w;
                    byte my_byte;
                    int numChannels = format.getChannels();

                    List<AudioWaveValues> values = new ArrayList<>();

                    for (double x = 1; x < w && audioData != null; x++) {
                        int idx = (int) (frames_per_pixel * numChannels * x);
                        if (format.getSampleSizeInBits() == 8) {
                             my_byte = (byte) audioData[idx];
                        } else {
                             my_byte = (byte) (128 * audioData[idx] / 32768 );
                        }
                        double y_new = (double) (waveHeight * (128 - my_byte) / 256);

                        values.add(AudioWaveValues.create(x, y_new));
                    }

                    // On cherche les valeurs hautes et basses
                    double ceiling = values.get(0).getY();
                    double floor = values.get(0).getY();
                    for(AudioWaveValues val : values){
                        ceiling = Math.max(val.getY(), ceiling);
                        floor = Math.min(val.getY(), floor);
                    }

                    // On prépare une écriture de fichier
                    AudioWaveStorage store = new AudioWaveStorage();
                    store.startSave(videoFilePath);

                    // On détermine les valeurs à entrer avec un ratio
                    double amplitude = ceiling - floor;
                    double max = waveHeight;
                    for(AudioWaveValues val : values){
                        double x = val.getX();
                        double y_calc = val.getY() - floor;
                        double y = max * y_calc / amplitude;
    //                    gPathCurve.lineTo(x, y);
                        store.saveOneValue((int)x, (int)y);
                    }

                    // On ferme le flux du fichier
                    store.stopSave();
                }
            } catch (UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(AudioWave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Evenements">
    
    //==========================================================================
    // Events
    //==========================================================================

    private final EventListenerList listeners = new EventListenerList();

    public void addAudioListener(IAudioListener listener) {
        listeners.add(AudioListener.class, (AudioListener)listener);
    }

    public void removeAudioListener(IAudioListener listener) {
        listeners.remove(AudioListener.class, (AudioListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireDisplayProgress(AudioEvent event) {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.displayProgress(event);
                break;
            }
        }
    }    

    protected void fireEndOfSoundReached() {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.endOfSoundReached();
                break;
            }
        }
    }    

    protected void fireEndOfAreaReached() {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.endOfAreaReached();
                break;
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Répertoire du programme">
    
    private String getApplicationDirectory(){
        if(System.getProperty("os.name").equalsIgnoreCase("Mac OS X")){
            java.io.File f = new java.io.File("");
            return f.getAbsolutePath();
        }
        String path = System.getProperty("user.dir");
        if(path.toLowerCase().contains("jre")){
            File f = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toString()
                    .substring(6));
            path = f.getParent();
        }
        return path;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accesseurs">
    
    public File getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(File videoFilePath) {
        this.videoFilePath = videoFilePath;
        if(videoFilePath == null){
            hasFilePath = false;
        }else hasFilePath = videoFilePath.getPath().isEmpty() != true;
        
//        if(hasFilePath == true){
//            getInfo();
//        }
    }

    public Color getBackColor() {
        return backColor;
    }

    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    public Color getCenterColor() {
        return centerColor;
    }

    public void setCenterColor(Color centerColor) {
        this.centerColor = centerColor;
    }

    public Color getCurveColor() {
        return curveColor;
    }

    public void setCurveColor(Color curveColor) {
        this.curveColor = curveColor;
    }

    public Color getvSecondsColor() {
        return vSecondsColor;
    }

    public void setvSecondsColor(Color vSecondsColor) {
        this.vSecondsColor = vSecondsColor;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public float getSamplesPerPixel() {
        return samplesPerPixel;
    }

    public void setSamplesPerPixel(float samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public Color getKeyFrameColor() {
        return keyFrameColor;
    }

    public void setKeyFrameColor(Color keyFrameColor) {
        this.keyFrameColor = keyFrameColor;
    }
    
    
    //--------------------------------------------------------------------------
    // Accesseurs de classes fille
    //--------------------------------------------------------------------------
    
    public void setAudioPanelSize(int width, int height){
        setSize(width, height);
    }
    
    public void setRatioPixelsMilliseconds(double pixels, double ms){
        aws.setPixels(pixels);
        aws.setMilliseconds(ms);
        repaint();
    }
    
//    public void setKaraoke(SyllableCollection col){
//        wp.karaokeOverlay = col;
//        wp.updateView();
//    }
//    
//    public void setKaraoke(Event event, KaraokeLanguage language){
////        wp.karaokeOverlay = SyllableCollection.create(event, language);
////        wp.updateView();
//    }
    
    public void setActiveSyllableIndex(int index){
//        wp.karaokeOverlay.setSyllableIndex(index);
//        wp.updateView();
    }
    
    public int getActiveSyllableIndex(){
//        return wp.karaokeOverlay.getSyllableIndex();
        return 0;
    }
    
    // </editor-fold>

    public JScrollBar getHorizontalWaveBar() {
        return horizontalWaveBar;
    }

    public void setHorizontalWaveBar(JScrollBar horizontalWaveBar) {
        this.horizontalWaveBar = horizontalWaveBar;
    }

    public Shape getWholeCurveShape() {
        return wholeCurveShape;
    }

    public void setWholeCurveShape(Shape wholeCurveShape) {
        this.wholeCurveShape = wholeCurveShape;
    }

    public Shape getCenterShape() {
        return centerShape;
    }

    public void setCenterShape(Shape centerShape) {
        this.centerShape = centerShape;
    }

    public Time getOldTimeStart() {
        return oldTimeStart;
    }

    public void setOldTimeStart(Time oldTimeStart) {
        this.oldTimeStart = oldTimeStart;
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

    public Time getOldMsCurrent() {
        return oldMsCurrent;
    }

    public void setOldMsCurrent(Time oldMsCurrent) {
        this.oldMsCurrent = oldMsCurrent;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
