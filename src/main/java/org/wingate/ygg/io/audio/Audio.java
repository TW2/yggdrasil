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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.EventListenerList;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;

/**
 *
 * @author util2
 */
public class Audio extends JPanel implements Runnable {

    // <editor-fold defaultstate="collapsed" desc="CLASSE >> Couleurs de la forme d'onde">
    
    public enum AudioWaveColors{
        Background("Background", DrawColor.white_smoke.getColor(), new Color(71, 75, 76)),
        WaveFormCenter("Center of waveform", DrawColor.corn_flower_blue.getColor(), DrawColor.corn_flower_blue.getColor()),
        WaveForm("Main waveform", Color.blue.brighter(), Color.blue.brighter()),
        Seconds("Seconds", Color.red, Color.red),
        KeyFrame("Key frames", Color.black, Color.white),
        StartArea("Start", DrawColor.dark_green.getColor(), DrawColor.lime.getColor()),
        StopArea("Stop", DrawColor.dark_red.getColor(), DrawColor.orange_red.getColor()),
        Area("Area", DrawColor.green_yellow.getColor(0.5f), DrawColor.green_yellow.getColor(0.5f)),
        Progress("Progress", DrawColor.violet.getColor(), DrawColor.violet.getColor()),
        Pointer("Pointer", Color.pink, Color.pink);
        
        
        String name;
        Color light;
        Color dark;
        
        private AudioWaveColors(String name, Color light, Color dark){
            this.name = name;
            this.light = light;
            this.dark = dark;
        }

        public String getName() {
            return name;
        }

        public Color getColor(boolean isDark) {
            return isDark == false ? light : dark;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="CLASSE >> Registre de la forme d'onde">
    
    class AudioWaveStorage {
        
        private FileReader fileReader;
        private BufferedReader bufferedReader;

        private PrintWriter printWriter;

        public AudioWaveStorage() {
        }

        //=====
        // SAVE
        //-----

        public void startSave(File videoFile) throws IOException{        
            File temp = new File(videoFile.getPath().substring(0, videoFile.getPath().lastIndexOf(".")) + ".wst");
            if(temp.exists() == true) { temp.delete(); }
            printWriter = new PrintWriter(temp);
        }

        public void saveOneValue(int x, int y){
            printWriter.println(x + "," + y);
        }

        public void stopSave(){
            printWriter.flush();
            printWriter.close();
        }

        //=====
        // LOAD
        //-----

        public void startConsult(File videoFile) throws IOException{
            File temp = new File(videoFile.getPath().substring(0, videoFile.getPath().lastIndexOf(".")) + ".wst");
            fileReader = new FileReader(temp);
            bufferedReader = new BufferedReader(fileReader);
        }

        public int consultOneValue(int x) throws IOException{
            String line; int y = 0;
            while((line = bufferedReader.readLine()) != null){
                if(line.contains(Integer.toString(x)) == true){
                    y = Integer.parseInt(line.substring(line.lastIndexOf(",") + 1));
                    break;
                }
            }
            return y;
        }

        public Map<Integer, Integer> consultMultiValues(int xFROM, int xTO) throws IOException{
            String line; Map<Integer, Integer> ys = new HashMap<>(); boolean copy = false;
            int count = 0;
            while((line = bufferedReader.readLine()) != null){            
                if(line.contains(Integer.toString(xFROM)) == true){
                    ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
                    copy = true;
                }else if(line.contains(Integer.toString(xTO)) == true){
                    ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
                    break;
                }else if(copy == true){
                    ys.put(count, Integer.parseInt(line.substring(line.lastIndexOf(",") + 1)));
                }
                count++;
            }
            return ys;
        }

        public void stopConsult() throws IOException{
            bufferedReader.close();
            fileReader.close();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="CLASSE >> Panneau de la forme d'onde">
    
    class WaveFormPanel extends JPanel {

        public WaveFormPanel() {
            init();
        }
        
        public void refresh(){
            repaint();
        }
        
        private void init(){
            setDoubleBuffered(true);
            
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    repaint();
                }            
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    currentMouseMotion = new Point(e.getX() - offset, e.getY());
                    repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1){
                        startArea = new Point(e.getX() - offset, e.getY());
                    }else if(e.getButton() == MouseEvent.BUTTON3){
                        stopArea  = new Point(e.getX() - offset, e.getY());
                    }else if(e.getButton() == MouseEvent.BUTTON2 && e.isControlDown() == true){
                        startArea = null;
                        stopArea = null;
                    }
                    repaint();
                }
            });

