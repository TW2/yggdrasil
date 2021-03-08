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
package org.wingate.ygg.ui.synctable;

import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.subs.ass.AssStyle;
import org.wingate.ygg.ui.IfrTable;
import org.wingate.ygg.ui.PropsDialog;
import org.wingate.ygg.ui.StylesDialog;

/**
 *
 * @author util2
 */
public class AssLinkPanel extends javax.swing.JPanel {

    private final ASS ass = ASS.NoFileToLoad();
    
    // ifrAssSubCommands components and variables
    private final SpinnerNumberModel snmLayer = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmLeft = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmRight = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmVertical = new SpinnerNumberModel(0, 0, 100000, 1);
    private final DefaultComboBoxModel dcbmStyle = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmName = new DefaultComboBoxModel();    
    private float tpTextFontSize;
    private Font tpTextNormalFont;
    private final float tpTextScaleMin = 1f;
    private final float tpTextScaleMax = 5f;
    private float tpTextScaleCur = 1f;
    // ifrAssSubCommands stop
    
    public AssLinkPanel() {
        initComponents();
        init();
    }
    
    private void init(){        
        // ASS Style Commands
        spinLayer.setModel(snmLayer);
        spinL.setModel(snmLeft);
        spinR.setModel(snmRight);
        spinV.setModel(snmVertical);
        comboStyle.setModel(dcbmStyle);
        comboName.setModel(dcbmName);
        tpTextFontSize = tpText.getFont().getSize2D();
        tpTextNormalFont = tpText.getFont();
        tpText.addMouseWheelListener((MouseWheelEvent e) -> {
            if(e.isControlDown()){
                if(e.getPreciseWheelRotation() < 0){
                    // On augmente la taille
                    tpTextScaleCur += 0.2f;
                    tpTextScaleCur = Math.min(tpTextScaleMax, tpTextScaleCur);
                    float fontSize = tpTextScaleCur * tpTextFontSize;
                    tpText.setFont(tpTextNormalFont.deriveFont(fontSize));
                }
                if(e.getPreciseWheelRotation() > 0){
                    // On réduit la taille
                    tpTextScaleCur -= 0.2f;
                    tpTextScaleCur = Math.max(tpTextScaleMin, tpTextScaleCur);
                    float fontSize = tpTextScaleCur * tpTextFontSize;
                    tpText.setFont(tpTextNormalFont.deriveFont(fontSize));
                }
            }
        });
        initAssComboStyle();
        initAssComboName();
    }
    
    public void alter(AssEvent ev){
        // Type de ligne
        switch(ev.getLineType()){
            case Dialogue -> toggleDialogue.setSelected(true);
            case Comment -> toggleComment.setSelected(true);
            case Proposal -> toggleProposal.setSelected(true);
            case Request -> toggleRequest.setSelected(true);      }
        
        // Couche
        snmLayer.setValue(ev.getLayer());
        
        // Start - End - Duration
        Time startTime, endTime, duration;
        startTime = ev.getStartTime();
        endTime = ev.getEndTime();
        duration = Time.substract(startTime, endTime);
        tfStartTime.setText(startTime.toProgramExtendedTime());
        tfEndTime.setText(endTime.toProgramExtendedTime());
        tfDurationTime.setText(duration.toProgramExtendedTime());
//        try{
//            tfStartFrame.setText(Integer.toString(Time.getFrame(startTime, ffss.getFps())));
//            tfEndFrame.setText(Integer.toString(Time.getFrame(endTime, ffss.getFps())));
//            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, ffss.getFps())));
//        }catch(Exception ex){
//            
//        }        
        
        // ML
        snmLeft.setValue(ev.getMarginL());
        
        // MR
        snmRight.setValue(ev.getMarginR());
        
        // MV
        snmVertical.setValue(ev.getMarginV());
        
        // Style
        comboStyle.setSelectedItem(ev.getStyle());
        
        // Name
        comboName.setSelectedItem(ev.getName());
        
        // Effect
        tfEffects.setText(ev.getEffect());
        
        // Text
        tpText.setText(ev.getText());
    }
    
