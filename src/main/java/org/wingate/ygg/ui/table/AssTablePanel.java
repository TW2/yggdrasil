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
package org.wingate.ygg.ui.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableColumn;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.AssEvent;
import org.wingate.ygg.ass.AssEvent.LineType;
import org.wingate.ygg.ass.AssStyle;
import org.wingate.ygg.ass.AssTime;
import org.wingate.ygg.helper.AssFileFilter;
import org.wingate.ygg.helper.AssLanguageAccessory;
import org.wingate.ygg.helper.DialogResult;
import org.wingate.ygg.theme.Theme;
import org.wingate.ygg.ui.StyleEditDialog;

/**
 *
 * @author util2
 */
public class AssTablePanel extends javax.swing.JPanel {
    
    private final Theme theme;

    private final AssTableModel model;
    private final AssTableRenderer renderer;
    
    private final DefaultComboBoxModel cbModelLineType;
    private final DefaultComboBoxModel cbModelStyle;
    private final SpinnerNumberModel spModelLayer;
    private final SpinnerNumberModel spModelML;
    private final SpinnerNumberModel spModelMR;
    private final SpinnerNumberModel spModelMV;
    
    private final AssLanguageAccessory assLanguageOpen;
    private boolean editOneSimpleClick = false;
    
    private StyleEditDialog styleEditDialog;
    
