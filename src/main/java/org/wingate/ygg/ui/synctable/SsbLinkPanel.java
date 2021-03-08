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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ssb.SSB;
import org.wingate.ygg.subs.ssb.SsbEvent;
import org.wingate.ygg.subs.ssb.SsbEventType;
import org.wingate.ygg.subs.ssb.SsbMacro;
import org.wingate.ygg.ui.IfrTable;

/**
 *
 * @author util2
 */
public class SsbLinkPanel extends javax.swing.JPanel {
    
    private final SSB ssb = SSB.NoFileToLoad();
    private final IfrTable table = MainFrame.getTableFrame();

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
//        try{
//            tfStartFrame.setText(Integer.toString(Time.getFrame(startTime, ffss.getFps())));
//            tfEndFrame.setText(Integer.toString(Time.getFrame(endTime, ffss.getFps())));
//            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, ffss.getFps())));
//        }catch(Exception ex){
//            
//        }        
                
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
//        tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStartTime(), ffss.getFps())));
        tfEndTime.setText(ev.getEnd().toProgramExtendedTime());
//        tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEndTime(), ffss.getFps())));
        tfDurationTime.setText(dur.toProgramExtendedTime());
//        tfDurationFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
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
    
    public SsbEvent getFromAssSubCommands(){
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
        File folder = new File("configuration");
        if(folder.exists() == false) folder.mkdirs();
        File filepath = new File(folder, "temp.ssb");
        table.save(filepath, ".ssb");
        MainFrame.getVideoFrame().setSubtitlesFile(filepath);
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
    
    public List<SsbMacro> getMacros(){
        return ssb.getMacros();
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

        btnAddAfter.setText("Add after");

        btnAddBefore.setText("Add before");

        btnChange.setText("Change");

        btnInfo.setText("Info");

        btnTarget.setText("Target");

        btnMacro.setText("Macro");

        btnResources.setText("Resources");

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
