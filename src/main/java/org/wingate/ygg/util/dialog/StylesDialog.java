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
package org.wingate.ygg.util.dialog;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;

/**
 *
 * @author util2
 */
public class StylesDialog extends javax.swing.JDialog {
    
    public enum DialogResult{
        Unknown, Cancel, Ok;
    }
    
    private DialogResult dialogResult = DialogResult.Unknown;    
    private Map<String, Style> styles = new HashMap<>();    
    private final DefaultComboBoxModel dcbmStyles = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmFonts = new DefaultComboBoxModel();
    private final SpinnerNumberModel snmFontsize = new SpinnerNumberModel(12, 1, 10000, 1);
    // Alpha
    private final SpinnerNumberModel snmA1 = new SpinnerNumberModel(0, 0, 255, 1);
    private final SpinnerNumberModel snmA2 = new SpinnerNumberModel(0, 0, 255, 1);
    private final SpinnerNumberModel snmA3 = new SpinnerNumberModel(0, 0, 255, 1);
    private final SpinnerNumberModel snmA4 = new SpinnerNumberModel(0, 0, 255, 1);
    // Border & Shadow
    private final SpinnerNumberModel snmBorder = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmShadow = new SpinnerNumberModel(0, 0, 100000, 1);
    // ScaleXY, Angle & Spacing
    private final SpinnerNumberModel snmScaleX = new SpinnerNumberModel(100, 0, 100000, 1);
    private final SpinnerNumberModel snmScaleY = new SpinnerNumberModel(100, 0, 100000, 1);
    private final SpinnerNumberModel snmAngle = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmSpacing = new SpinnerNumberModel(0, 0, 100000, 1);
    // Encoding
    private final DefaultComboBoxModel dcbmEncoding = new DefaultComboBoxModel();
    // Margins
    private final SpinnerNumberModel snmMarginL = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmMarginR = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmMarginV = new SpinnerNumberModel(0, 0, 100000, 1);

