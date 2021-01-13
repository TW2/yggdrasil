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
package org.wingate.ygg.io.audio;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;

/**
 *
 * @author util2
 */
public class AudioWavePanel extends JPanel {
    
    boolean dark = false;
    
    AudioWave awm;
    AudioWaveScale aws;
    FFStuffs ffss;
    
    File videoFilePath;
    
    // For play area
    long msAreaStart = 0L, msAreaStop = 0L;
    Time msCurrent = Time.create(0L), oldMsCurrent = Time.create(0L);
    // For the movement of waveform
    int offset = 0;
    // To play sound
    File generatedWAV = null, oldSelectedWAV = null;
    float samplesPerPixel = 1000f;
    
    Point startArea = null;
    Point stopArea = null;
    Point currentMouseMotion = null;
    Point currentProgressOnPlay = null;
    
    boolean karaokeMode = false;
    int karaokeCurrentIndex = 0;
//    List<AssKaraokeCollection> assKaraokeCollections = new ArrayList<>();
    
    public void setKaraokeMode(boolean active){
        karaokeMode = active;
        
        repaint();
    }
    
//    public int getAssKaraokeCollectionIndex(Event ev){
//        int index = -1;
//        
//        for(int i=0; i<assKaraokeCollections.size(); i++){
//            AssKaraokeCollection akc = assKaraokeCollections.get(i);
//            if(akc.getEvent().equals(ev) == true){
//                index = i;
//                break;
//            }
//        }
//            
//        return index;
//    }

//    public List<AssKaraokeCollection> getAssKaraokeCollections() {
//        return assKaraokeCollections;
//    }
//
//    public void setAssKaraokeCollections(List<AssKaraokeCollection> assKaraokeCollections) {
//        this.assKaraokeCollections = assKaraokeCollections;
//        
//        repaint();
//    }
    
    public void updateView(){
        // For play area
        msAreaStart = awm.msAreaStart;
        msAreaStop = awm.msAreaStop;
        msCurrent = awm.msCurrent;
        oldMsCurrent = awm.oldMsCurrent;
        
        // For the movement of waveform
        offset = awm.offset;
        
        // To play sound
        samplesPerPixel = awm.samplesPerPixel;
        
        repaint();
    }

    public AudioWavePanel(FFStuffs ffss, AudioWaveScale aws, AudioWave awm, boolean dark) {
        this.ffss = ffss;
        this.aws = aws;
        this.awm = awm;
        this.dark = dark;
        init();
    }

