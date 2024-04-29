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

import java.awt.BorderLayout;

/**
 *
 * @author util2
 */
public class ContainerInternalFrame extends javax.swing.JInternalFrame {
    
    private final ContainersDesktopPane containersDesktopPane;
    private final ElementAbstract elementAbstract;

    /**
     * Creates new form ContainerInternalFrame
     * @param containersDesktopPane
     * @param elementAbstract
     */
    public ContainerInternalFrame(ContainersDesktopPane containersDesktopPane, ElementAbstract elementAbstract) {
        initComponents();
        this.containersDesktopPane = containersDesktopPane;
        this.elementAbstract = elementAbstract;
        
        embedPanel.setLayout(new BorderLayout());
        if(elementAbstract.getPanel() instanceof javax.swing.JPanel content){
            embedPanel.add(content, BorderLayout.CENTER);
        }
    }

    public ContainersDesktopPane getContainersDesktopPane() {
        return containersDesktopPane;
    }

    public ElementAbstract getElementAbstract() {
        return elementAbstract;
    }
    
    public javax.swing.JMenu getFileMenu(){
        return mnuFile;
    }
    
    public javax.swing.JMenu getEditMenu(){
        return mnuEdit;
    }
    
    public javax.swing.JMenu getLinkElementsMenu(){
        return mnuLinkElements;
    }
    
    public javax.swing.JMenu getLinkResourcesMenu(){
        return mnuLinkResources;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        embedPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuEdit = new javax.swing.JMenu();
        mnuLinkElements = new javax.swing.JMenu();
        mnuLinkResources = new javax.swing.JMenu();
        mnuW = new javax.swing.JMenu();
        mnuH = new javax.swing.JMenu();
        mnuLeft = new javax.swing.JMenu();
        mnuRight = new javax.swing.JMenu();
        mnuUp = new javax.swing.JMenu();
        mnuDown = new javax.swing.JMenu();
        mnuSettings = new javax.swing.JMenu();

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        javax.swing.GroupLayout embedPanelLayout = new javax.swing.GroupLayout(embedPanel);
        embedPanel.setLayout(embedPanelLayout);
        embedPanelLayout.setHorizontalGroup(
            embedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 595, Short.MAX_VALUE)
        );
        embedPanelLayout.setVerticalGroup(
            embedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 251, Short.MAX_VALUE)
        );

        getContentPane().add(embedPanel, java.awt.BorderLayout.CENTER);

        mnuFile.setText("File");
        jMenuBar1.add(mnuFile);

        mnuEdit.setText("Edit");

        mnuLinkElements.setText("Link elements");
        mnuEdit.add(mnuLinkElements);

        mnuLinkResources.setText("Link resources");
        mnuEdit.add(mnuLinkResources);

        jMenuBar1.add(mnuEdit);

        mnuW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew_46_16.png"))); // NOI18N
        mnuW.setToolTipText("Change size of the element horizontally");
        mnuW.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuWMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuW);

        mnuH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew_28_16.png"))); // NOI18N
        mnuH.setToolTipText("Change size of the element vertically");
        mnuH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuHMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuH);

        mnuLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew4_16.png"))); // NOI18N
        mnuLeft.setToolTipText("Move element to the left");
        mnuLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuLeftMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuLeft);

        mnuRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew6_16.png"))); // NOI18N
        mnuRight.setToolTipText("Move element to the right");
        mnuRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuRightMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuRight);

        mnuUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew8_16.png"))); // NOI18N
        mnuUp.setToolTipText("Move element to the up");
        mnuUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuUpMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuUp);

        mnuDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fnew2_16.png"))); // NOI18N
        mnuDown.setToolTipText("Move element to the down");
        mnuDown.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuDownMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuDown);

        mnuSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/16 engrenage mauve.png"))); // NOI18N
        mnuSettings.setToolTipText("Change settings");
        mnuSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnuSettingsMouseClicked(evt);
            }
        });
        jMenuBar1.add(mnuSettings);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuWMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuWMouseClicked
        // Increase width
        containersDesktopPane.increaseBy(elementAbstract, 1, ContainersDesktopPane.ResizeType.Horizontal);
    }//GEN-LAST:event_mnuWMouseClicked

    private void mnuHMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuHMouseClicked
        // Increase height
        containersDesktopPane.increaseBy(elementAbstract, 1, ContainersDesktopPane.ResizeType.Vertical);
    }//GEN-LAST:event_mnuHMouseClicked

    private void mnuLeftMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuLeftMouseClicked
        // Move to left
        containersDesktopPane.toLeft(elementAbstract);
    }//GEN-LAST:event_mnuLeftMouseClicked

    private void mnuRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuRightMouseClicked
        // Move to right
        containersDesktopPane.toRight(elementAbstract);
    }//GEN-LAST:event_mnuRightMouseClicked

    private void mnuUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuUpMouseClicked
        // Move to up
        containersDesktopPane.toUp(elementAbstract);
    }//GEN-LAST:event_mnuUpMouseClicked

    private void mnuDownMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuDownMouseClicked
        // Move to down
        containersDesktopPane.toDown(elementAbstract);
    }//GEN-LAST:event_mnuDownMouseClicked

    private void mnuSettingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuSettingsMouseClicked
        // Other settings
    }//GEN-LAST:event_mnuSettingsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel embedPanel;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu mnuDown;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuH;
    private javax.swing.JMenu mnuLeft;
    private javax.swing.JMenu mnuLinkElements;
    private javax.swing.JMenu mnuLinkResources;
    private javax.swing.JMenu mnuRight;
    private javax.swing.JMenu mnuSettings;
    private javax.swing.JMenu mnuUp;
    private javax.swing.JMenu mnuW;
    // End of variables declaration//GEN-END:variables
}