            addMouseWheelListener((MouseWheelEvent e) -> {
                int samples;
                int value = horizontalWaveBar.getValue();
                final int min = horizontalWaveBar.getMinimum();
                final int max = horizontalWaveBar.getMaximum();
                if(e.getWheelRotation() > 0){
                    value = value - 50 < min ? min : value - 50;
                    samples = Math.round(getSamplesFromFrame(value) / samplesPerPixel);                
                }else{
                    value = value + 50 > max ? max : value + 50;
                    samples = Math.round(getSamplesFromFrame(value) / samplesPerPixel);
                }
                horizontalWaveBar.setValue(value);
                horizontalWaveBar.setMaximum(ffss.getFrameCount());
                offset = -samples;
                repaint();
//                awm.updateDisplayWithOffset(-samples, awm.timeStart, awm.timeEnd);
            });
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            Graphics2D g2d = (Graphics2D)g;

            g2d.setColor(AudioWaveColors.Background.getColor(dark));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            if(video != null && video.exists() == true){

                // On obtient une valeur corrective de la courbe
                // à partir des 3 premières valeurs (blanc anormalement non à zéro)
                int blankCorrection = 0;
                int blankCounter = 0;
                int bkv1 = -1, bkv2 = -2, bkv3 = -3;
                for(Map.Entry<Double, Double> entry  : xy.entrySet()){
                    blankCounter++;
                    if(blankCounter == 1) bkv1 = (int)Math.round(entry.getValue());
                    if(blankCounter == 2) bkv2 = (int)Math.round(entry.getValue());
                    if(blankCounter == 3) bkv3 = (int)Math.round(entry.getValue());

                    if(blankCounter == 3){
                        break;
                    }
                }
                if(bkv1 == bkv2 && bkv2 == bkv3){
                    blankCorrection = bkv1;
                }

                // On dessine la courbe (ligne du milieu)
                g2d.setColor(AudioWaveColors.WaveForm.getColor(dark));
                g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);

                Line2D l2d;
                int count = 0, times = 0;
                for(Map.Entry<Double, Double> entry  : xy.entrySet()){
                    int x = offset + (int)Math.round(entry.getKey());
                    int y = (int)Math.round(entry.getValue());
                    // z = getHeight() / 2 = blankCorrection
                    // z = y               = entry.getValue()
                    if(blankCorrection != 0){
                        y = getHeight() / 2 * (int)Math.round(entry.getValue()) / blankCorrection;
                    }

                    if(x > 0 && x <= getWidth()){
                        // On dessine la courbe (générale)
                        g2d.setColor(AudioWaveColors.WaveForm.getColor(dark));
                        l2d = new Line2D.Double(
                                x, 
                                y, 
                                x, 
                                getHeight() - y);
                        g2d.draw(l2d);

                        // On dessine la courbe (centre)
                        g2d.setColor(AudioWaveColors.WaveFormCenter.getColor(dark));
                        l2d = new Line2D.Double(
                                x,
                                y / 2 + getHeight() / 4,
                                x,
                                getHeight() - y / 2 - getHeight() / 4);
                        g2d.draw(l2d);
                    }

                    count++;
                    if(count == samplesPerPixel / 4){
                        times++;
                        // On dessine les repères
                        g2d.setColor(AudioWaveColors.Seconds.getColor(dark));
                        l2d = new Line2D.Double(
                                x, 
                                0, 
                                x, 
                                getHeight());
                        g2d.draw(l2d);
                        g2d.setColor(AudioWaveColors.Seconds.getColor(dark));
    //                        double msframe = samplesPerPixel / 4 * times / fps * 1000;
    //                        Time mark = Time.create(Math.round(msframe));
    //                        Time mark = Time.getTimeFromFrame(getFrameFromSamples(offset + Math.round(samplesPerPixel / 4 * times * (x - offset))), fps);
    //                        g2d.drawString(mark.toDisplayTime(), x + 5, 10);
    //                        g2d.drawString("frame " + Integer.toString(Time.getFrame(mark, fps)), x + 5, getHeight() - 2);
                        count = 0;
                    }
                }

