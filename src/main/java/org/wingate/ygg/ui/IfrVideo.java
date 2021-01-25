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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.audiovideo.PlayAudio;
import org.wingate.ygg.audiovideo.PlaySubtitles;
import org.wingate.ygg.audiovideo.PlayVideo;
import org.wingate.ygg.audiovideo.PlayVideo.VideoPanel;
import org.wingate.ygg.audiovideo.VideoListener;
import org.wingate.ygg.util.FramesPanel;
import org.wingate.ygg.util.SubtitlesChoice;

/**
 *
 * @author util2
 */
public class IfrVideo extends javax.swing.JInternalFrame {
    
    private IfrWave wave = null;
    private PlayAudio playAudio = null;
    private VideoPanel videoPanel = null;
    
    private FramesPanel fp;
    
    private final PlayVideo playVideo = new PlayVideo();
    private final PlaySubtitles playSubs = new PlaySubtitles();
    
    private boolean media = false;
    
    private final SubtitlesChoice sChoice = new SubtitlesChoice();

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
        
        // Audio
        playAudio = wave.getPlayAudio();
        
        // Subtitles
        lblSubs.setHorizontalAlignment(JLabel.CENTER);
        lblSubs.setVerticalAlignment(JLabel.CENTER);
    }

    public void setVideoPath(String videopath){
        //free();
        // Préparation de variables
        File video = new File(videopath);
        
        try {
            playVideo.setVideo(video);
            media = video.exists();
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
    
    public void updateSubtitlesImage(BufferedImage image){
        lblSubs.setIcon(new ImageIcon(image));
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
        lblSubs = new javax.swing.JLabel();
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
            .addComponent(lblSubs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panVideoLayout.setVerticalGroup(
            panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSubs, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
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

        javax.swing.GroupLayout panKeyFramesLayout = new javax.swing.GroupLayout(panKeyFrames);
        panKeyFrames.setLayout(panKeyFramesLayout);
        panKeyFramesLayout.setHorizontalGroup(
            panKeyFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panKeyFramesLayout.setVerticalGroup(
            panKeyFramesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 8, Short.MAX_VALUE)
        );

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
                .addComponent(panKeyFrames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if(fp != null){
            Time end = fp.getAreaStartTime();
            Time start = Time.substract(end, Time.create(500L));
            playArea(start, end);
        }        
    }//GEN-LAST:event_btnBeforeStartActionPerformed

    private void btnBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBeginActionPerformed
        if(fp != null){
            Time start = fp.getAreaStartTime();
            Time end = Time.addition(start, Time.create(500L));
            playArea(start, end);
        }
    }//GEN-LAST:event_btnBeginActionPerformed

    private void btnInsideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsideActionPerformed
        if(fp != null){
            Time start = fp.getAreaStartTime();
            Time end = fp.getAreaEndTime();
            playArea(start, end);
        }        
    }//GEN-LAST:event_btnInsideActionPerformed

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        if(fp != null){
            Time end = fp.getAreaEndTime();
            Time start = Time.substract(end, Time.create(500L));
            playArea(start, end);
        }        
    }//GEN-LAST:event_btnEndActionPerformed

    private void btnAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAfterActionPerformed
        if(fp != null){
            Time start = fp.getAreaEndTime();
            Time end = Time.addition(start, Time.create(500L));
            playArea(start, end);
        }        
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
    private javax.swing.JLabel lblSubs;
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
    
}
