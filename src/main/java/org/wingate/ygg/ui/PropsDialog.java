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

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.util.FFStuffs;

/**
 *
 * @author util2
 */
public class PropsDialog extends javax.swing.JDialog {
    
    public enum DialogResult{
        NONE, OK, CANCEL;
    }
    
    public enum Matrix{
        None(MainFrame.getLanguage().getTranslated("AssPropsMatrixNone", MainFrame.getISO(), "None")),
        TV_601("TV.601"),
        PC_601("PC.601"),
        TV_709("TV.709"),
        PC_709("PC.709"),
        TV_FCC("TV.FCC"),
        PC_FCC("PC.FCC"),
        TV_240M("TV.240M"),
        PC_240M("PC.240M");
        
        String matrixName;
        
        private Matrix(String matrixName){
            this.matrixName = matrixName;
        }

        public String getMatrixName() {
            return matrixName;
        }
        
        public static Matrix setMatrixName(String name){
            Matrix matrix = None;
            for(Matrix m : values()){
                if(m.getMatrixName().equalsIgnoreCase(name)){
                    matrix = m;
                    break;
                }
            }
            return matrix;
        }

        @Override
        public String toString() {
            return matrixName;
        }
    }
    
    public enum WrapStyle{
        WS0(0, MainFrame.getLanguage().getTranslated("AssPropsWS0", MainFrame.getISO(), "")),
        WS1(1, MainFrame.getLanguage().getTranslated("AssPropsWS1", MainFrame.getISO(), "")),
        WS2(2, MainFrame.getLanguage().getTranslated("AssPropsWS2", MainFrame.getISO(), "")),
        WS3(3, MainFrame.getLanguage().getTranslated("AssPropsWS3", MainFrame.getISO(), ""));
        
        int wrapStyle;
        String wrapStyleName;
        
        private WrapStyle(int wrapStyle, String wrapStyleName){
            this.wrapStyle = wrapStyle;
            this.wrapStyleName = wrapStyleName;
        }

        public int getWrapStyle() {
            return wrapStyle;
        }

        public String getWrapStyleName() {
            return wrapStyleName;
        }
        
        public static WrapStyle setWrapStyleName(int style){
            WrapStyle wrapStyle = WS0;
            for(WrapStyle ws : values()){
                if(ws.getWrapStyle() == style){
                    wrapStyle = ws;
                    break;
                }
            }
            return wrapStyle;
        }
        
        public static WrapStyle setWrapStyleName(String style){
            WrapStyle wrapStyle = WS0;
            for(WrapStyle ws : values()){
                if(ws.getWrapStyleName().equalsIgnoreCase(style)){
                    wrapStyle = ws;
                    break;
                }
            }
            return wrapStyle;
        }

        @Override
        public String toString() {
            return wrapStyle + " : " + wrapStyleName;
        }
    }
    
    private final SpinnerNumberModel snmWidth = new SpinnerNumberModel(1920, 0, 1000000, 1);
    private final SpinnerNumberModel snmHeight = new SpinnerNumberModel(1920, 0, 1000000, 1);
    private final DefaultComboBoxModel dcbmYCbCr = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmWrapStyle = new DefaultComboBoxModel();
    
    private DialogResult dialogResult = DialogResult.NONE;
    
    private FFStuffs ffss = null;

