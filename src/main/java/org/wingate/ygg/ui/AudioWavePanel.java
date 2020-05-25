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
package org.wingate.ygg.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import org.wingate.ygg.karaoke.Syllable;
import org.wingate.ygg.karaoke.SyllableCollection;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class AudioWavePanel extends JPanel {
    
    AudioWave awm;
    AudioWaveScale aws;
    FFStuffs ffss;
    
    File videoFilePath;
    Color backColor = DrawColor.white_smoke.getColor();
    Color centerColor = DrawColor.corn_flower_blue.getColor();
    Color curveColor = Color.blue.brighter();
    Color vSecondsColor = Color.red;
    Color keyFrameColor = Color.black;
    
    // For play area
    long msAreaStart = 0L, msAreaStop = 0L;
    Time msCurrent = Time.create(0L), oldMsCurrent = Time.create(0L);
    // For the movement of waveform
    int offset = 0;
    // To play sound
    File generatedWAV = null, oldSelectedWAV = null;
    float samplesPerPixel = 1000f;
    int[] startAreaKaraPixels = null, stopAreaKaraPixels = null;
    
    Point startArea = null, startKaraokeArea = null;
    Point stopArea = null, stopKaraokeArea = null;
    Point currentMouseMotion = null;
    Point currentProgressOnPlay = null;

    SyllableCollection karaokeOverlay = null;
    int activeSyllableIndex = 0;
    
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
        startAreaKaraPixels = awm.startAreaKaraPixels;
        stopAreaKaraPixels = awm.stopAreaKaraPixels;
        
        repaint();
    }

    public AudioWavePanel(FFStuffs ffss, AudioWaveScale aws, AudioWave awm) {
        this.ffss = ffss;
        this.aws = aws;
        this.awm = awm;
        init();
    }

    private void init(){
        setDoubleBuffered(true);
        videoFilePath = awm.getVideoFilePath();

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
                // TODO Glue on karaoke time (start end start end start...)
                if(startAreaKaraPixels != null) { startAreaKaraPixels[activeSyllableIndex] = e.getX() - offset; }
                if(stopAreaKaraPixels != null) { stopAreaKaraPixels[activeSyllableIndex] = e.getX() - offset; }
                if(e.getButton() == MouseEvent.BUTTON1 && karaokeOverlay != null){
                    startKaraokeArea = new Point(e.getX() - offset, e.getY());
                    karaokeOverlay.getSyllables().get(activeSyllableIndex).setStart(aws.getTimeFrom(e.getX() - offset));                        
                }else if(e.getButton() == MouseEvent.BUTTON3 && karaokeOverlay != null){
                    stopKaraokeArea = new Point(e.getX() - offset, e.getY());
                    karaokeOverlay.getSyllables().get(activeSyllableIndex).setEnd(aws.getTimeFrom(e.getX() - offset));                        
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
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(backColor);
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

            // On dessine la courbe (ligne du milieu)
            g2d.setColor(curveColor);
            g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);

            Line2D l2d;
            int count = 0, times = 0;
            for(Map.Entry<Integer, Integer> entry  : values.entrySet()){
                int x = offset + entry.getKey();
                int y = entry.getValue();

                if(x > 0 && x <= getWidth()){
                    // On dessine la courbe (générale)
                    g2d.setColor(curveColor);
                    l2d = new Line2D.Double(
                            x, 
                            y, 
                            x, 
                            awm.wp.getHeight() - y);
                    g2d.draw(l2d);

                    // On dessine la courbe (centre)
                    g2d.setColor(centerColor);
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
                    g2d.setColor(vSecondsColor);
                    l2d = new Line2D.Double(
                            x, 
                            0, 
                            x, 
                            getHeight());
                    g2d.draw(l2d);
                    g2d.setColor(Color.black);
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
                g2d.setColor(DrawColor.dark_green.getColor());
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
                g2d.setColor(DrawColor.dark_red.getColor());
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
                g2d.setColor(DrawColor.green_yellow.getColor(0.5f));
                Rectangle2D r2d = new Rectangle2D.Float(
                        offset + startArea.x,
                        0,
                        stopArea.x - startArea.x,
                        getHeight());
                g2d.fill(r2d);
            }

            if(currentMouseMotion != null){
                // On dessine le curseur
                g2d.setColor(Color.pink);
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
                
                g2d.setColor(keyFrameColor);
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
            g2d.setColor(DrawColor.violet.getColor());
            int progressSamples = awm.getSamplesFromFrame(Time.getFrame(msCurrent, ffss.getFps()));
            l2d = new Line2D.Double(
                    offset + progressSamples / samplesPerPixel, 
                    0, 
                    offset + progressSamples / samplesPerPixel, 
                    getHeight());
            g2d.draw(l2d);
            g2d.setStroke(oldStroke);

            if(karaokeOverlay != null){
                int xps, xpe;
                // On dessine chaque zone de syllabe
                for(int i=0; i<karaokeOverlay.getSyllableCount(); i++){                        
                    // Début >> startArea >> xps
                    // Fin >> startArea + ms >> xps + ms = xpe
                    xps = i == 0 ?
                            startArea.x :
                            aws.getXFrom(karaokeOverlay.getSyllables().get(i).getStart());

                    xpe = i == karaokeOverlay.getSyllableCount() - 1 ?
                            stopArea.x :
                            aws.getXFrom(karaokeOverlay.getSyllables().get(i).getEnd());

                    g2d.setColor(activeSyllableIndex == i ?
                            DrawColor.gold.getColor(0.5f) :
                            DrawColor.medium_purple.getColor(0.2f));                        
                    Rectangle2D r2d = new Rectangle2D.Float(
                            offset + xps,
                            0,
                            xpe - xps,
                            getHeight());
                    g2d.fill(r2d);
                }                    

                // On dessine l'overlay
                FontMetrics fm = g2d.getFontMetrics();
                Font oldFont = g2d.getFont();
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                int xPosKaraTime = 600;
                for(int i=0; i<karaokeOverlay.getSyllableCount(); i++){
                    // On dessine l'overlay de syllabe
                    Syllable syl = karaokeOverlay.getSyllables().get(i);
                    int strSize = fm.stringWidth(syl.getContent()) + 3;
                    g2d.setColor(activeSyllableIndex == i ?
                            DrawColor.gold.getColor(0.5f) :
                            DrawColor.medium_purple.getColor(0.5f));                        
                    g2d.fillRect(xPosKaraTime, 140, strSize, 20);
                    // On dessine les syllabes
                    g2d.setColor(Color.black);
                    g2d.drawString(syl.getContent(), xPosKaraTime, 155);
                    xPosKaraTime = xPosKaraTime + strSize;
                }
                g2d.setFont(oldFont);
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
    
    public void updatePoint(Point start, Point stop){
        startArea = start;
        stopArea = stop;
        repaint();
    }
}
