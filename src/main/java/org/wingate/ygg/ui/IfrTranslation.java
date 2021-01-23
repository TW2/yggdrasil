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
package org.wingate.ygg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.EventListenerList;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.subs.ASS;
import org.wingate.ygg.subs.AssEvent;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.translate.ISO_3166_Panel;
import org.wingate.ygg.translate.IVideoTranslate;
import org.wingate.ygg.translate.VideoTranslateListener;
import org.wingate.ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class IfrTranslation extends javax.swing.JInternalFrame {

    private ASS ass = null;
    private long msAreaStart = 0L;
    private long msAreaStop = 0L;
    private final PlayAudio playAudio = new PlayAudio();
    private final PlayVideo playVideo = new PlayVideo();
    
    // Spécifique à la ligne d'événement active du fichier de sous-titres
    private AssEvent ev = null;        // Evénement en cours
    private int index = 0;          // Numéro de l'événement en cours
    
    // Modèles
    private final DefaultComboBoxModel dcbmOriginal = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmSource = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmNew = new DefaultComboBoxModel();
    
    // Langue en cours
    private ISO_3166 currentLanguage = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    
    // Langues ajoutées pour la traduction
    private final List<ISO_3166> enabledLanguages = new ArrayList<>();
    private final List<ISO_3166_Panel> panels = new ArrayList<>();
    
    // Ce qui est réellement en cours de traduction (sauvegarde de phrases)
    private final Map<ISO_3166, String> sentences = new HashMap<>();
    
    // Lecture vidéo
    private final VideoPanel videoPanel = new VideoPanel();
    
    /**
     * Creates new form IfrTranslation
     */
    public IfrTranslation() {
        initComponents();
        init();
    }

    private void init(){
        // Assignation des modèles
        comboOriginal.setModel(dcbmOriginal);
        comboSourceDictionary.setModel(dcbmSource);
        comboNewDictionary.setModel(dcbmNew);
        
        // Remplissage des modèles
        for(ISO_3166 iso : ISO_3166.values()){
            dcbmOriginal.addElement(iso);
        }
        for(ISO_3166 iso : ISO_3166.values()){
            dcbmSource.addElement(iso);
        }
        for(ISO_3166 iso : ISO_3166.values()){
            dcbmNew.addElement(iso);
        }
        
        // Sélection de la langue
        comboOriginal.setSelectedItem(ISO_3166.getISO_3166(Locale.getDefault().getISO3Country()));
        comboSourceDictionary.setSelectedItem(ISO_3166.getISO_3166(Locale.getDefault().getISO3Country()));
        comboNewDictionary.setSelectedItem(ISO_3166.getISO_3166(Locale.getDefault().getISO3Country()));
        
        // Vidéo
        panEmbeddedVideo.setLayout(new BorderLayout());
        panEmbeddedVideo.add(videoPanel, BorderLayout.CENTER);
        panEmbeddedVideo.setPreferredSize(new Dimension(310, 184));
        
        addVideoTranslateListener(new VideoTranslateListener() {
            @Override
            public void getImage(BufferedImage image) {
                videoPanel.updateImage(image);
            }
        });
    }
    
    public void loadASS(File assFile){
        ass = ASS.Read(assFile.getPath());
        if(ass.getEvents().isEmpty() == false){
            ev = ass.getEvents().get(0);
            lblOriginalText.setText(ev.getText());
        }
    }
    
    public void loadAudio(File audioFile){
        try {
            playAudio.setAudio(audioFile);
            playVideo.setVideo(audioFile);
        } catch (FrameGrabber.Exception | LineUnavailableException ex) {
            Logger.getLogger(IfrTranslation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveSubtitles(File folder){
        
    }
    
    public void playArea(){
        if(ev != null && playAudio.getAction() != Action.None
                 && playVideo.getAction() != Action.None){
            msAreaStart = Time.toMillisecondsTime(ev.getStartTime());
            msAreaStop = Time.toMillisecondsTime(ev.getEndTime());

            playAudio.playStopAudio();
            playVideo.playStopVideo();
        }        
    }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgDictionaries = new javax.swing.ButtonGroup();
        tabbedSubs = new javax.swing.JTabbedPane();
        jToolBar1 = new javax.swing.JToolBar();
        btnNewTab = new javax.swing.JButton();
        btnRemoveTab = new javax.swing.JButton();
        btnAddTranslation = new javax.swing.JButton();
        btnModifyTranslation = new javax.swing.JButton();
        btnRemoveTranslation = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPlayArea = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnPrevious = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        panContainer = new javax.swing.JPanel();
        panVideo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblOriginalText = new javax.swing.JLabel();
        comboOriginal = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpOriginal = new javax.swing.JTextPane();
        panEmbeddedVideo = new javax.swing.JPanel();
        panDictionary = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        comboSourceDictionary = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        comboNewDictionary = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tpDictionary = new javax.swing.JTextPane();
        panDictionaries = new javax.swing.JPanel();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnNewTab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_plus.png"))); // NOI18N
        btnNewTab.setText("New tab");
        btnNewTab.setFocusable(false);
        btnNewTab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewTab.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewTabActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNewTab);

        btnRemoveTab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_minus.png"))); // NOI18N
        btnRemoveTab.setText("Remove tab");
        btnRemoveTab.setFocusable(false);
        btnRemoveTab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveTab.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveTabActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveTab);

        btnAddTranslation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32 accept.png"))); // NOI18N
        btnAddTranslation.setText("Apply");
        btnAddTranslation.setFocusable(false);
        btnAddTranslation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTranslation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddTranslation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTranslationActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddTranslation);

        btnModifyTranslation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32 modify.png"))); // NOI18N
        btnModifyTranslation.setText("Modify");
        btnModifyTranslation.setFocusable(false);
        btnModifyTranslation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnModifyTranslation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnModifyTranslation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyTranslationActionPerformed(evt);
            }
        });
        jToolBar1.add(btnModifyTranslation);

        btnRemoveTranslation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cross-small.png"))); // NOI18N
        btnRemoveTranslation.setText("Remove");
        btnRemoveTranslation.setFocusable(false);
        btnRemoveTranslation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRemoveTranslation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveTranslation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveTranslationActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveTranslation);
        jToolBar1.add(jSeparator1);

        btnPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnPlayArea.setText("Play area");
        btnPlayArea.setFocusable(false);
        btnPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAreaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayArea);
        jToolBar1.add(jSeparator2);

        btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_previous.png"))); // NOI18N
        btnPrevious.setText("Previous");
        btnPrevious.setFocusable(false);
        btnPrevious.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrevious.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviousActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrevious);

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_next.png"))); // NOI18N
        btnNext.setText("Next");
        btnNext.setFocusable(false);
        btnNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNext);

        panContainer.setLayout(new java.awt.GridLayout(1, 0));

        panVideo.setBorder(javax.swing.BorderFactory.createTitledBorder("Subtitles View and Write"));

        jLabel1.setText("Scene :");

        jLabel3.setText("Text :");

        lblOriginalText.setText("Waiting for text");

        comboOriginal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboOriginalActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tpOriginal.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tpOriginal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tpOriginalKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tpOriginal);

        panEmbeddedVideo.setBackground(new java.awt.Color(0, 153, 255));
        panEmbeddedVideo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panEmbeddedVideoLayout = new javax.swing.GroupLayout(panEmbeddedVideo);
        panEmbeddedVideo.setLayout(panEmbeddedVideoLayout);
        panEmbeddedVideoLayout.setHorizontalGroup(
            panEmbeddedVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 308, Short.MAX_VALUE)
        );
        panEmbeddedVideoLayout.setVerticalGroup(
            panEmbeddedVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panVideoLayout = new javax.swing.GroupLayout(panVideo);
        panVideo.setLayout(panVideoLayout);
        panVideoLayout.setHorizontalGroup(
            panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panVideoLayout.createSequentialGroup()
                .addGroup(panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panVideoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panVideoLayout.createSequentialGroup()
                        .addComponent(panEmbeddedVideo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(comboOriginal, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblOriginalText, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panVideoLayout.setVerticalGroup(
            panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panVideoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panVideoLayout.createSequentialGroup()
                        .addComponent(lblOriginalText, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addComponent(panEmbeddedVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panContainer.add(panVideo);

        panDictionary.setBorder(javax.swing.BorderFactory.createTitledBorder("Dictionary"));
        panDictionary.setPreferredSize(new java.awt.Dimension(300, 241));

        jLabel5.setText("Source :");

        comboSourceDictionary.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("New :");

        comboNewDictionary.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tpDictionary.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jScrollPane2.setViewportView(tpDictionary);

        javax.swing.GroupLayout panDictionariesLayout = new javax.swing.GroupLayout(panDictionaries);
        panDictionaries.setLayout(panDictionariesLayout);
        panDictionariesLayout.setHorizontalGroup(
            panDictionariesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panDictionariesLayout.setVerticalGroup(
            panDictionariesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panDictionaryLayout = new javax.swing.GroupLayout(panDictionary);
        panDictionary.setLayout(panDictionaryLayout);
        panDictionaryLayout.setHorizontalGroup(
            panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDictionaryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(panDictionaryLayout.createSequentialGroup()
                        .addGroup(panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboSourceDictionary, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboNewDictionary, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(panDictionaries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panDictionaryLayout.setVerticalGroup(
            panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDictionaryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(comboSourceDictionary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panDictionaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(comboNewDictionary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panDictionaries, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addContainerGap())
        );

        panContainer.add(panDictionary);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedSubs)
            .addComponent(panContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabbedSubs, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTabActionPerformed
        ISO_3166 iso = (ISO_3166)comboOriginal.getSelectedItem();
        if(enabledLanguages.contains(iso) == false && ev != null){
            enabledLanguages.add(iso);
            ISO_3166_Panel isop = new ISO_3166_Panel();
            isop.setIso3166(iso);
            panels.add(isop);
            tabbedSubs.addTab(iso.getCountry(), isop);
            if(tpOriginal.getText().isEmpty() == false){
                AssEvent nv = ev.getCopy();
                nv.setText(tpOriginal.getText());
                isop.getAssEventTableModel().insertOne(nv);
                isop.getTable().updateUI();
                isop.getScrollPane().updateUI();
            }            
        }
    }//GEN-LAST:event_btnNewTabActionPerformed

    private void btnRemoveTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveTabActionPerformed
        if(tabbedSubs.getSelectedIndex() != -1){            
            int selected = tabbedSubs.getSelectedIndex();
            ISO_3166 iso = ISO_3166.getISO_3166(tabbedSubs.getTitleAt(selected));
            
            int isoIndex = -1;
            ISO_3166_Panel pane = null;
            for(int i=0; i<panels.size(); i++){
                pane = panels.get(i);
                if(pane.getIso3166().equals(iso) == true){
                    isoIndex = i;
                    break;
                }
            }
            
            if(isoIndex != -1 && pane != null){
                enabledLanguages.remove(iso);
                panels.remove(isoIndex);
                tabbedSubs.remove(pane);
            }
        }
    }//GEN-LAST:event_btnRemoveTabActionPerformed

    private void btnAddTranslationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTranslationActionPerformed
        if(ev == null) return;

        // Cherche les tableaux correspondants
        for(ISO_3166_Panel pane : panels){
            // Crée une copie d'un événement (nouveau)
            AssEvent nv = ev.getCopy();
            
            // Entre une phrase dans cette copie
            nv.setText(sentences.get(pane.getIso3166()));
            
            // Assigne cette copie au tableau correspondant
            pane.getAssEventTableModel().insertOne(nv);
            pane.getTable().updateUI();
            pane.getScrollPane().updateUI();
        }
    }//GEN-LAST:event_btnAddTranslationActionPerformed

    private void btnPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAreaActionPerformed
        playArea();
    }//GEN-LAST:event_btnPlayAreaActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        if(ass != null && ass.getEvents().isEmpty() == false){
            index = index - 1 > 0 ? index - 1 : 0;
            ev = ass.getEvents().get(index);
            lblOriginalText.setText(ev.getText());
        }
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if(ass != null && ass.getEvents().isEmpty() == false){
            index = index + 1 < ass.getEvents().size() - 1 ? index + 1 : ass.getEvents().size() - 1;
            ev = ass.getEvents().get(index);
            lblOriginalText.setText(ev.getText());
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void tpOriginalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tpOriginalKeyReleased
        // Obtient la langue en cours
        currentLanguage = (ISO_3166)comboOriginal.getSelectedItem();
        
        // Sauvegarde la phrase en cours dans la langue désirée
        sentences.put(currentLanguage, tpOriginal.getText());
    }//GEN-LAST:event_tpOriginalKeyReleased

    private void comboOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboOriginalActionPerformed
        // Sauvegarde la phrase d'avant dans la langue désirée
        sentences.put(currentLanguage, tpOriginal.getText());
        
        // Restitue la  phrase enregistrée ssi
        currentLanguage = (ISO_3166)comboOriginal.getSelectedItem();
        if(sentences.containsKey(currentLanguage) == true){
            tpOriginal.setText(sentences.get(currentLanguage));
        }
    }//GEN-LAST:event_comboOriginalActionPerformed

    private void btnModifyTranslationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyTranslationActionPerformed
        // Vérification d'état
        if(tabbedSubs.getSelectedComponent() == null | ev == null) return;

        // Pour la sélection unique de la ligne
        ISO_3166_Panel selectedPane = (ISO_3166_Panel)tabbedSubs.getSelectedComponent();
        
        // Identification de la ligne sélectionnée dans le tableau sélectionné
        int selectedRow = selectedPane.getTable().getSelectedRow();

        // Cherche les tableaux correspondants
        for(ISO_3166_Panel pane : panels){
            // Crée une copie d'un événement (nouveau)
            AssEvent nv = ev.getCopy();
            
            // Entre une phrase dans cette copie
            nv.setText(sentences.get(pane.getIso3166()));
            
            // Assigne cette copie au tableau correspondant
            if(selectedRow != -1){
                pane.getAssEventTableModel().changeEventAt(nv, selectedRow);
                pane.getTable().updateUI();
                pane.getScrollPane().updateUI();
            }            
        }
    }//GEN-LAST:event_btnModifyTranslationActionPerformed

    private void btnRemoveTranslationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveTranslationActionPerformed
        // Vérification d'état
        if(tabbedSubs.getSelectedComponent() == null | ev == null) return;
        
        // Pour la sélection unique de la ligne
        ISO_3166_Panel selectedPane = (ISO_3166_Panel)tabbedSubs.getSelectedComponent();
        
        // Identification de la ligne sélectionnée dans le tableau sélectionné
        int selectedRow = selectedPane.getTable().getSelectedRow();
        
        // Cherche les tableaux correspondants
        for(ISO_3166_Panel pane : panels){
            // Enlève l'événement du tableau correspondant
            if(selectedRow != -1){
                pane.getAssEventTableModel().removeOne(selectedRow);
                pane.getTable().updateUI();
                pane.getScrollPane().updateUI();
            }            
        }
    }//GEN-LAST:event_btnRemoveTranslationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDictionaries;
    private javax.swing.JButton btnAddTranslation;
    private javax.swing.JButton btnModifyTranslation;
    private javax.swing.JButton btnNewTab;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPlayArea;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnRemoveTab;
    private javax.swing.JButton btnRemoveTranslation;
    private javax.swing.JComboBox<String> comboNewDictionary;
    private javax.swing.JComboBox<String> comboOriginal;
    private javax.swing.JComboBox<String> comboSourceDictionary;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblOriginalText;
    private javax.swing.JPanel panContainer;
    private javax.swing.JPanel panDictionaries;
    private javax.swing.JPanel panDictionary;
    private javax.swing.JPanel panEmbeddedVideo;
    private javax.swing.JPanel panVideo;
    private javax.swing.JTabbedPane tabbedSubs;
    private javax.swing.JTextPane tpDictionary;
    private javax.swing.JTextPane tpOriginal;
    // End of variables declaration//GEN-END:variables

    public enum Action {
        None, Ready, Play, Pause, Stop;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Lecture audio">
    
    class PlayAudio implements Runnable {
    
        private volatile FFmpegFrameGrabber grabber = null;
        private volatile SourceDataLine soundLine = null;
        private volatile ExecutorService executor = Executors.newSingleThreadExecutor();

        private volatile Action action = Action.None;

        private Thread thAct = null;

        private boolean area = false;

        public PlayAudio() {
            init();
        }
        
        private void init(){
            startThread();
        }
        
        public void setAudio(File audio) throws FrameGrabber.Exception, LineUnavailableException{
            // Vérification de la présence de fichier existant
            if(audio.exists() == false){
                action = Action.None;
                return;
            }
            
            // Clôture de la ligne du son si elle existe (lors d'un précédent passage ici)
            if(soundLine != null){
                soundLine.stop();
                soundLine.flush();
                soundLine.drain();
                soundLine.close();
                soundLine = null;
            }
            
            // Clôture du composant FFMpeg s'il existe (lors d'un précédent passage ici)
            if(grabber != null){
                grabber.stop();
                grabber.release();
                grabber = null;
            }
            
            // Lancement du composant FFMpeg (nouveau)
            grabber = new FFmpegFrameGrabber(audio);
            grabber.start();

            // Définition du format audio pour lire le son
            AudioFormat audioFormat = new AudioFormat(
                    grabber.getSampleRate(), 
                    16, 
                    grabber.getAudioChannels(), 
                    true, 
                    true
            );

            // Ouverture d'un processus audio pour le son
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();

            // Renseignement de l'état de l'action (état de lecture)
            action = Action.Ready;
        }
        
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
                Logger.getLogger(PlayAudio.class.getName()).log(Level.SEVERE, null, ex);
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

                        org.bytedeco.javacv.Frame frame = grabber.grab();

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
                            
                        }

                        if(frame.timestamp / 1000L >= msAreaStop){
                            action = Action.Ready;
                            msAreaStart = 0L;
                            msAreaStop = 0L;
                        }
                    } catch (FrameGrabber.Exception ex) {
                        Logger.getLogger(PlayAudio.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        public void playStopAudio(){
            area = true;
            switch(action){
                case Ready -> action = Action.Play;
                case Play -> action = Action.Pause;
                case Pause -> action = Action.Play;
                case Stop -> action = Action.Play;
            }            
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Lecture vidéo">
    
    class PlayVideo implements Runnable {
    
        private volatile FFmpegFrameGrabber grabber = null;
        private volatile long oldNanos = 0L;

        private volatile Action action = Action.None;

        private Thread thAct = null;

        private boolean area = false;
        
        private final Java2DFrameConverter converter = new Java2DFrameConverter();

        public PlayVideo() {
            init();
        }
        
        private void init(){
            startThread();
        }
        
        public void setVideo(File video) throws FrameGrabber.Exception, LineUnavailableException{
            // Vérification de la présence de fichier existant
            if(video.exists() == false){
                action = Action.None;
                return;
            }
            
            // Clôture du composant FFMpeg s'il existe (lors d'un précédent passage ici)
            if(grabber != null){
                grabber.stop();
                grabber.release();
                grabber = null;
            }
            
            // Lancement du composant FFMpeg (nouveau)
            grabber = new FFmpegFrameGrabber(video);
            grabber.start();

            // Renseignement de l'état de l'action (état de lecture)
            action = Action.Ready;
            System.out.println("Action : " + action.toString());
        }
        
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
        
        @Override
        public void run() {
            while(true){
                if(action == Action.Play){
                    try {
                        if(msAreaStart != 0L && area == true){
                            grabber.setVideoTimestamp(msAreaStart * 1000L);
                            oldNanos = msAreaStart * 1000L;
                            area = false;
                        }

                        org.bytedeco.javacv.Frame frame = grabber.grab();

                        if (frame == null) {
                            action = Action.None;
                            continue;
                        }
                        
                        System.out.println("1");
                        if(frame.image != null){
                            System.out.println("2");
                            fireVideoTranslate(converter.convert(frame));
                            
                            try {
                                TimeUnit.NANOSECONDS.sleep(
                                        Math.round(1L / grabber.getVideoFrameRate() * Math.pow(10, 9)));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IfrTranslation.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if(frame.timestamp / 1000L >= msAreaStop){
                            action = Action.Ready;
                            msAreaStart = 0L;
                            msAreaStop = 0L;
                        }
                    } catch (FrameGrabber.Exception ex) {
                        Logger.getLogger(PlayVideo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        public void playStopVideo(){
            area = true;
            switch(action){
                case Ready -> action = Action.Play;
                case Play -> action = Action.Pause;
                case Pause -> action = Action.Play;
                case Stop -> action = Action.Play;
            }            
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }
        
        public void updateImage(BufferedImage image){
            videoPanel.updateImage(image);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Composant vidéo">
    
    class VideoPanel extends javax.swing.JPanel {

        private BufferedImage img = null;
        private BufferedImage sub = null; // For subtitles
        private boolean darkMode = false;
        
        public VideoPanel() {            
            init();
        }
        
        private void init(){
            setDoubleBuffered(true);
        }

        public void setDarkMode(boolean darkMode) {
            this.darkMode = darkMode;
        }
        
        public void updateImage(BufferedImage img, BufferedImage sub){
            this.img = img;
            this.sub = sub;
            repaint();
        }
        
        public void updateImage(BufferedImage img){
            this.img = img;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            if(img != null){
                g.setColor(darkMode == true ? Color.gray : DrawColor.alice_blue.getColor());
                g.fillRect(0, 0, getWidth(), getHeight());

                Dimension dim = getScaledDimension(
                        new Dimension(img.getWidth(), img.getHeight()), 
                        new Dimension(getWidth(), getHeight())
                );

                int x = (getWidth() - dim.width) / 2;
                int y = (getHeight() - dim.height) / 2;
                g.drawImage(img, x, y, dim.width, dim.height, null);
            }
            
            if(sub != null){
                Dimension dim = getScaledDimension(
                        new Dimension(sub.getWidth(), sub.getHeight()), 
                        new Dimension(getWidth(), getHeight())
                );

                int x = (getWidth() - dim.width) / 2;
                int y = (getHeight() - dim.height) / 2;
                g.drawImage(sub, x, y, dim.width, dim.height, null);
            }
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
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Evénements">
    
    private final EventListenerList listeners = new EventListenerList();
    
    public void addVideoTranslateListener(IVideoTranslate listener) {
        listeners.add(VideoTranslateListener.class, (VideoTranslateListener)listener);
    }

    public void removeVideoTranslateListener(IVideoTranslate listener) {
        listeners.remove(VideoTranslateListener.class, (VideoTranslateListener)listener);
    }

    public Object[] getListeners() {
        return listeners.getListenerList();
    }
    
    protected void fireVideoTranslate(BufferedImage image) {
        for(Object o : getListeners()){
            if(o instanceof VideoTranslateListener){
                VideoTranslateListener listen = (VideoTranslateListener)o;
                listen.getImage(image);
                break;
            }
        }
    }
    
    // </editor-fold>
}