                if(startArea != null){
                    // On dessine le repère de début de zone
                    g2d.setColor(AudioWaveColors.StartArea.getColor(dark));
                    l2d = new Line2D.Float(
                            offset + startArea.x, 
                            0, 
                            offset + startArea.x, 
                            getHeight());
                    g2d.draw(l2d);

    //                    double msframe = (startArea.x - 7) / fps * 1000;
    //                    Time mark = Time.create(Math.round(msframe));
                    Time mark = Time.getTimeFromFrame(getFrameFromSamples(Math.round(startArea.x * samplesPerPixel)), ffss.getFps());
                    g2d.drawString(mark.toDisplayTime(), offset + startArea.x + 5, 22);
                    g2d.drawString("frame " + Integer.toString(Time.getFrame(mark, ffss.getFps())), offset + startArea.x + 5, getHeight() - 14);
                }

                if(stopArea != null){
                    // On dessine le repère de fin de zone
                    g2d.setColor(AudioWaveColors.StopArea.getColor(dark));
                    l2d = new Line2D.Float(
                            offset + stopArea.x, 
                            0, 
                            offset + stopArea.x, 
                            getHeight());
                    g2d.draw(l2d);

    //                    double msframe = (stopArea.x - 7) / fps * 1000;
    //                    Time mark = Time.create(Math.round(msframe));
                    Time mark = Time.getTimeFromFrame(getFrameFromSamples(Math.round(stopArea.x * samplesPerPixel)), ffss.getFps());
                    g2d.drawString(mark.toDisplayTime(), offset + stopArea.x + 5, 34);
                    g2d.drawString("frame " + Integer.toString(Time.getFrame(mark, ffss.getFps())), offset + stopArea.x + 5, getHeight() - 26);
                }

                if(startArea != null && stopArea != null){
                    // On dessine la zone
                    g2d.setColor(AudioWaveColors.Area.getColor(dark));
                    Rectangle2D r2d = new Rectangle2D.Float(
                            offset + startArea.x,
                            0,
                            stopArea.x - startArea.x,
                            getHeight());
                    g2d.fill(r2d);
                }

                if(currentMouseMotion != null){
                    // On dessine le curseur
                    g2d.setColor(AudioWaveColors.Pointer.getColor(dark));
                    l2d = new Line2D.Float(
                            offset + currentMouseMotion.x, 
                            0, 
                            offset + currentMouseMotion.x, 
                            getHeight());
                    g2d.draw(l2d);
                    Time mark = Time.getTimeFromFrame(getFrameFromSamples(Math.round(currentMouseMotion.x * samplesPerPixel)), ffss.getFps());
                    g2d.drawString(mark.toDisplayTime(), offset + currentMouseMotion.x + 5, 60);
                    g2d.drawString("frame " + Integer.toString(Time.getFrame(mark, ffss.getFps())), offset + currentMouseMotion.x + 5, getHeight() - 40);
                }

