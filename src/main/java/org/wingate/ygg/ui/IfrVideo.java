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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Checksum;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.audiovideo.KeyFrameListener;
import org.wingate.ygg.audiovideo.PlayAudio;
import org.wingate.ygg.audiovideo.PlaySubtitles;
import org.wingate.ygg.audiovideo.PlayVideo;
import org.wingate.ygg.audiovideo.PlayVideo.VideoPanel;
import org.wingate.ygg.audiovideo.VideoKeyFrames;
import org.wingate.ygg.audiovideo.VideoListener;
import org.wingate.ygg.util.DrawColor;
import org.wingate.ygg.util.SubtitlesChoice;

/**
 *
 * @author util2
 */
public class IfrVideo extends javax.swing.JInternalFrame {
    
    private IfrWave wave = null;
    private PlayAudio playAudio = null;
    private VideoPanel videoPanel = null;
    private final VideoKeyFrames videoKeyFrames = new VideoKeyFrames();
    
    private final KeyFramesPanel keyFramesPanel = new KeyFramesPanel();
    
    private final PlayVideo playVideo = new PlayVideo();
    private final PlaySubtitles playSubs = new PlaySubtitles();
    
    private boolean media = false;
    
    private final SubtitlesChoice sChoice = new SubtitlesChoice();
    
    // On garde en mémoire l'image de subs pécédente
    private BufferedImage oldSubs = null;

    /**
     * Creates new form IfrVideo
     * @param wave
     */
    public IfrVideo(IfrWave wave) {        
        initComponents();
        this.wave = wave;
        init();
    }
    
    private void init(){
        // Vidéo
        videoPanel = playVideo.getVideoPanel();
        panVideo.setLayout(new BorderLayout());
        panVideo.add(videoPanel, BorderLayout.CENTER);
        
        playVideo.addVideoListener(new VideoListener() {
            @Override
            public void getImage(BufferedImage image) {
                videoPanel.updateImage(image);
            }

            @Override
            public void getTime(long ms) {
                Time t = Time.fromMillisecondsTime(ms);
                tfCurrentTime.setText(t.toProgramExtendedTime());
                playSubs.changeMilliseconds(ms);
            }

            @Override
            public void getFrameNumber(int frame) {
                String frameNumber = Integer.toString(frame);
                tfCurrentFrame.setText(frameNumber);
            }
        });
        
        videoKeyFrames.addKeyFrameListener(new KeyFrameListener() {
            @Override
            public void getKeyFrames(Map<Integer, Long> keyframes) {
                wave.setKeyframes(keyframes);
                keyFramesPanel.updateKeyFrames(keyframes, wave.getInfo().getDuration());
            }
        });
        
        slideTime.addChangeListener((ChangeEvent e) -> {
            org.wingate.ygg.audiovideo.AVInfo info = wave.getInfo();
            if(info != null){
                // Position du direct (slide vidéo)
                // duration (sec) = frame / fps
                long msDuration = Math.round(slideTime.getValue() / info.getFps() * 1000L);
                playVideo.setMsAreaStart(msDuration - 2);
                playVideo.setMsAreaStop(msDuration);
                playVideo.playStopVideo();
            }
        });
        
        // Audio
        playAudio = wave.getPlayAudio();
        
        // Keyframes
        panKeyFrames.add(keyFramesPanel, BorderLayout.CENTER);
        
        // Position du direct (slide vidéo)
        slideTime.setMinimum(0);
    }

    public void setVideoPath(String videopath){
        //free();
        // Préparation de variables
        File video = new File(videopath);
        
        try {            
            playVideo.setVideo(video);
            videoKeyFrames.startSearch(video);
            media = video.exists();
            // Position du direct (slide vidéo)
            // frame = duration (sec) / fps
            org.wingate.ygg.audiovideo.AVInfo info = wave.getInfo();
            int frame = (int)(info.getDuration() / 1000L / info.getFps());
            slideTime.setMaximum(frame);
        } catch (FrameGrabber.Exception | LineUnavailableException ex) {
            Logger.getLogger(IfrVideo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setSubtitlesFile(File subspath){
        sChoice.setSubtitlesFile(subspath);
        playSubs.setSubsFile(subspath);
    }
    
    public void setStartTime(Time start, double fps){
        tfStartTime.setText(start.toProgramExtendedTime());
        tfStartFrame.setText(Integer.toString(Time.getFrame(start, fps)));
    }
    
    public void setEndTime(Time end, double fps){
        tfEndTime.setText(end.toProgramExtendedTime());
        tfEndFrame.setText(Integer.toString(Time.getFrame(end, fps)));
    }
    
    public void setDurationTime(Time duration, double fps){
        tfDurationTime.setText(duration.toProgramExtendedTime());
        tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, fps)));
    }
    
    public void play(){
        playVideo.setAction(PlayVideo.Action.Play);
        playAudio.setAction(PlayAudio.Action.Play);
    }
    
    public void pause(){
        playVideo.setAction(PlayVideo.Action.Pause);
        playAudio.setAction(PlayAudio.Action.Pause);
    }
    
