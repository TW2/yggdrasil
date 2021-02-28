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

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.subs.ass.AssStyle;
import org.wingate.ygg.subs.ass.AssStylesCollection;
import org.wingate.ygg.subs.ass.AssStylesCollectionConf;
import org.wingate.ygg.subs.ass.AssYggyApply;

/**
 *
 * @author util2
 */
public class StylesDialog extends javax.swing.JDialog {
    
    public enum DialogResult{
        Unknown, Cancel, Ok;
    }
    
    public enum Encoding{
        Ansi("ANSI", 0),
        Default("Default", 1),
        Symbol("Symbol", 2),
        Mac("MAC", 77),
        ShiftJIS("Shift-JIS", 128),
        Hangul("Hangul", 129),
        Johab("Johab", 130),
        GB2312("GB2312", 134),
        Chinese("Big5", 136),
        Greek("Greek", 161),
        Turkish("Turkish", 162),
        Vietnamese("Vietnamese", 163),
        Hebrew("Hebrew", 177),
        Arab("Arab", 178),
        Balt("Balt", 186),
        Russian("Russian", 204),
        Thai("Thai", 222),
        EasternEUR("Eastern EUR", 238),
        OEM("OEM", 255);
        
        String name;
        int pageCode;
        
        private Encoding(String name, int pageCode){
            this.name = name;
            this.pageCode = pageCode;
        }
        
        public String getName(){
            return name;
        }
        
        public int getCode(){
            return pageCode;
        }
        
        public static Encoding find(Object o){
            Encoding enc = Default;
            
            if(o instanceof String){
                String str = (String)o;
                for(Encoding e : values()){
                    if(e.name.equalsIgnoreCase(str) == true){
                        enc = e; break;
                    }
                }
            }else if(o instanceof Integer){
                int code = (Integer)o;
                for(Encoding e : values()){
                    if(e.pageCode == code){
                        enc = e; break;
                    }
                }
            }
            
            return enc;
        }

        @Override
        public String toString() {
            return name + " - " + pageCode;
        }
    }
    
    private DialogResult dialogResult = DialogResult.Unknown;    
    private Map<String, AssStyle> styles = new HashMap<>();
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
    
    // ASS
    private ASS ass = ASS.NoFileToLoad();
    // Style
    private AssStyle style = AssStyle.getDefault();
    // Event
    private final AssEvent ev = new AssEvent();
    // View
    private final AssStyleGridPanel assStyleGridPanel = new AssStyleGridPanel(null);
    // Managing styles collections
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("All collections");
    private final DefaultTreeModel dtmMoviesStyles = new DefaultTreeModel(root);
    private DefaultTableModel stylesModel = null;
    private final List<AssSelectedPacket> packets = new ArrayList<>();

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
        // Save of styles
        File folder = new File("save");
        if(folder.exists() == false) folder.mkdir();
        
        // Models for style
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
        
        // Fill encoding
        for(Encoding enc : Encoding.values()){
            dcbmEncoding.addElement(enc);
        }
        
        // Style
        jPanel16.add(assStyleGridPanel);
        assStyleGridPanel.setSize(554, 132);
        