                // On dessine les images clé
                AffineTransform normalTransform = g2d.getTransform();
                AffineTransform atTime = new AffineTransform(normalTransform);
                AffineTransform atFrame = new AffineTransform(normalTransform);
                for(Map.Entry<Integer, Time> entry : ffss.getIFrames().entrySet()){
                    int x = offset + Math.round(getSamplesFromFrame(Time.getFrame(entry.getValue(), ffss.getFps())) / samplesPerPixel);

                    g2d.setColor(AudioWaveColors.KeyFrame.getColor(dark));
                    l2d = new Line2D.Double(
                            x, 
                            0, 
                            x, 
                            getHeight());
                    g2d.draw(l2d);

                    atTime.rotate(Math.toRadians(-90d), x - 5, getHeight() - 10);
                    g2d.setTransform(atTime);

                    g2d.drawString(entry.getValue().toDisplayTime(), x + 2, getHeight() - 10);

                    atFrame.rotate(Math.toRadians(-90d), x - 5, getHeight() - 10);
                    g2d.setTransform(atFrame);

                    g2d.drawString("frame " + Integer.toString(Time.getFrame(entry.getValue(), ffss.getFps())), x + 2, getHeight() + 10);

                    g2d.setTransform(normalTransform);
                    atTime = new AffineTransform(normalTransform);
                    atFrame = new AffineTransform(normalTransform);
                }

                // On dessine la progression
                Stroke oldStroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(AudioWaveColors.Progress.getColor(dark));
                if(ffss.hasVideo()){
                    int progressSamples = getSamplesFromFrame(Time.getFrame(msCurrent, ffss.getFps()));
                    l2d = new Line2D.Double(
                            offset + progressSamples / samplesPerPixel, 
                            0, 
                            offset + progressSamples / samplesPerPixel, 
                            getHeight());
                    g2d.draw(l2d);
                    g2d.setStroke(oldStroke);
                }else if(ffss.hasAudio()){
                    // pixels = ms * samplerate in kHz / samplesPerPixel
                    double pixels = Time.toMillisecondsTime(msCurrent) * ffss.getSampleRate() / samplesPerPixel;
                    l2d = new Line2D.Double(
                            offset + pixels, 
                            0, 
                            offset + pixels, 
                            getHeight());
                    g2d.draw(l2d);
                    g2d.setStroke(oldStroke);
                }
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="CLASSE >> Morceau de forme d'onde">
    
    class VisibleAudioBox {
        
        private Map<Float, Float> xy = new HashMap<>();
        private float offset = 0f;
        
        private Time waveFormStart = Time.create(0L);
        private Time waveFormEnd = Time.create(0L);
        
        public void drawWaveForm(Graphics2D g2d){
            
        }
        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="Variables de fonctionnement">
    
    public enum Action {
        None, Ready, Play, Pause, Stop;
    }
    
    private volatile FFmpegFrameGrabber grabber = null;
    private volatile SourceDataLine soundLine = null;
    private volatile ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private volatile Action action = Action.None;
    
    private Thread thAct = null;
    
    private boolean area = false;
    
    // Informations
    FFStuffs ffss = null;
    
    // Pour info :
    // https://www.liveabout.com/convert-milliseconds-to-samples-1817867
    // ms x sample rate = samples
    // samples / sample rate = ms
    
    File video = null;
    
    private final boolean dark = MainFrame.isDark();
    
    WaveFormPanel waveFormPanel = new WaveFormPanel();
    JScrollBar horizontalWaveBar = new JScrollBar(JScrollBar.HORIZONTAL);
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Variables pour la forme d'onde">
    
    private int waveHeight = 100;
    private Map<Double, Double> xy = new HashMap<>();
    
    // For play area
    long msAreaStart = 0L, msAreaStop = 0L;
    Time msCurrent = Time.create(0L), oldMsCurrent = Time.create(0L);
    // For the movement of waveform
    int offset = 0;
    // To play sound
    float samplesPerPixel = 1000f;
    
    Point startArea = null;
    Point stopArea = null;
    Point currentMouseMotion = null;
    Point currentProgressOnPlay = null;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Variables de confort">
    
    Color backColor = DrawColor.white_smoke.getColor();
    Color centerColor = DrawColor.corn_flower_blue.getColor();
    Color curveColor = Color.blue.brighter();
    Color vSecondsColor = Color.red;
    Color keyFrameColor = Color.black;
    
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
    
    protected void fireStartTime(Time start) {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.startTimeChanged(start);
                break;
            }
        }
    }
    