    private void init(){
        setDoubleBuffered(true);
        videoFilePath = awm.getVideoFilePath();
        
//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyReleased(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_LEFT && karaokeMode == true){
//                    if(assKaraokeCollections.isEmpty() == false){
//                        
//                    }
//                }else if(e.getKeyCode() == KeyEvent.VK_RIGHT && karaokeMode == true){
//                    if(assKaraokeCollections.isEmpty() == false){
//                        
//                    }
//                }
//            }
//        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateView();
            }            
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                currentMouseMotion = new Point(e.getX() - offset, e.getY());
                updateView();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && karaokeMode == true){
                    // Syllabe en cours : pointeur de la souris sur le début
//                    Syllable syl = karaokeOverlay.getSyllableAt(karaokeOverlay.getSyllableIndex());
//                    syl.setStart(aws.getTimeFrom(e.getX() - offset));
//                    syl.setStartPoint(new Point(e.getX() - offset, e.getY()));
                    
//                    if(karaokeOverlay.getSyllableIndex() > 0){
//                        Syllable beforeSYL = karaokeOverlay.getSyllableAt(karaokeOverlay.getSyllableIndex() - 1);
//                        beforeSYL.setEnd(aws.getTimeFrom(e.getX() - offset));
//                        beforeSYL.setEndPoint(new Point(e.getX() - offset, e.getY()));
//                    }
                }else if(e.getButton() == MouseEvent.BUTTON3 && karaokeMode == true){
//                    Syllable syl = karaokeOverlay.getSyllableAt(karaokeOverlay.getSyllableIndex());
//                    syl.setEnd(aws.getTimeFrom(e.getX() - offset));
//                    syl.setEndPoint(new Point(e.getX() - offset, e.getY()));
                    
//                    if(karaokeOverlay.getSyllableIndex() < karaokeOverlay.getSyllableCount() - 1){
//                        Syllable afterSYL = karaokeOverlay.getSyllableAt(karaokeOverlay.getSyllableIndex() + 1);
//                        afterSYL.setStart(aws.getTimeFrom(e.getX() - offset));
//                        afterSYL.setStartPoint(new Point(e.getX() - offset, e.getY()));
//                    }
                }else if(e.getButton() == MouseEvent.BUTTON1){
                    startArea = new Point(e.getX() - offset, e.getY());
                }else if(e.getButton() == MouseEvent.BUTTON3){
                    stopArea  = new Point(e.getX() - offset, e.getY());
                }else if(e.getButton() == MouseEvent.BUTTON2 && e.isControlDown() == true){
                    startArea = null;
                    stopArea = null;
                }
                updateView();
            }
        });
        
        addMouseWheelListener((MouseWheelEvent e) -> {
            int samples;
            int value = awm.horizontalWaveBar.getValue();
            final int min = awm.horizontalWaveBar.getMinimum();
            final int max = awm.horizontalWaveBar.getMaximum();
            if(e.getWheelRotation() > 0){
                value = value - 50 < min ? min : value - 50;
                samples = Math.round(getSamplesFromFrame(value) / samplesPerPixel);                
            }else{
                value = value + 50 > max ? max : value + 50;
                samples = Math.round(getSamplesFromFrame(value) / samplesPerPixel);
            }
            awm.horizontalWaveBar.setValue(value);
            awm.updateDisplayWithOffset(-samples, awm.timeStart, awm.timeEnd);
        });
    }
    
    public static Color getBackgroundColor(boolean dark){
        return AudioWavePanel.AudioWaveColors.Background.getColor(dark);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(AudioWaveColors.Background.getColor(dark));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if(videoFilePath != null && videoFilePath.exists() == true){
            // On obtient un tableau des données
            AudioWaveStorage store;
            Map<Integer, Integer> values = new HashMap<>();
            try {
                store = new AudioWaveStorage();
                store.startConsult(videoFilePath);

                values = store.consultMultiValues(0, awm.getSamplesFromFrame(ffss.getFrameCount()));

                store.stopConsult();
            } catch (IOException ex) {
                Logger.getLogger(AudioWavePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // On obtient une valeur corrective de la courbe
            // à partir des 3 premières valeurs (blanc anormalement non à zéro)
            int blankCorrection = 0;
            int blankCounter = 0;
            int bkv1 = -1, bkv2 = -2, bkv3 = -3;
            for(Map.Entry<Integer, Integer> entry  : values.entrySet()){
                blankCounter++;
                if(blankCounter == 1) bkv1 = entry.getValue();
                if(blankCounter == 2) bkv2 = entry.getValue();
                if(blankCounter == 3) bkv3 = entry.getValue();
                
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
            for(Map.Entry<Integer, Integer> entry  : values.entrySet()){
                int x = offset + entry.getKey();
                int y = entry.getValue();
                // z = getHeight() / 2 = blankCorrection
                // z = y               = entry.getValue()
                if(blankCorrection != 0){
                    y = getHeight() / 2 * entry.getValue() / blankCorrection;
                }

                if(x > 0 && x <= getWidth()){
                    // On dessine la courbe (générale)
                    g2d.setColor(AudioWaveColors.WaveForm.getColor(dark));
                    l2d = new Line2D.Double(
                            x, 
                            y, 
                            x, 
                            awm.wp.getHeight() - y);
                    g2d.draw(l2d);

                    // On dessine la courbe (centre)
                    g2d.setColor(AudioWaveColors.WaveFormCenter.getColor(dark));
                    l2d = new Line2D.Double(
                            x,
                            y / 2 + awm.wp.getHeight() / 4,
                            x,
                            awm.wp.getHeight() - y / 2 - awm.wp.getHeight() / 4);
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
                Time mark = Time.getTimeFromFrame(awm.getFrameFromSamples(Math.round(startArea.x * samplesPerPixel)), ffss.getFps());
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
                Time mark = Time.getTimeFromFrame(awm.getFrameFromSamples(Math.round(stopArea.x * samplesPerPixel)), ffss.getFps());
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
                Time mark = Time.getTimeFromFrame(awm.getFrameFromSamples(Math.round(currentMouseMotion.x * samplesPerPixel)), ffss.getFps());
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
                int progressSamples = awm.getSamplesFromFrame(Time.getFrame(msCurrent, ffss.getFps()));
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
            
            
//            if(karaokeOverlay != null){
//                int xps, xpe;
//                // On dessine chaque zone de syllabe
//                for(int i=0; i<karaokeOverlay.getSyllableCount(); i++){                        
//                    // Début >> startArea >> xps
//                    // Fin >> startArea + ms >> xps + ms = xpe
//                    xps = karaokeOverlay.getStartPointAt(i).x;
//                    xpe = karaokeOverlay.getEndPointAt(i).x;
//                    
//                    if(xps == 0 & xpe == 0){
//                        Time start = karaokeOverlay.getStartTimeAt(i);
//                        Time end = karaokeOverlay.getEndTimeAt(i);
//                        xps = Math.round(awm.getSamplesFromFrame(Time.getFrame(start, ffss.getFps())) / samplesPerPixel);
//                        xpe = Math.round(awm.getSamplesFromFrame(Time.getFrame(end, ffss.getFps())) / samplesPerPixel);
//                    }
//
//                    g2d.setColor(karaokeOverlay.getSyllableIndex() == i ?
//                            DrawColor.gold.getColor(0.5f) :
//                            DrawColor.medium_purple.getColor(0.2f));                        
//                    Rectangle2D r2d = new Rectangle2D.Float(
//                            offset + xps,
//                            0,
//                            xpe - xps,
//                            getHeight());
//                    g2d.fill(r2d);
//                }                    
//
//                // On dessine l'overlay
//                FontMetrics fm = g2d.getFontMetrics();
//                Font oldFont = g2d.getFont();
//                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
//                int xPosKaraTime = 600;
//                for(int i=0; i<karaokeOverlay.getSyllableCount(); i++){
//                    // On dessine l'overlay de syllabe
////                    Syllable syl = karaokeOverlay.getSyllables().get(i);
////                    int strSize = fm.stringWidth(syl.getContent()) + 3;
////                    g2d.setColor(karaokeOverlay.getSyllableIndex() == i ?
////                            DrawColor.gold.getColor(0.5f) :
////                            DrawColor.medium_purple.getColor(0.5f));                        
////                    g2d.fillRect(xPosKaraTime, 140, strSize, 20);
////                    // On dessine les syllabes
////                    g2d.setColor(Color.black);
////                    g2d.drawString(syl.getContent(), xPosKaraTime, 155);
////                    xPosKaraTime = xPosKaraTime + strSize;
//                }
//                g2d.setFont(oldFont);
//            }
        }
    }

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
    
    public int getSamplesFromFrame(int frame){
        // On a des frames, on veut des samples
        // 1. Avoir des milliseconds
        Time ref = Time.getTimeFromFrame(frame, ffss.getFps());
        // 2. Avoir des samples
        return (int)Math.round(Time.toMillisecondsTime(ref) * ffss.getSampleRate());
    }
    
    public void updatePoint(Point start, Point stop){
        startArea = start;
        stopArea = stop;
        repaint();
    }
}
