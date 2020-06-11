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
package org.wingate.ygg.ifrm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.event.VideoTimeEvent;
import org.wingate.ygg.event.VideoTimeListener;
import org.wingate.ygg.ui.FramesPanel;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.Time;
import org.wingate.ygg.util.VideoTimeHandler;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

/**
 *
 * @author util2
 */
public class FrmVideo extends javax.swing.JInternalFrame {
    
    private final AVStudio studio;
    private final JFXPanel fXPanel = new JFXPanel();
    
    private final VideoTimeHandler vth = new VideoTimeHandler();
    
    private MediaPlayerFactory mediaPlayerFactory;
    private EmbeddedMediaPlayer embeddedMediaPlayer;
    private ImageView videoImageView;
    
    private FFStuffs ffss = null;
    private final FramesPanel fp = new FramesPanel();
    private final JLabel lblImage = new JLabel();
    
    private File video = null;
    private ASS ass = null;
    
    private Event currentEvent = null;
    
    /**
     * Creates new form FrmVideo
     * @param studio
     */
    public FrmVideo(AVStudio studio) {
        initComponents();
        this.studio = studio;
        init();
    }
    
    private void init(){        
        panTimeline.add(fp, BorderLayout.CENTER);
        
        panVideo.add(fXPanel, BorderLayout.CENTER);
        fXPanel.setLayout(new BorderLayout());
        fXPanel.add(lblImage, BorderLayout.CENTER);        
        
        vth.addVideoTimeListener(new VideoTimeListener() {
            @Override
            public void timeChanged(VideoTimeEvent event) {
                tfCurTime.setText(event.getCurrentTime().toProgramExtendedTime());
                tfCurFrame.setText(Integer.toString(Time.getFrame(event.getCurrentTime(), ffss.getFps())));
                fp.updatePosition(event.getCurrentTime());
                
                if(ass != null){
                    Image subtitlesImage = getAssShape(ass, event.getCurrentTime());
                    int w = Integer.parseInt(ass.getResX());
                    int h = Integer.parseInt(ass.getResY());
                    Dimension size = getScaledDimension(new Dimension(w, h), fXPanel.getSize());
                    if(subtitlesImage != null){
                        Image image = subtitlesImage.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
                        lblImage.setIcon(new ImageIcon(image));
                    }else{
                        lblImage.setIcon(new ImageIcon(new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB)));
                    }                    
                    lblImage.setLocation((fXPanel.getWidth() - size.width) / 2, (fXPanel.getHeight() - size.height) / 2);
                }
            }

            @Override
            public void timeReached(VideoTimeEvent event) {
                vth.stopVTH();
                vth.resetVTH(Time.create(0L));
                Platform.runLater(() -> {
                    embeddedMediaPlayer.submit(() -> {
                        embeddedMediaPlayer.controls().stop();                        
                    });
                });
            }

            @Override
            public void timeEnded(VideoTimeEvent event) {
                
            }
        });
        
