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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.audiovideo.AVInfo;
import org.wingate.ygg.subs.ssb.SSB;
import org.wingate.ygg.subs.ssb.SsbEvent;
import org.wingate.ygg.subs.ssb.SsbEventType;
import org.wingate.ygg.subs.ssb.SsbMacro;
import org.wingate.ygg.ui.IfrTable;
import org.wingate.ygg.ui.SsbInfoDialog;
import org.wingate.ygg.ui.SsbTargetDialog;

/**
 *
 * @author util2
 */
public class SsbLinkPanel extends javax.swing.JPanel {
    
    private AVInfo avInfos = null;
    
    private SSB ssb = SSB.NoFileToLoad();

    private final DefaultComboBoxModel dcbmMacro = new DefaultComboBoxModel();
    
    private float tpTextFontSize;
    private Font tpTextNormalFont;
    private final float tpTextScaleMin = 1f;
    private final float tpTextScaleMax = 5f;
    private float tpTextScaleCur = 1f;
    
    public SsbLinkPanel() {
        initComponents();
        init();
    }
    
    private void init(){        
        // SSB Style Commands
        comboMacro.setModel(dcbmMacro);
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
        initSsbComboMacro();
    }

    public AVInfo getAvInfos() {
        return avInfos;
    }

    public void setAvInfos(AVInfo avInfos) {
        this.avInfos = avInfos;
    }
    