    /**
     * Creates new form AssTablePanel
     * @param theme
     */
    public AssTablePanel(Theme theme) {
        initComponents();
        
        model = new AssTableModel();
        jTable1.setModel(model);
        
        renderer = new AssTableRenderer();
        jTable1.setDefaultRenderer(String.class, renderer);
        jTable1.setDefaultRenderer(AssTime.class, renderer);
        jTable1.setDefaultRenderer(Integer.class, renderer);
        jTable1.setDefaultRenderer(LineType.class, renderer);
        
        cbModelLineType = new DefaultComboBoxModel();
        
        cbLineType.setModel(cbModelLineType);
        
        for(LineType linetype : LineType.values()){
            cbModelLineType.addElement(linetype);
        }
        
        cbModelStyle = new DefaultComboBoxModel();
        
        cbStyle.setModel(cbModelStyle);
        
        spModelLayer = new SpinnerNumberModel(0, 0, 1000, 1);
        
        spinLayer.setModel(spModelLayer);
        
        spModelML = new SpinnerNumberModel(0, 0, 25600, 1);
        spModelMR = new SpinnerNumberModel(0, 0, 25600, 1);
        spModelMV = new SpinnerNumberModel(0, 0, 25600, 1);
        
        spinML.setModel(spModelML);
        spinMR.setModel(spModelMR);
        spinMV.setModel(spModelMV);
        
        for(javax.swing.filechooser.FileFilter ff : fcOpenDoc.getChoosableFileFilters()){
            fcOpenDoc.removeChoosableFileFilter(ff);
        }
        fcOpenDoc.addChoosableFileFilter(new AssFileFilter());
        assLanguageOpen = new AssLanguageAccessory();
        fcOpenDoc.addPropertyChangeListener(assLanguageOpen);
        fcOpenDoc.setAccessory(assLanguageOpen);
        
        for(javax.swing.filechooser.FileFilter ff : fcSaveDoc.getChoosableFileFilters()){
            fcSaveDoc.removeChoosableFileFilter(ff);
        }
        fcSaveDoc.addChoosableFileFilter(new AssFileFilter());
        
        jTable1.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                
                if(e.getButton() == MouseEvent.BUTTON1){
                    switch(e.getClickCount()){
                        case 1 -> {
                            if(editOneSimpleClick){
                                setToControls(getModel().getAss());
                            }
                        }
                        case 2 -> {
                            if(!editOneSimpleClick){
                                setToControls(getModel().getAss());
                            }
                        }
                    }
                }
            }
        });
        
        this.theme = theme;
        
        init();
    }
    
    private void init(){
        theme.apply(this);
        resetColumnWidth();
        jTable1.updateUI();
    }

    public AssTableModel getModel() {
        return model;
    }

    public AssTableRenderer getRenderer() {
        return renderer;
    }
    
    public JTable getTable(){
        return jTable1;
    }
    
    public JFileChooser getOpenFileChooser(){
        return fcOpenDoc;
    }
    
    public JFileChooser getSaveFileChooser(){
        return fcSaveDoc;
    }
    
    public void resetColumnWidth(){
        int max = 1860, size;
        TableColumn col;
        for(int i=0; i<jTable1.getColumnCount(); i++){            
            switch(i){
                case 0 -> { size = 40; } // Line number
                case 1 -> { size = 60; } // LineType
                case 2 -> { size = 40; } // Layer
                case 3 -> { size = 90; } // Start
                case 4 -> { size = 90; } // End
                case 5 -> { size = 90; } // Duration
                case 6 -> { size = 100; } // Style
                case 7 -> { size = 100; } // Actor
                case 8 -> { size = 60; } // MarginL
                case 9 -> { size = 60; } // MarginR
                case 10 -> { size = 60; } // MarginV
                case 11 -> { size = 100; } // Effects
                case 12 -> { size = max; } // Text
                default -> { size = 0; }
            }
            col = jTable1.getColumnModel().getColumn(i);
            col.setPreferredWidth(size);
            max -= size;
        }
    }

    public boolean isEditOneSimpleClick() {
        return editOneSimpleClick;
    }

    public void setEditOneSimpleClick(boolean editOneSimpleClick) {
        this.editOneSimpleClick = editOneSimpleClick;
    }
    
    /**
     * On obtient des informations depuis la table (par le modèle).
     * L'événement montre ce qu'il y a à afficher, il contient aussi les traductions
     * @param ass le fichier ASSA d'origine afin de charger styles et noms
     */
    public void setToControls(ASS ass){
        AssEvent ev = ass.getEvents().get(jTable1.getSelectedRow());
        
        // Rechargement des styles
        // (si nouveaux styles utiliser ailleurs qu'ici les scripts pour stockage)
        cbModelStyle.removeAllElements();
        for(Map.Entry<String, AssStyle> entry : ass.getStyles().entrySet()){
            cbModelStyle.addElement(entry.getKey());
        }
        // On définit le style courant
        cbStyle.setSelectedItem(ev.getStyle().getName());
        
        cbLineType.setSelectedItem(ev.getLineType());
        spinLayer.setValue(ev.getLayer());
        tfStart.setText(ev.getStartTime().toASSTime());
        tfEnd.setText(ev.getEndTime().toASSTime());
        AssTime dur = AssTime.substract(ev.getStartTime(), ev.getEndTime());
        tfDuration.setText(dur.toASSTime());
        spinML.setValue(ev.getMarginL());
        spinMR.setValue(ev.getMarginR());
        spinMV.setValue(ev.getMarginV());
        tfName.setText(ev.getName());
        tfEffects.setText(ev.getEffect());
        tfText.setText(ev.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcOpenDoc = new javax.swing.JFileChooser();
        fcSaveDoc = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        tfText = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnStyles = new javax.swing.JButton();
        btnNames = new javax.swing.JButton();
        btnEffects = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cbLineType = new javax.swing.JComboBox<>();
        spinLayer = new javax.swing.JSpinner();
        tfStart = new javax.swing.JTextField();
        tfEnd = new javax.swing.JTextField();
        tfDuration = new javax.swing.JTextField();
        spinML = new javax.swing.JSpinner();
        spinMR = new javax.swing.JSpinner();
        spinMV = new javax.swing.JSpinner();
        cbStyle = new javax.swing.JComboBox<>();
        tfName = new javax.swing.JTextField();
        tfEffects = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnReplace = new javax.swing.JButton();
        btnAddBefore = new javax.swing.JButton();
        btnAddAfter = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        jPanel6 = new javax.swing.JPanel();
        btnSettings = new javax.swing.JButton();
        btnTranslate = new javax.swing.JButton();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setDividerLocation(70);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(tfText, java.awt.BorderLayout.SOUTH);

        jPanel3.setLayout(new java.awt.GridLayout(2, 12));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Type");
        jPanel3.add(jLabel1);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Layer");
        jPanel3.add(jLabel3);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Start time");
        jPanel3.add(jLabel5);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("End time");
        jPanel3.add(jLabel2);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Duration");
        jPanel3.add(jLabel9);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Margin Left");
        jPanel3.add(jLabel4);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Margin Right");
        jPanel3.add(jLabel6);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Margin Vertical");
        jPanel3.add(jLabel7);

        btnStyles.setText("Style");
        btnStyles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStylesActionPerformed(evt);
            }
        });
        jPanel3.add(btnStyles);

        btnNames.setText("Name");
        jPanel3.add(btnNames);

        btnEffects.setText("Effect(s)");
        jPanel3.add(btnEffects);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Action");
        jPanel3.add(jLabel8);

        cbLineType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cbLineType);
        jPanel3.add(spinLayer);

        tfStart.setText("0.00.00.00");
        jPanel3.add(tfStart);

        tfEnd.setText("0.00.00.00");
        jPanel3.add(tfEnd);

        tfDuration.setText("0.00.00.00");
        jPanel3.add(tfDuration);
        jPanel3.add(spinML);
        jPanel3.add(spinMR);
        jPanel3.add(spinMV);

        cbStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel3.add(cbStyle);
        jPanel3.add(tfName);
        jPanel3.add(tfEffects);

        jPanel4.setLayout(new java.awt.GridLayout(1, 4));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16OK-custom-green.png"))); // NOI18N
        jPanel4.add(btnAdd);

        btnReplace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16OK-custom-blue.png"))); // NOI18N
        jPanel4.add(btnReplace);

        btnAddBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16OK-custom-orange.png"))); // NOI18N
        jPanel4.add(btnAddBefore);

        btnAddAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16OK-custom-violet.png"))); // NOI18N
        jPanel4.add(btnAddAfter);

        jPanel3.add(jPanel4);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        jSplitPane1.setTopComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setViewportView(tpText);

        jPanel5.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.GridLayout(1, 2));

        btnSettings.setText("Settings");
        jPanel6.add(btnSettings);

        btnTranslate.setText("Translate");
        jPanel6.add(btnTranslate);

        jPanel5.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jScrollPane2.setViewportView(jPanel5);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 842, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStylesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStylesActionPerformed
        // Ouvre la boîte d'édition de style
        styleEditDialog = new StyleEditDialog(new javax.swing.JFrame(), true);
        styleEditDialog.showDialog();
        if(styleEditDialog.getDialogResult() == DialogResult.Ok){
            // TODO
        }
        
        // Reset look & feel
        theme.apply(this);
    }//GEN-LAST:event_btnStylesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddAfter;
    private javax.swing.JButton btnAddBefore;
    private javax.swing.JButton btnEffects;
    private javax.swing.JButton btnNames;
    private javax.swing.JButton btnReplace;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnStyles;
    private javax.swing.JButton btnTranslate;
    private javax.swing.JComboBox<String> cbLineType;
    private javax.swing.JComboBox<String> cbStyle;
    private javax.swing.JFileChooser fcOpenDoc;
    private javax.swing.JFileChooser fcSaveDoc;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JSpinner spinLayer;
    private javax.swing.JSpinner spinML;
    private javax.swing.JSpinner spinMR;
    private javax.swing.JSpinner spinMV;
    private javax.swing.JTextField tfDuration;
    private javax.swing.JTextField tfEffects;
    private javax.swing.JTextField tfEnd;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfStart;
    private javax.swing.JTextField tfText;
    private javax.swing.JTextPane tpText;
    // End of variables declaration//GEN-END:variables
}
