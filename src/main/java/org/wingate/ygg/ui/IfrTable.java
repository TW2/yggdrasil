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
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.ui.synctable.AssSynchroTable;
import org.wingate.ygg.ui.synctable.SelectedFormat;
import org.wingate.ygg.ui.synctable.SrtSynchroTable;
import org.wingate.ygg.ui.synctable.SsbSynchroTable;
import org.wingate.ygg.ui.synctable.VesSynchroTable;
import org.wingate.ygg.ui.synctable.WebVTTSynchroTable;

/**
 *
 * @author util2
 */
public class IfrTable extends javax.swing.JInternalFrame {

    private final List<AssSynchroTable> st_ass_s = new ArrayList<>();
    private AssSynchroTable lastAssSynchroTable = null;
    
    private final List<SsbSynchroTable> st_ssb_s = new ArrayList<>();
    private SsbSynchroTable lastSsbSynchroTable = null;
    
    /**
     * Creates new form ifrTable
     */
    public IfrTable() {
        initComponents();
        init();
    }
    
    private void init(){
        
    }
    
    public void addASSTable(File file){
        AssSynchroTable st_ass = new AssSynchroTable();
        st_ass_s.add(st_ass);
        st_ass.loadASSTable(file);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/Aegisub min.png"));        
        tabbedSubs.addTab(file.getName(), icon, st_ass);        
    }
    
    public void addSSBTable(File file){
        SsbSynchroTable st_ssb = new SsbSynchroTable();
        st_ssb_s.add(st_ssb);
        st_ssb.loadSSBTable(file);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/SubStation Beta min.png"));        
        tabbedSubs.addTab(file.getName(), icon, st_ssb);
    }
    
    public void addSRTTable(File file){
        
    }
    
    public void addVESTable(File file){
        
    }
    
    public void addVTTTable(File file){
        
    }
    
    public void save(File file, String extension){
        if(tabbedSubs.getSelectedComponent() instanceof AssSynchroTable && extension.contains("ass")){
            AssSynchroTable st_ass = (AssSynchroTable)tabbedSubs.getSelectedComponent();
            st_ass.saveASSTable(file);
        }else if(tabbedSubs.getSelectedComponent() instanceof SsbSynchroTable && extension.contains("ssb")){
            SsbSynchroTable st_ssb = (SsbSynchroTable)tabbedSubs.getSelectedComponent();
            st_ssb.saveSSBTable(file);
        }else if(tabbedSubs.getSelectedComponent() instanceof SrtSynchroTable && extension.contains("srt")){
            
        }else if(tabbedSubs.getSelectedComponent() instanceof VesSynchroTable && extension.contains("ves")){
            
        }else if(tabbedSubs.getSelectedComponent() instanceof WebVTTSynchroTable && extension.contains("vtt")){
            
        }
    }
    
    public JTable getTable(){
        if(tabbedSubs.getSelectedComponent() instanceof AssSynchroTable){
            AssSynchroTable st_ass = (AssSynchroTable)tabbedSubs.getSelectedComponent();            
            return st_ass.getTable();
        }else if(tabbedSubs.getSelectedComponent() instanceof SsbSynchroTable){
            SsbSynchroTable st_ssb = (SsbSynchroTable)tabbedSubs.getSelectedComponent();            
            return st_ssb.getTable();
        }else if(tabbedSubs.getSelectedComponent() instanceof SrtSynchroTable){
            
        }else if(tabbedSubs.getSelectedComponent() instanceof VesSynchroTable){
            
        }else if(tabbedSubs.getSelectedComponent() instanceof WebVTTSynchroTable){
            
        }
        return null;
    }
    
    public SelectedFormat getFormat(){
        if(tabbedSubs.getSelectedComponent() instanceof AssSynchroTable){
            AssSynchroTable st_ass = (AssSynchroTable)tabbedSubs.getSelectedComponent();
            lastAssSynchroTable = st_ass;
            return SelectedFormat.ASS;
        }else if(tabbedSubs.getSelectedComponent() instanceof SsbSynchroTable){
            SsbSynchroTable st_ssb = (SsbSynchroTable)tabbedSubs.getSelectedComponent();
            lastSsbSynchroTable = st_ssb;
            return SelectedFormat.SSB;
        }else if(tabbedSubs.getSelectedComponent() instanceof SrtSynchroTable){
            return SelectedFormat.SRT;
        }else if(tabbedSubs.getSelectedComponent() instanceof VesSynchroTable){
            return SelectedFormat.VES;
        }else if(tabbedSubs.getSelectedComponent() instanceof WebVTTSynchroTable){
            return SelectedFormat.WebVTT;
        }
        return null;
    }
    
    public AssSynchroTable getLastAssSynchroTable(){
        return lastAssSynchroTable;
    }
    
    public SsbSynchroTable getLastSsbSynchroTable(){
        return lastSsbSynchroTable;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedSubs = new javax.swing.JTabbedPane();
        panAssCompare = new javax.swing.JPanel();

        setMaximizable(true);
        setResizable(true);
        setTitle("Table");

        tabbedSubs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedSubsStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panAssCompareLayout = new javax.swing.GroupLayout(panAssCompare);
        panAssCompare.setLayout(panAssCompareLayout);
        panAssCompareLayout.setHorizontalGroup(
            panAssCompareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 841, Short.MAX_VALUE)
        );
        panAssCompareLayout.setVerticalGroup(
            panAssCompareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
        );

        tabbedSubs.addTab("Compare", panAssCompare);

        getContentPane().add(tabbedSubs, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabbedSubsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedSubsStateChanged
        try{
            MainFrame.getTableLinkFrame().setSelected(true);
            MainFrame.getTableLinkFrame().setSelectedFormat(getFormat());
            MainFrame.getTableLinkFrame().updateUI();
        }catch(Exception ex){
            // No selection or in software initialization
        }        
    }//GEN-LAST:event_tabbedSubsStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panAssCompare;
    private javax.swing.JTabbedPane tabbedSubs;
    // End of variables declaration//GEN-END:variables
}
