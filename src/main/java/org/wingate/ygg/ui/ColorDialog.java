/*
 * Copyright (C) 2024 util2
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

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import org.wingate.ygg.helper.DialogResult;
import org.wingate.ygg.helper.DrawColor;

/**
 *
 * @author util2
 */
public class ColorDialog extends java.awt.Dialog {
    
    private final PreviewPanel previewPanel;
    
    private DialogResult dialogResult = DialogResult.Unknown;
    private final java.awt.Frame parent;
    
    private final DefaultComboBoxModel modelKnownColor;
    
    private Color color = Color.red;
    private int alpha = 0;
    
    private String lastBGR = "", lastRGB = "";
    private int lastSpinR = -1, lastSpinG = -1, lastSpinB = -1, lastSpinA = -1;
    private int lastSpinH = -1, lastSpinS = -1, lastSpinL = -1;
    private int lastSlidR = -1, lastSlidG = -1, lastSlidB = -1, lastSlidA = -1;
    private int lastSlidH = -1, lastSlidS = -1, lastSlidL = -1;

    /**
     * Creates new form ColorDialog
     * @param parent
     * @param modal
     */
    public ColorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.parent = parent;
        
        previewPanel = new PreviewPanel();
        embedPanel.add(previewPanel, BorderLayout.CENTER);
        
        modelKnownColor = new DefaultComboBoxModel();
        cbKnownColor.setModel(modelKnownColor);
        cbKnownColor.setRenderer(new KnownColorRenderer());
        
        for(DrawColor d : DrawColor.values()){
            modelKnownColor.addElement(d);
        }
        
