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

import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.filechooser.FileFilter;
import org.bytedeco.javacv.FrameGrabber;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.audiovideo.AVInfo;
import org.wingate.ygg.audiovideo.AudioPanel;
import org.wingate.ygg.audiovideo.AudioRender;
import org.wingate.ygg.audiovideo.PlayAudio;
import org.wingate.ygg.io.VideoFileChooserFileFilter;

/**
 *
 * @author util2
 */
public class IfrWave extends javax.swing.JInternalFrame implements Runnable {

    // Lire le son
    private final PlayAudio playAudio = new PlayAudio();
    
    // Faire des images en forme d'onde ou spectrogramme
    private final AudioRender audioRender = new AudioRender();
    
    // Montrer les images
    private final AudioPanel audioPanel = new AudioPanel();
    
    // Echelle
    private final long msPerImage = 10000L;
    
    // Conserve le fichier audio
    private File media = null;
    
    // Conserve la base d'infos
    private AVInfo info = null;
    
    // Etat en transit des keyframes que l'on doit passer à la forme d'onde/spectrogramme
    private Map<Integer, Long> keyframes = new HashMap<>();
    
    // Etat de la boucle de création d'images de forme d'onde et de spectrograme
    private volatile boolean ended = true;
    private volatile int counter = 0;
    
    // Contrôle de la tâche
    private Thread th = null;
    
    /**
     * Creates new form IfrWave
     */
    public IfrWave() {
        initComponents();
        init();
    }
    
    private void init(){
        for(FileFilter ff : fcAV.getChoosableFileFilters()){
            fcAV.removeChoosableFileFilter(ff);
        }
        fcAV.addChoosableFileFilter(new VideoFileChooserFileFilter());        
        
        panGraph.setSize(920, 135);
        audioPanel.setSize(920, 135);
        panGraph.add(audioPanel);
        
        scGraph.setMinimum(0);
        scGraph.setMaximum(1000000);
        scGraph.addAdjustmentListener((AdjustmentEvent e) -> {
            audioPanel.setCurrentScrolledMilliseconds(e.getValue());
        });
        panGraph.addMouseWheelListener((MouseWheelEvent e) -> {
            scGraph.setValue(e.getWheelRotation() < 0 ? 
                    scGraph.getValue() + 100 : 
                    scGraph.getValue() - 100);
        });
        
        audioPanel.setWave(this);
        
        updateUI();
        
        th = new Thread(this);
        th.start();
    }
    
    public void stopThread(){
        if(th != null){
            th.interrupt();
            th = null;
        }
    }
    
