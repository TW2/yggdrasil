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

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.util.Clipboard;
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
    private final DefaultComboBoxModel dcbmStyle = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmName = new DefaultComboBoxModel();
    
    private float tpTextFontSize;
    private Font tpTextNormalFont;
    private final float tpTextScaleMin = 1f;
    private final float tpTextScaleMax = 5f;
    private float tpTextScaleCur = 1f;
    
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
    }
    
    public void reinit(){
        dcbmStyle.removeAllElements();
        studio.getAss().getStyles().entrySet().forEach((entry) -> {
            dcbmStyle.addElement(entry.getValue());
        });
        dcbmName.removeAllElements();
        studio.getAss().getNames().forEach((name) -> {
            dcbmName.addElement(name);
        });
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
        popText = new javax.swing.JPopupMenu();
        popTextCut = new javax.swing.JMenuItem();
        popTextCopy = new javax.swing.JMenuItem();
        popTextPaste = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelTypes = new javax.swing.JPanel();
        toggleDialogue = new javax.swing.JToggleButton();
        toggleComment = new javax.swing.JToggleButton();
        toggleProposal = new javax.swing.JToggleButton();
        toggleRequest = new javax.swing.JToggleButton();
        togglePicture = new javax.swing.JToggleButton();
        toggleSound = new javax.swing.JToggleButton();
        toggleMovie = new javax.swing.JToggleButton();
        toggleCommands = new javax.swing.JToggleButton();
        spinLayer = new javax.swing.JSpinner();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        tfDurTime = new javax.swing.JTextField();
        tfDurFrame = new javax.swing.JTextField();
        slideSpeed = new javax.swing.JSlider();
        comboStyle = new javax.swing.JComboBox<>();
        btnBold = new javax.swing.JButton();
        btnItalic = new javax.swing.JButton();
        btnUnderline = new javax.swing.JButton();
        btnStrikeOut = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        comboName = new javax.swing.JComboBox<>();
        tfSFX = new javax.swing.JTextField();
        lblLeft = new javax.swing.JLabel();
        spinLeft = new javax.swing.JSpinner();
        spinVertical = new javax.swing.JSpinner();
        lblVertical = new javax.swing.JLabel();
        spinRight = new javax.swing.JSpinner();
        lblRight = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        btnSeeEventInWave = new javax.swing.JButton();
        btnOpenTypes = new javax.swing.JButton();
        splitPaneText = new javax.swing.JSplitPane();
        paneTranslate = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        btnChange = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();

        popTextCut.setText("Cut");
        popTextCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTextCutActionPerformed(evt);
            }
        });
        popText.add(popTextCut);

        popTextCopy.setText("Copy");
        popTextCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTextCopyActionPerformed(evt);
            }
        });
        popText.add(popTextCopy);

        popTextPaste.setText("Paste");
        popTextPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTextPasteActionPerformed(evt);
            }
        });
        popText.add(popTextPaste);

        setClosable(true);
        setMaximizable(true);
        setResizable(true);

        bgTypeOfLine.add(toggleDialogue);
        toggleDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub dialogue black.png"))); // NOI18N
        toggleDialogue.setSelected(true);
        toggleDialogue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleDialogueActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleComment);
        toggleComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment.png"))); // NOI18N
        toggleComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCommentActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleProposal);
        toggleProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment yellow.png"))); // NOI18N
        toggleProposal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleProposalActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleRequest);
        toggleRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment blue.png"))); // NOI18N
        toggleRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleRequestActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(togglePicture);
        togglePicture.setText("P");
        togglePicture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePictureActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleSound);
        toggleSound.setText("S");
        toggleSound.setToolTipText("");
        toggleSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleSoundActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleMovie);
        toggleMovie.setText("M");
        toggleMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleMovieActionPerformed(evt);
            }
        });

        bgTypeOfLine.add(toggleCommands);
        toggleCommands.setText("C");
        toggleCommands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleCommandsActionPerformed(evt);
            }
        });

        spinLayer.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLayerStateChanged(evt);
            }
        });

        tfStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartTime.setText("0.00.00.000");
        tfStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartTimeActionPerformed(evt);
            }
        });

        tfStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartFrame.setText("0");
        tfStartFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfStartFrameActionPerformed(evt);
            }
        });

        tfEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndTime.setText("0.00.00.000");
        tfEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndTimeActionPerformed(evt);
            }
        });

        tfEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndFrame.setText("0");
        tfEndFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfEndFrameActionPerformed(evt);
            }
        });

        tfDurTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurTime.setText("0.00.00.000");
        tfDurTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurTimeActionPerformed(evt);
            }
        });

        tfDurFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurFrame.setText("0");
        tfDurFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDurFrameActionPerformed(evt);
            }
        });

        slideSpeed.setMaximum(200);
        slideSpeed.setOrientation(javax.swing.JSlider.VERTICAL);
        slideSpeed.setValue(100);
        slideSpeed.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slideSpeedStateChanged(evt);
            }
        });

        comboStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStyleActionPerformed(evt);
            }
        });

        btnBold.setText("B");
        btnBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoldActionPerformed(evt);
            }
        });

        btnItalic.setText("I");
        btnItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnItalicActionPerformed(evt);
            }
        });

        btnUnderline.setText("U");
        btnUnderline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnderlineActionPerformed(evt);
            }
        });

        btnStrikeOut.setText("S");
        btnStrikeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStrikeOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTypesLayout = new javax.swing.GroupLayout(panelTypes);
        panelTypes.setLayout(panelTypesLayout);
        panelTypesLayout.setHorizontalGroup(
            panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTypesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelTypesLayout.createSequentialGroup()
                        .addComponent(toggleDialogue, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleComment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleProposal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(togglePicture, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleSound, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toggleCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTypesLayout.createSequentialGroup()
                        .addComponent(btnBold, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnItalic, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUnderline, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStrikeOut, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboStyle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTypesLayout.createSequentialGroup()
                        .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTypesLayout.createSequentialGroup()
                        .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slideSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        panelTypesLayout.setVerticalGroup(
            panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTypesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(slideSpeed, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTypesLayout.createSequentialGroup()
                            .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfDurTime, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tfDurFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelTypesLayout.createSequentialGroup()
                        .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(togglePicture, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(toggleSound, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(toggleMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(toggleCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(toggleRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toggleProposal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toggleComment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toggleDialogue, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelTypesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBold, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnItalic, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUnderline, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnStrikeOut, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Type (SSA/ASS/ASX), Layer, Style and Sync", panelTypes);

        comboName.setEditable(true);
        comboName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboNameActionPerformed(evt);
            }
        });

        tfSFX.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfSFX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSFXActionPerformed(evt);
            }
        });

        lblLeft.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblLeft.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLeft.setText("L");

        spinLeft.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLeftStateChanged(evt);
            }
        });

        spinVertical.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVerticalStateChanged(evt);
            }
        });

        lblVertical.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblVertical.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVertical.setText("V");

        spinRight.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRightStateChanged(evt);
            }
        });

        lblRight.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblRight.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRight.setText("R");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(comboName, 0, 250, Short.MAX_VALUE)
                    .addComponent(tfSFX))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spinLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinVertical, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(lblVertical, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinRight, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .addComponent(lblRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(292, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblVertical, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRight, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinVertical, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinRight, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(comboName, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfSFX, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Actor, Effect and Margins", jPanel5);

        btnSeeEventInWave.setText("See");
        btnSeeEventInWave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeeEventInWaveActionPerformed(evt);
            }
        });

        btnOpenTypes.setText("...");
        btnOpenTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTypesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpenTypes)
                    .addComponent(btnSeeEventInWave))
                .addContainerGap(779, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSeeEventInWave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenTypes)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Control", jPanel7);

        splitPaneText.setDividerLocation(0);
        splitPaneText.setDividerSize(10);
        splitPaneText.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPaneText.setOneTouchExpandable(true);

        javax.swing.GroupLayout paneTranslateLayout = new javax.swing.GroupLayout(paneTranslate);
        paneTranslate.setLayout(paneTranslateLayout);
        paneTranslateLayout.setHorizontalGroup(
            paneTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 843, Short.MAX_VALUE)
        );
        paneTranslateLayout.setVerticalGroup(
            paneTranslateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        splitPaneText.setTopComponent(paneTranslate);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tpText.setComponentPopupMenu(popText);
        jScrollPane1.setViewportView(tpText);

        splitPaneText.setRightComponent(jScrollPane1);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAdd)
                .addContainerGap())
            .addComponent(jTabbedPane1)
            .addComponent(splitPaneText)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPaneText, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnChange))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        Event nv = ev.getCopy();
        nv.setText(tpText.getText());
        nv.setEffect(tfSFX.getText());
        studio.commandToTable(nv, true);
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Event nv = ev.getCopy();
        nv.setText(tpText.getText());
        nv.setEffect(tfSFX.getText());
        studio.commandToTable(nv, false);
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

    private void popTextCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTextCutActionPerformed
        if(tpText.getSelectedText() != null){
            int s = tpText.getSelectionStart();
            int e = tpText.getSelectionEnd();
            boolean result = Clipboard.CCopy(tpText.getSelectedText());
            if(result == true){
                String oldText = tpText.getText();
                String newText = oldText.substring(0, s) + oldText.substring(e);
                tpText.setText(newText);
            }
        }
    }//GEN-LAST:event_popTextCutActionPerformed

    private void popTextCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTextCopyActionPerformed
        if(tpText.getSelectedText() != null){
            Clipboard.CCopy(tpText.getSelectedText());
        }        
    }//GEN-LAST:event_popTextCopyActionPerformed

    private void popTextPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTextPasteActionPerformed
        String clpText = Clipboard.CPaste();
        if(tpText.getSelectedText() != null && clpText.isEmpty() == false){
            int s = tpText.getSelectionStart();
            int e = tpText.getSelectionEnd();            
            String oldText = tpText.getText();            
            String newText = oldText.substring(0, s) + clpText + oldText.substring(e);
            tpText.setText(newText);
        }else if(clpText.isEmpty() == false){
            int t = tpText.getCaretPosition();
            String oldText = tpText.getText();            
            String newText = oldText.substring(0, t) + clpText + oldText.substring(t);
            tpText.setText(newText);
        }
    }//GEN-LAST:event_popTextPasteActionPerformed


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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblLeft;
    private javax.swing.JLabel lblRight;
    private javax.swing.JLabel lblVertical;
    private javax.swing.JPanel paneTranslate;
    private javax.swing.JPanel panelTypes;
    private javax.swing.JPopupMenu popText;
    private javax.swing.JMenuItem popTextCopy;
    private javax.swing.JMenuItem popTextCut;
    private javax.swing.JMenuItem popTextPaste;
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
