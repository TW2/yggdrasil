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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class FrmSubCommand extends javax.swing.JInternalFrame {

    private final AVStudio studio;
    private boolean initOK = false;
    
    private final SpinnerNumberModel snmLayer = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmLeft = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmRight = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmVertical = new SpinnerNumberModel(0, 0, 100000, 1);
    private DefaultComboBoxModel dcbmStyle = new DefaultComboBoxModel();
    private DefaultComboBoxModel dcbmName = new DefaultComboBoxModel();
    
    private Event ev = new Event();
    
    /**
     * Creates new form FrmCommand
     * @param studio
     */
    public FrmSubCommand(AVStudio studio) {
        initComponents();
        this.studio = studio;
        ev.setLineType(Event.LineType.Dialogue);
        init();
    }

    private void init(){
        spinLayer.setModel(snmLayer);
        spinLeft.setModel(snmLeft);
        spinRight.setModel(snmRight);
        spinVertical.setModel(snmVertical);
        comboStyle.setModel(dcbmStyle);
        comboName.setModel(dcbmName);
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(initOK != true){
                    reinit();
                    initOK = true;
                }
            }
        });
    }
    
    public void reinit(){
        dcbmStyle.removeAllElements();
        for(Map.Entry<String, Style> entry : studio.getAss().getStyles().entrySet()){
            dcbmStyle.addElement(entry.getValue());
        }
        dcbmName.removeAllElements();
        for(String name : studio.getAss().getNames()){
            dcbmName.addElement(name);
        }
    }
    
    public void alter(Event ev){
        this.ev = ev;
        
        // Type de ligne
        switch(ev.getLineType()){
            case Dialogue: toggleDialogue.setSelected(true); break;
            case Comment: toggleComment.setSelected(true); break;
            case Proposal: toggleProposal.setSelected(true); break;
            case Request: toggleRequest.setSelected(true); break;
            case Sound: toggleSound.setSelected(true); break;
            case Movie: toggleMovie.setSelected(true); break;
            case Picture: togglePicture.setSelected(true); break;
            case Commands: toggleCommands.setSelected(true); break;
        }
        
        // Couche
        snmLayer.setValue(ev.getLayer());
        
        // Start - End - Duration
        Time start, end, duration;
        start = ev.getStartTime();
        end = ev.getEndTime();
        duration = Time.substract(start, end);
        tfStartTime.setText(start.toProgramExtendedTime());
        tfEndTime.setText(end.toProgramExtendedTime());
        tfDurTime.setText(duration.toProgramExtendedTime());
        try{
            tfStartFrame.setText(Integer.toString(Time.getFrame(start, studio.getFfss().getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
            tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
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
        //comboName.setSelectedItem(ev.getName());
        
        // Effect
        tfSFX.setText(ev.getEffect());
        
        // Text
        tpText.setText(ev.getText());
    }
    
    public void changeTime(Time start, Time end, boolean current){
        if(current == true){
            ev.setStartTime(start);
            ev.setEndTime(end);
            
            tfStartTime.setText(start.toProgramExtendedTime());
            tfEndTime.setText(end.toProgramExtendedTime());
            Time duration = Time.substract(start, end);
            tfDurTime.setText(duration.toProgramExtendedTime());
            if(studio.getFfss() != null){
                tfStartFrame.setText(Integer.toString(Time.getFrame(start, studio.getFfss().getFps())));
                tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
                tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
            }
        }else{
            ev = new Event();
            ev.setStartTime(start);
            ev.setEndTime(end);
            
            tfStartTime.setText(start.toProgramExtendedTime());
            tfEndTime.setText(end.toProgramExtendedTime());
            Time duration = Time.substract(start, end);
            tfDurTime.setText(duration.toProgramExtendedTime());
            if(studio.getFfss() != null){
                tfStartFrame.setText(Integer.toString(Time.getFrame(start, studio.getFfss().getFps())));
                tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
                tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
            }
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

        bgTypeOfLine = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        paneTypes = new javax.swing.JPanel();
        toggleDialogue = new javax.swing.JToggleButton();
        toggleComment = new javax.swing.JToggleButton();
        toggleProposal = new javax.swing.JToggleButton();
        toggleRequest = new javax.swing.JToggleButton();
        togglePicture = new javax.swing.JToggleButton();
        toggleSound = new javax.swing.JToggleButton();
        toggleMovie = new javax.swing.JToggleButton();
        toggleCommands = new javax.swing.JToggleButton();
        paneStyle = new javax.swing.JPanel();
        comboStyle = new javax.swing.JComboBox<>();
        btnBold = new javax.swing.JButton();
        btnItalic = new javax.swing.JButton();
        btnUnderline = new javax.swing.JButton();
        btnStrikeOut = new javax.swing.JButton();
        paneMargins = new javax.swing.JPanel();
        spinRight = new javax.swing.JSpinner();
        spinVertical = new javax.swing.JSpinner();
        spinLeft = new javax.swing.JSpinner();
        lblRight = new javax.swing.JLabel();
        lblVertical = new javax.swing.JLabel();
        lblLeft = new javax.swing.JLabel();
        paneTime = new javax.swing.JPanel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        tfDurTime = new javax.swing.JTextField();
        tfDurFrame = new javax.swing.JTextField();
        slideSpeed = new javax.swing.JSlider();
        splitPaneText = new javax.swing.JSplitPane();
        paneTranslate = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        paneLayer = new javax.swing.JPanel();
        spinLayer = new javax.swing.JSpinner();
        btnOpenTypes = new javax.swing.JButton();
        paneMisc = new javax.swing.JPanel();
        comboName = new javax.swing.JComboBox<>();
        tfSFX = new javax.swing.JTextField();
        btnChange = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        paneWave = new javax.swing.JPanel();
        btnSeeEventInWave = new javax.swing.JButton();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);

        paneTypes.setBorder(javax.swing.BorderFactory.createTitledBorder("Type (SSA/ASS/ASX)"));
        paneTypes.setLayout(null);

        bgTypeOfLine.add(toggleDialogue);
        toggleDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub dialogue black.png"))); // NOI18N
        toggleDialogue.setSelected(true);
        toggleDialogue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleDialogueActionPerformed(evt);
            }
        });
        paneTypes.add(toggleDialogue);
        toggleDialogue.setBounds(10, 20, 40, 40);

        bgTypeOfLine.add(toggleComment);
        toggleComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment.png"))); // NOI18N
        toggleComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCommentActionPerformed(evt);
            }
        });
        paneTypes.add(toggleComment);
        toggleComment.setBounds(50, 20, 40, 40);

        bgTypeOfLine.add(toggleProposal);
        toggleProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment yellow.png"))); // NOI18N
        toggleProposal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleProposalActionPerformed(evt);
            }
        });
        paneTypes.add(toggleProposal);
        toggleProposal.setBounds(90, 20, 40, 40);

        bgTypeOfLine.add(toggleRequest);
        toggleRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment blue.png"))); // NOI18N
        toggleRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleRequestActionPerformed(evt);
            }
        });
        paneTypes.add(toggleRequest);
        toggleRequest.setBounds(130, 20, 40, 40);

        bgTypeOfLine.add(togglePicture);
        togglePicture.setText("P");
        togglePicture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePictureActionPerformed(evt);
            }
        });
        paneTypes.add(togglePicture);
        togglePicture.setBounds(10, 60, 40, 40);

        bgTypeOfLine.add(toggleSound);
        toggleSound.setText("S");
        toggleSound.setToolTipText("");
        toggleSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleSoundActionPerformed(evt);
            }
        });
        paneTypes.add(toggleSound);
        toggleSound.setBounds(50, 60, 40, 40);

        bgTypeOfLine.add(toggleMovie);
        toggleMovie.setText("M");
        toggleMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleMovieActionPerformed(evt);
            }
        });
        paneTypes.add(toggleMovie);
        toggleMovie.setBounds(90, 60, 40, 40);

        bgTypeOfLine.add(toggleCommands);
        toggleCommands.setText("C");
        toggleCommands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCommandsActionPerformed(evt);
            }
        });
        paneTypes.add(toggleCommands);
        toggleCommands.setBounds(130, 60, 40, 40);

        paneStyle.setBorder(javax.swing.BorderFactory.createTitledBorder("Style"));
        paneStyle.setLayout(null);

        comboStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStyleActionPerformed(evt);
            }
        });
        paneStyle.add(comboStyle);
        comboStyle.setBounds(10, 20, 160, 40);

        btnBold.setText("B");
        btnBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoldActionPerformed(evt);
            }
        });
        paneStyle.add(btnBold);
        btnBold.setBounds(10, 60, 40, 40);

        btnItalic.setText("I");
        btnItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnItalicActionPerformed(evt);
            }
        });
        paneStyle.add(btnItalic);
        btnItalic.setBounds(50, 60, 40, 40);

        btnUnderline.setText("U");
        btnUnderline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnderlineActionPerformed(evt);
            }
        });
        paneStyle.add(btnUnderline);
        btnUnderline.setBounds(90, 60, 40, 40);

        btnStrikeOut.setText("S");
        btnStrikeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStrikeOutActionPerformed(evt);
            }
        });
        paneStyle.add(btnStrikeOut);
        btnStrikeOut.setBounds(130, 60, 40, 40);

        paneMargins.setBorder(javax.swing.BorderFactory.createTitledBorder("Margins"));
        paneMargins.setLayout(null);

        spinRight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRightStateChanged(evt);
            }
        });
        paneMargins.add(spinRight);
        spinRight.setBounds(190, 60, 80, 40);

        spinVertical.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVerticalStateChanged(evt);
            }
        });
        paneMargins.add(spinVertical);
        spinVertical.setBounds(100, 60, 80, 40);

        spinLeft.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLeftStateChanged(evt);
            }
        });
        paneMargins.add(spinLeft);
        spinLeft.setBounds(10, 60, 80, 40);

        lblRight.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblRight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRight.setText("R");
        paneMargins.add(lblRight);
        lblRight.setBounds(190, 20, 80, 40);

        lblVertical.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblVertical.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVertical.setText("V");
        paneMargins.add(lblVertical);
        lblVertical.setBounds(100, 20, 80, 40);

        lblLeft.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblLeft.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLeft.setText("L");
        paneMargins.add(lblLeft);
        lblLeft.setBounds(10, 20, 80, 40);

        paneTime.setBorder(javax.swing.BorderFactory.createTitledBorder("Synchronization"));
        paneTime.setLayout(null);

        tfStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartTime.setText("0.00.00.000");
        tfStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartTimeActionPerformed(evt);
            }
        });
        paneTime.add(tfStartTime);
        tfStartTime.setBounds(10, 20, 80, 40);

        tfStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartFrame.setText("0");
        tfStartFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartFrameActionPerformed(evt);
            }
        });
        paneTime.add(tfStartFrame);
        tfStartFrame.setBounds(10, 60, 80, 40);

        tfEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndTime.setText("0.00.00.000");
        tfEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndTimeActionPerformed(evt);
            }
        });
        paneTime.add(tfEndTime);
        tfEndTime.setBounds(90, 20, 80, 40);

        tfEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndFrame.setText("0");
        tfEndFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndFrameActionPerformed(evt);
            }
        });
        paneTime.add(tfEndFrame);
        tfEndFrame.setBounds(90, 60, 80, 40);

        tfDurTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurTime.setText("0.00.00.000");
        tfDurTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurTimeActionPerformed(evt);
            }
        });
        paneTime.add(tfDurTime);
        tfDurTime.setBounds(170, 20, 80, 40);

        tfDurFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurFrame.setText("0");
        tfDurFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurFrameActionPerformed(evt);
            }
        });
        paneTime.add(tfDurFrame);
        tfDurFrame.setBounds(170, 60, 80, 40);

        slideSpeed.setMaximum(200);
        slideSpeed.setOrientation(javax.swing.JSlider.VERTICAL);
        slideSpeed.setValue(100);
        slideSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slideSpeedStateChanged(evt);
            }
        });
        paneTime.add(slideSpeed);
        slideSpeed.setBounds(250, 20, 10, 80);

        splitPaneText.setDividerLocation(0);
        splitPaneText.setDividerSize(10);
        splitPaneText.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPaneText.setOneTouchExpandable(true);

        javax.swing.GroupLayout paneTranslateLayout = new javax.swing.GroupLayout(paneTranslate);
        paneTranslate.setLayout(paneTranslateLayout);
        paneTranslateLayout.setHorizontalGroup(
            paneTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1388, Short.MAX_VALUE)
        );
        paneTranslateLayout.setVerticalGroup(
            paneTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        splitPaneText.setTopComponent(paneTranslate);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportView(tpText);

        splitPaneText.setRightComponent(jScrollPane1);

        paneLayer.setBorder(javax.swing.BorderFactory.createTitledBorder("Layer"));
        paneLayer.setLayout(null);

        spinLayer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLayerStateChanged(evt);
            }
        });
        paneLayer.add(spinLayer);
        spinLayer.setBounds(10, 20, 80, 40);

        btnOpenTypes.setText("...");
        btnOpenTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTypesActionPerformed(evt);
            }
        });

        paneMisc.setBorder(javax.swing.BorderFactory.createTitledBorder("Misc."));
        paneMisc.setLayout(null);

        comboName.setEditable(true);
        comboName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboNameActionPerformed(evt);
            }
        });
        paneMisc.add(comboName);
        comboName.setBounds(10, 20, 190, 40);

        tfSFX.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfSFX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSFXActionPerformed(evt);
            }
        });
        paneMisc.add(tfSFX);
        tfSFX.setBounds(10, 60, 190, 40);

        btnChange.setText("CHANGE");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        paneWave.setBorder(javax.swing.BorderFactory.createTitledBorder("Wave"));

        btnSeeEventInWave.setText("See");
        btnSeeEventInWave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeeEventInWaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneWaveLayout = new javax.swing.GroupLayout(paneWave);
        paneWave.setLayout(paneWaveLayout);
        paneWaveLayout.setHorizontalGroup(
            paneWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneWaveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSeeEventInWave, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );
        paneWaveLayout.setVerticalGroup(
            paneWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneWaveLayout.createSequentialGroup()
                .addComponent(btnSeeEventInWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splitPaneText, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnChange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(paneTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(paneLayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnOpenTypes, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(paneMargins, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(paneTime, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneMisc, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(paneWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(paneMargins, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(paneStyle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(paneLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenTypes))
                    .addComponent(paneTypes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paneTime, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paneMisc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paneWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPaneText, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnChange))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        try {
            Event nv = (Event)ev.clone();
            nv.setText(tpText.getText());
            nv.setEffect(tfSFX.getText());
            studio.commandToTable(nv, true);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FrmSubCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        try {
            Event nv = (Event)ev.clone();
            nv.setText(tpText.getText());
            nv.setEffect(tfSFX.getText());
            studio.commandToTable(nv, false);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FrmSubCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void toggleDialogueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleDialogueActionPerformed
        ev.setLineType(Event.LineType.Dialogue);
    }//GEN-LAST:event_toggleDialogueActionPerformed

    private void toggleCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleCommentActionPerformed
        ev.setLineType(Event.LineType.Comment);
    }//GEN-LAST:event_toggleCommentActionPerformed

    private void toggleProposalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleProposalActionPerformed
        ev.setLineType(Event.LineType.Proposal);
    }//GEN-LAST:event_toggleProposalActionPerformed

    private void toggleRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleRequestActionPerformed
        ev.setLineType(Event.LineType.Request);
    }//GEN-LAST:event_toggleRequestActionPerformed

    private void togglePictureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togglePictureActionPerformed
        ev.setLineType(Event.LineType.Picture);
    }//GEN-LAST:event_togglePictureActionPerformed

    private void toggleSoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleSoundActionPerformed
        ev.setLineType(Event.LineType.Sound);
    }//GEN-LAST:event_toggleSoundActionPerformed

    private void toggleMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleMovieActionPerformed
        ev.setLineType(Event.LineType.Movie);
    }//GEN-LAST:event_toggleMovieActionPerformed

    private void toggleCommandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleCommandsActionPerformed
        ev.setLineType(Event.LineType.Commands);
    }//GEN-LAST:event_toggleCommandsActionPerformed

    private void btnOpenTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTypesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOpenTypesActionPerformed

    private void btnBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoldActionPerformed
        tpText.replaceSelection("{\\b1}"+tpText.getSelectedText()+"{\\b0}");
        ev.setText(tpText.getText());
    }//GEN-LAST:event_btnBoldActionPerformed

    private void btnItalicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnItalicActionPerformed
        tpText.replaceSelection("{\\i1}"+tpText.getSelectedText()+"{\\i0}");
        ev.setText(tpText.getText());
    }//GEN-LAST:event_btnItalicActionPerformed

    private void btnUnderlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnderlineActionPerformed
        tpText.replaceSelection("{\\u1}"+tpText.getSelectedText()+"{\\u0}");
        ev.setText(tpText.getText());
    }//GEN-LAST:event_btnUnderlineActionPerformed

    private void btnStrikeOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrikeOutActionPerformed
        tpText.replaceSelection("{\\s1}"+tpText.getSelectedText()+"{\\s0}");
        ev.setText(tpText.getText());
    }//GEN-LAST:event_btnStrikeOutActionPerformed

    private void spinLayerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLayerStateChanged
        ev.setLayer(snmLayer.getNumber().intValue());
    }//GEN-LAST:event_spinLayerStateChanged

    private void spinLeftStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLeftStateChanged
        ev.setMarginL(snmLeft.getNumber().intValue());
    }//GEN-LAST:event_spinLeftStateChanged

    private void spinVerticalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVerticalStateChanged
        ev.setMarginV(snmVertical.getNumber().intValue());
    }//GEN-LAST:event_spinVerticalStateChanged

    private void spinRightStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRightStateChanged
        ev.setMarginR(snmRight.getNumber().intValue());
    }//GEN-LAST:event_spinRightStateChanged

    private void comboStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStyleActionPerformed
        ev.setStyle((Style)comboStyle.getSelectedItem());
    }//GEN-LAST:event_comboStyleActionPerformed

    private void comboNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboNameActionPerformed
        ev.setName(comboName.getSelectedItem().toString());        
    }//GEN-LAST:event_comboNameActionPerformed

    private void tfStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStartTimeActionPerformed
        Time start = Time.create(tfStartTime.getText());
        ev.setStartTime(start);
        if(studio.getFfss() != null){
            tfStartFrame.setText(Integer.toString(Time.getFrame(start, studio.getFfss().getFps())));
        }
    }//GEN-LAST:event_tfStartTimeActionPerformed

    private void tfEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEndTimeActionPerformed
        Time end = Time.create(tfEndTime.getText());
        Time start = Time.create(tfStartTime.getText());
        Time duration = Time.substract(start, end);
        ev.setEndTime(end);
        tfDurTime.setText(duration.toProgramExtendedTime());
        if(studio.getFfss() != null){
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
            tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
        }
    }//GEN-LAST:event_tfEndTimeActionPerformed

    private void tfDurTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDurTimeActionPerformed
        Time start = Time.create(tfStartTime.getText());
        Time duration = Time.create(tfDurTime.getText());
        Time end = Time.addition(start, duration);
        ev.setEndTime(end);
        tfEndTime.setText(end.toProgramExtendedTime());
        if(studio.getFfss() != null){
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
            tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
        }
    }//GEN-LAST:event_tfDurTimeActionPerformed

    private void tfStartFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfStartFrameActionPerformed
        if(studio.getFfss() != null){
            Time start = Time.getTimeFromFrame(Integer.parseInt(tfStartFrame.getText()), studio.getFfss().getFps());
            tfStartTime.setText(start.toProgramExtendedTime());
            ev.setStartTime(start);
        }
    }//GEN-LAST:event_tfStartFrameActionPerformed

    private void tfEndFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfEndFrameActionPerformed
        if(studio.getFfss() != null){
            Time start = Time.getTimeFromFrame(Integer.parseInt(tfStartFrame.getText()), studio.getFfss().getFps());
            Time end = Time.getTimeFromFrame(Integer.parseInt(tfEndFrame.getText()), studio.getFfss().getFps());
            Time duration = Time.substract(start, end);
            tfDurTime.setText(duration.toProgramExtendedTime());
            tfDurFrame.setText(Integer.toString(Time.getFrame(duration, studio.getFfss().getFps())));
            ev.setEndTime(end);
        }
    }//GEN-LAST:event_tfEndFrameActionPerformed

    private void tfDurFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDurFrameActionPerformed
        if(studio.getFfss() != null){
            Time start = Time.getTimeFromFrame(Integer.parseInt(tfStartFrame.getText()), studio.getFfss().getFps());
            Time duration = Time.getTimeFromFrame(Integer.parseInt(tfDurFrame.getText()), studio.getFfss().getFps());
            Time end = Time.addition(start, duration);
            tfEndTime.setText(end.toProgramExtendedTime());
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, studio.getFfss().getFps())));
            ev.setEndTime(end);
        }
    }//GEN-LAST:event_tfDurFrameActionPerformed

    private void tfSFXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSFXActionPerformed
        ev.setEffect(tfSFX.getText());
    }//GEN-LAST:event_tfSFXActionPerformed

    private void slideSpeedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slideSpeedStateChanged
        
    }//GEN-LAST:event_slideSpeedStateChanged

    private void btnSeeEventInWaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeeEventInWaveActionPerformed
        if(studio.getFrmWave() != null){
            studio.getFrmWave().displayEventTime(ev);
        }
    }//GEN-LAST:event_btnSeeEventInWaveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgTypeOfLine;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBold;
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnItalic;
    private javax.swing.JButton btnOpenTypes;
    private javax.swing.JButton btnSeeEventInWave;
    private javax.swing.JButton btnStrikeOut;
    private javax.swing.JButton btnUnderline;
    private javax.swing.JComboBox<String> comboName;
    private javax.swing.JComboBox<String> comboStyle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblRight;
    private javax.swing.JLabel lblVertical;
    private javax.swing.JPanel paneLayer;
    private javax.swing.JPanel paneMargins;
    private javax.swing.JPanel paneMisc;
    private javax.swing.JPanel paneStyle;
    private javax.swing.JPanel paneTime;
    private javax.swing.JPanel paneTranslate;
    private javax.swing.JPanel paneTypes;
    private javax.swing.JPanel paneWave;
    private javax.swing.JSlider slideSpeed;
    private javax.swing.JSpinner spinLayer;
    private javax.swing.JSpinner spinLeft;
    private javax.swing.JSpinner spinRight;
    private javax.swing.JSpinner spinVertical;
    private javax.swing.JSplitPane splitPaneText;
    private javax.swing.JTextField tfDurFrame;
    private javax.swing.JTextField tfDurTime;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private javax.swing.JTextField tfSFX;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    private javax.swing.JToggleButton toggleCommands;
    private javax.swing.JToggleButton toggleComment;
    private javax.swing.JToggleButton toggleDialogue;
    private javax.swing.JToggleButton toggleMovie;
    private javax.swing.JToggleButton togglePicture;
    private javax.swing.JToggleButton toggleProposal;
    private javax.swing.JToggleButton toggleRequest;
    private javax.swing.JToggleButton toggleSound;
    private javax.swing.JTextPane tpText;
    // End of variables declaration//GEN-END:variables
}