        jPanel16.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                showScript();
            }
        });
        
        // Styles collection
        // Tableau
        stylesModel = new DefaultTableModel(
                null,
                new String[]{"In script ?", "Style"}
        ){
            Class[] types = new Class [] {Boolean.class, String.class};
            boolean[] canEdit = new boolean [] {true, false};
            @Override
            public Class getColumnClass(int columnIndex) {return types [columnIndex];}
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        };
        
        tableCollections.setModel(stylesModel);
        
        TableColumn column;
        for (int i = 0; i < 2; i++) {
            column = tableCollections.getColumnModel().getColumn(i);
            switch(i){
                case 0 -> column.setPreferredWidth(60);
                case 1 -> column.setPreferredWidth(300);
            }
        }
        
        stylesModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)treeCollections.getLastSelectedPathComponent();
                
                //Nothing is selected.
                if (node == null) return;
                
                if(node.getUserObject() instanceof AssStylesCollection && e.getColumn() == 0){
                    AssStylesCollection coll = (AssStylesCollection)node.getUserObject();
                    boolean value = (boolean)tableCollections.getValueAt(e.getFirstRow(), 0);
                    AssStyle sty = (AssStyle)tableCollections.getValueAt(e.getFirstRow(), 1);
                    for(AssSelectedPacket packet : packets){
                        if(packet.getAssCollectionName().equals(coll.getCollectionName()) == true){
                            for(Map.Entry<String, AssStyle> entry : coll.getStyles().entrySet()){
                                if(entry.getKey().equals(sty.getName()) 
                                        && entry.getKey().equals(packet.getAssStyle().getName())){
                                    packet.setSelected(value);
                                    
                                }
                            }
                            
                        }
                    }
                    refreshFromPackets();
                }
                revalidate();
            }
        });
        
        popmAddStyleToCollection.setText(in.getTranslated("StyleColAdd", get, "Add this collection's name"));
        popmRemoveStyleFromCollection.setText(in.getTranslated("StyleColRem", get, "Remove this collection"));
        treeCollections.setModel(dtmMoviesStyles);
        treeCollections.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)treeCollections.getLastSelectedPathComponent();
                
                //Nothing is selected.
                if (node == null) return;
                
                if(node.getUserObject() instanceof AssStylesCollection){
                    AssStylesCollection coll = (AssStylesCollection)node.getUserObject();
                    if(stylesModel.getRowCount() > 0){
                        for(int i=stylesModel.getRowCount()-1; i>=0; i--){
                            stylesModel.removeRow(i);
                        }
                    }
                    packets.sort(new Comparator<AssSelectedPacket>(){
                        @Override
                        public int compare(AssSelectedPacket o1, AssSelectedPacket o2) {
                            return o1.assStyle.getName().compareTo(o2.assStyle.getName());
                        }
                    });
                    for(AssSelectedPacket packet : packets){
                        if(packet.getAssCollectionName().equals(coll.getCollectionName()) == true){
                            stylesModel.addRow(new Object[]{
                                packet.isSelected(), packet.getAssStyle()
                            });
                        }
                    }
                }
                tableCollections.updateUI();
            }
        });
    }
    
    private void resetStyle(){
        // Verify and correct
        if(styles.isEmpty() == true){
            styles.put("Default", AssStyle.getDefault());
        }
        
        // Fill styles
        dcbmStyles.removeAllElements();
        Map<String, AssStyle> sortedStyles = new LinkedHashMap<>();
        
        // On range en ordre (besoin de LinkedHashMap)
        styles.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedStyles.put(x.getKey(), x.getValue()));
        
        // On remplit le combobox de styles
        sortedStyles.entrySet().forEach((entry) -> {
            dcbmStyles.addElement(entry.getValue());
        });
        
        // Search for fonts
        dcbmFonts.removeAllElements();
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = e.getAllFonts();
        for(Font font : fonts){
            dcbmFonts.addElement(font.getFontName());
        }
        
        // Select fontname or name it if error
        String fName = ((AssStyle)comboStyleName.getSelectedItem()).getFontname();
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
        AssStyle sty = styles.get(comboStyleName.getSelectedItem().toString());
        if(sty == null) sty = AssStyle.getDefault();
        
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

    public ASS getAss() {
        return ass;
    }

    public void setAss(ASS ass) {
        this.ass = ass;
    }
    
    public int showScript(){
        try{
            ass.setResX(Integer.toString(554));
            ass.setResY(Integer.toString(132));
            ev.setLineType(AssEvent.LineType.Dialogue);            
            ev.setStartTime(Time.create(0L));
            ev.setEndTime(Time.create(10L));
            ev.setMarginL(snmMarginL.getNumber().intValue());
            ev.setMarginR(snmMarginR.getNumber().intValue());
            ev.setMarginV(snmMarginV.getNumber().intValue());
            ass.getStyles().clear();
            ass.getStyles().put("Default", style);
            String wordsSample = tfWordsSample.getText();
            ev.setText(wordsSample.isEmpty() ? assStyleGridPanel.getTest() : wordsSample);
            if(ass.getEvents().isEmpty()){
                ass.getEvents().add(ev);
            }else{
                ass.getEvents().clear();
                ass.getEvents().add(ev);
            }
            File folder = new File("configuration");
            if(folder.exists() == false) folder.mkdirs();
            String path = new File(folder, "test.ass").getPath();
            ASS.Save(path, ass);
            BufferedImage img = AssYggyApply.getSubsImage(path, Time.create(5L));
            assStyleGridPanel.setBufferedImage(img);
            assStyleGridPanel.repaint();
        }catch(Exception exc){
            return 1;
        }        
        return 0;
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }
    
    public void showDialog(Map<String, AssStyle> styles){
        this.styles = styles;
        resetStyle();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public Map<String, AssStyle> getStyles(){
        return styles;
    }
    
    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }

        if(tree.getRowCount()!=rowCount){
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
    
    private void addStyleCollection(String collection, Map<String, AssStyle> map){
        AssStylesCollection asc = AssStylesCollection.create(collection, map);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(asc);
        root.add(node);        
        expandAllNodes(treeCollections, 0, treeCollections.getRowCount());
        treeCollections.updateUI();
    }
    
    private void deleteCollection(){
        TreePath[] paths = treeCollections.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(node.getUserObject() instanceof AssStylesCollection
                        && treeCollections.getSelectionPath().equals(path) == true){
                    // Retrait dans l'arbre
                    dtmMoviesStyles.removeNodeFromParent(node);
                    break;
                }
            }
        }
    }
    
    private AssStyle copy(AssStyle old, String name){
        AssStyle sty = new AssStyle();
        
        sty.setName(name);
        sty.setAlignment(old.getAlignment());
        sty.setAngle(old.getAngle());
        sty.setBold(old.isBold());
        sty.setBordColor(old.getBordColor());
        sty.setBorderStyle(old.getBorderStyle());
        sty.setEncoding(old.getEncoding());
        sty.setFontname(old.getFontname());
        sty.setFontsize(old.getFontsize());
        sty.setItalic(old.isItalic());
        sty.setKaraokeColor(old.getKaraokeColor());
        sty.setMarginB(old.getMarginB());
        sty.setMarginL(old.getMarginL());
        sty.setMarginR(old.getMarginR());
        sty.setMarginT(old.getMarginT());
        sty.setMarginV(old.getMarginV());
        sty.setOutline(old.getOutline());
        sty.setScaleX(old.getScaleX());
        sty.setScaleY(old.getScaleY());
        sty.setShadColor(old.getShadColor());
        sty.setShadow(old.getShadow());
        sty.setSpacing(old.getSpacing());
        sty.setStrikeout(old.isStrikeout());
        sty.setTextColor(old.getTextColor());
        sty.setUnderline(old.isUnderline());
        
        return sty;
    }

    public List<AssSelectedPacket> getPackets() {
        return packets;
    }
    
    private void refreshFromPackets(){        
        for(AssSelectedPacket asp : packets){
            if(asp.isSelected() == true && styles.containsKey(asp.getAssStyle().getName()) == false){
                styles.put(asp.getAssStyle().getName(), asp.getAssStyle());
            }else if(asp.isSelected() == false && styles.containsKey(asp.getAssStyle().getName()) == true){
                styles.remove(asp.getAssStyle().getName());
            }
        }
        // Verify and correct
        if(styles.isEmpty() == true){
            styles.put("Default", AssStyle.getDefault());
        }
        
        // Fill styles
        dcbmStyles.removeAllElements();
        Map<String, AssStyle> sortedStyles = new LinkedHashMap<>();
        
        // On range en ordre (besoin de LinkedHashMap)
        styles.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> sortedStyles.put(x.getKey(), x.getValue()));
        
        // On remplit le combobox de styles
        sortedStyles.entrySet().forEach((entry) -> {
            dcbmStyles.addElement(entry.getValue());
        });
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
        popStyleRW = new javax.swing.JPopupMenu();
        popmAddStyleToCollection = new javax.swing.JMenuItem();
        popmRemoveStyleFromCollection = new javax.swing.JMenuItem();
        btnAddStyleToCollection = new javax.swing.JButton();
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
        tfVideoMediaName = new org.wingate.placeholdertextfield.PlaceholderTextField();
        btnSaveCollection = new javax.swing.JButton();

        popmAddStyleToCollection.setText("jMenuItem1");
        popmAddStyleToCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmAddStyleToCollectionActionPerformed(evt);
            }
        });
        popStyleRW.add(popmAddStyleToCollection);

        popmRemoveStyleFromCollection.setText("jMenuItem2");
        popmRemoveStyleFromCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmRemoveStyleFromCollectionActionPerformed(evt);
            }
        });
        popStyleRW.add(popmRemoveStyleFromCollection);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Styles");

        btnAddStyleToCollection.setText("Add style to collection");
        btnAddStyleToCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStyleToCollectionActionPerformed(evt);
            }
        });

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
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

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
        comboFonts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFontsActionPerformed(evt);
            }
        });

        spinFontsize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinFontsizeStateChanged(evt);
            }
        });

        jPanel4.setLayout(new java.awt.GridLayout(2, 2));

        checkBold.setText("Bold");
        checkBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoldActionPerformed(evt);
            }
        });
        jPanel4.add(checkBold);

        checkItalic.setText("Italic");
        checkItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkItalicActionPerformed(evt);
            }
        });
        jPanel4.add(checkItalic);

        checkUnderline.setText("Underline");
        checkUnderline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUnderlineActionPerformed(evt);
            }
        });
        jPanel4.add(checkUnderline);

        checkStrikeout.setText("Strikeout");
        checkStrikeout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkStrikeoutActionPerformed(evt);
            }
        });
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
        lblTextColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTextColorMouseClicked(evt);
            }
        });
        jPanel1.add(lblTextColor);

        lblKaraokeColor.setBackground(new java.awt.Color(255, 255, 153));
        lblKaraokeColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblKaraokeColor.setOpaque(true);
        lblKaraokeColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblKaraokeColorMouseClicked(evt);
            }
        });
        jPanel1.add(lblKaraokeColor);

        lblBorderColor.setBackground(new java.awt.Color(0, 0, 0));
        lblBorderColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblBorderColor.setOpaque(true);
        lblBorderColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBorderColorMouseClicked(evt);
            }
        });
        jPanel1.add(lblBorderColor);

        lblShadowColor.setBackground(new java.awt.Color(0, 0, 0));
        lblShadowColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblShadowColor.setOpaque(true);
        lblShadowColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShadowColorMouseClicked(evt);
            }
        });
        jPanel1.add(lblShadowColor);

        spinTextAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinTextAlphaStateChanged(evt);
            }
        });
        jPanel1.add(spinTextAlpha);

        spinKaraokeAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinKaraokeAlphaStateChanged(evt);
            }
        });
        jPanel1.add(spinKaraokeAlpha);

        spinBorderAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBorderAlphaStateChanged(evt);
            }
        });
        jPanel1.add(spinBorderAlpha);

        spinShadowAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinShadowAlphaStateChanged(evt);
            }
        });
        jPanel1.add(spinShadowAlpha);

        jPanel5.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel18.add(jPanel5);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Border & Shadow"));

        jPanel11.setLayout(new java.awt.GridLayout(3, 2));

        jLabel12.setText("<html>&nbsp;&nbsp;&nbsp;Border :");
        jPanel11.add(jLabel12);

        spinBorderValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinBorderValueStateChanged(evt);
            }
        });
        jPanel11.add(spinBorderValue);

        jLabel13.setText("<html>&nbsp;&nbsp;&nbsp;Shadow :");
        jPanel11.add(jLabel13);

        spinShadowValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinShadowValueStateChanged(evt);
            }
        });
        jPanel11.add(spinShadowValue);

        checkOpaqueBox.setText("Opaque box");
        checkOpaqueBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkOpaqueBoxActionPerformed(evt);
            }
        });
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
        radio7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio7ActionPerformed(evt);
            }
        });
        jPanel9.add(radio7);

        bgEditStyle.add(radio8);
        radio8.setText("8");
        radio8.setToolTipText("NumPad 8");
        radio8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio8ActionPerformed(evt);
            }
        });
        jPanel9.add(radio8);

        bgEditStyle.add(radio9);
        radio9.setText("9");
        radio9.setToolTipText("NumPad 9");
        radio9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio9ActionPerformed(evt);
            }
        });
        jPanel9.add(radio9);

        bgEditStyle.add(radio4);
        radio4.setText("4");
        radio4.setToolTipText("NumPad 4");
        radio4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio4ActionPerformed(evt);
            }
        });
        jPanel9.add(radio4);

        bgEditStyle.add(radio5);
        radio5.setText("5");
        radio5.setToolTipText("NumPad 5");
        radio5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio5ActionPerformed(evt);
            }
        });
        jPanel9.add(radio5);

        bgEditStyle.add(radio6);
        radio6.setText("6");
        radio6.setToolTipText("NumPad 6");
        radio6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio6ActionPerformed(evt);
            }
        });
        jPanel9.add(radio6);

        bgEditStyle.add(radio1);
        radio1.setText("1");
        radio1.setToolTipText("NumPad 1");
        radio1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio1ActionPerformed(evt);
            }
        });
        jPanel9.add(radio1);

        bgEditStyle.add(radio2);
        radio2.setSelected(true);
        radio2.setText("2");
        radio2.setToolTipText("NumPad 2");
        radio2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio2ActionPerformed(evt);
            }
        });
        jPanel9.add(radio2);

        bgEditStyle.add(radio3);
        radio3.setText("3");
        radio3.setToolTipText("NumPad 3");
        radio3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radio3ActionPerformed(evt);
            }
        });
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

        spinL.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinLStateChanged(evt);
            }
        });
        jPanel7.add(spinL);

        spinR.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRStateChanged(evt);
            }
        });
        jPanel7.add(spinR);

        spinV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinVStateChanged(evt);
            }
        });
        jPanel7.add(spinV);

        jPanel6.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel6);

        jPanel18.add(jPanel2);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jPanel12.setPreferredSize(new java.awt.Dimension(360, 99));

        jPanel13.setLayout(new java.awt.GridLayout(2, 4));

        jLabel14.setText("<html>&nbsp;&nbsp;&nbsp;ScaleX (%) :");
        jPanel13.add(jLabel14);

        spinScaleX.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinScaleXStateChanged(evt);
            }
        });
        jPanel13.add(spinScaleX);

        jLabel15.setText("<html>&nbsp;&nbsp;&nbsp;ScaleY (%) :");
        jPanel13.add(jLabel15);

        spinScaleY.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinScaleYStateChanged(evt);
            }
        });
        jPanel13.add(spinScaleY);

        jLabel16.setText("<html>&nbsp;&nbsp;&nbsp;Angle (°) :");
        jPanel13.add(jLabel16);

        spinAngle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinAngleStateChanged(evt);
            }
        });
        jPanel13.add(spinAngle);

        jLabel17.setText("<html>&nbsp;&nbsp;&nbsp;Spacing :");
        jPanel13.add(jLabel17);

        spinSpacing.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinSpacingStateChanged(evt);
            }
        });
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

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Display sample (click to update)"));

        jPanel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
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
        tfVideoMediaName.setComponentPopupMenu(popStyleRW);

        btnSaveCollection.setText("Update collection");
        btnSaveCollection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveCollectionActionPerformed(evt);
            }
        });

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
                        .addComponent(btnSaveCollection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddStyleToCollection)
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
                        .addComponent(btnAddStyleToCollection)
                        .addComponent(btnSaveCollection)))
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
        AssStyle sty = (AssStyle)comboStyleName.getSelectedItem();
        if(sty == null) sty = AssStyle.getDefault();
        
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
        
        // Font
        boolean hasFontname = false;
        for(int i=0; i<dcbmFonts.getSize(); i++){
            String fontname = dcbmFonts.getElementAt(i).toString();
            if(fontname.equalsIgnoreCase(sty.getFontname()) == true){
                hasFontname = true;
                break;
            }
        }
        if(hasFontname == true){
            comboFonts.setSelectedItem(sty.getFontname());
        }
        snmFontsize.setValue(sty.getFontsize());
        
        // Border-Shadow-OpaqueBox
        snmBorder.setValue(sty.getOutline());
        snmShadow.setValue(sty.getShadow());
        checkOpaqueBox.setSelected(sty.isBorderStyleOpaque());
        
        // Alignment
        radio1.setSelected(sty.getAlignment() == 1);
        radio2.setSelected(sty.getAlignment() == 2);
        radio3.setSelected(sty.getAlignment() == 3);
        radio4.setSelected(sty.getAlignment() == 4);
        radio5.setSelected(sty.getAlignment() == 5);
        radio6.setSelected(sty.getAlignment() == 6);
        radio7.setSelected(sty.getAlignment() == 7);
        radio8.setSelected(sty.getAlignment() == 8);
        radio9.setSelected(sty.getAlignment() == 9);
        
        // Margins
        snmMarginL.setValue(sty.getMarginL());
        snmMarginR.setValue(sty.getMarginR());
        snmMarginV.setValue(sty.getMarginV());
        
        // Scale-Angle-Spacing
        snmScaleX.setValue(sty.getScaleX());
        snmScaleY.setValue(sty.getScaleY());
        snmAngle.setValue(sty.getAngle());
        snmSpacing.setValue(sty.getSpacing());
        
        // Encoding
        dcbmEncoding.setSelectedItem(Encoding.find(sty.getEncoding()));
        
        style = sty;
    }//GEN-LAST:event_comboStyleNameActionPerformed

    private void lblTextColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTextColorMouseClicked
        ColorsDialog cd = new ColorsDialog(new JFrame(), true);
        cd.showDialog(lblTextColor.getBackground());
        if(cd.getDialogResult() == ColorsDialog.DialogResult.OK){
            lblTextColor.setBackground(cd.getColor());
            style.setTextColor(cd.getColor());
        }
    }//GEN-LAST:event_lblTextColorMouseClicked

    private void lblKaraokeColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblKaraokeColorMouseClicked
        ColorsDialog cd = new ColorsDialog(new JFrame(), true);
        cd.showDialog(lblKaraokeColor.getBackground());
        if(cd.getDialogResult() == ColorsDialog.DialogResult.OK){
            lblKaraokeColor.setBackground(cd.getColor());
            style.setKaraokeColor(cd.getColor());
        }
    }//GEN-LAST:event_lblKaraokeColorMouseClicked

    private void lblBorderColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBorderColorMouseClicked
        ColorsDialog cd = new ColorsDialog(new JFrame(), true);
        cd.showDialog(lblBorderColor.getBackground());
        if(cd.getDialogResult() == ColorsDialog.DialogResult.OK){
            lblBorderColor.setBackground(cd.getColor());
            style.setBordColor(cd.getColor());
        }
    }//GEN-LAST:event_lblBorderColorMouseClicked

    private void lblShadowColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShadowColorMouseClicked
        ColorsDialog cd = new ColorsDialog(new JFrame(), true);
        cd.showDialog(lblShadowColor.getBackground());
        if(cd.getDialogResult() == ColorsDialog.DialogResult.OK){
            lblShadowColor.setBackground(cd.getColor());
            style.setShadColor(cd.getColor());
        }
    }//GEN-LAST:event_lblShadowColorMouseClicked

    private void radio1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio1ActionPerformed
        style.setAlignment(1);
    }//GEN-LAST:event_radio1ActionPerformed

    private void radio2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio2ActionPerformed
        style.setAlignment(2);
    }//GEN-LAST:event_radio2ActionPerformed

    private void radio3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio3ActionPerformed
        style.setAlignment(3);
    }//GEN-LAST:event_radio3ActionPerformed

    private void radio4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio4ActionPerformed
        style.setAlignment(4);
    }//GEN-LAST:event_radio4ActionPerformed

    private void radio5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio5ActionPerformed
        style.setAlignment(5);
    }//GEN-LAST:event_radio5ActionPerformed

    private void radio6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio6ActionPerformed
        style.setAlignment(6);
    }//GEN-LAST:event_radio6ActionPerformed

    private void radio7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio7ActionPerformed
        style.setAlignment(7);
    }//GEN-LAST:event_radio7ActionPerformed

    private void radio8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio8ActionPerformed
        style.setAlignment(8);
    }//GEN-LAST:event_radio8ActionPerformed

    private void radio9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radio9ActionPerformed
        style.setAlignment(9);
    }//GEN-LAST:event_radio9ActionPerformed

    private void comboFontsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFontsActionPerformed
        try{
            style.setFontname(dcbmFonts.getSelectedItem().toString());
        }catch(Exception exc){}        
    }//GEN-LAST:event_comboFontsActionPerformed

    private void spinFontsizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinFontsizeStateChanged
        style.setFontsize(snmFontsize.getNumber().intValue());
    }//GEN-LAST:event_spinFontsizeStateChanged

    private void checkBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoldActionPerformed
        style.setBold(checkBold.isSelected());
    }//GEN-LAST:event_checkBoldActionPerformed

    private void checkItalicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkItalicActionPerformed
        style.setItalic(checkItalic.isSelected());
    }//GEN-LAST:event_checkItalicActionPerformed

    private void checkUnderlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUnderlineActionPerformed
        style.setUnderline(checkUnderline.isSelected());
    }//GEN-LAST:event_checkUnderlineActionPerformed

    private void checkStrikeoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkStrikeoutActionPerformed
        style.setStrikeout(checkStrikeout.isSelected());
    }//GEN-LAST:event_checkStrikeoutActionPerformed

    private void spinTextAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinTextAlphaStateChanged
        Color c = style.getTextColor();
        style.setTextColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                255-snmA1.getNumber().intValue()));
    }//GEN-LAST:event_spinTextAlphaStateChanged

    private void spinKaraokeAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinKaraokeAlphaStateChanged
        Color c = style.getKaraokeColor();
        style.setKaraokeColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                255-snmA2.getNumber().intValue()));
    }//GEN-LAST:event_spinKaraokeAlphaStateChanged

    private void spinBorderAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBorderAlphaStateChanged
        Color c = style.getBordColor();
        style.setBordColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                255-snmA3.getNumber().intValue()));
    }//GEN-LAST:event_spinBorderAlphaStateChanged

    private void spinShadowAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinShadowAlphaStateChanged
        Color c = style.getShadColor();
        style.setShadColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                255-snmA4.getNumber().intValue()));
    }//GEN-LAST:event_spinShadowAlphaStateChanged

    private void spinBorderValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinBorderValueStateChanged
        style.setOutline(snmBorder.getNumber().floatValue());
    }//GEN-LAST:event_spinBorderValueStateChanged

    private void spinShadowValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinShadowValueStateChanged
        style.setShadow(snmShadow.getNumber().floatValue());
    }//GEN-LAST:event_spinShadowValueStateChanged

    private void checkOpaqueBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkOpaqueBoxActionPerformed
        style.setBorderStyle(checkOpaqueBox.isSelected());
    }//GEN-LAST:event_checkOpaqueBoxActionPerformed

    private void spinLStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinLStateChanged
        style.setMarginL(snmMarginL.getNumber().intValue());
    }//GEN-LAST:event_spinLStateChanged

    private void spinRStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRStateChanged
        style.setMarginR(snmMarginR.getNumber().intValue());
    }//GEN-LAST:event_spinRStateChanged

    private void spinVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinVStateChanged
        style.setMarginV(snmMarginV.getNumber().intValue());
    }//GEN-LAST:event_spinVStateChanged

    private void spinScaleXStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinScaleXStateChanged
        style.setScaleX(snmScaleX.getNumber().intValue());
    }//GEN-LAST:event_spinScaleXStateChanged

    private void spinAngleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinAngleStateChanged
        style.setAngle(snmAngle.getNumber().floatValue());
    }//GEN-LAST:event_spinAngleStateChanged

    private void spinScaleYStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinScaleYStateChanged
        style.setScaleY(snmScaleY.getNumber().intValue());
    }//GEN-LAST:event_spinScaleYStateChanged

    private void spinSpacingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinSpacingStateChanged
        style.setSpacing(snmSpacing.getNumber().floatValue());
    }//GEN-LAST:event_spinSpacingStateChanged

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        ImportStylesDialog isd = new ImportStylesDialog(new javax.swing.JFrame(), true);
        isd.showDialog();
        if(isd.getDialogResult() == ImportStylesDialog.DialogResult.Ok){
            addStyleCollection(isd.getCollection(), isd.getStyles());
            for(Map.Entry<String, AssStyle> entry : isd.getStyles().entrySet()){
                AssSelectedPacket packet = new AssSelectedPacket();
                packet.setAssCollectionName(isd.getCollection());
                packet.setAssStyle(entry.getValue());
                packets.add(packet);
            }
        }
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnAddStyleToCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStyleToCollectionActionPerformed
        if(treeCollections.getSelectionCount() > 0){            
            TreePath tp = treeCollections.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();
            if(!node.isRoot() && node.getUserObject() instanceof String){
                // Création de Style
                DefaultMutableTreeNode styleNode = new DefaultMutableTreeNode(style);

                // Vérification de la présence d'un style ayant le même nom
                boolean exists = false;
                for(int i=0; i<node.getChildCount(); i++){
                    DefaultMutableTreeNode tn = (DefaultMutableTreeNode)node.getChildAt(i);
                    if(tn.getUserObject() instanceof AssStyle){
                        AssStyle sty = (AssStyle)tn.getUserObject();
                        if(sty.getName().equalsIgnoreCase(style.getName())){
                            exists = true;
                            break;
                        }
                    }
                }
                
                // Si le style existe déjà, on demande un nouveau nom
                if(exists == true){
                    String name = style.getName();
                    String newName = style.getName();
                    int tryAgain = 0;
                    while(tryAgain <= 5 && newName.equalsIgnoreCase(name)){
                        newName = JOptionPane.showInputDialog("Please choose a new name:");
                        tryAgain++;
                    }
                    if(tryAgain > 5){
                        return;
                    }
                    AssStyle sty = copy(style, newName);
                    styleNode = new DefaultMutableTreeNode(sty);
                }
                
                // Exclut un nom vide
                if(((AssStyle)styleNode.getUserObject()).getName().isEmpty()){
                    return;
                }
                
                // Ajout à l'arbre
                node.add(styleNode);
                
                // Ajout aux styles
                dcbmStyles.addElement((AssStyle)styleNode.getUserObject());
                
                treeCollections.updateUI();                
            }
        }
    }//GEN-LAST:event_btnAddStyleToCollectionActionPerformed

    private void popmAddStyleToCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmAddStyleToCollectionActionPerformed
        if(tfVideoMediaName.getText().isEmpty() == false){
            // TODO
        }
    }//GEN-LAST:event_popmAddStyleToCollectionActionPerformed

    private void popmRemoveStyleFromCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmRemoveStyleFromCollectionActionPerformed
        deleteCollection();
    }//GEN-LAST:event_popmRemoveStyleFromCollectionActionPerformed

    private void btnSaveCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveCollectionActionPerformed
        if(treeCollections.getSelectionCount() > 0){
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)treeCollections.getLastSelectedPathComponent();
            if(node.getUserObject() instanceof String && node.getParent() == root | node.isLeaf()){
                String name = node.isLeaf() ? node.getParent().toString() : node.getUserObject().toString();
                AssStylesCollectionConf.save(name, styles);
            }
        }
    }//GEN-LAST:event_btnSaveCollectionActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> {
            StylesDialog dialog = new StylesDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton CANCEL_BUTTON;
    private javax.swing.JButton OK_BUTTON;
    private javax.swing.ButtonGroup bgEditStyle;
    private javax.swing.JButton btnAddStyleToCollection;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnSaveCollection;
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
    private javax.swing.JLabel lblKaraokeColor;
    private javax.swing.JLabel lblShadowColor;
    private javax.swing.JLabel lblTextColor;
    private javax.swing.JPanel paneStyleName;
    private javax.swing.JPopupMenu popStyleRW;
    private javax.swing.JMenuItem popmAddStyleToCollection;
    private javax.swing.JMenuItem popmRemoveStyleFromCollection;
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

    public class AssSelectedPacket {
        
        private boolean selected = false;
        private AssStyle assStyle = AssStyle.getDefault();
        private String assCollectionName = "Default movie";

        public AssSelectedPacket() {
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public AssStyle getAssStyle() {
            return assStyle;
        }

        public void setAssStyle(AssStyle assStyle) {
            this.assStyle = assStyle;
        }

        @Override
        public String toString() {
            return assStyle.getName();
        }

        public String getAssCollectionName() {
            return assCollectionName;
        }

        public void setAssCollectionName(String assCollectionName) {
            this.assCollectionName = assCollectionName;
        }
        
        
    }
}
