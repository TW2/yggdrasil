/*
 * Copyright (C) 2024 util2
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
package org.wingate.ygg.ui.audio;

import java.awt.BorderLayout;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javax.swing.JFileChooser;
import org.wingate.ygg.helper.AudioFileFilter;
import org.wingate.ygg.helper.VideoFileFilter;
import org.wingate.ygg.theme.Theme;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author util2
 */
public class AudioPanel extends javax.swing.JPanel {
    
    // Need for VLC installed
    // Need for action :
    // "C:\Program Files\VideoLAN\VLC\vlc-cache-gen.exe" "C:\Program Files\VideoLAN\VLC\plugins"
    
    private final Theme theme;
    private final JFXPanel fxPanel;
    
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private ImageView videoImageView;
    
    private String videoPath = null;
    
    private final AudioForm audioForm;

    /**
     * Creates new form AudioPanel
     * @param theme
     */
    public AudioPanel(Theme theme) {
        initComponents();
        
        fxPanel = new JFXPanel();
        
        audioForm = new AudioForm(AudioForm.Display.Both, AudioForm.Style.Both);
        embedPanel.add(audioForm, BorderLayout.CENTER);
        
        for(javax.swing.filechooser.FileFilter ff : fcOpenVideo.getChoosableFileFilters()){
            fcOpenVideo.removeChoosableFileFilter(ff);
        }
        fcOpenVideo.addChoosableFileFilter(new VideoFileFilter());
        fcOpenVideo.addChoosableFileFilter(new AudioFileFilter());
        
        
        
        this.theme = theme;
        
        init();
    }
    
    private void init(){
        theme.apply(this);
        
        Platform.runLater(()->{
            this.mediaPlayerFactory = new MediaPlayerFactory();
            this.embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
            this.embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                @Override
                public void playing(MediaPlayer mediaPlayer) {
                }

                @Override
                public void paused(MediaPlayer mediaPlayer) {
                }

                @Override
                public void stopped(MediaPlayer mediaPlayer) {
                }

                @Override
                public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                }
            });
            
            videoImageView = new ImageView();
            videoImageView.setPreserveRatio(true);

            embeddedMediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoImageView));
            
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: black;");

            videoImageView.fitWidthProperty().bind(root.widthProperty());
            videoImageView.fitHeightProperty().bind(root.heightProperty());

            root.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                // If you need to know about resizes
            });

            root.heightProperty().addListener((observableValue, oldValue, newValue) -> {
                // If you need to know about resizes
            });

            root.setCenter(videoImageView);

            Scene scene = new Scene(root, fxPanel.getWidth(), fxPanel.getHeight(), Color.BLACK);
            
            fxPanel.setScene(scene);
        });
        
    }

    public JFileChooser getOpenVideoChooser() {
        return fcOpenVideo;
    }
    
    public void setVideoFile(String s){
        if(s != null && s.isEmpty() == false){
            videoPath = s;
            audioForm.openFile(s, 5000L, embedPanel.getWidth(), embedPanel.getHeight());
        }
    }
    
    public void disposeAudio(){
        Platform.runLater(()->{
            embeddedMediaPlayer.release();
            mediaPlayerFactory.release();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcOpenVideo = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPlayBefore = new javax.swing.JButton();
        btnPlayBegin = new javax.swing.JButton();
        btnPlayArea = new javax.swing.JButton();
        btnPlayEnd = new javax.swing.JButton();
        btnPlayAfter = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnKPlayBefore = new javax.swing.JButton();
        btnKPlayBegin = new javax.swing.JButton();
        btnKPlayArea = new javax.swing.JButton();
        btnKPlayEnd = new javax.swing.JButton();
        btnKPlayAfter = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        toggleShowTracks = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jPanel4 = new javax.swing.JPanel();
        jScrollBar1 = new javax.swing.JScrollBar();
        jPanel5 = new javax.swing.JPanel();
        embedPanel = new javax.swing.JPanel();
        showTracksPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

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

        btnPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnPlayBefore.setText("Before");
        btnPlayBefore.setFocusable(false);
        btnPlayBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayBeforeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayBefore);

        btnPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 01.png"))); // NOI18N
        btnPlayBegin.setText("Begin");
        btnPlayBegin.setFocusable(false);
        btnPlayBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayBegin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayBeginActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayBegin);

        btnPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnPlayArea.setText("Area");
        btnPlayArea.setFocusable(false);
        btnPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAreaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayArea);

        btnPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 02.png"))); // NOI18N
        btnPlayEnd.setText("End");
        btnPlayEnd.setFocusable(false);
        btnPlayEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayEndActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayEnd);

        btnPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnPlayAfter.setText("After");
        btnPlayAfter.setFocusable(false);
        btnPlayAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAfterActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayAfter);
        jToolBar1.add(jSeparator2);

        btnKPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnKPlayBefore.setText("K Before");
        btnKPlayBefore.setFocusable(false);
        btnKPlayBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKPlayBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKPlayBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKPlayBeforeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnKPlayBefore);

        btnKPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 06.png"))); // NOI18N
        btnKPlayBegin.setText("K Begin");
        btnKPlayBegin.setFocusable(false);
        btnKPlayBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKPlayBegin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKPlayBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKPlayBeginActionPerformed(evt);
            }
        });
        jToolBar1.add(btnKPlayBegin);

        btnKPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs area yellow.png"))); // NOI18N
        btnKPlayArea.setText("K Area");
        btnKPlayArea.setFocusable(false);
        btnKPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKPlayAreaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnKPlayArea);

        btnKPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 05.png"))); // NOI18N
        btnKPlayEnd.setText("K End");
        btnKPlayEnd.setFocusable(false);
        btnKPlayEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKPlayEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKPlayEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKPlayEndActionPerformed(evt);
            }
        });
        jToolBar1.add(btnKPlayEnd);

        btnKPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnKPlayAfter.setText("K After");
        btnKPlayAfter.setFocusable(false);
        btnKPlayAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKPlayAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnKPlayAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKPlayAfterActionPerformed(evt);
            }
        });
        jToolBar1.add(btnKPlayAfter);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Audio controls", jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar2.setRollover(true);

        toggleShowTracks.setText("Tracks");
        toggleShowTracks.setFocusable(false);
        toggleShowTracks.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleShowTracks.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(toggleShowTracks);

        jPanel2.add(jToolBar2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Timing", jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jToolBar3.setRollover(true);
        jPanel3.add(jToolBar3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Karaoke", jPanel3);

        add(jTabbedPane1, java.awt.BorderLayout.SOUTH);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollBar1.setMaximum(250);
        jScrollBar1.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar1AdjustmentValueChanged(evt);
            }
        });
        jPanel4.add(jScrollBar1, java.awt.BorderLayout.SOUTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        embedPanel.setLayout(new java.awt.BorderLayout());
        jPanel5.add(embedPanel, java.awt.BorderLayout.CENTER);

        showTracksPanel.setLayout(new java.awt.BorderLayout());
        jPanel5.add(showTracksPanel, java.awt.BorderLayout.SOUTH);

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        // Play file
        Platform.runLater(()->{
            if(videoPath != null){                
                embeddedMediaPlayer.media().play(videoPath);
            }
        });
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        // Pause
        Platform.runLater(()->{
            if(videoPath != null){
                embeddedMediaPlayer.controls().pause();
            }
        });
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        // Stop and reset
        Platform.runLater(()->{
            if(videoPath != null){
                embeddedMediaPlayer.controls().stop();
            }
        });
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeforeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayBeforeActionPerformed

    private void btnPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayBeginActionPerformed

    private void btnPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayAreaActionPerformed

    private void btnPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayEndActionPerformed

    private void btnPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAfterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPlayAfterActionPerformed

    private void btnKPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKPlayBeforeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKPlayBeforeActionPerformed

    private void btnKPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKPlayBeginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKPlayBeginActionPerformed

    private void btnKPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKPlayAreaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKPlayAreaActionPerformed

    private void btnKPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKPlayEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKPlayEndActionPerformed

    private void btnKPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKPlayAfterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnKPlayAfterActionPerformed

    private void jScrollBar1AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar1AdjustmentValueChanged
        // Move waveform, spectrogram and tracks if visible
        // Must call for repaint object embedded
        if(videoPath != null){
            double percent = (double)jScrollBar1.getValue() / (double)jScrollBar1.getMaximum();
            audioForm.displayTime(percent);
        }        
    }//GEN-LAST:event_jScrollBar1AdjustmentValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKPlayAfter;
    private javax.swing.JButton btnKPlayArea;
    private javax.swing.JButton btnKPlayBefore;
    private javax.swing.JButton btnKPlayBegin;
    private javax.swing.JButton btnKPlayEnd;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnPlayAfter;
    private javax.swing.JButton btnPlayArea;
    private javax.swing.JButton btnPlayBefore;
    private javax.swing.JButton btnPlayBegin;
    private javax.swing.JButton btnPlayEnd;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel embedPanel;
    private javax.swing.JFileChooser fcOpenVideo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JPanel showTracksPanel;
    private javax.swing.JToggleButton toggleShowTracks;
    // End of variables declaration//GEN-END:variables
}