    protected void fireEndTime(Time end) {
        for(Object o : getListeners()){
            if(o instanceof AudioListener){
                AudioListener listen = (AudioListener)o;
                listen.endTimeChanged(end);
                break;
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tâche">
    
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
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        while(true){
            if(action == Action.Play){
                try {
                    if(msAreaStart != 0L && area == true){
                        grabber.setAudioTimestamp(msAreaStart * 1000L);
                        area = false;
                    }
                    
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
                        
                        Time t = Time.create(frame.timestamp / 1000L);
                        fireProgress(new AudioEvent(t));
                        
                        updateWaveform(t);
                    }
                    
                    if(frame.timestamp / 1000L >= msAreaStop){
                        action = Action.None;
                        msAreaStart = 0L;
                        msAreaStop = 0L;
                    }
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
    
    public void playAudioAndStop(Time start, Time end){
        area = true;        
        msAreaStart = Time.toMillisecondsTime(start);
        msAreaStop = Time.toMillisecondsTime(end);        
        playAudio();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Accesseurs">
    
    public void setFile(File video) throws FrameGrabber.Exception, LineUnavailableException {
        if(video.exists() == false){
            action = Action.None;
            return;
        }
        
        MainFrame.setProgress(0f, "Collecting informations");
        
        ffss = FFStuffs.create(video);
        
        MainFrame.setProgress(.1f, "Open audio");
        
        this.video = video;
        
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
        
        MainFrame.setProgress(.3f, "Set media ready to playback");
        
        action = Action.Ready;
        
        String basename = video.getName().substring(0, video.getName().lastIndexOf("."));
        
        // WST (forme d'onde - waveform)
        File wstFile = new File(basename + ".wst");
        if(wstFile.exists() == false){
            MainFrame.setProgress(.5f, "Create waveform");
            xy = doWaveFormFFmpeg(); 
        }else{
            MainFrame.setProgress(.5f, "Retrieve waveform");
            try{
                AudioWaveStorage aws = new AudioWaveStorage();
                aws.startConsult(video);
                Map<Integer, Integer> ii = aws.consultMultiValues(0, getSamplesFromFrame(ffss.getFrameCount()));
                aws.stopConsult();
                ii.entrySet().forEach(entry -> {
                    xy.put((double)entry.getKey(), (double)entry.getValue());
                });
            }catch(IOException exc){
                
            }
        }
        
        MainFrame.setProgress(1f, "OK");
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
    
    public void setWaveHeight(int height) {
        waveHeight = height;
        waveFormPanel.repaint();
    }
    
    public Time getTimeOfStartArea() {
        if(startArea != null){
            // On raisonne en samples et on veut des millisecondes
            int frames = getFrameFromSamples(Math.round(startArea.x * samplesPerPixel));
            return Time.getTimeFromFrame(frames, ffss.getFps());
            //return Time.fromMillisecondsTime(Math.round(wp.startArea.x / aws.pixels * aws.milliseconds));
        }
        return Time.create(0L);
    }
    
    public Time getTimeOfStartArea(boolean useFPS) {
        if(startArea != null){
            if(useFPS == true){
                return getTimeOfStartArea();            
            }else{
                return Time.getTimeFromSamples(ffss.getSampleRate(), samplesPerPixel, startArea.x);
            }
        }
        return Time.create(0L);
    }

    public void setTimeOfStartArea(Time startAreaTime) {
        // On a des millisecondes on veut des samples
        int frames = Time.getFrame(startAreaTime, ffss.getFps());
        int x = Math.round(getSamplesFromFrame(frames) / samplesPerPixel);
//        int x = (int)(Time.toMillisecondsTime(startAreaTime) / aws.milliseconds * aws.pixels);
        startArea = new Point(x, 0);
        waveFormPanel.repaint();
    }

    public Time getTimeOfStopArea() {
        if(stopArea != null){
            // On raisonne en samples et on veut des millisecondes
            int frames = getFrameFromSamples(Math.round(stopArea.x * samplesPerPixel));
            return Time.getTimeFromFrame(frames, ffss.getFps());
//            return Time.fromMillisecondsTime(Math.round(wp.stopArea.x / aws.pixels * aws.milliseconds));
        }
        return Time.create(0L);
    }
    
    public Time getTimeOfStopArea(boolean useFPS) {
        if(startArea != null){
            if(useFPS == true){
                return getTimeOfStopArea();            
            }else{
                return Time.getTimeFromSamples(ffss.getSampleRate(), samplesPerPixel, stopArea.x);
            }
        }
        return Time.create(0L);
    }

    public void setTimeOfStopArea(Time stopAreaTime) {
        // On a des millisecondes on veut des samples
        int frames = Time.getFrame(stopAreaTime, ffss.getFps());
        int x = Math.round(getSamplesFromFrame(frames) / samplesPerPixel);
//        int x = (int)(Time.toMillisecondsTime(stopAreaTime) / aws.milliseconds * aws.pixels);
        stopArea = new Point(x, 0);
        waveFormPanel.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Waveform">
    
    private Map<Double, Double> doWaveFormFFmpeg() throws FrameGrabber.Exception{
        
        Map<Double, Double> _xy = new HashMap<>();
        
        // on veut des samples on a des milliseconds
        Time substractWhole = Time.substract(Time.create(0L), ffss.getDuration());
        
        // w >> largeur totale        
        int w = Math.round(getSamplesFromFrame(Time.getFrame(substractWhole, ffss.getFps())) / samplesPerPixel);
        
        if(w == 0) return _xy;
        
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(video);
        g.start();
        
        AudioFormat format = new AudioFormat(
                g.getSampleRate(), 
                16, 
                g.getAudioChannels(), 
                true, 
                true
        );
        
        boolean stop = false;
        
        while(stop == false){
            try {
                Frame frame = g.grab();
                
                if (frame == null) {
                    stop = true;
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
                    
                    //----------------------------------------------------------
                    // On souhaite maintenant lire les bytes
                    // afin de créer la courbe
                    //----------------------------------------------------------
                    
                    try(ByteArrayInputStream bais = new ByteArrayInputStream(outBuffer.array(), 0, outBuffer.capacity());
                            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(format, new AudioInputStream(bais, format, outBuffer.capacity()));){
                        
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

                            // On détermine les valeurs à entrer avec un ratio
                            double amplitude = ceiling - floor;
                            double max = waveHeight;
                            for(AudioWaveValues val : values){
                                double x = val.getX();
                                double y_calc = val.getY() - floor;
                                double y = max * y_calc / amplitude;
                                _xy.put(x, y);
                            }
                        }
                        
                        outBuffer.clear();
                    } catch (IOException ex) {
                        Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
                    }
                            
                }
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        g.stop();
        g.release();
                
        try{
            // Enregistrement
            AudioWaveStorage aws = new AudioWaveStorage();
            aws.startSave(video);
            _xy.entrySet().forEach(entry -> {
                aws.saveOneValue((int)(Math.round(entry.getKey())), (int)(Math.round(entry.getValue())));
            });
            aws.stopSave();
        }catch(IOException ex){
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return _xy;
    }
    
    
    
    // </editor-fold>
    
    public Audio() {
        init();
    }
    
    private void init(){
        setLayout(new BorderLayout());
        
        add(waveFormPanel, BorderLayout.CENTER);
        add(horizontalWaveBar, BorderLayout.SOUTH);
        
        horizontalWaveBar.addAdjustmentListener((AdjustmentEvent e) -> {
            // e.getValue() > frames
            // on raisonne en samples
            if(ffss != null){
                int samples = Math.round(getSamplesFromFrame(e.getValue()) / samplesPerPixel);
                offset = -samples;
                waveFormPanel.refresh();
//                updateDisplayWithOffset(-samples, timeStart, timeEnd);
            }
        });
        
        startThread();
    }
    
    public void refresh(){
        waveFormPanel.refresh();
    }
    
    private void updateWaveform(Time t) {
        msCurrent = t;
        waveFormPanel.refresh();
    }
    
}