    public PropsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }
    
    private void init(){
        spinWidth.setModel(snmWidth);
        spinHeight.setModel(snmHeight);
        comboYCbCr.setModel(dcbmYCbCr);
        comboWrapStyle.setModel(dcbmWrapStyle);
        
        Language in = MainFrame.getLanguage();
        ISO_3166 get = MainFrame.getISO();
        
        for(Matrix m : Matrix.values()){
            dcbmYCbCr.addElement(m);
        }
        
        for(WrapStyle ws : WrapStyle.values()){
            dcbmWrapStyle.addElement(ws);
        }
        
        tfTitle.setText("");
        tfTitle.setPlaceholder(in.getTranslated("AssPropsTitle", get, "Set a title"));
        
        tfOriginalScript.setText("");
        tfOriginalScript.setPlaceholder(in.getTranslated("AssPropsOriginalScript", get, "Set a name"));
        
        tfTranslation.setText("");
        tfTranslation.setPlaceholder(in.getTranslated("AssPropsTranslation", get, "Set the name of the translator"));
        
        tfEditing.setText("");
        tfEditing.setPlaceholder(in.getTranslated("AssPropsEditing", get, "Set the name of the editor"));
        
        tfTiming.setText("");
        tfTiming.setPlaceholder(in.getTranslated("AssPropsTiming", get, "Set the name of the timer"));
        
        tfSynchPoint.setText("");
        tfSynchPoint.setPlaceholder(in.getTranslated("AssPropsSynchPoint", get, "Set the name of the timer for the synch point"));
        
        tfUpdatedBy.setText("");
        tfUpdatedBy.setPlaceholder(in.getTranslated("AssPropsUpdatedBy", get, "Set the names of the last authors"));
        
        tfUpdateDetails.setText("");
        tfUpdateDetails.setPlaceholder(in.getTranslated("AssPropsUpdateDetails", get, "Set what's up"));
        
    }
    
    public void showDialog(java.awt.Frame parent){
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    public void setAssInfos(ASS ass){
        tfTitle.setText(ass.getTitle());
        tfOriginalScript.setText(ass.getOriginalScript());
        tfTranslation.setText(ass.getTranslation());
        tfEditing.setText(ass.getEditing());
        tfTiming.setText(ass.getTiming());
        tfSynchPoint.setText(ass.getSynchPoint());
        tfUpdatedBy.setText(ass.getUpdateBy());
        tfUpdateDetails.setText(ass.getUpdateDetails());
        snmWidth.setValue(Integer.parseInt(ass.getResX()));
        snmHeight.setValue(Integer.parseInt(ass.getResY()));
        dcbmYCbCr.setSelectedItem(Matrix.setMatrixName(ass.getMatrix()));
        dcbmWrapStyle.setSelectedItem(WrapStyle.setWrapStyleName(Integer.parseInt(ass.getWrapStyle())));
        checkScalingState.setSelected(ass.getScaled().equalsIgnoreCase("yes"));
    }
    
    public void getAssInfos(ASS ass){
        ass.setTitle(tfTitle.getText());
        ass.setOriginalScript(tfOriginalScript.getText());
        ass.setTranslation(tfTranslation.getText());
        ass.setEditing(tfEditing.getText());
        ass.setTiming(tfTiming.getText());
        ass.setSynchPoint(tfSynchPoint.getText());
        ass.setUpdateBy(tfUpdatedBy.getText());
        ass.setUpdateDetails(tfUpdateDetails.getText());
        ass.setResX(Integer.toString(snmWidth.getNumber().intValue()));
        ass.setResY(Integer.toString(snmHeight.getNumber().intValue()));
        ass.setMatrix(((Matrix)comboYCbCr.getSelectedItem()).getMatrixName());
        ass.setWrapStyle(Integer.toString(((WrapStyle)comboWrapStyle.getSelectedItem()).getWrapStyle()));
        ass.setScaled(checkScalingState.isSelected() ? "yes" : "no");
    }
    
    public void setVideoInfos(FFStuffs ffss){
        this.ffss = ffss;
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcCopy = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnResolutionFromVideo = new javax.swing.JButton();
        spinWidth = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        spinHeight = new javax.swing.JSpinner();
        tfTitle = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfOriginalScript = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfTranslation = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfEditing = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfTiming = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfSynchPoint = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfUpdatedBy = new org.wingate.placeholdertextfield.PlaceholderTextField();
        tfUpdateDetails = new org.wingate.placeholdertextfield.PlaceholderTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        comboYCbCr = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        comboWrapStyle = new javax.swing.JComboBox<>();
        checkScalingState = new javax.swing.JCheckBox();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        btnCopyFrom = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informations"));

        jLabel1.setText("Title : ");

        jLabel2.setText("Original script : ");

        jLabel3.setText("Translation : ");

        jLabel4.setText("Editing : ");

        jLabel5.setText("Timing : ");

        jLabel6.setText("Synch point : ");

        jLabel7.setText("Updated by : ");

        jLabel8.setText("Update details : ");

        jLabel9.setText("Resolution : ");

        btnResolutionFromVideo.setText("From video");
        btnResolutionFromVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResolutionFromVideoActionPerformed(evt);
            }
        });

        jLabel10.setText("x");

        tfTitle.setText("placeholderTextField1");

        tfOriginalScript.setText("placeholderTextField2");

        tfTranslation.setText("placeholderTextField3");

        tfEditing.setText("placeholderTextField4");

        tfTiming.setText("placeholderTextField5");

        tfSynchPoint.setText("placeholderTextField6");

        tfUpdatedBy.setText("placeholderTextField7");

        tfUpdateDetails.setText("placeholderTextField8");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfOriginalScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfTranslation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfEditing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfTiming, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfSynchPoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfUpdatedBy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(spinWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinHeight, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResolutionFromVideo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tfUpdateDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfOriginalScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfTranslation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfEditing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tfTiming, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tfSynchPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tfUpdatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tfUpdateDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(btnResolutionFromVideo)
                    .addComponent(spinWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(spinHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        jLabel11.setText("YCbCr matrix : ");

        comboYCbCr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel12.setText("Wrap style : ");

        comboWrapStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        checkScalingState.setSelected(true);
        checkScalingState.setText("Scale border and shadow");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboYCbCr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboWrapStyle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(checkScalingState)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(comboYCbCr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(comboWrapStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkScalingState)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        OKButton.setText("OK");
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        btnCopyFrom.setText("Copy from...");
        btnCopyFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyFromActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCopyFrom)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(OKButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKButton)
                    .addComponent(CancelButton)
                    .addComponent(btnCopyFrom))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        dialogResult = DialogResult.CANCEL;
        dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        dialogResult = DialogResult.OK;
        dispose();
    }//GEN-LAST:event_OKButtonActionPerformed

    private void btnResolutionFromVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResolutionFromVideoActionPerformed
        if(ffss != null){
            snmWidth.setValue(ffss.getVideoWidth());
            snmHeight.setValue(ffss.getVideoHeight());
        }        
    }//GEN-LAST:event_btnResolutionFromVideoActionPerformed

    private void btnCopyFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyFromActionPerformed
        for(FileFilter ff : fcCopy.getChoosableFileFilters()){
            fcCopy.removeChoosableFileFilter(ff);
        }
        fcCopy.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory()) return true;
                return f.getName().endsWith(".ass");                        
            }

            @Override
            public String getDescription() {
                return "ASS files";
            }
        });
        int z = fcCopy.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            ASS ass = ASS.Read(fcCopy.getSelectedFile().getPath());
            tfTitle.setText(ass.getTitle());
            tfOriginalScript.setText(ass.getOriginalScript());
            tfTranslation.setText(ass.getTranslation());
            tfEditing.setText(ass.getEditing());
            tfTiming.setText(ass.getTiming());
            tfSynchPoint.setText(ass.getSynchPoint());
            tfUpdatedBy.setText(ass.getUpdateBy());
            tfUpdateDetails.setText(ass.getUpdateDetails());
            snmWidth.setValue(Integer.parseInt(ass.getResX()));
            snmHeight.setValue(Integer.parseInt(ass.getResY()));
            dcbmYCbCr.setSelectedItem(Matrix.setMatrixName(ass.getMatrix()));
            dcbmWrapStyle.setSelectedItem(WrapStyle.setWrapStyleName(Integer.parseInt(ass.getWrapStyle())));
            checkScalingState.setSelected(ass.getScaled().equalsIgnoreCase("yes"));
        }
    }//GEN-LAST:event_btnCopyFromActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PropsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PropsDialog dialog = new PropsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton OKButton;
    private javax.swing.JButton btnCopyFrom;
    private javax.swing.JButton btnResolutionFromVideo;
    private javax.swing.JCheckBox checkScalingState;
    private javax.swing.JComboBox<String> comboWrapStyle;
    private javax.swing.JComboBox<String> comboYCbCr;
    private javax.swing.JFileChooser fcCopy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSpinner spinHeight;
    private javax.swing.JSpinner spinWidth;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfEditing;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfOriginalScript;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfSynchPoint;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfTiming;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfTitle;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfTranslation;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfUpdateDetails;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfUpdatedBy;
    // End of variables declaration//GEN-END:variables
}