    public void stop(){
        playVideo.setAction(PlayVideo.Action.Stop);
        playAudio.setAction(PlayAudio.Action.Stop);
    }
    
    public void playArea(Time start, Time stop){        
        playVideo.setMsAreaStart(Time.toMillisecondsTime(start));
        playAudio.setMsAreaStart(Time.toMillisecondsTime(start));
        
        playVideo.setMsAreaStop(Time.toMillisecondsTime(stop));
        playAudio.setMsAreaStop(Time.toMillisecondsTime(stop));        
        
        playVideo.playStopVideo();
        playAudio.playStopAudio();
    }

    public boolean hasMedia() {
        return media;
    }
    
    public void updateSubtitlesImage(BufferedImage subs){
        if((oldSubs != null && isSameImage(oldSubs, subs) == false) | oldSubs == null){
//            playVideo.updateSubtitlesImage(subs);
//            oldSubs = subs;
        }        
    }
    
    private boolean isSameImage(BufferedImage a, BufferedImage b){
        return getMD5(a).equalsIgnoreCase(getMD5(b));        
    }
    
    private String getMD5(BufferedImage bi){
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            ImageIO.write(bi, "jpg", baos);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(baos.toByteArray());
            byte[] digest = md.digest();
            BigInteger no = new BigInteger(1, digest); 
            String hashtext = no.toString(16); 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            return hashtext; 
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(IfrVideo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "0";
     }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panVideo = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panControls = new javax.swing.JPanel();
        tfStartFrame = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        tfDurationFrame = new javax.swing.JTextField();
        tfCurrentFrame = new javax.swing.JTextField();
        tfStartTime = new javax.swing.JTextField();
        tfEndTime = new javax.swing.JTextField();
        tfDurationTime = new javax.swing.JTextField();
        tfCurrentTime = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnBeforeStart = new javax.swing.JButton();
        btnBegin = new javax.swing.JButton();
        btnInside = new javax.swing.JButton();
        btnEnd = new javax.swing.JButton();
        btnAfter = new javax.swing.JButton();
        slideTime = new javax.swing.JSlider();
        panKeyFrames = new javax.swing.JPanel();

        setMaximizable(true);
        setResizable(true);
        setTitle("Video");

        panVideo.setBackground(new java.awt.Color(153, 204, 255));

        javax.swing.GroupLayout panVideoLayout = new javax.swing.GroupLayout(panVideo);
        panVideo.setLayout(panVideoLayout);
        panVideoLayout.setHorizontalGroup(
            panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panVideoLayout.setVerticalGroup(
            panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );

        tfStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartFrame.setText("0");

        tfEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndFrame.setText("0");

        tfDurationFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurationFrame.setText("0");

        tfCurrentFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCurrentFrame.setText("0");

        tfStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartTime.setText("00.00.00.000");

        tfEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndTime.setText("00.00.00.000");

        tfDurationTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurationTime.setText("00.00.00.000");

        tfCurrentTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCurrentTime.setText("00.00.00.000");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play.png"))); // NOI18N
        btnPlay.setText("Play");
        btnPlay.setFocusable(false);
        btnPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlay);

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs pause.png"))); // NOI18N
        btnPause.setText("Pause");
        btnPause.setFocusable(false);
        btnPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPause);

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs stop.png"))); // NOI18N
        btnStop.setText("Stop");
        btnStop.setFocusable(false);
        btnStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        jToolBar1.add(btnStop);
        jToolBar1.add(jSeparator1);

        btnBeforeStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnBeforeStart.setText("Before");
        btnBeforeStart.setFocusable(false);
        btnBeforeStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBeforeStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBeforeStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeforeStartActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBeforeStart);

        btnBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 01.png"))); // NOI18N
        btnBegin.setText("Begin");
        btnBegin.setFocusable(false);
        btnBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBegin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBeginActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBegin);

        btnInside.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnInside.setText("At time");
        btnInside.setFocusable(false);
        btnInside.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInside.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInside.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsideActionPerformed(evt);
            }
        });
        jToolBar1.add(btnInside);

        btnEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 02.png"))); // NOI18N
        btnEnd.setText("End");
        btnEnd.setFocusable(false);
        btnEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEnd);

        btnAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnAfter.setText("After");
        btnAfter.setFocusable(false);
        btnAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAfterActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAfter);

        javax.swing.GroupLayout panControlsLayout = new javax.swing.GroupLayout(panControls);
        panControls.setLayout(panControlsLayout);
        panControlsLayout.setHorizontalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slideTime, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panControlsLayout.createSequentialGroup()
                        .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCurrentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panControlsLayout.createSequentialGroup()
                        .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCurrentFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );
        panControlsLayout.setVerticalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panControlsLayout.createSequentialGroup()
                        .addComponent(slideTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfCurrentTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfCurrentFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Play", panControls);

        panKeyFrames.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)
                    .addComponent(panKeyFrames, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panKeyFrames, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        play();
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        pause();
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        stop();
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnBeforeStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeforeStartActionPerformed
        Time end = keyFramesPanel.getStartTime();
        Time start = Time.substract(end, Time.create(500L));
        if(Time.toMillisecondsTime(start) < 0L) return;
        playArea(start, end);
    }//GEN-LAST:event_btnBeforeStartActionPerformed

    private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeginActionPerformed
        Time start = keyFramesPanel.getStartTime();
        Time end = Time.addition(start, Time.create(500L));
        playArea(start, end);
    }//GEN-LAST:event_btnBeginActionPerformed

    private void btnInsideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsideActionPerformed
        Time start = keyFramesPanel.getStartTime();
        Time end = keyFramesPanel.getEndTime();
        playArea(start, end);
    }//GEN-LAST:event_btnInsideActionPerformed

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        Time end = keyFramesPanel.getEndTime();
        Time start = Time.substract(end, Time.create(500L));
        if(Time.toMillisecondsTime(start) < 0L) return;
        playArea(start, end);
    }//GEN-LAST:event_btnEndActionPerformed

    private void btnAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAfterActionPerformed
        Time start = keyFramesPanel.getEndTime();
        Time end = Time.addition(start, Time.create(500L));
        playArea(start, end);
    }//GEN-LAST:event_btnAfterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAfter;
    private javax.swing.JButton btnBeforeStart;
    private javax.swing.JButton btnBegin;
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnInside;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStop;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panControls;
    private javax.swing.JPanel panKeyFrames;
    private javax.swing.JPanel panVideo;
    private javax.swing.JSlider slideTime;
    private javax.swing.JTextField tfCurrentFrame;
    private javax.swing.JTextField tfCurrentTime;
    private javax.swing.JTextField tfDurationFrame;
    private javax.swing.JTextField tfDurationTime;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    // End of variables declaration//GEN-END:variables
    
    public class KeyFramesPanel extends JPanel {
        
        private Map<Integer, Long> keyframes = new HashMap<>();
        private long microsecondsLength = -1L;
        
        private int start = 0;
        private int end = 0;
        private int current = 0;
        private int cursor = 0;
        
        private Time startTime = Time.create(0L);
        private Time endTime = Time.create(0L);

        public KeyFramesPanel() {
            init();
        }
        
        private void init(){
            setDoubleBuffered(true);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1){                        
                        // getWidth <> microsecondsLength
                        // x        <> value microseconds from map
                        start = e.getX();
                        startTime = Time.create(start * microsecondsLength / 1000L / getWidth());
                        repaint();
                    }else if(e.getButton() == MouseEvent.BUTTON3){                        
                        // getWidth <> microsecondsLength
                        // x        <> value microseconds from map
                        end = e.getX();
                        endTime = Time.create(end * microsecondsLength / 1000L / getWidth());
                        repaint();
                    }
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    cursor = e.getX();
                    repaint();
                }
                
            });
        }
        
        public void updateCurrentMicrosenconds(long currentMicros){
            if(microsecondsLength != -1L){
                current = (int)(getWidth() * currentMicros / microsecondsLength);
                repaint();
            }
        }
        
        public void updateKeyFrames(Map<Integer, Long> keyframes, long microsecondsLength){
            this.keyframes = keyframes;
            this.microsecondsLength = microsecondsLength;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            g.setColor(MainFrame.isDark() ? Color.gray : DrawColor.alice_blue.getColor());
            g.fillRect(0, 0, getWidth(), getHeight());
            
            if(microsecondsLength != -1L){                
                // On affiche les images clés
                Color keyframeColor = MainFrame.isDark() ? Color.white : Color.black;
                g.setColor(keyframeColor);
                for(Map.Entry<Integer, Long> entry : keyframes.entrySet()){
                    // getWidth <> microsecondsLength
                    // x        <> value microseconds from map
                    int xa = (int)(getWidth() * entry.getValue() / microsecondsLength);
                    g.drawLine(xa, 0, xa, getHeight());
                }
                
                // On affiche la zone
                g.setColor(DrawColor.lime.getColor(0.3f));
                g.fillRect(start, 0, end - start, getHeight());
                
                // On affiche le début de la zone
                g.setColor(DrawColor.dark_khaki.getColor());
                g.drawLine(start, 0, start, getHeight());
                
                // On affiche la fin de la zone
                g.setColor(DrawColor.chocolate.getColor());
                g.drawLine(end, 0, end, getHeight());
                
                // On affiche le curseur
                g.setColor(Color.pink);
                g.drawLine(cursor, 0, cursor, getHeight());
                
                // On affiche le curseur
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(current-1, 0, current-1, getHeight());
                g.setColor(DrawColor.magenta.getColor());
                g.drawLine(current, 0, current, getHeight());
                g.setColor(DrawColor.magenta.getColor(0.5f));
                g.drawLine(current+1, 0, current+1, getHeight());
            }            
        }

        public Time getStartTime() {
            return startTime;
        }

        public void setStartTime(Time startTime) {
            this.startTime = startTime;
            repaint();
        }

        public Time getEndTime() {
            return endTime;
        }

        public void setEndTime(Time endTime) {
            this.endTime = endTime;
            repaint();
        }
        
        
    } 
    
}
