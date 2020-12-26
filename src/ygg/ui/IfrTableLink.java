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
package ygg.ui;

import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import org.wingate.timelibrary.Time;
import ygg.MainFrame;
import ygg.io.subs.ass.ASS;
import ygg.io.subs.ass.Event;
import ygg.io.subs.ass.Style;
import ygg.util.FFStuffs;
import ygg.util.FramesPanel;

/**
 *
 * @author util2
 */
public class IfrTableLink extends javax.swing.JInternalFrame {

    private ASS ass = ASS.NoFileToLoad();
    private Event currentEvent = new Event();
    private final IfrTable table = MainFrame.getTableFrame();
    
    private FramesPanel fp = null;
    private FFStuffs ffss = null;
    
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
    
    /**
     * Creates new form IfrTableLink
     */
    public IfrTableLink() {
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
    
    public void alter(Event ev){
        currentEvent = ev;
        
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
        try{
            tfStartFrame.setText(Integer.toString(Time.getFrame(startTime, ffss.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(endTime, ffss.getFps())));
            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, ffss.getFps())));
        }catch(Exception ex){
            
        }        
        
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
    
    public void updateAreaFrames(Event ev){
        currentEvent = ev;
        
        if(fp != null){
            fp.updateArea(ev.getStartTime(), ev.getEndTime());
        }        
        
        Time dur = Time.substract(ev.getStartTime(), ev.getEndTime());
        
        tfStartTime.setText(ev.getStartTime().toProgramExtendedTime());
        tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStartTime(), ffss.getFps())));
        tfEndTime.setText(ev.getEndTime().toProgramExtendedTime());
        tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEndTime(), ffss.getFps())));
        tfDurationTime.setText(dur.toProgramExtendedTime());
        tfDurationFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
    }
    
    public void displayEventTime(Event ev){
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
    
    private void initAssComboStyle(){
        dcbmStyle.removeAllElements();
        ass.getStyles().entrySet().forEach((entry) -> {
            dcbmStyle.addElement(entry.getValue());
        });
    }
    
    private void initAssComboName(){
        dcbmName.removeAllElements();
        dcbmName.addElement("");
        ass.getNames().forEach((name) -> {
            dcbmName.addElement(name);
        });
    }
    
    private Event getFromAssSubCommands(){
        Event nv = new Event();
        
        // Type
        if(toggleDialogue.isSelected()) nv.setLineType(Event.LineType.Dialogue);
        if(toggleComment.isSelected()) nv.setLineType(Event.LineType.Comment);
        if(toggleProposal.isSelected()) nv.setLineType(Event.LineType.Proposal);
        if(toggleRequest.isSelected()) nv.setLineType(Event.LineType.Request);
        
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
        nv.setStyle(dcbmStyle.getSize() == 0 ? new Style() : (Style)dcbmStyle.getSelectedItem());
        
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
        saveASSTable(new File(MainFrame.getTempFolder(), "temp.ass"));
    }
    
    @SuppressWarnings("static-access")
    public void loadASSTable(File f){        
        ASS loading = ASS.Read(f.getPath());
        table.getTableV1().removeAll();
        table.getAssTableModel().insertAll(loading.getEvents());
        table.getTableV1().updateUI();
        initAssComboStyle();
        initAssComboName();
    }
    
    @SuppressWarnings("static-access")
    public void saveASSTable(File f){
        ASS saving = new ASS();        
        List<Event> events = table.getAssTableModel().getAllEvents();
        saving.setEvents(events);
        ASS.Save(f.getPath(), saving);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgEventType = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        toggleDialogue = new javax.swing.JToggleButton();
        toggleComment = new javax.swing.JToggleButton();
        toggleProposal = new javax.swing.JToggleButton();
        toggleRequest = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        spinLayer = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        comboStyle = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        tfDurationFrame = new javax.swing.JTextField();
        tfDurationTime = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        spinL = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        spinR = new javax.swing.JSpinner();
        jPanel9 = new javax.swing.JPanel();
        spinV = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        comboName = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        tfEffects = new javax.swing.JTextField();
        btnAddSubAtEnd = new javax.swing.JButton();
        btnAddSubAfter = new javax.swing.JButton();
        btnbAddSubBefore = new javax.swing.JButton();
        btnChangeSub = new javax.swing.JButton();
        btnProperties = new javax.swing.JButton();
        btnStyles = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setMaximizable(true);
        setResizable(true);
        setTitle("Commands");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        bgEventType.add(toggleDialogue);
        toggleDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub dialogue.png"))); // NOI18N
        toggleDialogue.setSelected(true);
        toggleDialogue.setText("Dialogue");
        toggleDialogue.setFocusable(false);
        toggleDialogue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleDialogue.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleDialogue);

        bgEventType.add(toggleComment);
        toggleComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment.png"))); // NOI18N
        toggleComment.setText("Comment");
        toggleComment.setFocusable(false);
        toggleComment.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleComment.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleComment);

        bgEventType.add(toggleProposal);
        toggleProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment yellow.png"))); // NOI18N
        toggleProposal.setText("Proposal");
        toggleProposal.setFocusable(false);
        toggleProposal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleProposal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleProposal);

        bgEventType.add(toggleRequest);
        toggleRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment blue.png"))); // NOI18N
        toggleRequest.setText("Request");
        toggleRequest.setFocusable(false);
        toggleRequest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleRequest.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(toggleRequest);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(spinLayer);
        jToolBar1.add(jSeparator2);

        comboStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(comboStyle);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 867, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Type, Layer, Style", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Start time"));

        tfStartTime.setText("00.00.00.000");
        tfStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartTimeActionPerformed(evt);
            }
        });

        tfStartFrame.setText("00000000");
        tfStartFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartFrameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("End time"));

        tfEndTime.setText("00.00.00.000");
        tfEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndTimeActionPerformed(evt);
            }
        });

        tfEndFrame.setText("00000000");
        tfEndFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndFrameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Duration"));

        tfDurationFrame.setText("00000000");
        tfDurationFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurationFrameActionPerformed(evt);
            }
        });

        tfDurationTime.setText("00.00.00.000");
        tfDurationTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurationTimeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("L"));

        spinL.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("R"));

        spinR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("V"));

        spinV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spinV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 141, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sync and Margins", jPanel2);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));

        comboName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comboName, 0, 350, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(comboName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Effects"));

        tfEffects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEffectsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfEffects, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfEffects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(141, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Name and Effect", jPanel3);

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

        btnProperties.setText("Properties");
        btnProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertiesActionPerformed(evt);
            }
        });

        btnStyles.setText("Styles");
        btnStyles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStylesActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportView(tpText);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnProperties)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStyles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnChangeSub)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnbAddSubBefore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddSubAfter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddSubAtEnd)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddSubAtEnd)
                    .addComponent(btnAddSubAfter)
                    .addComponent(btnbAddSubBefore)
                    .addComponent(btnChangeSub)
                    .addComponent(btnProperties)
                    .addComponent(btnStyles))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStartTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfStartTimeActionPerformed

    private void tfStartFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStartFrameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfStartFrameActionPerformed

    private void tfEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEndTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEndTimeActionPerformed

    private void tfEndFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEndFrameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEndFrameActionPerformed

    private void tfDurationTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDurationTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDurationTimeActionPerformed

    private void tfDurationFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDurationFrameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDurationFrameActionPerformed

    private void spinLStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinLStateChanged

    private void spinRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinRStateChanged

    private void spinVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_spinVStateChanged

    private void comboNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboNameActionPerformed

    private void tfEffectsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEffectsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfEffectsActionPerformed

    private void btnPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertiesActionPerformed
        PropsDialog pd = new PropsDialog(new JFrame(), true);
        pd.setAssInfos(ass);
        pd.showDialog(new JFrame());
        if(pd.getDialogResult() == PropsDialog.DialogResult.OK){
            pd.getAssInfos(ass);
        }
    }//GEN-LAST:event_btnPropertiesActionPerformed

    private void btnStylesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStylesActionPerformed
        StylesDialog sd = new StylesDialog(new JFrame(), true);
        sd.showDialog(ass.getStyles());
        if(sd.getDialogResult() == StylesDialog.DialogResult.Ok){
            
        }
    }//GEN-LAST:event_btnStylesActionPerformed
    
    @SuppressWarnings("static-access")
    private void btnChangeSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeSubActionPerformed
        if(table.getTableV1().getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            table.getAssTableModel().changeEventAt(nv, table.getTableV1().getSelectedRow());
            table.getTableV1().updateUI();
            refreshTempASS();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnChangeSubActionPerformed

    @SuppressWarnings("static-access")
    private void btnbAddSubBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbAddSubBeforeActionPerformed
        if(table.getTableV1().getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            table.getAssTableModel().insertOneAt(nv, table.getTableV1().getSelectedRow());
            table.getTableV1().updateUI();
            refreshTempASS();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnbAddSubBeforeActionPerformed

    @SuppressWarnings("static-access")
    private void btnAddSubAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSubAfterActionPerformed
        if(table.getTableV1().getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            if(table.getTableV1().getRowCount() - 1 == table.getTableV1().getSelectedRow()){
                // We are at last event
                table.getAssTableModel().insertOne(nv);
            }else{
                // We are inside the events cosmos
                table.getAssTableModel().insertOneAt(nv, table.getTableV1().getSelectedRow() + 1);
            }
            table.getTableV1().updateUI();
            refreshTempASS();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnAddSubAfterActionPerformed

    @SuppressWarnings("static-access")
    private void btnAddSubAtEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSubAtEndActionPerformed
        Event nv = getFromAssSubCommands();
        table.getAssTableModel().insertOne(nv);
        table.getTableV1().updateUI();
        refreshTempASS();
    }//GEN-LAST:event_btnAddSubAtEndActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgEventType;
    private javax.swing.JButton btnAddSubAfter;
    private javax.swing.JButton btnAddSubAtEnd;
    private javax.swing.JButton btnChangeSub;
    private javax.swing.JButton btnProperties;
    private javax.swing.JButton btnStyles;
    private javax.swing.JButton btnbAddSubBefore;
    private javax.swing.JComboBox<String> comboName;
    private javax.swing.JComboBox<String> comboStyle;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
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
    private javax.swing.JTabbedPane jTabbedPane1;
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