        Platform.runLater(() -> {
            mediaPlayerFactory = new MediaPlayerFactory();
            embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
            
            videoImageView = new ImageView();
            videoImageView.setFitWidth(fXPanel.getWidth());
            videoImageView.setFitHeight(fXPanel.getHeight());
            videoImageView.setPreserveRatio(true);
            
            embeddedMediaPlayer.videoSurface().set(videoSurfaceForImageView(videoImageView));
            
            Group root = new Group();
            Scene scene = new Scene(root, fXPanel.getWidth(), fXPanel.getHeight());            
            
            StackPane stack = new StackPane();
            stack.getChildren().add(videoImageView);
            scene.setRoot(stack);
            fXPanel.setScene(scene);
            fXPanel.setVisible(true);
        });
    }
    
    public void openAudioVideo(File video){
        this.video = video;
        
        ffss = FFStuffs.create(video);
        
        studio.setVideo(video);
        studio.setFfss(ffss);
        
        fp.configure(ffss, studio.isDark());
        
        studio.setFp(fp);
        
        Time current = Time.create(0L);
        Time start = Time.create(0L);
        Time end = ffss.getDuration();
        Time dur = ffss.getDuration();
        
        tfStartTime.setText(start.toProgramExtendedTime());
        tfStartFrame.setText(Integer.toString(Time.getFrame(start, ffss.getFps())));
        tfEndTime.setText(end.toProgramExtendedTime());
        tfEndFrame.setText(Integer.toString(Time.getFrame(end, ffss.getFps())));
        tfDurTime.setText(dur.toProgramExtendedTime());
        tfDurFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
        tfCurTime.setText(current.toProgramExtendedTime());
        tfCurFrame.setText(Integer.toString(Time.getFrame(current, ffss.getFps())));

        fp.updatePosition(current);
    }
    
    public void openASS(File assFile){
        ass = assFile != null ? ASS.Read(assFile.getPath()) : null;
    }
    
    private void playAndStop(Time startTime, Time endTime){
        if(currentEvent != null){
            vth.setStopTime(endTime);
            vth.setDuration(ffss.getDuration());
            vth.playVTH();
            Platform.runLater(() -> {                
                embeddedMediaPlayer.submit(() -> {
                    embeddedMediaPlayer.media().play(video.getPath());
                    embeddedMediaPlayer.controls().setTime(Time.toMillisecondsTime(startTime));
                });
            });
        }
    }

    public FFStuffs getFfss() {
        return ffss;
    }
    
    private Image getAssShape(ASS ass, Time current){
        // On définit le fichier PNG cible
        String pngPath = new File(video.getParentFile(), "temp.png").getPath();
        
        // On définit le fichier ASS source
        String assPath = ass.getAssFile().getPath();
        
        // On définit le temps actuel en secondes flottantes
        String time = Double.toString(Time.getLengthInSeconds(current));
        
        // On définit la taille de la vidéo
        int width = Integer.parseInt(ass.getResX());
        int height = Integer.parseInt(ass.getResY());
        
        // On lance YGGY (libass)
        int result = MainFrame.getYGGY().getYggy().executor(pngPath, assPath, time, width, height);
        
        // Si on a pas de sous-titres à cet endroit de la vidéo
        if(result == 1){
            return null;
        }
        
        // On crée une image avec alpha
        Image img = MainFrame.makeColorTransparent(new ImageIcon(pngPath).getImage(), new Color(63, 63, 63));
        
        // On retourne une image avec alpha
        return img;
    }
    
    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    public void updateAreaFrames(Event ev){
        currentEvent = ev;
        
        fp.updateArea(ev.getStartTime(), ev.getEndTime());
        
        Time dur = Time.substract(ev.getStartTime(), ev.getEndTime());
        
        tfStartTime.setText(ev.getStartTime().toProgramExtendedTime());
        tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStartTime(), ffss.getFps())));
        tfEndTime.setText(ev.getEndTime().toProgramExtendedTime());
        tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEndTime(), ffss.getFps())));
        tfDurTime.setText(dur.toProgramExtendedTime());
        tfDurFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panTimeline = new javax.swing.JPanel();
        panVideo = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        tfDurTime = new javax.swing.JTextField();
        tfDurFrame = new javax.swing.JTextField();
        tfCurTime = new javax.swing.JTextField();
        tfCurFrame = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        sep01 = new javax.swing.JToolBar.Separator();
        btnPlayBeforeStart = new javax.swing.JButton();
        btnPlayAfterStart = new javax.swing.JButton();
        btnPlayArea = new javax.swing.JButton();
        btnPlayBeforeEnd = new javax.swing.JButton();
        btnPlayAfterEnd = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Video FX");

        panTimeline.setLayout(new java.awt.BorderLayout());

        panVideo.setBackground(new java.awt.Color(102, 153, 255));
        panVideo.setLayout(new java.awt.BorderLayout());

        tfStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartTime.setText("0.00.00.000");

        tfStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartFrame.setText("0");

        tfEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndTime.setText("0.00.00.000");

        tfEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndFrame.setText("0");

        tfDurTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurTime.setText("0.00.00.000");

        tfDurFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurFrame.setText("0");

        tfCurTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCurTime.setText("0.00.00.000");

        tfCurFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfCurFrame.setText("0");

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

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
        jToolBar2.add(btnPlay);

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
        jToolBar2.add(btnPause);

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
        jToolBar2.add(btnStop);
        jToolBar2.add(sep01);

        btnPlayBeforeStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnPlayBeforeStart.setText("Before");
        btnPlayBeforeStart.setFocusable(false);
        btnPlayBeforeStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayBeforeStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayBeforeStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayBeforeStartActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPlayBeforeStart);

        btnPlayAfterStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 01.png"))); // NOI18N
        btnPlayAfterStart.setText("Begin");
        btnPlayAfterStart.setFocusable(false);
        btnPlayAfterStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayAfterStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayAfterStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAfterStartActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPlayAfterStart);

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
        jToolBar2.add(btnPlayArea);

        btnPlayBeforeEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 02.png"))); // NOI18N
        btnPlayBeforeEnd.setText("End");
        btnPlayBeforeEnd.setFocusable(false);
        btnPlayBeforeEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayBeforeEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayBeforeEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayBeforeEndActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPlayBeforeEnd);

        btnPlayAfterEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnPlayAfterEnd.setText("After");
        btnPlayAfterEnd.setFocusable(false);
        btnPlayAfterEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayAfterEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayAfterEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAfterEndActionPerformed(evt);
            }
        });
        jToolBar2.add(btnPlayAfterEnd);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfDurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfCurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(tfDurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tfDurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(tfCurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(tfCurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panTimeline, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        vth.setDuration(ffss.getDuration());
        vth.playVTH();
        Platform.runLater(() -> {
            embeddedMediaPlayer.submit(() -> {
                embeddedMediaPlayer.media().play(video.getPath());
            });
        });
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        vth.pauseVTH();
        Platform.runLater(() -> {
            embeddedMediaPlayer.submit(() -> {
                embeddedMediaPlayer.controls().pause();
            });
        });
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        vth.stopVTH();
        vth.resetVTH(Time.create(0L));
        Platform.runLater(() -> {
            embeddedMediaPlayer.submit(() -> {
                embeddedMediaPlayer.controls().stop();
            });
        });
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPlayBeforeStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeforeStartActionPerformed
        Time end = currentEvent.getStartTime();
        Time start = Time.substract(end, Time.create(500L));
        playAndStop(start, end);
    }//GEN-LAST:event_btnPlayBeforeStartActionPerformed

    private void btnPlayAfterStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAfterStartActionPerformed
        Time start = currentEvent.getStartTime();
        Time end = Time.addition(start, Time.create(500L));
        playAndStop(start, end);
    }//GEN-LAST:event_btnPlayAfterStartActionPerformed

    private void btnPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAreaActionPerformed
        Time start = currentEvent.getStartTime();
        Time end = currentEvent.getEndTime();
        playAndStop(start, end);
    }//GEN-LAST:event_btnPlayAreaActionPerformed

    private void btnPlayBeforeEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeforeEndActionPerformed
        Time end = currentEvent.getEndTime();
        Time start = Time.substract(end, Time.create(500L));
        playAndStop(start, end);
    }//GEN-LAST:event_btnPlayBeforeEndActionPerformed

    private void btnPlayAfterEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAfterEndActionPerformed
        Time start = currentEvent.getEndTime();
        Time end = Time.addition(start, Time.create(500L));
        playAndStop(start, end);
    }//GEN-LAST:event_btnPlayAfterEndActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnPlayAfterEnd;
    private javax.swing.JButton btnPlayAfterStart;
    private javax.swing.JButton btnPlayArea;
    private javax.swing.JButton btnPlayBeforeEnd;
    private javax.swing.JButton btnPlayBeforeStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel panTimeline;
    private javax.swing.JPanel panVideo;
    private javax.swing.JToolBar.Separator sep01;
    private javax.swing.JTextField tfCurFrame;
    private javax.swing.JTextField tfCurTime;
    private javax.swing.JTextField tfDurFrame;
    private javax.swing.JTextField tfDurTime;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    // End of variables declaration//GEN-END:variables
}