    /**
     * Creates new form StylesDialog
     * @param parent
     * @param modal
     */
    public StylesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }
    
    private void init(){
        comboStyleName.setModel(dcbmStyles);
        comboFonts.setModel(dcbmFonts);
        spinFontsize.setModel(snmFontsize);
        spinTextAlpha.setModel(snmA1);
        spinKaraokeAlpha.setModel(snmA2);
        spinBorderAlpha.setModel(snmA3);
        spinShadowAlpha.setModel(snmA4);
        spinBorderValue.setModel(snmBorder);
        spinShadowValue.setModel(snmShadow);
        spinScaleX.setModel(snmScaleX);
        spinScaleY.setModel(snmScaleY);
        spinAngle.setModel(snmAngle);
        spinSpacing.setModel(snmSpacing);
        comboEncoding.setModel(dcbmEncoding);
        spinL.setModel(snmMarginL);
        spinR.setModel(snmMarginR);
        spinV.setModel(snmMarginV);
        
        Language in = MainFrame.getLanguage();
        ISO_3166 get = MainFrame.getISO();
        
        // Set video media name placeholder
        tfVideoMediaName.setText("");
        tfVideoMediaName.setPlaceholder(in.getTranslated("AssStyleMediaTitle", get, "Title of the media"));
        
        // Set words sample placeholder
        tfWordsSample.setText("");
        tfWordsSample.setPlaceholder(in.getTranslated("AssStyleWordsSample", get, "[ALPHABET];[alphabet];[numbers];"));
        
    }
    
    private void resetStyle(){
        // Fill styles
        dcbmStyles.removeAllElements();
        styles.entrySet().forEach((entry) -> {
            dcbmStyles.addElement(entry.getValue());
        });
        
        // Verify and correct
        if(comboStyleName.getItemCount() == 0){
            dcbmStyles.addElement(Style.getDefault());
        }
        
        // Choose a style (first)
        comboStyleName.setSelectedIndex(0);
        
        // Search for fonts
        dcbmFonts.removeAllElements();
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = e.getAllFonts();
        for(Font font : fonts){
            dcbmFonts.addElement(font.getFontName());
        }
        
        // Select fontname or name it if error
        String fName = ((Style)comboStyleName.getSelectedItem()).getFontname();
        boolean fontNotInSystem = true;
        for(Font font : fonts){
            if(font.getFontName().equals(fName) == true){
                fontNotInSystem = false;
                break;
            }
        }
        if(fontNotInSystem){
            comboFonts.addItem(fName);
        }
        comboFonts.setSelectedItem(fName);
        
        // Get selected style
        Style sty = styles.get(comboStyleName.getSelectedItem().toString());
        
        // BIUS
        checkBold.setSelected(sty.isBold());
        checkItalic.setSelected(sty.isItalic());
        checkUnderline.setSelected(sty.isUnderline());
        checkStrikeout.setSelected(sty.isStrikeout());
        
        // Color and alpha
        lblTextColor.setBackground(sty.getTextColor());
        lblKaraokeColor.setBackground(sty.getKaraokeColor());
        lblBorderColor.setBackground(sty.getBordColor());
        lblShadowColor.setBackground(sty.getShadColor());
        snmA1.setValue(sty.getTextAlphaStr());
        snmA2.setValue(sty.getKaraokeAlphaStr());
        snmA3.setValue(sty.getBordAlphaStr());
        snmA4.setValue(sty.getShadAlphaStr());
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }
    
    public void showDialog(Map<String, Style> styles){
        this.styles = styles;
        resetStyle();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgEditStyle = new javax.swing.ButtonGroup();
        btnSaveStyle = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeCollections = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableCollections = new javax.swing.JTable();
        OK_BUTTON = new javax.swing.JButton();
        CANCEL_BUTTON = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        paneStyleName = new javax.swing.JPanel();
        comboStyleName = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        comboFonts = new javax.swing.JComboBox<>();
        spinFontsize = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        checkBold = new javax.swing.JCheckBox();
        checkItalic = new javax.swing.JCheckBox();
        checkUnderline = new javax.swing.JCheckBox();
        checkStrikeout = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTextColor = new javax.swing.JLabel();
        lblKaraokeColor = new javax.swing.JLabel();
        lblBorderColor = new javax.swing.JLabel();
        lblShadowColor = new javax.swing.JLabel();
        spinTextAlpha = new javax.swing.JSpinner();
        spinKaraokeAlpha = new javax.swing.JSpinner();
        spinBorderAlpha = new javax.swing.JSpinner();
        spinShadowAlpha = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        spinBorderValue = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        spinShadowValue = new javax.swing.JSpinner();
        checkOpaqueBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        radio7 = new javax.swing.JRadioButton();
        radio8 = new javax.swing.JRadioButton();
        radio9 = new javax.swing.JRadioButton();
        radio4 = new javax.swing.JRadioButton();
        radio5 = new javax.swing.JRadioButton();
        radio6 = new javax.swing.JRadioButton();
        radio1 = new javax.swing.JRadioButton();
        radio2 = new javax.swing.JRadioButton();
        radio3 = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        spinL = new javax.swing.JSpinner();
        spinR = new javax.swing.JSpinner();
        spinV = new javax.swing.JSpinner();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        spinScaleX = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        spinScaleY = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        spinAngle = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        spinSpacing = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        comboEncoding = new javax.swing.JComboBox<>();
        jPanel14 = new javax.swing.JPanel();
        tfWordsSample = new org.wingate.placeholdertextfield.PlaceholderTextField();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        lblDisplay = new javax.swing.JLabel();
        tfVideoMediaName = new org.wingate.placeholdertextfield.PlaceholderTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Styles");

        btnSaveStyle.setText("Save style");

        jLabel20.setText("Video media name : ");

        jScrollPane1.setViewportView(treeCollections);

        tableCollections.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tableCollections);

        OK_BUTTON.setText("OK");
        OK_BUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OK_BUTTONActionPerformed(evt);
            }
        });

        CANCEL_BUTTON.setText("Cancel");
        CANCEL_BUTTON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CANCEL_BUTTONActionPerformed(evt);
            }
        });

        btnImport.setText("Import styles...");

        jPanel18.setLayout(new javax.swing.BoxLayout(jPanel18, javax.swing.BoxLayout.Y_AXIS));

        paneStyleName.setBorder(javax.swing.BorderFactory.createTitledBorder("Style name"));

        comboStyleName.setEditable(true);
        comboStyleName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboStyleName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboStyleNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneStyleNameLayout = new javax.swing.GroupLayout(paneStyleName);
        paneStyleName.setLayout(paneStyleNameLayout);
        paneStyleNameLayout.setHorizontalGroup(
            paneStyleNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comboStyleName, 0, 555, Short.MAX_VALUE)
        );
        paneStyleNameLayout.setVerticalGroup(
            paneStyleNameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneStyleNameLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(comboStyleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.add(paneStyleName);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Font properties"));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 94));

        comboFonts.setEditable(true);
        comboFonts.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jPanel4.setLayout(new java.awt.GridLayout(2, 2));

        checkBold.setText("Bold");
        jPanel4.add(checkBold);

        checkItalic.setText("Italic");
        jPanel4.add(checkItalic);

        checkUnderline.setText("Underline");
        jPanel4.add(checkUnderline);

        checkStrikeout.setText("Strikeout");
        jPanel4.add(checkStrikeout);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(comboFonts, 0, 489, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinFontsize, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboFonts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinFontsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.add(jPanel3);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Colors"));
        jPanel5.setPreferredSize(new java.awt.Dimension(200, 106));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(156, 60));
        jPanel1.setLayout(new java.awt.GridLayout(3, 4));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Text");
        jPanel1.add(jLabel1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Karaoke");
        jPanel1.add(jLabel2);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Border");
        jPanel1.add(jLabel5);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Shadow");
        jPanel1.add(jLabel3);

        lblTextColor.setBackground(new java.awt.Color(255, 255, 255));
        lblTextColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTextColor.setOpaque(true);
        jPanel1.add(lblTextColor);

        lblKaraokeColor.setBackground(new java.awt.Color(255, 255, 153));
        lblKaraokeColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKaraokeColor.setOpaque(true);
        jPanel1.add(lblKaraokeColor);

        lblBorderColor.setBackground(new java.awt.Color(0, 0, 0));
        lblBorderColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblBorderColor.setOpaque(true);
        jPanel1.add(lblBorderColor);

        lblShadowColor.setBackground(new java.awt.Color(0, 0, 0));
        lblShadowColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblShadowColor.setOpaque(true);
        jPanel1.add(lblShadowColor);
        jPanel1.add(spinTextAlpha);
        jPanel1.add(spinKaraokeAlpha);
        jPanel1.add(spinBorderAlpha);
        jPanel1.add(spinShadowAlpha);

        jPanel5.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel18.add(jPanel5);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Border & Shadow"));

        jPanel11.setLayout(new java.awt.GridLayout(3, 2));

        jLabel12.setText("<html>&nbsp;&nbsp;&nbsp;Border :");
        jPanel11.add(jLabel12);
        jPanel11.add(spinBorderValue);

        jLabel13.setText("<html>&nbsp;&nbsp;&nbsp;Shadow :");
        jPanel11.add(jLabel13);
        jPanel11.add(spinShadowValue);

        checkOpaqueBox.setText("Opaque box");
        jPanel11.add(checkOpaqueBox);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.add(jPanel10);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Alignment"));

        jPanel9.setLayout(new java.awt.GridLayout(3, 3));

        bgEditStyle.add(radio7);
        radio7.setText("7");
        radio7.setToolTipText("NumPad 7");
        jPanel9.add(radio7);

        bgEditStyle.add(radio8);
        radio8.setText("8");
        radio8.setToolTipText("NumPad 8");
        jPanel9.add(radio8);

        bgEditStyle.add(radio9);
        radio9.setText("9");
        radio9.setToolTipText("NumPad 9");
        jPanel9.add(radio9);

        bgEditStyle.add(radio4);
        radio4.setText("4");
        radio4.setToolTipText("NumPad 4");
        jPanel9.add(radio4);

        bgEditStyle.add(radio5);
        radio5.setText("5");
        radio5.setToolTipText("NumPad 5");
        jPanel9.add(radio5);

        bgEditStyle.add(radio6);
        radio6.setText("6");
        radio6.setToolTipText("NumPad 6");
        jPanel9.add(radio6);

        bgEditStyle.add(radio1);
        radio1.setText("1");
        radio1.setToolTipText("NumPad 1");
        jPanel9.add(radio1);

        bgEditStyle.add(radio2);
        radio2.setSelected(true);
        radio2.setText("2");
        radio2.setToolTipText("NumPad 2");
        jPanel9.add(radio2);

        bgEditStyle.add(radio3);
        radio3.setText("3");
        radio3.setToolTipText("NumPad 3");
        jPanel9.add(radio3);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.add(jPanel8);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Margins"));
        jPanel6.setPreferredSize(new java.awt.Dimension(200, 62));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel7.setLayout(new java.awt.GridLayout(2, 3));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("L");
        jPanel7.add(jLabel9);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("R");
        jPanel7.add(jLabel11);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("V");
        jPanel7.add(jLabel10);
        jPanel7.add(spinL);
        jPanel7.add(spinR);
        jPanel7.add(spinV);

        jPanel6.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel6);

        jPanel18.add(jPanel2);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jPanel12.setPreferredSize(new java.awt.Dimension(360, 99));

        jPanel13.setLayout(new java.awt.GridLayout(2, 4));

        jLabel14.setText("<html>&nbsp;&nbsp;&nbsp;ScaleX (%) :");
        jPanel13.add(jLabel14);
        jPanel13.add(spinScaleX);

        jLabel15.setText("<html>&nbsp;&nbsp;&nbsp;ScaleY (%) :");
        jPanel13.add(jLabel15);
        jPanel13.add(spinScaleY);

        jLabel16.setText("<html>&nbsp;&nbsp;&nbsp;Angle (°) :");
        jPanel13.add(jLabel16);
        jPanel13.add(spinAngle);

        jLabel17.setText("<html>&nbsp;&nbsp;&nbsp;Spacing :");
        jPanel13.add(jLabel17);
        jPanel13.add(spinSpacing);

        jLabel18.setText("<html>&nbsp;&nbsp;&nbsp;Encoding :");

        comboEncoding.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.add(jPanel12);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Words sample"));

        tfWordsSample.setText("placeholderTextField1");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfWordsSample, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(tfWordsSample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 20, Short.MAX_VALUE))
        );

        jPanel18.add(jPanel14);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Display sample"));

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblDisplay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel18.add(jPanel15);

        tfVideoMediaName.setText("placeholderTextField2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnImport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSaveStyle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CANCEL_BUTTON)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(OK_BUTTON))
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(tfVideoMediaName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(tfVideoMediaName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImport)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CANCEL_BUTTON)
                        .addComponent(OK_BUTTON)
                        .addComponent(btnSaveStyle)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OK_BUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OK_BUTTONActionPerformed
        dialogResult = DialogResult.Ok;
        dispose();
    }//GEN-LAST:event_OK_BUTTONActionPerformed

    private void CANCEL_BUTTONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CANCEL_BUTTONActionPerformed
        dialogResult = DialogResult.Cancel;
        dispose();
    }//GEN-LAST:event_CANCEL_BUTTONActionPerformed

    private void comboStyleNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboStyleNameActionPerformed
        resetStyle();
    }//GEN-LAST:event_comboStyleNameActionPerformed

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
            java.util.logging.Logger.getLogger(StylesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                StylesDialog dialog = new StylesDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton CANCEL_BUTTON;
    private javax.swing.JButton OK_BUTTON;
    private javax.swing.ButtonGroup bgEditStyle;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnSaveStyle;
    private javax.swing.JCheckBox checkBold;
    private javax.swing.JCheckBox checkItalic;
    private javax.swing.JCheckBox checkOpaqueBox;
    private javax.swing.JCheckBox checkStrikeout;
    private javax.swing.JCheckBox checkUnderline;
    private javax.swing.JComboBox<String> comboEncoding;
    private javax.swing.JComboBox<String> comboFonts;
    private javax.swing.JComboBox<String> comboStyleName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBorderColor;
    private javax.swing.JLabel lblDisplay;
    private javax.swing.JLabel lblKaraokeColor;
    private javax.swing.JLabel lblShadowColor;
    private javax.swing.JLabel lblTextColor;
    private javax.swing.JPanel paneStyleName;
    private javax.swing.JRadioButton radio1;
    private javax.swing.JRadioButton radio2;
    private javax.swing.JRadioButton radio3;
    private javax.swing.JRadioButton radio4;
    private javax.swing.JRadioButton radio5;
    private javax.swing.JRadioButton radio6;
    private javax.swing.JRadioButton radio7;
    private javax.swing.JRadioButton radio8;
    private javax.swing.JRadioButton radio9;
    private javax.swing.JSpinner spinAngle;
    private javax.swing.JSpinner spinBorderAlpha;
    private javax.swing.JSpinner spinBorderValue;
    private javax.swing.JSpinner spinFontsize;
    private javax.swing.JSpinner spinKaraokeAlpha;
    private javax.swing.JSpinner spinL;
    private javax.swing.JSpinner spinR;
    private javax.swing.JSpinner spinScaleX;
    private javax.swing.JSpinner spinScaleY;
    private javax.swing.JSpinner spinShadowAlpha;
    private javax.swing.JSpinner spinShadowValue;
    private javax.swing.JSpinner spinSpacing;
    private javax.swing.JSpinner spinTextAlpha;
    private javax.swing.JSpinner spinV;
    private javax.swing.JTable tableCollections;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfVideoMediaName;
    private org.wingate.placeholdertextfield.PlaceholderTextField tfWordsSample;
    private javax.swing.JTree treeCollections;
    // End of variables declaration//GEN-END:variables
}
