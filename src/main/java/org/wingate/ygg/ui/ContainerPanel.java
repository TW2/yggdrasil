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
import org.wingate.ygg.MainFrame;

/**
 *
 * @author util2
 */
public class ContainerPanel extends javax.swing.JPanel {
    
    private final MainFrame mainFrame;
    
    private final ElementAbstract elementAbstract;

    public ContainerPanel(MainFrame mainFrame, ElementAbstract elementAbstract) {
        initComponents();
        
        this.mainFrame = mainFrame;
        this.elementAbstract = elementAbstract;
        
        centerPanel.setLayout(new BorderLayout());
        if(elementAbstract.getPanel() instanceof javax.swing.JPanel content){
            centerPanel.add(content, BorderLayout.CENTER);
        }
    }

    public ElementAbstract getElementAbstract() {
        return elementAbstract;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        setLayout(new java.awt.BorderLayout());

        topPanel.setBackground(new java.awt.Color(102, 102, 102));
        topPanel.setPreferredSize(new java.awt.Dimension(0, 20));

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        add(topPanel, java.awt.BorderLayout.NORTH);

        javax.swing.GroupLayout centerPanelLayout = new javax.swing.GroupLayout(centerPanel);
        centerPanel.setLayout(centerPanelLayout);
        centerPanelLayout.setHorizontalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        centerPanelLayout.setVerticalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        add(centerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

}