    public void updateAreaFrames(AssEvent ev){        
        Time dur = Time.substract(ev.getStartTime(), ev.getEndTime());
        
        tfStartTime.setText(ev.getStartTime().toProgramExtendedTime());
//        tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStartTime(), ffss.getFps())));
        tfEndTime.setText(ev.getEndTime().toProgramExtendedTime());
//        tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEndTime(), ffss.getFps())));
        tfDurationTime.setText(dur.toProgramExtendedTime());
//        tfDurationFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
    }
    
    public void displayEventTime(AssEvent ev){
//        int startFrame = Time.getFrame(ev.getStartTime(), ffss.getFps());
//        int endFrame = Time.getFrame(ev.getEndTime(), ffss.getFps());
//        
//        int startSamples = aw.getSamplesFromFrame(startFrame);
//        int endSamples = aw.getSamplesFromFrame(endFrame);
//        
//        Point startPoint = new Point(Math.round(startSamples / aw.getSamplesPerPixel()), 0);
//        Point stopPoint = new Point(Math.round(endSamples / aw.getSamplesPerPixel()), 0);
//        
//        aw.getAudioWavePanel().updatePoint(startPoint, stopPoint);
//        
//        try{
//            int index = aw.getAudioWavePanel().getAssKaraokeCollectionIndex(ev);
//            if(index != -1){
//                AssKaraokeCollection akc = aw.getAudioWavePanel().getAssKaraokeCollections().get(index);
//            }
//        }catch(Exception exc){
//            // Nothing to do but not mention it!
//        }
                
    }
    
    public void initAssComboStyle(){
        dcbmStyle.removeAllElements();
        ass.getStyles().entrySet().forEach((entry) -> {
            dcbmStyle.addElement(entry.getValue());
        });
    }
    
    public void initAssComboName(){
        dcbmName.removeAllElements();
        dcbmName.addElement("");
        ass.getNames().forEach((name) -> {
            dcbmName.addElement(name);
        });
    }
    
    public AssEvent getFromAssSubCommands(){
        AssEvent nv = new AssEvent();
        
        // Type
        if(toggleDialogue.isSelected()) nv.setLineType(AssEvent.LineType.Dialogue);
        if(toggleComment.isSelected()) nv.setLineType(AssEvent.LineType.Comment);
        if(toggleProposal.isSelected()) nv.setLineType(AssEvent.LineType.Proposal);
        if(toggleRequest.isSelected()) nv.setLineType(AssEvent.LineType.Request);
        
        // Layer
        nv.setLayer(snmLayer.getNumber().intValue());
        
        // Start time
        String strStart = tfStartTime.getText();
        Time tStart = new Time();
        Pattern ps = Pattern.compile("(\\d+).{1}(\\d+).{1}(\\d+).{1}(\\d+)");
        Matcher ms = ps.matcher(strStart);
        if(ms.find()){
            tStart.setHours(Integer.parseInt(ms.group(1)));
            tStart.setMinutes(Integer.parseInt(ms.group(2)));
            tStart.setSeconds(Integer.parseInt(ms.group(3)));
            tStart.setMilliseconds(Integer.parseInt(ms.group(4)));
        }
        nv.setStartTime(tStart);
        
        // End time
        String strEnd = tfEndTime.getText();
        Time tEnd = new Time();
        Pattern pe = Pattern.compile("(\\d+).{1}(\\d+).{1}(\\d+).{1}(\\d+)");
        Matcher me = pe.matcher(strEnd);
        if(me.find()){
            tEnd.setHours(Integer.parseInt(me.group(1)));
            tEnd.setMinutes(Integer.parseInt(me.group(2)));
            tEnd.setSeconds(Integer.parseInt(me.group(3)));
            tEnd.setMilliseconds(Integer.parseInt(me.group(4)));
        }
        nv.setEndTime(tEnd);
        
        // Margin L
        nv.setMarginL(snmLeft.getNumber().intValue());
        
        // Margin R
        nv.setMarginR(snmRight.getNumber().intValue());
        
        // Margin V
        nv.setMarginV(snmVertical.getNumber().intValue());
        
        // Style        
        nv.setStyle(dcbmStyle.getSize() == 0 ? new AssStyle() : (AssStyle)dcbmStyle.getSelectedItem());
        
        // Name
        nv.setName(dcbmName.getSize() == 0 ? "" : (String)dcbmName.getSelectedItem());
        
        // Effect
        nv.setEffect(tfEffects.getText());
        
        // Text
        nv.setText(tpText.getText());
        
        return nv;
    }
    
