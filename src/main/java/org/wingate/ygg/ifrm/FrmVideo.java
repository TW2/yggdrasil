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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.ui.FramesPanel;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.GlyphInfo;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class FrmVideo extends javax.swing.JInternalFrame {
    
    private final AVStudio studio;
    private final JFXPanel fXPanel = new JFXPanel();
    
    private FFStuffs ffss = null;
    private FramesPanel fp = new FramesPanel();
    private final JLabel lblImage = new JLabel();
    
    private File video = null;
    private ASS ass = null;
    
    // JavaFX ----------------------------------------------
    private static final double SIZE = 1;
    private Group group = new Group();
    private final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    private final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    private Path path;
    // -----------------------------------------------------
    
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
        
        setVisible(true);
    }
    
    public void openAudioVideo(File video){
        this.video = video;
        
        ffss = FFStuffs.create(video);
        changeMediaAndSetup(video);
        
        studio.setVideo(video);
        studio.setFfss(ffss);
        
        fp.configure(ffss, AVStudio.isDark());
        
        studio.setFp(fp);
    }
    
    public void openASS(File assFile){
        ass = assFile != null ? ASS.Read(assFile.getPath()) : null;
    }
    
    private void changeMediaAndSetup(File f){
        Platform.runLater(() -> {
            Media m = new Media(f.toURI().toString());
            MediaPlayer p = new MediaPlayer(m);
            
            AnimationTimer animate = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    Time current = Time.create(Math.round(p.getCurrentTime().toMillis()));
                    Time start = Time.create(Math.round(p.getStartTime().toMillis()));
                    Time end = Time.create(Math.round(p.getStopTime().toMillis()));
                    Time dur = Time.create(Math.round(p.getTotalDuration().toMillis()));
                    
                    EventQueue.invokeLater(() -> {
                        tfStartTime.setText(start.toProgramExtendedTime());
                        tfStartFrame.setText(Integer.toString(Time.getFrame(start, ffss.getFps())));
                        tfEndTime.setText(end.toProgramExtendedTime());
                        tfEndFrame.setText(Integer.toString(Time.getFrame(end, ffss.getFps())));
                        tfDurTime.setText(dur.toProgramExtendedTime());
                        tfDurFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
                        tfCurTime.setText(current.toProgramExtendedTime());
                        tfCurFrame.setText(Integer.toString(Time.getFrame(current, ffss.getFps())));
                        
                        fp.updatePosition(current);
                    });
                    
                    if(ass != null){
                        lblImage.setIcon(new ImageIcon(getAssShape(ass, current)));
                    }
                }
            };
            
            p.setOnEndOfMedia(() -> {
                p.seek(new Duration(0d));
            });
            p.setOnStopped(() -> {
                p.seek(new Duration(0d));
            });
            EventQueue.invokeLater(() -> {
                btnPlay.addActionListener((e) -> {
                    animate.start();
                    p.play();
                });
                btnPause.addActionListener((e) -> {
                    animate.stop();
                    p.pause();
                });
                btnStop.addActionListener((e) -> {
                    animate.stop();
                    p.stop();
                });
            });
            MediaView mv = new MediaView(p);
            mv.setPreserveRatio(true);
            
            Group root = new Group();
            Scene scene = new Scene(root, getWidth(), getHeight());
            
            StackPane stack = new StackPane();
            stack.getChildren().add(mv);
            stack.getChildren().add(group);
            scene.setRoot(stack);
            fXPanel.setScene(scene);
            fXPanel.setVisible(true);
        });
        
    }

    public FFStuffs getFfss() {
        return ffss;
    }
    
    private BufferedImage getAssShape(ASS ass, Time current){
        // On récupère les événements en cours
        List<Event> now = new ArrayList<>();
        for(Event ev : ass.getEvents()){
            long start = Time.toMillisecondsTime(ev.getStartTime());
            long stop = Time.toMillisecondsTime(ev.getEndTime());
            long cur = Time.toMillisecondsTime(current);
                    
            if(start <= cur && cur < stop){
                now.add(ev);
            }
        }
        
        BufferedImage bi = new BufferedImage(
                Integer.parseInt(ass.getResX()),
                Integer.parseInt(ass.getResY()),
                BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = bi.createGraphics();
        
        // On itère sur les événements
        // TODO Centrage et marges + couches
        for(Event ev : now){
            GlyphInfo info = GlyphInfo.create(ass, ev);            
            
            for(int i=0; i<info.getWords().size(); i++){
                Point2D pos = info.getAlign().getXY(
                        new Dimension(
                                (int)info.getMetrics().get(i).getBounds2D().getX(),
                                (int)info.getMetrics().get(i).getBounds2D().getY()
                        )
                );
                g2d.drawImage(info.getImage(), (int)pos.getX() / 2, (int)pos.getY(), null);
            }            
        }
        
        g2d.dispose();
        
        return bi;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panControls = new javax.swing.JPanel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        tfDurTime = new javax.swing.JTextField();
        tfDurFrame = new javax.swing.JTextField();
        tfCurTime = new javax.swing.JTextField();
        tfCurFrame = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        panTimeline = new javax.swing.JPanel();
        panVideo = new javax.swing.JPanel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Video FX");

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
        jToolBar2.add(btnPlay);

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs pause.png"))); // NOI18N
        btnPause.setText("Pause");
        btnPause.setFocusable(false);
        btnPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnPause);

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs stop.png"))); // NOI18N
        btnStop.setText("Stop");
        btnStop.setFocusable(false);
        btnStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnStop);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panControlsLayout = new javax.swing.GroupLayout(panControls);
        panControls.setLayout(panControlsLayout);
        panControlsLayout.setHorizontalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfStartTime)
                    .addComponent(tfStartFrame))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfEndTime)
                    .addComponent(tfEndFrame))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfDurFrame)
                    .addComponent(tfDurTime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfCurTime)
                    .addComponent(tfCurFrame))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panControlsLayout.setVerticalGroup(
            panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDurTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCurTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfCurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panTimeline.setLayout(new java.awt.BorderLayout());

        panVideo.setBackground(new java.awt.Color(102, 153, 255));
        panVideo.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panTimeline, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel panControls;
    private javax.swing.JPanel panTimeline;
    private javax.swing.JPanel panVideo;
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