    public void openAudio(File audio){
        this.media = audio;
        try {
            playAudio.setAudio(audio);
            info = new AVInfo(audio);
            audioPanel.setInfo(info, 1000000, 920);
            //==================================================================
            // On relance la tâche de création de forme d'onde et spectrogramme
            //------------------------------------------------------------------
            counter = 0;
            ended = false;
            //------------------------------------------------- FIN ONDE SPECTRO
        } catch (FrameGrabber.Exception | LineUnavailableException ex) {
            Logger.getLogger(IfrWave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void playAudioAndStop(Time from, Time to){
        if(playAudio.getGrabber() != null){
            playAudio.setMsAreaStart(Time.toMillisecondsTime(from));
            playAudio.setMsAreaStop(Time.toMillisecondsTime(to));
            playAudio.setAction(PlayAudio.Action.Ready);
            playAudio.playStopAudio();
        }
    }
    
    private boolean createImages(int counter){
        if(info != null){
            long frMs = Math.round(msPerImage * counter);
            long toMs = Math.round(msPerImage * (counter + 1));
            long drMs = info.getDuration() / 1000L;
            Time from = Time.create(frMs);
            Time to = Time.create(toMs);
            int w = 920;
            int h = 135;
            if(toMs > drMs){
                long diff = drMs - frMs;
                long total = msPerImage;
                w = (int)Math.round(920 * diff / total);
                to = Time.create(drMs);
            }
            audioRender.renderWaveform(media, w, h, from, to);
            audioRender.renderSpectrogram(media, w, h, from, to);
            audioPanel.repaint();
            return toMs > drMs;
        }
        return false;
    }
    
    public PlayAudio getPlayAudio(){
        return playAudio;
    }

    public Map<Integer, Long> getKeyframes() {
        return keyframes;
    }

    public void setKeyframes(Map<Integer, Long> keyframes) {
        this.keyframes = keyframes;
        audioPanel.setKeyframes(keyframes);
    }
    
    public void setArea(Time start, Time end){
        audioPanel.setArea(start, end);
    }

    public AVInfo getInfo() {
        return info;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcAV = new javax.swing.JFileChooser();
        paneWave = new javax.swing.JPanel();
        panGraph = new javax.swing.JPanel();
        scGraph = new javax.swing.JScrollBar();
        tabbedWave = new javax.swing.JTabbedPane();
        paneCtrl = new javax.swing.JPanel();
        toolBarCtrl = new javax.swing.JToolBar();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPlayBefore = new javax.swing.JButton();
        btnPlayBegin = new javax.swing.JButton();
        btnPlayArea = new javax.swing.JButton();
        btnPlayEnd = new javax.swing.JButton();
        btnPlayAfter = new javax.swing.JButton();
        panKaraoke = new javax.swing.JPanel();
        panSignal = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        cbSpectrum = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();

        setMaximizable(true);
        setResizable(true);
        setTitle("Audio");

        paneWave.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panGraphLayout = new javax.swing.GroupLayout(panGraph);
        panGraph.setLayout(panGraphLayout);
        panGraphLayout.setHorizontalGroup(
            panGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 883, Short.MAX_VALUE)
        );
        panGraphLayout.setVerticalGroup(
            panGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 135, Short.MAX_VALUE)
        );

        paneWave.add(panGraph, java.awt.BorderLayout.CENTER);

        scGraph.setOrientation(javax.swing.JScrollBar.HORIZONTAL);

        toolBarCtrl.setFloatable(false);
        toolBarCtrl.setRollover(true);

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
        toolBarCtrl.add(btnPlay);

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
        toolBarCtrl.add(btnPause);

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
        toolBarCtrl.add(btnStop);
        toolBarCtrl.add(jSeparator1);

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
        toolBarCtrl.add(btnPlayBefore);

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
        toolBarCtrl.add(btnPlayBegin);

        btnPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnPlayArea.setText("At time");
        btnPlayArea.setFocusable(false);
        btnPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAreaActionPerformed(evt);
            }
        });
        toolBarCtrl.add(btnPlayArea);

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
        toolBarCtrl.add(btnPlayEnd);

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
        toolBarCtrl.add(btnPlayAfter);

        javax.swing.GroupLayout paneCtrlLayout = new javax.swing.GroupLayout(paneCtrl);
        paneCtrl.setLayout(paneCtrlLayout);
        paneCtrlLayout.setHorizontalGroup(
            paneCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBarCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
        );
        paneCtrlLayout.setVerticalGroup(
            paneCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneCtrlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toolBarCtrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedWave.addTab("Time", paneCtrl);

        javax.swing.GroupLayout panKaraokeLayout = new javax.swing.GroupLayout(panKaraoke);
        panKaraoke.setLayout(panKaraokeLayout);
        panKaraokeLayout.setHorizontalGroup(
            panKaraokeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 883, Short.MAX_VALUE)
        );
        panKaraokeLayout.setVerticalGroup(
            panKaraokeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 76, Short.MAX_VALUE)
        );

        tabbedWave.addTab("Karaoke", panKaraoke);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        cbSpectrum.setText("Spectrum");
        cbSpectrum.setFocusable(false);
        cbSpectrum.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cbSpectrum.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cbSpectrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSpectrumActionPerformed(evt);
            }
        });
        jToolBar1.add(cbSpectrum);
        jToolBar1.add(jSeparator2);

        jLabel1.setText("Scale on X :");
        jToolBar1.add(jLabel1);
        jToolBar1.add(jSlider1);
        jToolBar1.add(jSeparator3);

        jLabel2.setText("Scale on Y :");
        jToolBar1.add(jLabel2);
        jToolBar1.add(jSlider2);

        javax.swing.GroupLayout panSignalLayout = new javax.swing.GroupLayout(panSignal);
        panSignal.setLayout(panSignalLayout);
        panSignalLayout.setHorizontalGroup(
            panSignalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
        );
        panSignalLayout.setVerticalGroup(
            panSignalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSignalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        tabbedWave.addTab("Signal", panSignal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedWave)
            .addComponent(paneWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(paneWave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabbedWave, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        playAudio.playStopAudio();
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        playAudio.playStopAudio();
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        playAudio.setAction(PlayAudio.Action.Stop);
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeforeActionPerformed
        Time real_start = audioPanel.getStart();
        Time start = Time.substract(real_start, Time.create(500L));
        playAudioAndStop(start, real_start);
    }//GEN-LAST:event_btnPlayBeforeActionPerformed

    private void btnPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayBeginActionPerformed
        Time real_start = audioPanel.getStart();
        Time end = Time.addition(real_start, Time.create(500L));
        playAudioAndStop(real_start, end);
    }//GEN-LAST:event_btnPlayBeginActionPerformed

    private void btnPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAreaActionPerformed
        Time real_start = audioPanel.getStart();
        Time real_end = audioPanel.getEnd();
        playAudioAndStop(real_start, real_end);
    }//GEN-LAST:event_btnPlayAreaActionPerformed

    private void btnPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayEndActionPerformed
        Time real_end = audioPanel.getEnd();
        Time start = Time.substract(real_end, Time.create(500L));            
        playAudioAndStop(start, real_end);
    }//GEN-LAST:event_btnPlayEndActionPerformed

    private void btnPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAfterActionPerformed
        Time real_end = audioPanel.getEnd();
        Time end = Time.addition(real_end, Time.create(500L));            
        playAudioAndStop(real_end, end);
    }//GEN-LAST:event_btnPlayAfterActionPerformed

    private void cbSpectrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSpectrumActionPerformed
        audioPanel.setShowSpectrum(cbSpectrum.isSelected());
    }//GEN-LAST:event_cbSpectrumActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnPlayAfter;
    private javax.swing.JButton btnPlayArea;
    private javax.swing.JButton btnPlayBefore;
    private javax.swing.JButton btnPlayBegin;
    private javax.swing.JButton btnPlayEnd;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox cbSpectrum;
    private javax.swing.JFileChooser fcAV;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panGraph;
    private javax.swing.JPanel panKaraoke;
    private javax.swing.JPanel panSignal;
    private javax.swing.JPanel paneCtrl;
    private javax.swing.JPanel paneWave;
    private javax.swing.JScrollBar scGraph;
    private javax.swing.JTabbedPane tabbedWave;
    private javax.swing.JToolBar toolBarCtrl;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        while (true){
            while(ended == false){
                ended = createImages(counter);
                counter++;
            }
        }
    }
}