    // Refresh ASS rendering by updating ass temporary file
    private void refreshTempASS(){
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
            File folder = new File("configuration");
            if(folder.exists() == false) folder.mkdirs();
            File filepath = new File(folder, "temp.ass");        
            MainFrame.getTableFrame().save(filepath, ".ass");
            MainFrame.getVideoFrame().setSubtitlesFile(filepath);
        }        
    }
    
//    @SuppressWarnings("static-access")
//    public void loadASSTable(File f){        
//        ASS loading = ASS.Read(f.getPath());
//        table.getTableV1().removeAll();
//        table.getAssTableModel().insertAll(loading.getEvents());
//        table.getTableV1().updateUI();
//        initAssComboStyle();
//        initAssComboName();
//    }
//    
//    @SuppressWarnings("static-access")
//    public void saveASSTable(File f){
//        ASS saving = new ASS();        
//        List<AssEvent> events = table.getAssTableModel().getAllEvents();
//        saving.setEvents(events);
//        ASS.Save(f.getPath(), saving);
//    }
    
    public Map<String, AssStyle> getStyles(){
        return ass.getStyles();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgType = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        toggleDialogue = new javax.swing.JToggleButton();
        toggleComment = new javax.swing.JToggleButton();
        toggleProposal = new javax.swing.JToggleButton();
        toggleRequest = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        spinLayer = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        comboStyle = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        comboName = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        tfEffects = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        spinL = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        spinR = new javax.swing.JSpinner();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        spinV = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        btnAssProperties = new javax.swing.JButton();
        btnAssStyles = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        btnAddSubAtEnd = new javax.swing.JButton();
        btnAddSubAfter = new javax.swing.JButton();
        btnbAddSubBefore = new javax.swing.JButton();
        btnChangeSub = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfDurationTime = new javax.swing.JTextField();
        tfDurationFrame = new javax.swing.JTextField();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        bgType.add(toggleDialogue);
        toggleDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub dialogue.png"))); // NOI18N
        toggleDialogue.setSelected(true);
        toggleDialogue.setText("Dialogue");
        toggleDialogue.setFocusable(false);
        toggleDialogue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleDialogue.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleDialogue);

        bgType.add(toggleComment);
        toggleComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment.png"))); // NOI18N
        toggleComment.setText("Comment");
        toggleComment.setFocusable(false);
        toggleComment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleComment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleComment);

        bgType.add(toggleProposal);
        toggleProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment yellow.png"))); // NOI18N
        toggleProposal.setText("Proposal");
        toggleProposal.setFocusable(false);
        toggleProposal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleProposal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleProposal);

        bgType.add(toggleRequest);
        toggleRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment blue.png"))); // NOI18N
        toggleRequest.setText("Request");
        toggleRequest.setFocusable(false);
        toggleRequest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleRequest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleRequest);
        jToolBar1.add(jSeparator1);

        jPanel4.setPreferredSize(new java.awt.Dimension(228, 60));
        jPanel4.setLayout(new java.awt.GridLayout(2, 2));

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel7.setText("Layer : ");
        jLabel7.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanel5.add(jLabel7, java.awt.BorderLayout.WEST);
        jPanel5.add(spinLayer, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jLabel8.setText(" Style : ");
        jLabel8.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanel6.add(jLabel8, java.awt.BorderLayout.WEST);

        comboStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel6.add(comboStyle, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel6);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabel9.setText("Name : ");
        jLabel9.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanel7.add(jLabel9, java.awt.BorderLayout.WEST);

        comboName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboNameActionPerformed(evt);
            }
        });
        jPanel7.add(comboName, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel7);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jLabel10.setText(" Effects : ");
        jLabel10.setPreferredSize(new java.awt.Dimension(50, 16));
        jPanel8.add(jLabel10, java.awt.BorderLayout.WEST);

        tfEffects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEffectsActionPerformed(evt);
            }
        });
        jPanel8.add(tfEffects, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel8);

        jToolBar1.add(jPanel4);
        jToolBar1.add(jSeparator2);

        jPanel3.setPreferredSize(new java.awt.Dimension(60, 60));
        jPanel3.setLayout(new java.awt.GridLayout(2, 2));

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel11.setText("L : ");
        jPanel9.add(jLabel11, java.awt.BorderLayout.WEST);

        spinL.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLStateChanged(evt);
            }
        });
        jPanel9.add(spinL, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel9);

        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel12.setText(" R : ");
        jPanel10.add(jLabel12, java.awt.BorderLayout.WEST);

        spinR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRStateChanged(evt);
            }
        });
        jPanel10.add(spinR, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel10);

        jPanel11.setLayout(new java.awt.BorderLayout());

        jLabel13.setText("V : ");
        jPanel11.add(jLabel13, java.awt.BorderLayout.WEST);

        spinV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVStateChanged(evt);
            }
        });
        jPanel11.add(spinV, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel11);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        btnAssProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32 losange carré.png"))); // NOI18N
        btnAssProperties.setToolTipText("Properties");
        btnAssProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssPropertiesActionPerformed(evt);
            }
        });
        jPanel1.add(btnAssProperties);

        btnAssStyles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32 rond.png"))); // NOI18N
        btnAssStyles.setToolTipText("Styles");
        btnAssStyles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssStylesActionPerformed(evt);
            }
        });
        jPanel1.add(btnAssStyles);

        jPanel3.add(jPanel1);

        jToolBar1.add(jPanel3);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportView(tpText);

        btnAddSubAtEnd.setForeground(new java.awt.Color(0, 204, 0));
        btnAddSubAtEnd.setText("Add at end");
        btnAddSubAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSubAtEndActionPerformed(evt);
            }
        });

        btnAddSubAfter.setForeground(new java.awt.Color(0, 153, 204));
        btnAddSubAfter.setText("Add after");
        btnAddSubAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSubAfterActionPerformed(evt);
            }
        });

        btnbAddSubBefore.setForeground(new java.awt.Color(204, 0, 204));
        btnbAddSubBefore.setText("Add before");
        btnbAddSubBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbAddSubBeforeActionPerformed(evt);
            }
        });

        btnChangeSub.setForeground(new java.awt.Color(204, 0, 0));
        btnChangeSub.setText("Change");
        btnChangeSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeSubActionPerformed(evt);
            }
        });

        jPanel2.setLayout(new java.awt.GridLayout(1, 9));

        jLabel1.setText(" Start");
        jPanel2.add(jLabel1);

        tfStartTime.setText("00.00.00.000");
        jPanel2.add(tfStartTime);

        tfStartFrame.setText("0");
        jPanel2.add(tfStartFrame);

        jLabel2.setText(" End");
        jPanel2.add(jLabel2);

        tfEndTime.setText("00.00.00.000");
        jPanel2.add(tfEndTime);

        tfEndFrame.setText("0");
        jPanel2.add(tfEndFrame);

        jLabel3.setText(" Duration");
        jPanel2.add(jLabel3);

        tfDurationTime.setText("00.00.00.000");
        jPanel2.add(tfDurationTime);

        tfDurationFrame.setText("0");
        jPanel2.add(tfDurationFrame);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnChangeSub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnbAddSubBefore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddSubAfter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddSubAtEnd)))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddSubAtEnd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnbAddSubBefore)
                        .addComponent(btnChangeSub)
                        .addComponent(btnAddSubAfter)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboNameActionPerformed

    private void tfEffectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEffectsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEffectsActionPerformed

    private void spinLStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinLStateChanged

    private void spinRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinRStateChanged

    private void spinVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinVStateChanged

    private void btnAssPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssPropertiesActionPerformed
        PropsDialog pd = new PropsDialog(new JFrame(), true);
        pd.setAssInfos(ass);
        pd.showDialog(new JFrame());
        if(pd.getDialogResult() == PropsDialog.DialogResult.OK){
            pd.getAssInfos(ass);
        }
    }//GEN-LAST:event_btnAssPropertiesActionPerformed

    private void btnAssStylesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssStylesActionPerformed
        StylesDialog sd = new StylesDialog(new JFrame(), true);
        sd.showDialog(ass.getStyles());
        if(sd.getDialogResult() == StylesDialog.DialogResult.Ok){
            ass.setStyles(sd.getStyles());
            initAssComboStyle();
        }
    }//GEN-LAST:event_btnAssStylesActionPerformed

    private void btnAddSubAtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSubAtEndActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            AssEvent nv = getFromAssSubCommands();
            table.getLastAssSynchroTable().getAssTableModel().insertOne(nv);
            table.getLastAssSynchroTable().getTable().updateUI();
            refreshTempASS();
        }        
    }//GEN-LAST:event_btnAddSubAtEndActionPerformed

    private void btnAddSubAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSubAfterActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            AssEvent nv = getFromAssSubCommands();
            if(table.getTable().getSelectedRow() != -1){
                if(table.getTable().getRowCount() - 1 == table.getTable().getSelectedRow()){
                    // We are at last event
                    table.getLastAssSynchroTable().getAssTableModel().insertOne(nv);
                }else{
                    // We are inside the events cosmos
                    table.getLastAssSynchroTable().getAssTableModel().insertOneAt(nv, table.getTable().getSelectedRow() + 1);
                }
                table.getLastAssSynchroTable().getTable().updateUI();
                refreshTempASS();
            }
        }
    }//GEN-LAST:event_btnAddSubAfterActionPerformed

    private void btnbAddSubBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbAddSubBeforeActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            AssEvent nv = getFromAssSubCommands();
            if(table.getTable().getSelectedRow() != -1){
                table.getLastAssSynchroTable().getAssTableModel().insertOneAt(nv, table.getTable().getSelectedRow());
                table.getLastAssSynchroTable().getTable().updateUI();
                refreshTempASS();
            }
        }
    }//GEN-LAST:event_btnbAddSubBeforeActionPerformed

    private void btnChangeSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeSubActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            AssEvent nv = getFromAssSubCommands();
            if(table.getTable().getSelectedRow() != -1){
                table.getLastAssSynchroTable().getAssTableModel().changeEventAt(nv, table.getTable().getSelectedRow());
                table.getLastAssSynchroTable().getTable().updateUI();
                refreshTempASS();
            }
        }
    }//GEN-LAST:event_btnChangeSubActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgType;
    private javax.swing.JButton btnAddSubAfter;
    private javax.swing.JButton btnAddSubAtEnd;
    private javax.swing.JButton btnAssProperties;
    private javax.swing.JButton btnAssStyles;
    private javax.swing.JButton btnChangeSub;
    private javax.swing.JButton btnbAddSubBefore;
    private javax.swing.JComboBox<String> comboName;
    private javax.swing.JComboBox<String> comboStyle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JSpinner spinL;
    private javax.swing.JSpinner spinLayer;
    private javax.swing.JSpinner spinR;
    private javax.swing.JSpinner spinV;
    private javax.swing.JTextField tfDurationFrame;
    private javax.swing.JTextField tfDurationTime;
    private javax.swing.JTextField tfEffects;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    private javax.swing.JToggleButton toggleComment;
    private javax.swing.JToggleButton toggleDialogue;
    private javax.swing.JToggleButton toggleProposal;
    private javax.swing.JToggleButton toggleRequest;
    private javax.swing.JTextPane tpText;
    // End of variables declaration//GEN-END:variables
}
