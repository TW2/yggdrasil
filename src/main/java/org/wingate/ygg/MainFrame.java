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
package org.wingate.ygg;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.wingate.ygg.helper.DialogResult;
import org.wingate.ygg.theme.Theme;
import org.wingate.ygg.ui.ContainerPanel;
import org.wingate.ygg.ui.ElementAbstract;
import org.wingate.ygg.ui.ElementDialog;
import org.wingate.ygg.ui.ElementDialog;
import org.wingate.ygg.ui.Matrix;
import org.wingate.ygg.ui.table.AssTablePanel;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {
    
    private Theme theme;
    
    private Matrix matrix;
    private final Map<String, ContainerPanel> cs = new HashMap<>();

    /**
     * Creates new form MainFrame
     * @param theme
     */
    public MainFrame(Theme theme) {
        initComponents();
        
        this.theme = theme;
        
        matrix = new Matrix();
        
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                for(Map.Entry<String, ContainerPanel> entry : cs.entrySet()){
                    ContainerPanel cp = entry.getValue();
                    ElementAbstract ea = cp.getElementAbstract();
                    applyBoundaries(cp);
                    shiftMax(ea.getCorner(), cp);
                }
            }
        });
    }

    public Theme getTheme() {
        return theme;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
    
    public void applyBoundaries(ContainerPanel cp){
        ElementAbstract ea = cp.getElementAbstract();
        JPanel panel = (JPanel)ea.getPanel();
        
        if(mainPanel.getWidth() != 0 && mainPanel.getHeight() != 0 && panel != null){
            int x = 0, y = 0, width = mainPanel.getWidth(), height = mainPanel.getHeight();
            
            int lastWCase = ea.getLastWCase();
            int firstWCase = ea.getFirstWCase();
            int caseWidth = matrix.getWCases();
            
            if(lastWCase - firstWCase < caseWidth){
                int partWidth = width / caseWidth;
                x = firstWCase * partWidth;
                width = (lastWCase - firstWCase) * partWidth;
            }
            
            int lastHCase = ea.getLastHCase();
            int firstHCase = ea.getFirstHCase();
            int caseHeight = matrix.getHCases();
            
            if(lastHCase - firstHCase < caseHeight){
                int partHeight = height / caseHeight;
                y = firstHCase * partHeight;
                height = (lastHCase - firstHCase) * partHeight;
            }
            
            cp.setLocation(x, y);
            cp.setSize(width, height);
            
            mainPanel.updateUI();
        }
    }
    
    public void shiftMax(int corner, ContainerPanel cp){
        ElementAbstract ea = cp.getElementAbstract();
        JPanel panel = (JPanel)ea.getPanel();
        
        if(mainPanel.getWidth() != 0 && mainPanel.getHeight() != 0 && panel != null){
            int x = 0, y = 0, width = mainPanel.getWidth(), height = mainPanel.getHeight();
            
            int lastWCase = ea.getLastWCase();
            int firstWCase = ea.getFirstWCase();
            int caseWidth = matrix.getWCases();
            
            int lastHCase = ea.getLastHCase();
            int firstHCase = ea.getFirstHCase();
            int caseHeight = matrix.getHCases();
            
            int eW = lastWCase - firstWCase;
            int eH = lastHCase - firstHCase;
            
            // X
            switch(corner){
                case 1, 4, 7 -> {
                    // x = 0
                    firstWCase = 0; lastWCase = firstWCase + eW;
                }
                case 2, 5, 8 -> {
                    // x = middle

                }
                case 3, 6, 9 -> {
                    // x = max
                    lastWCase = caseWidth; firstWCase = lastWCase - eW;
                }
            }

            // Y
            switch(corner){
                case 1, 2, 3 -> {
                    // y = max
                    lastHCase = caseHeight; firstHCase = lastHCase - eH;
                }
                case 4, 5, 6 -> {
                    // y = middle

                }
                case 7, 8, 9 -> {
                    // y = 0
                    firstHCase = 0; lastHCase = firstHCase + eH;
                }
            }
            
            if(lastWCase - firstWCase < caseWidth){
                int partWidth = width / caseWidth;
                x = firstWCase * partWidth;
                width = (lastWCase - firstWCase) * partWidth;
            }
            
            if(lastHCase - firstHCase < caseHeight){
                int partHeight = height / caseHeight;
                y = firstHCase * partHeight;
                height = (lastHCase - firstHCase) * partHeight;
            }
            
            ea.setFirstWCase(firstWCase);
            ea.setLastWCase(lastWCase);
            ea.setFirstHCase(firstHCase);
            ea.setLastHCase(lastHCase);
            
            cp.setLocation(x, y);
            cp.setSize(width, height);
            
            mainPanel.updateUI();
        }
    }
    
    public enum ShiftDirection {
        Left, Right, Top, Bottom;
    }
    
    public void shift(ShiftDirection dir, ContainerPanel cp){
        ElementAbstract ea = cp.getElementAbstract();
        JPanel panel = (JPanel)ea.getPanel();
        
        if(mainPanel.getWidth() != 0 && mainPanel.getHeight() != 0 && panel != null){
            int x = 0, y = 0, width = mainPanel.getWidth(), height = mainPanel.getHeight();
            
            int lastWCase = ea.getLastWCase();
            int firstWCase = ea.getFirstWCase();
            int caseWidth = matrix.getWCases();
            
            int lastHCase = ea.getLastHCase();
            int firstHCase = ea.getFirstHCase();
            int caseHeight = matrix.getHCases();
            
            switch(dir){
                case Left -> {
                    int oldValue = firstWCase;
                    firstWCase = firstWCase > 0 ? firstWCase - 1 : 0;
                    if(oldValue != firstWCase){
                        lastWCase = lastWCase > 1 ? lastWCase - 1 : 1;
                    }
                }
                case Right -> {
                    int oldValue = lastWCase;
                    lastWCase = lastWCase < caseWidth ? lastWCase + 1 : caseWidth;
                    if(oldValue != lastWCase){
                        firstWCase = firstWCase < caseWidth - 1 ? firstWCase + 1 : caseWidth - 1;
                    }
                }
                case Top -> {
                    int oldValue = firstHCase;
                    firstHCase = firstHCase > 0 ? firstHCase - 1 : 0;
                    if(oldValue != firstHCase){
                        lastHCase = lastHCase > 1 ? lastHCase - 1 : 1;
                    }
                }
                case Bottom -> {
                    int oldValue = lastHCase;
                    lastHCase = lastHCase < caseHeight ? lastHCase + 1 : caseHeight;
                    if(oldValue != lastHCase){
                        firstHCase = firstHCase < caseHeight - 1 ? firstHCase + 1 : caseHeight - 1;
                    }
                }
            }
            
            if(lastWCase - firstWCase < caseWidth){
                int partWidth = width / caseWidth;
                x = firstWCase * partWidth;
                width = (lastWCase - firstWCase) * partWidth;
            }
            
            if(lastHCase - firstHCase < caseHeight){
                int partHeight = height / caseHeight;
                y = firstHCase * partHeight;
                height = (lastHCase - firstHCase) * partHeight;
            }
            
            ea.setFirstWCase(firstWCase);
            ea.setLastWCase(lastWCase);
            ea.setFirstHCase(firstHCase);
            ea.setLastHCase(lastHCase);
            
            ea.setCorner(-1);
            
            cp.setLocation(x, y);
            cp.setSize(width, height);
            
            mainPanel.updateUI();
        }
    }
    
    public void removeContainerPanel(ContainerPanel cp){
        try{
            cs.remove(cp.getElementAbstract().getName());
            mainPanel.remove(cp);

            JMenu m = cp.getElementAbstract().getMenu();
            mElements.remove(m);
        }catch(Exception exc){
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

        bgEditClickASSATable = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mFile = new javax.swing.JMenu();
        mFileNewProject = new javax.swing.JMenuItem();
        mFileOpenProject = new javax.swing.JMenuItem();
        mFileSaveProjectAs = new javax.swing.JMenuItem();
        mFileSaveProject = new javax.swing.JMenuItem();
        mEdit = new javax.swing.JMenu();
        rbmEditDoubleClick = new javax.swing.JRadioButtonMenuItem();
        rbmEditOneClick = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mElements = new javax.swing.JMenu();
        mFileAddElement = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 744, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );

        mFile.setText("File");

        mFileNewProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mFileNewProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16 newdocument.png"))); // NOI18N
        mFileNewProject.setText("New project");
        mFileNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileNewProjectActionPerformed(evt);
            }
        });
        mFile.add(mFileNewProject);

        mFileOpenProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mFileOpenProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16 folder.png"))); // NOI18N
        mFileOpenProject.setText("Open project");
        mFileOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileOpenProjectActionPerformed(evt);
            }
        });
        mFile.add(mFileOpenProject);

        mFileSaveProjectAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mFileSaveProjectAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16 floppydisk.png"))); // NOI18N
        mFileSaveProjectAs.setText("Save project as");
        mFileSaveProjectAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileSaveProjectAsActionPerformed(evt);
            }
        });
        mFile.add(mFileSaveProjectAs);

        mFileSaveProject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mFileSaveProject.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16 floppydisk.png"))); // NOI18N
        mFileSaveProject.setText("Save project");
        mFileSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileSaveProjectActionPerformed(evt);
            }
        });
        mFile.add(mFileSaveProject);

        jMenuBar1.add(mFile);

        mEdit.setText("Edit");

        bgEditClickASSATable.add(rbmEditDoubleClick);
        rbmEditDoubleClick.setSelected(true);
        rbmEditDoubleClick.setText("Edit on double click in ASSA Table");
        rbmEditDoubleClick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmEditDoubleClickActionPerformed(evt);
            }
        });
        mEdit.add(rbmEditDoubleClick);

        bgEditClickASSATable.add(rbmEditOneClick);
        rbmEditOneClick.setText("Edit on simple click in ASSA Table");
        rbmEditOneClick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmEditOneClickActionPerformed(evt);
            }
        });
        mEdit.add(rbmEditOneClick);
        mEdit.add(jSeparator1);

        jMenuBar1.add(mEdit);

        mElements.setText("Elements");

        mFileAddElement.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mFileAddElement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16px-Crystal_Clear_action_edit_add.png"))); // NOI18N
        mFileAddElement.setText("Add element");
        mFileAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFileAddElementActionPerformed(evt);
            }
        });
        mElements.add(mFileAddElement);
        mElements.add(jSeparator2);

        jMenuBar1.add(mElements);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mFileNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileNewProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mFileNewProjectActionPerformed

    private void mFileOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileOpenProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mFileOpenProjectActionPerformed

    private void mFileSaveProjectAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileSaveProjectAsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mFileSaveProjectAsActionPerformed

    private void mFileSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileSaveProjectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mFileSaveProjectActionPerformed

    private void mFileAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFileAddElementActionPerformed
        // Open dialog to choose a new element
        ElementDialog d = new ElementDialog(this, true, theme);
        d.showDialog();
        if(d.getDialogResult() == DialogResult.Ok){
            ElementAbstract ea = d.getElementComponent();
            String name = d.getElementName();
            
            if(ea.getPanel() instanceof javax.swing.JPanel){
                ContainerPanel cp = new ContainerPanel(this, ea);
                mainPanel.add(cp);                
                cs.put(name, cp);                
                applyBoundaries(cp);
                
                ea.setupMenu(name, cp);
                mElements.add(ea.getMenu());
            }
        }
    }//GEN-LAST:event_mFileAddElementActionPerformed

    private void rbmEditDoubleClickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmEditDoubleClickActionPerformed
        // Sélectionne le double clic sur les éléments ASSA Table
        for(Map.Entry<String, ContainerPanel> entry : cs.entrySet()){
            if(entry.getValue().getElementAbstract().getPanel() instanceof AssTablePanel p){
                p.setEditOneSimpleClick(false);
            }
        }
    }//GEN-LAST:event_rbmEditDoubleClickActionPerformed

    private void rbmEditOneClickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmEditOneClickActionPerformed
        // Sélectionne le simple clic sur les éléments ASSA Table
        for(Map.Entry<String, ContainerPanel> entry : cs.entrySet()){
            if(entry.getValue().getElementAbstract().getPanel() instanceof AssTablePanel p){
                p.setEditOneSimpleClick(true);
            }
        }
    }//GEN-LAST:event_rbmEditOneClickActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgEditClickASSATable;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenu mEdit;
    private javax.swing.JMenu mElements;
    private javax.swing.JMenu mFile;
    private javax.swing.JMenuItem mFileAddElement;
    private javax.swing.JMenuItem mFileNewProject;
    private javax.swing.JMenuItem mFileOpenProject;
    private javax.swing.JMenuItem mFileSaveProject;
    private javax.swing.JMenuItem mFileSaveProjectAs;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButtonMenuItem rbmEditDoubleClick;
    private javax.swing.JRadioButtonMenuItem rbmEditOneClick;
    // End of variables declaration//GEN-END:variables
}