        initUI();
    }
    
    private void initUI(){
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
            SwingUtilities.updateComponentTreeUI(this);
        } catch( UnsupportedLookAndFeelException ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
    }
    
    public void showDialog(Color color, int alpha){
        updateColor(color, alpha);
        
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }

    public Color getColor() {
        return color;
    }

    public int getAlpha() {
        return alpha;
    }
    
    private void updateColor(Color c, int transparency){
        this.color = c;
        this.alpha = transparency;
        
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        
        String sr = Integer.toHexString(r).toUpperCase(); if(sr.length() == 1) sr = "0" + sr;
        String sg = Integer.toHexString(g).toUpperCase(); if(sg.length() == 1) sg = "0" + sg;
        String sb = Integer.toHexString(b).toUpperCase(); if(sb.length() == 1) sb = "0" + sb;
        
        // Couleur de gauche (big)
        previewPanel.setColor(color);
        previewPanel.setAlpha(transparency);
        
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        
        lastBGR = tfBGR.getText();
        lastRGB = tfHTML.getText();
        lastSpinR = r; lastSlidR = r;
        lastSpinG = g; lastSlidG = g;
        lastSpinB = b; lastSlidB = b;
        lastSpinA = transparency; lastSlidA = transparency;
        lastSpinH = Math.round(hsb[0] * 360f); lastSlidH = Math.round(hsb[0] * 360f);
        lastSpinS = Math.round(hsb[1] * 100f); lastSlidS = Math.round(hsb[1] * 100f);
        lastSpinL = Math.round(hsb[2] * 100f); lastSlidL = Math.round(hsb[2] * 100f);
        
        // BGR
        tfBGR.setText(sb + sg + sr);
        
        // RGB
        tfHTML.setText(sr + sg + sb);
        
        // Spinners
        spinR.setValue(r);
        spinG.setValue(g);
        spinB.setValue(b);
        spinAlpha.setValue(transparency);        
        spinHue.setValue(Math.round(hsb[0] * 360f));
        spinSat.setValue(Math.round(hsb[1] * 100f));
        spinBri.setValue(Math.round(hsb[2] * 100f));
        
        // Sliders
        sliderR.setValue(r);
        sliderG.setValue(g);
        sliderB.setValue(b);
        sliderAlpha.setValue(transparency);
        sliderHue.setValue(Math.round(hsb[0] * 360f));
        sliderSat.setValue(Math.round(hsb[1] * 100f));
        sliderBri.setValue(Math.round(hsb[2] * 100f));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBGR = new javax.swing.JLabel();
        tfBGR = new javax.swing.JTextField();
        lblHTML = new javax.swing.JLabel();
        tfHTML = new javax.swing.JTextField();
        lblKnownColor = new javax.swing.JLabel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        cbKnownColor = new javax.swing.JComboBox<>();
        sliderAlpha = new javax.swing.JSlider();
        lblAlpha = new javax.swing.JLabel();
        lblRed = new javax.swing.JLabel();
        lblGreen = new javax.swing.JLabel();
        lblBlue = new javax.swing.JLabel();
        spinR = new javax.swing.JSpinner();
        spinG = new javax.swing.JSpinner();
        spinB = new javax.swing.JSpinner();
        sliderR = new javax.swing.JSlider();
        sliderG = new javax.swing.JSlider();
        sliderB = new javax.swing.JSlider();
        spinAlpha = new javax.swing.JSpinner();
        lblHue = new javax.swing.JLabel();
        lblSaturation = new javax.swing.JLabel();
        lblBrightness = new javax.swing.JLabel();
        sliderHue = new javax.swing.JSlider();
        sliderSat = new javax.swing.JSlider();
        sliderBri = new javax.swing.JSlider();
        spinHue = new javax.swing.JSpinner();
        spinSat = new javax.swing.JSpinner();
        spinBri = new javax.swing.JSpinner();
        embedPanel = new javax.swing.JPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        lblBGR.setText("BGR Color : ");

        tfBGR.setText("000000");
        tfBGR.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tfBGRCaretUpdate(evt);
            }
        });

        lblHTML.setText("HTML Color : ");

        tfHTML.setText("000000");
        tfHTML.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tfHTMLCaretUpdate(evt);
            }
        });

        lblKnownColor.setText("Known color : ");

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        cbKnownColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbKnownColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbKnownColorActionPerformed(evt);
            }
        });

        sliderAlpha.setMaximum(255);
        sliderAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderAlphaStateChanged(evt);
            }
        });

        lblAlpha.setText("Alpha : ");

        lblRed.setText("Red : ");

        lblGreen.setText("Green : ");

        lblBlue.setText("Blue : ");

        spinR.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRStateChanged(evt);
            }
        });

        spinG.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinG.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinGStateChanged(evt);
            }
        });

        spinB.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBStateChanged(evt);
            }
        });

        sliderR.setMaximum(255);
        sliderR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderRStateChanged(evt);
            }
        });

        sliderG.setMaximum(255);
        sliderG.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderGStateChanged(evt);
            }
        });

        sliderB.setMaximum(255);
        sliderB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBStateChanged(evt);
            }
        });

        spinAlpha.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        spinAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinAlphaStateChanged(evt);
            }
        });

        lblHue.setText("Hue : ");

        lblSaturation.setText("Saturation : ");

        lblBrightness.setText("Brightness : ");

        sliderHue.setMaximum(360);
        sliderHue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderHueStateChanged(evt);
            }
        });

        sliderSat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderSatStateChanged(evt);
            }
        });

        sliderBri.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderBriStateChanged(evt);
            }
        });

        spinHue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 360, 1));
        spinHue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinHueStateChanged(evt);
            }
        });

        spinSat.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spinSat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSatStateChanged(evt);
            }
        });

        spinBri.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spinBri.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBriStateChanged(evt);
            }
        });

        embedPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSaturation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderSat, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinSat, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblHue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderHue, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinHue, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblBlue)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderB, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblGreen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderG, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblKnownColor)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(49, 49, 49)
                                        .addComponent(lblBGR)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfBGR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblHTML)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfHTML, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbKnownColor, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblRed)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderR, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblAlpha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblBrightness)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sliderBri, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinBri, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOK)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(embedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbKnownColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblKnownColor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBGR)
                    .addComponent(lblHTML)
                    .addComponent(tfHTML, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfBGR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAlpha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRed))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGreen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBlue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinHue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderHue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderSat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSaturation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinBri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sliderBri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBrightness))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(embedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // OK
        dialogResult = DialogResult.Ok;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // Annuler
        dialogResult = DialogResult.Cancel;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cbKnownColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbKnownColorActionPerformed
        // Met à jour la couleur
        try{
            int r = ((DrawColor)cbKnownColor.getSelectedItem()).getRed();
            int g = ((DrawColor)cbKnownColor.getSelectedItem()).getGreen();
            int b = ((DrawColor)cbKnownColor.getSelectedItem()).getBlue();
            updateColor(new Color(r, g, b), alpha);
        }catch(Exception exc){
            
        }        
    }//GEN-LAST:event_cbKnownColorActionPerformed

    private void sliderAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderAlphaStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidA != sliderAlpha.getValue()){
            try{
                spinAlpha.setValue(sliderAlpha.getValue());
                alpha = sliderAlpha.getValue();
                previewPanel.setAlpha(sliderAlpha.getValue());
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_sliderAlphaStateChanged

    private void spinAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinAlphaStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinA != (int)spinAlpha.getValue()){
            try{
                sliderAlpha.setValue((int)spinAlpha.getValue());
                alpha = (int)spinAlpha.getValue();
                previewPanel.setAlpha((int)spinAlpha.getValue());
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_spinAlphaStateChanged

    private void sliderRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidR != sliderR.getValue()){
            try{
                spinR.setValue(sliderR.getValue());
                Color c = new Color(
                        sliderR.getValue(),
                        sliderG.getValue(),
                        sliderB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_sliderRStateChanged

    private void spinRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinR != (int)spinR.getValue()){
            try{
                sliderR.setValue((int)spinR.getValue());
                Color c = new Color(
                        (int)spinR.getValue(),
                        (int)spinG.getValue(),
                        (int)spinB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_spinRStateChanged

    private void sliderGStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderGStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidG != sliderG.getValue()){
            try{
                spinG.setValue(sliderG.getValue());
                Color c = new Color(
                        sliderR.getValue(),
                        sliderG.getValue(),
                        sliderB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_sliderGStateChanged

    private void spinGStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinGStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinG != (int)spinG.getValue()){
            try{
                sliderG.setValue((int)spinG.getValue());
                Color c = new Color(
                        (int)spinR.getValue(),
                        (int)spinG.getValue(),
                        (int)spinB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_spinGStateChanged

    private void sliderBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidB != sliderB.getValue()){
            try{
                spinB.setValue(sliderB.getValue());
                Color c = new Color(
                        sliderR.getValue(),
                        sliderG.getValue(),
                        sliderB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_sliderBStateChanged

    private void spinBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinB != (int)spinB.getValue()){
            try{
                sliderB.setValue((int)spinB.getValue());
                Color c = new Color(
                        (int)spinR.getValue(),
                        (int)spinG.getValue(),
                        (int)spinB.getValue()
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_spinBStateChanged

    private void sliderHueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderHueStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidH != sliderHue.getValue()){
            try{
                spinHue.setValue(sliderHue.getValue());
                Color c = Color.getHSBColor(
                        (float)sliderHue.getValue() / 360f, // Hue (0 to 1 float) (0->360)
                        (float)sliderSat.getValue() / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)sliderBri.getValue() / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_sliderHueStateChanged

    private void spinHueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinHueStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinH != (int)spinHue.getValue()){
            try{
                sliderHue.setValue((int)spinHue.getValue());
                Color c = Color.getHSBColor(
                        (float)((int)spinHue.getValue()) / 360f, // Hue (0 to 1 float) (0->360)
                        (float)((int)spinSat.getValue()) / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)((int)spinBri.getValue()) / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_spinHueStateChanged

    private void sliderSatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderSatStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidS != sliderSat.getValue()){
            try{
                spinSat.setValue(sliderSat.getValue());
                Color c = Color.getHSBColor(
                        (float)sliderHue.getValue() / 360f, // Hue (0 to 1 float) (0->360)
                        (float)sliderSat.getValue() / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)sliderBri.getValue() / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }        
    }//GEN-LAST:event_sliderSatStateChanged

    private void spinSatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSatStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinS != (int)spinSat.getValue()){
            try{
                sliderSat.setValue((int)spinSat.getValue());
                Color c = Color.getHSBColor(
                        (float)((int)spinHue.getValue()) / 360f, // Hue (0 to 1 float) (0->360)
                        (float)((int)spinSat.getValue()) / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)((int)spinBri.getValue()) / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }            
    }//GEN-LAST:event_spinSatStateChanged

    private void sliderBriStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBriStateChanged
        // Pour voir ce qu'on fait
        if(lastSlidL != sliderBri.getValue()){
            try{
                spinBri.setValue(sliderBri.getValue());
                Color c = Color.getHSBColor(
                        (float)sliderHue.getValue() / 360f, // Hue (0 to 1 float) (0->360)
                        (float)sliderSat.getValue() / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)sliderBri.getValue() / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_sliderBriStateChanged

    private void spinBriStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBriStateChanged
        // Pour voir ce qu'on fait
        if(lastSpinL != (int)spinBri.getValue()){
            try{
                sliderBri.setValue((int)spinBri.getValue());
                Color c = Color.getHSBColor(
                        (float)((int)spinHue.getValue()) / 360f, // Hue (0 to 1 float) (0->360)
                        (float)((int)spinSat.getValue()) / 100f, // Saturation (0 to 1 float) (0->100)
                        (float)((int)spinBri.getValue()) / 100f  // Brightness (0 to 1 float) (0->100)
                );
                previewPanel.setColor(c);
                updateColor(c, alpha);
            }catch(Exception exc){
                
            }
        }        
    }//GEN-LAST:event_spinBriStateChanged

    @SuppressWarnings("UseSpecificCatch")
    private void tfBGRCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tfBGRCaretUpdate
        if(lastBGR.equals(tfBGR.getText()) == false){
            try{
                String s = tfBGR.getText();
                if(s.contains("&HF")) s = s.replace("&HF", "");
                if(s.contains("&")) s = s.replace("&", "");
                if(s.toUpperCase().matches("[A-F0-9]+") == false) return;
                if(s.length() == 8){
                    alpha = Integer.parseInt(s.substring(0, 2), 16);
                    s = s.substring(2);
                }
                int r = Integer.parseInt(s.substring(4), 16);
                int g = Integer.parseInt(s.substring(2, 4), 16);
                int b = Integer.parseInt(s.substring(0, 2), 16);
                updateColor(new Color(r, g, b), alpha);
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_tfBGRCaretUpdate

    @SuppressWarnings("UseSpecificCatch")
    private void tfHTMLCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tfHTMLCaretUpdate
        if(lastRGB.equals(tfHTML.getText()) == false){
            try{
                String s = tfHTML.getText();
                if(s.contains("#")) s = s.replace("#", "");
                if(s.toUpperCase().matches("[A-F0-9]+") == false) return;
                if(s.length() == 8){
                    alpha = Integer.parseInt(s.substring(0, 2), 16);
                    s = s.substring(2);
                }
                int r = Integer.parseInt(s.substring(0, 2), 16);
                int g = Integer.parseInt(s.substring(2, 4), 16);
                int b = Integer.parseInt(s.substring(4), 16);
                updateColor(new Color(r, g, b), alpha);
            }catch(Exception exc){
                
            }            
        }
    }//GEN-LAST:event_tfHTMLCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ColorDialog dialog = new ColorDialog(new java.awt.Frame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox<String> cbKnownColor;
    private javax.swing.JPanel embedPanel;
    private javax.swing.JLabel lblAlpha;
    private javax.swing.JLabel lblBGR;
    private javax.swing.JLabel lblBlue;
    private javax.swing.JLabel lblBrightness;
    private javax.swing.JLabel lblGreen;
    private javax.swing.JLabel lblHTML;
    private javax.swing.JLabel lblHue;
    private javax.swing.JLabel lblKnownColor;
    private javax.swing.JLabel lblRed;
    private javax.swing.JLabel lblSaturation;
    private javax.swing.JSlider sliderAlpha;
    private javax.swing.JSlider sliderB;
    private javax.swing.JSlider sliderBri;
    private javax.swing.JSlider sliderG;
    private javax.swing.JSlider sliderHue;
    private javax.swing.JSlider sliderR;
    private javax.swing.JSlider sliderSat;
    private javax.swing.JSpinner spinAlpha;
    private javax.swing.JSpinner spinB;
    private javax.swing.JSpinner spinBri;
    private javax.swing.JSpinner spinG;
    private javax.swing.JSpinner spinHue;
    private javax.swing.JSpinner spinR;
    private javax.swing.JSpinner spinSat;
    private javax.swing.JTextField tfBGR;
    private javax.swing.JTextField tfHTML;
    // End of variables declaration//GEN-END:variables

    public class PreviewPanel extends javax.swing.JPanel {

        private Color color;
        private int alpha;
        private static final int SQUARE_SIZE = 16;

        public PreviewPanel() {
            color = Color.white;
            alpha = 255;
            setBorder(new LineBorder(Color.black, 1, true));
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
            repaint();
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.lightGray);
            boolean isSquare;
            int plus = SQUARE_SIZE;
            
            for(int y=0; y<getHeight(); y+=SQUARE_SIZE){
                isSquare = true;
                for(int x=plus; x<getWidth(); x+=SQUARE_SIZE){
                    if(isSquare == true){
                        g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                        isSquare = false;
                    }else{
                        isSquare = true;
                    }
                }
                plus = plus == SQUARE_SIZE ? 0 : SQUARE_SIZE;
            }
            
            g.setColor(color);
            g.fillRect(0, 0, getWidth()/2, getHeight());
            
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255 - alpha));
            g.fillRect(getWidth()/2, 0, getWidth()/2, getHeight());
        }
        
    }
}