    public void alter(SsbEvent ev){
        // Type de ligne
        switch(ev.getType()){
            case Dialogue -> toggleDialogue.setSelected(true);
            case Comment -> toggleComment.setSelected(true);
//            case Proposal -> toggleProposal.setSelected(true);
//            case Request -> toggleRequest.setSelected(true);
        }
        
        // Start - End - Duration
        Time startTime, endTime, duration;
        startTime = ev.getStart();
        endTime = ev.getEnd();
        duration = Time.substract(startTime, endTime);
        tfStartTime.setText(startTime.toProgramExtendedTime());
        tfEndTime.setText(endTime.toProgramExtendedTime());
        tfDurationTime.setText(duration.toProgramExtendedTime());
        
        if(avInfos != null && avInfos.getVideoStream() > -1){
            tfStartFrame.setText(Integer.toString(Time.getFrame(startTime, avInfos.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(endTime, avInfos.getFps())));
            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, avInfos.getFps())));
        }        
                
        // Macro
        comboMacro.setSelectedItem(ev.getMacro());
        
        // Note
        tfNote.setText(ev.getNote());
        
        // Text
        tpText.setText(ev.getText());
        
        // ID
        tfID.setText(ev.getId());
    }
    
    public void updateAreaFrames(SsbEvent ev){        
        Time dur = Time.substract(ev.getStart(), ev.getEnd());
        
        tfStartTime.setText(ev.getStart().toProgramExtendedTime());
        tfEndTime.setText(ev.getEnd().toProgramExtendedTime());
        tfDurationTime.setText(dur.toProgramExtendedTime());
        
        if(avInfos != null && avInfos.getVideoStream() > -1){
            tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStart(), avInfos.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEnd(), avInfos.getFps())));
            tfDurationFrame.setText(Integer.toString(Time.getFrame(dur, avInfos.getFps())));
        }
    }
    
    public void displayEventTime(SsbEvent ev){
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
    
    public void initSsbComboMacro(){
        dcbmMacro.removeAllElements();
        for(SsbMacro macro : ssb.getMacros()){
            dcbmMacro.addElement(macro);
        }
    }
    
    public SsbEvent getFromSsbSubCommands(){
        SsbEvent nv = new SsbEvent();
        
        // Type
        if(toggleDialogue.isSelected()) nv.setType(SsbEventType.Dialogue);
        if(toggleComment.isSelected()) nv.setType(SsbEventType.Comment);
//        if(toggleProposal.isSelected()) nv.setLineType(AssEvent.LineType.Proposal);
//        if(toggleRequest.isSelected()) nv.setLineType(AssEvent.LineType.Request);
        
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
        nv.setStart(tStart);
        
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
        nv.setEnd(tEnd);
        
        // Style        
        nv.setMacro(dcbmMacro.getSize() == 0 ? new SsbMacro() : (SsbMacro)dcbmMacro.getSelectedItem());
        
        // Note
        nv.setNote(tfNote.getText());
        
        // ID
        nv.setId(tfID.getText());
        
        // Text
        nv.setText(tpText.getText());
        
        return nv;
    }
    
    // Refresh ASS rendering by updating ass temporary file
    private void refreshTempSSB(){
//        File folder = new File("configuration");
//        if(folder.exists() == false) folder.mkdirs();
//        File filepath = new File(folder, "temp.ssb");
//        table.save(filepath, ".ssb");
//        MainFrame.getVideoFrame().setSubtitlesFile(filepath);
    }
    
    public List<SsbMacro> getMacros(){
        return ssb.getMacros();
    }

    public SSB getSsb() {
        return ssb;
    }

    public void setSsb(SSB ssb) {
        this.ssb = ssb;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        toggleDialogue = new javax.swing.JToggleButton();
        toggleComment = new javax.swing.JToggleButton();
        toggleProposal = new javax.swing.JToggleButton();
        toggleRequest = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblMacro = new javax.swing.JLabel();
        lblNote = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        comboMacro = new javax.swing.JComboBox<>();
        tfNote = new org.wingate.freectrl.PlaceholderTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblID = new javax.swing.JLabel();
        tfID = new org.wingate.freectrl.PlaceholderTextField();
        jPanel8 = new javax.swing.JPanel();
        lblStart = new javax.swing.JLabel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        lblEnd = new javax.swing.JLabel();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        lblDuration = new javax.swing.JLabel();
        tfDurationTime = new javax.swing.JTextField();
        tfDurationFrame = new javax.swing.JTextField();
        btnAddAtEnd = new javax.swing.JButton();
        btnAddAfter = new javax.swing.JButton();
        btnAddBefore = new javax.swing.JButton();
        btnChange = new javax.swing.JButton();
        btnInfo = new javax.swing.JButton();
        btnTarget = new javax.swing.JButton();
        btnMacro = new javax.swing.JButton();
        btnResources = new javax.swing.JButton();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        toggleDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub dialogue.png"))); // NOI18N
        toggleDialogue.setText("Text");
        toggleDialogue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleDialogue.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleDialogue);

        toggleComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment.png"))); // NOI18N
        toggleComment.setText("Note");
        toggleComment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleComment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleComment);

        toggleProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment yellow.png"))); // NOI18N
        toggleProposal.setText("Proposal");
        toggleProposal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleProposal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleProposal);

        toggleRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment blue.png"))); // NOI18N
        toggleRequest.setText("Request");
        toggleRequest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleRequest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleRequest);
        jToolBar1.add(jSeparator1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));

        lblMacro.setText("Macro : ");
        jPanel2.add(lblMacro);

        lblNote.setText("Note : ");
        jPanel2.add(lblNote);

        jPanel1.add(jPanel2, java.awt.BorderLayout.WEST);

        jPanel4.setLayout(new java.awt.GridLayout(2, 1));

        comboMacro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel4.add(comboMacro);

        tfNote.setPlaceholder("Note is just an information beside, renderers ignore it.");
        jPanel4.add(tfNote);

        jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

        jToolBar1.add(jPanel1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportView(tpText);

        jPanel7.setLayout(new java.awt.GridLayout(1, 3));

        jPanel3.setLayout(new java.awt.BorderLayout());

        lblID.setText(" ID : ");
        jPanel3.add(lblID, java.awt.BorderLayout.WEST);

        tfID.setPlaceholder("You can use an ID instead of time");
        jPanel3.add(tfID, java.awt.BorderLayout.CENTER);

        jPanel7.add(jPanel3);

        jPanel8.setLayout(new java.awt.BorderLayout());

        lblStart.setText(" Start : ");
        jPanel8.add(lblStart, java.awt.BorderLayout.WEST);

        tfStartTime.setText("0.00.00.000");
        jPanel8.add(tfStartTime, java.awt.BorderLayout.CENTER);

        tfStartFrame.setText("0");
        jPanel8.add(tfStartFrame, java.awt.BorderLayout.EAST);

        jPanel7.add(jPanel8);

        jPanel9.setLayout(new java.awt.BorderLayout());

        lblEnd.setText(" End : ");
        jPanel9.add(lblEnd, java.awt.BorderLayout.WEST);

        tfEndTime.setText("0.00.00.000");
        jPanel9.add(tfEndTime, java.awt.BorderLayout.CENTER);

        tfEndFrame.setText("0");
        jPanel9.add(tfEndFrame, java.awt.BorderLayout.EAST);

        jPanel7.add(jPanel9);

        jPanel10.setLayout(new java.awt.BorderLayout());

        lblDuration.setText(" Duration : ");
        jPanel10.add(lblDuration, java.awt.BorderLayout.WEST);

        tfDurationTime.setText("0.00.00.000");
        jPanel10.add(tfDurationTime, java.awt.BorderLayout.CENTER);

        tfDurationFrame.setText("0");
        jPanel10.add(tfDurationFrame, java.awt.BorderLayout.EAST);

        jPanel7.add(jPanel10);

        btnAddAtEnd.setText("Add at end");
        btnAddAtEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAtEndActionPerformed(evt);
            }
        });

        btnAddAfter.setText("Add after");
        btnAddAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAfterActionPerformed(evt);
            }
        });

        btnAddBefore.setText("Add before");
        btnAddBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBeforeActionPerformed(evt);
            }
        });

        btnChange.setText("Change");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });

        btnInfo.setText("Info");
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoActionPerformed(evt);
            }
        });

        btnTarget.setText("Target");
        btnTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTargetActionPerformed(evt);
            }
        });

        btnMacro.setText("Macro");
        btnMacro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMacroActionPerformed(evt);
            }
        });

        btnResources.setText("Resources");
        btnResources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResourcesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTarget)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMacro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnResources)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddBefore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddAfter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddAtEnd)
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddAtEnd)
                    .addComponent(btnAddAfter)
                    .addComponent(btnAddBefore)
                    .addComponent(btnChange)
                    .addComponent(btnInfo)
                    .addComponent(btnTarget)
                    .addComponent(btnMacro)
                    .addComponent(btnResources))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddAtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAtEndActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.SSB){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            SsbEvent nv = getFromSsbSubCommands();
            nv.setUseId(!nv.getId().isEmpty());
            table.getLastSsbSynchroTable().getSsbTableModel().insertOne(nv);
            table.getLastSsbSynchroTable().getTable().updateUI();
            refreshTempSSB();
        }
    }//GEN-LAST:event_btnAddAtEndActionPerformed

    private void btnAddAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAfterActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.SSB){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            SsbEvent nv = getFromSsbSubCommands();
            nv.setUseId(!nv.getId().isEmpty());
            if(table.getTable().getSelectedRow() != -1){
                if(table.getTable().getRowCount() - 1 == table.getTable().getSelectedRow()){
                    // We are at last event
                    table.getLastSsbSynchroTable().getSsbTableModel().insertOne(nv);
                }else{
                    // We are inside the events cosmos
                    table.getLastSsbSynchroTable().getSsbTableModel().insertOneAt(nv, table.getTable().getSelectedRow() + 1);
                }
                table.getLastSsbSynchroTable().getTable().updateUI();
                refreshTempSSB();
            }
        }
    }//GEN-LAST:event_btnAddAfterActionPerformed

    private void btnAddBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBeforeActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.SSB){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            SsbEvent nv = getFromSsbSubCommands();
            nv.setUseId(!nv.getId().isEmpty());
            if(table.getTable().getSelectedRow() != -1){
                table.getLastSsbSynchroTable().getSsbTableModel().insertOneAt(nv, table.getTable().getSelectedRow());
                table.getLastSsbSynchroTable().getTable().updateUI();
                refreshTempSSB();
            }
        }
    }//GEN-LAST:event_btnAddBeforeActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        if(MainFrame.getTableFrame() == null) return;
        if(MainFrame.getTableFrame().getFormat() == SelectedFormat.SSB){
            //===---
            IfrTable table = MainFrame.getTableFrame();
            //===---
            SsbEvent nv = getFromSsbSubCommands();
            nv.setUseId(!nv.getId().isEmpty());
            if(table.getTable().getSelectedRow() != -1){
                table.getLastSsbSynchroTable().getSsbTableModel().changeEventAt(nv, table.getTable().getSelectedRow());
                table.getLastSsbSynchroTable().getTable().updateUI();
                refreshTempSSB();
            }
        }
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        SsbInfoDialog dialog = new SsbInfoDialog(new javax.swing.JFrame(), true);
        dialog.showDialog(
                ssb.getSubInfoTitle(),
                ssb.getSubInfoAuthor(),
                ssb.getSubInfoVersion(),
                ssb.getSubInfoDescription()
        );
        if(dialog.getDialogResult() == SsbInfoDialog.DialogResult.OK){
            ssb.setSubInfoTitle(dialog.getTitle());
            ssb.setSubInfoAuthor(dialog.getAuthor());
            ssb.setSubInfoVersion(dialog.getVersion());
            ssb.setSubInfoDescription(dialog.getDescription());
        }
    }//GEN-LAST:event_btnInfoActionPerformed

    private void btnTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTargetActionPerformed
        SsbTargetDialog dialog = new SsbTargetDialog(new javax.swing.JFrame(), true);
        dialog.showDialog(
                ssb.getSubTargetWidth(),
                ssb.getSubTargetHeight(),
                ssb.getSubTargetDepth(),
                ssb.getSubTargetView()
        );
        if(dialog.getDialogResult() == SsbTargetDialog.DialogResult.OK){
            ssb.setSubTargetWidth(Integer.parseInt(dialog.getPlaneWidth()));
            ssb.setSubTargetHeight(Integer.parseInt(dialog.getPlaneHeight()));
            ssb.setSubTargetDepth(Integer.parseInt(dialog.getPlaneDepth()));
            ssb.setSubTargetView(dialog.getView());
        }
    }//GEN-LAST:event_btnTargetActionPerformed

    private void btnMacroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMacroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMacroActionPerformed

    private void btnResourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResourcesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnResourcesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddAfter;
    private javax.swing.JButton btnAddAtEnd;
    private javax.swing.JButton btnAddBefore;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnMacro;
    private javax.swing.JButton btnResources;
    private javax.swing.JButton btnTarget;
    private javax.swing.JComboBox<String> comboMacro;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblDuration;
    private javax.swing.JLabel lblEnd;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblMacro;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblStart;
    private javax.swing.JTextField tfDurationFrame;
    private javax.swing.JTextField tfDurationTime;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private org.wingate.freectrl.PlaceholderTextField tfID;
    private org.wingate.freectrl.PlaceholderTextField tfNote;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    private javax.swing.JToggleButton toggleComment;
    private javax.swing.JToggleButton toggleDialogue;
    private javax.swing.JToggleButton toggleProposal;
    private javax.swing.JToggleButton toggleRequest;
    private javax.swing.JTextPane tpText;
    // End of variables declaration//GEN-END:variables
}
