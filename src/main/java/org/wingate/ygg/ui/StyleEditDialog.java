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

import java.awt.GraphicsEnvironment;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.wingate.ygg.ass.AssEncoding;
import org.wingate.ygg.helper.DialogResult;

/**
 *
 * @author util2
 */
public class StyleEditDialog extends java.awt.Dialog {
    
    private  ColorDialog colorDialog;
    
    private DialogResult dialogResult = DialogResult.Unknown;
    private final java.awt.Frame parent;
    
    private final DefaultListModel modelFontNames;
    private final DefaultComboBoxModel modelEncoding;

    /**
     * Creates new form StyleEditDialog
     * @param parent
     * @param modal
     */
    public StyleEditDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.parent = parent;
        
        modelFontNames = new DefaultListModel();
        listFontNames.setModel(modelFontNames);
        
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String s : fonts){
            modelFontNames.addElement(s);
        }
        
        modelEncoding = new DefaultComboBoxModel();
        cbEncoding.setModel(modelEncoding);
        
        for(AssEncoding enc : AssEncoding.values()){
            modelEncoding.addElement(enc);
        }
        modelEncoding.setSelectedItem(AssEncoding.Default);
    }
    
    public void showDialog(){
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgAlignment = new javax.swing.ButtonGroup();
        lblFontName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listFontNames = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        listFontStyles = new javax.swing.JList<>();
        lblFontStyle = new javax.swing.JLabel();
        spinFontSize = new javax.swing.JSpinner();
        lblFontSize = new javax.swing.JLabel();
        lblStyleName = new javax.swing.JLabel();
        tfStyleName = new javax.swing.JTextField();
        lblBorder = new javax.swing.JLabel();
        spinBorder = new javax.swing.JSpinner();
        lblShadow = new javax.swing.JLabel();
        spinShadow = new javax.swing.JSpinner();
        chkOpaqueBox = new javax.swing.JCheckBox();
        rb7 = new javax.swing.JRadioButton();
        rb4 = new javax.swing.JRadioButton();
        rb1 = new javax.swing.JRadioButton();
        rb8 = new javax.swing.JRadioButton();
        rb5 = new javax.swing.JRadioButton();
        rb2 = new javax.swing.JRadioButton();
        rb3 = new javax.swing.JRadioButton();
        rb9 = new javax.swing.JRadioButton();
        rb6 = new javax.swing.JRadioButton();
        lblAlign = new javax.swing.JLabel();
        lblMarginL = new javax.swing.JLabel();
        lblMarginR = new javax.swing.JLabel();
        lblMarginV = new javax.swing.JLabel();
        spinL = new javax.swing.JSpinner();
        spinR = new javax.swing.JSpinner();
        spinV = new javax.swing.JSpinner();
        lblScaleX = new javax.swing.JLabel();
        lblScaleY = new javax.swing.JLabel();
        spinScaleX = new javax.swing.JSpinner();
        spinScaleY = new javax.swing.JSpinner();
        lblAngleZ = new javax.swing.JLabel();
        lblSpacing = new javax.swing.JLabel();
        lblEncoding = new javax.swing.JLabel();
        spinAngleZ = new javax.swing.JSpinner();
        spinSpacing = new javax.swing.JSpinner();
        cbEncoding = new javax.swing.JComboBox<>();
        lblSample = new javax.swing.JLabel();
        tfSample = new javax.swing.JTextField();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnCollections = new javax.swing.JButton();
        btnImportStyles = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblColor1 = new javax.swing.JLabel();
        lblColor2 = new javax.swing.JLabel();
        lblColor3 = new javax.swing.JLabel();
        lblColor4 = new javax.swing.JLabel();
        lblColorText = new javax.swing.JLabel();
        lblColorKaraoke = new javax.swing.JLabel();
        lblColorOutline = new javax.swing.JLabel();
        lblColorShadow = new javax.swing.JLabel();
        spinAlphaText = new javax.swing.JSpinner();
        spinAlphaKaraoke = new javax.swing.JSpinner();
        spinAlphaOutline = new javax.swing.JSpinner();
        spinAlphaShadow = new javax.swing.JSpinner();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        lblFontName.setText("Font name : ");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listFontNames.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listFontNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFontNames.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFontNamesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listFontNames);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listFontStyles.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Plain", "Bold", "Italic", "Underline", "StrikeOut" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listFontStyles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFontStylesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listFontStyles);

        lblFontStyle.setText("Font style : ");

        spinFontSize.setModel(new javax.swing.SpinnerNumberModel(54, 2, 1000, 1));

        lblFontSize.setText("Font size : ");

        lblStyleName.setText("Style name : ");

        tfStyleName.setText("Default");

        lblBorder.setText("Border : ");

        spinBorder.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));

        lblShadow.setText("Shadow : ");

        spinShadow.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));

        chkOpaqueBox.setText("Opaque box");

        bgAlignment.add(rb7);
        rb7.setText("7");

        bgAlignment.add(rb4);
        rb4.setText("4");

        bgAlignment.add(rb1);
        rb1.setText("1");

        bgAlignment.add(rb8);
        rb8.setText("8");

        bgAlignment.add(rb5);
        rb5.setText("5");

        bgAlignment.add(rb2);
        rb2.setSelected(true);
        rb2.setText("2");

        bgAlignment.add(rb3);
        rb3.setText("3");

        bgAlignment.add(rb9);
        rb9.setText("9");

        bgAlignment.add(rb6);
        rb6.setText("6");

        lblAlign.setText("Alignment : ");

        lblMarginL.setText("L : ");
        lblMarginL.setToolTipText("Margin L");

        lblMarginR.setText("R : ");
        lblMarginR.setToolTipText("Margin R");

        lblMarginV.setText("V : ");
        lblMarginV.setToolTipText("Margin V");

        spinL.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9000, 1));

        spinR.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9000, 1));

        spinV.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9000, 1));

        lblScaleX.setText("Scale X (%) : ");

        lblScaleY.setText("Scale Y (%) : ");

        spinScaleX.setModel(new javax.swing.SpinnerNumberModel(100, 0, 10000, 1));

        spinScaleY.setModel(new javax.swing.SpinnerNumberModel(100, 0, 10000, 1));

        lblAngleZ.setText("Angle Z (°) : ");

        lblSpacing.setText("Spacing : ");

        lblEncoding.setText("Encoding : ");

        spinAngleZ.setModel(new javax.swing.SpinnerNumberModel(0, -10000, 10000, 1));

        spinSpacing.setModel(new javax.swing.SpinnerNumberModel(0, -10000, 10000, 1));

        cbEncoding.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblSample.setText("Sample : ");

        tfSample.setText("yggdrasil is an unknown new comer at the corner of the square 1234567890");

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

        btnCollections.setText("Collections...");

        btnImportStyles.setText("Import styles...");

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 204), 1, true));

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridLayout(3, 4));

        lblColor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColor1.setText("Text / Color & Alpha");
        jPanel2.add(lblColor1);

        lblColor2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColor2.setText("Karaoke / Color & Alpha");
        jPanel2.add(lblColor2);

        lblColor3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColor3.setText("Outline / Color & Alpha");
        jPanel2.add(lblColor3);

        lblColor4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblColor4.setText("Shadow / Color & Alpha");
        jPanel2.add(lblColor4);

        lblColorText.setBackground(new java.awt.Color(255, 255, 255));
        lblColorText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorText.setOpaque(true);
        lblColorText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorTextMouseClicked(evt);
            }
        });
        jPanel2.add(lblColorText);

        lblColorKaraoke.setBackground(new java.awt.Color(255, 255, 0));
        lblColorKaraoke.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorKaraoke.setOpaque(true);
        lblColorKaraoke.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorKaraokeMouseClicked(evt);
            }
        });
        jPanel2.add(lblColorKaraoke);

        lblColorOutline.setBackground(new java.awt.Color(0, 0, 0));
        lblColorOutline.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorOutline.setOpaque(true);
        lblColorOutline.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorOutlineMouseClicked(evt);
            }
        });
        jPanel2.add(lblColorOutline);

        lblColorShadow.setBackground(new java.awt.Color(0, 0, 0));
        lblColorShadow.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColorShadow.setOpaque(true);
        lblColorShadow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorShadowMouseClicked(evt);
            }
        });
        jPanel2.add(lblColorShadow);

        spinAlphaText.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        jPanel2.add(spinAlphaText);

        spinAlphaKaraoke.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        jPanel2.add(spinAlphaKaraoke);

        spinAlphaOutline.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        jPanel2.add(spinAlphaOutline);

        spinAlphaShadow.setModel(new javax.swing.SpinnerNumberModel(0, 0, 255, 1));
        jPanel2.add(spinAlphaShadow);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnImportStyles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCollections)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOK))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblFontName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblFontSize)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFontStyle)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rb7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb9))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rb4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rb1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rb3))
                            .addComponent(lblAlign)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblMarginV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinV))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMarginL)
                                    .addComponent(lblMarginR))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spinL)
                                    .addComponent(spinR))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblAngleZ)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(spinAngleZ, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblScaleY)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(spinScaleY, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(lblScaleX)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(spinScaleX, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblSpacing)
                                        .addGap(22, 22, 22)
                                        .addComponent(spinSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblBorder)
                                            .addComponent(lblShadow))
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(spinShadow)
                                            .addComponent(spinBorder))))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(chkOpaqueBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStyleName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfStyleName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEncoding)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblSample)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfSample)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFontName)
                    .addComponent(lblFontStyle)
                    .addComponent(spinFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFontSize)
                    .addComponent(lblAlign)
                    .addComponent(lblBorder)
                    .addComponent(spinBorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rb7)
                            .addComponent(rb8)
                            .addComponent(rb9)
                            .addComponent(spinShadow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblShadow))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rb4)
                            .addComponent(rb5)
                            .addComponent(rb6)
                            .addComponent(chkOpaqueBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rb1)
                            .addComponent(rb2)
                            .addComponent(rb3)
                            .addComponent(spinScaleX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblScaleX))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMarginL)
                            .addComponent(spinL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinScaleY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblScaleY))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMarginR)
                            .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinAngleZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAngleZ))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMarginV)
                            .addComponent(spinV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSpacing)
                            .addComponent(spinSpacing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEncoding)
                    .addComponent(cbEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStyleName)
                    .addComponent(tfStyleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSample))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel)
                    .addComponent(btnCollections)
                    .addComponent(btnImportStyles))
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

    private void listFontNamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFontNamesValueChanged
        // Quand on clique sur un nom de la liste de famille
        String selectedName = listFontNames.getSelectedValue();
        
    }//GEN-LAST:event_listFontNamesValueChanged

    private void listFontStylesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFontStylesValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_listFontStylesValueChanged

    private void lblColorTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorTextMouseClicked
        // Si on clique dans la zone colorée, invite à changer de couleur
        // Couleur de texte
        colorDialog = new ColorDialog(parent, true);
        colorDialog.showDialog(lblColorText.getBackground(), (int)spinAlphaText.getValue());
        if(colorDialog.getDialogResult() == DialogResult.Ok){
            lblColorText.setBackground(colorDialog.getColor());
            spinAlphaText.setValue(colorDialog.getAlpha());
        }
    }//GEN-LAST:event_lblColorTextMouseClicked

    private void lblColorKaraokeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorKaraokeMouseClicked
        // Si on clique dans la zone colorée, invite à changer de couleur
        // Couleur de karaoke
        colorDialog = new ColorDialog(parent, true);
        colorDialog.showDialog(lblColorKaraoke.getBackground(), (int)spinAlphaKaraoke.getValue());
        if(colorDialog.getDialogResult() == DialogResult.Ok){
            lblColorKaraoke.setBackground(colorDialog.getColor());
            spinAlphaKaraoke.setValue(colorDialog.getAlpha());
        }
    }//GEN-LAST:event_lblColorKaraokeMouseClicked

    private void lblColorOutlineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorOutlineMouseClicked
        // Si on clique dans la zone colorée, invite à changer de couleur
        // Couleur de bordure
        colorDialog = new ColorDialog(parent, true);
        colorDialog.showDialog(lblColorOutline.getBackground(), (int)spinAlphaOutline.getValue());
        if(colorDialog.getDialogResult() == DialogResult.Ok){
            lblColorOutline.setBackground(colorDialog.getColor());
            spinAlphaOutline.setValue(colorDialog.getAlpha());
        }
    }//GEN-LAST:event_lblColorOutlineMouseClicked

    private void lblColorShadowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorShadowMouseClicked
        // Si on clique dans la zone colorée, invite à changer de couleur
        // Couleur d'ombre portée
        colorDialog = new ColorDialog(parent, true);
        colorDialog.showDialog(lblColorShadow.getBackground(), (int)spinAlphaShadow.getValue());
        if(colorDialog.getDialogResult() == DialogResult.Ok){
            lblColorShadow.setBackground(colorDialog.getColor());
            spinAlphaShadow.setValue(colorDialog.getAlpha());
        }
    }//GEN-LAST:event_lblColorShadowMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                StyleEditDialog dialog = new StyleEditDialog(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgAlignment;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCollections;
    private javax.swing.JButton btnImportStyles;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox<String> cbEncoding;
    private javax.swing.JCheckBox chkOpaqueBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAlign;
    private javax.swing.JLabel lblAngleZ;
    private javax.swing.JLabel lblBorder;
    private javax.swing.JLabel lblColor1;
    private javax.swing.JLabel lblColor2;
    private javax.swing.JLabel lblColor3;
    private javax.swing.JLabel lblColor4;
    private javax.swing.JLabel lblColorKaraoke;
    private javax.swing.JLabel lblColorOutline;
    private javax.swing.JLabel lblColorShadow;
    private javax.swing.JLabel lblColorText;
    private javax.swing.JLabel lblEncoding;
    private javax.swing.JLabel lblFontName;
    private javax.swing.JLabel lblFontSize;
    private javax.swing.JLabel lblFontStyle;
    private javax.swing.JLabel lblMarginL;
    private javax.swing.JLabel lblMarginR;
    private javax.swing.JLabel lblMarginV;
    private javax.swing.JLabel lblSample;
    private javax.swing.JLabel lblScaleX;
    private javax.swing.JLabel lblScaleY;
    private javax.swing.JLabel lblShadow;
    private javax.swing.JLabel lblSpacing;
    private javax.swing.JLabel lblStyleName;
    private javax.swing.JList<String> listFontNames;
    private javax.swing.JList<String> listFontStyles;
    private javax.swing.JRadioButton rb1;
    private javax.swing.JRadioButton rb2;
    private javax.swing.JRadioButton rb3;
    private javax.swing.JRadioButton rb4;
    private javax.swing.JRadioButton rb5;
    private javax.swing.JRadioButton rb6;
    private javax.swing.JRadioButton rb7;
    private javax.swing.JRadioButton rb8;
    private javax.swing.JRadioButton rb9;
    private javax.swing.JSpinner spinAlphaKaraoke;
    private javax.swing.JSpinner spinAlphaOutline;
    private javax.swing.JSpinner spinAlphaShadow;
    private javax.swing.JSpinner spinAlphaText;
    private javax.swing.JSpinner spinAngleZ;
    private javax.swing.JSpinner spinBorder;
    private javax.swing.JSpinner spinFontSize;
    private javax.swing.JSpinner spinL;
    private javax.swing.JSpinner spinR;
    private javax.swing.JSpinner spinScaleX;
    private javax.swing.JSpinner spinScaleY;
    private javax.swing.JSpinner spinShadow;
    private javax.swing.JSpinner spinSpacing;
    private javax.swing.JSpinner spinV;
    private javax.swing.JTextField tfSample;
    private javax.swing.JTextField tfStyleName;
    // End of variables declaration//GEN-END:variables
}
