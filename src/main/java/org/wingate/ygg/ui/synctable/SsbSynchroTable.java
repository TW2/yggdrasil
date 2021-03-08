/*
 * Copyright (C) 2021 util2
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
package org.wingate.ygg.ui.synctable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.ssb.SSB;
import org.wingate.ygg.subs.ssb.SsbEvent;
import org.wingate.ygg.subs.ssb.SsbEventType;
import org.wingate.ygg.subs.ssb.SsbMacro;
import org.wingate.ygg.subs.ssb.tool.SsbEventTableModel;
import org.wingate.ygg.subs.ssb.tool.SsbEventTableRenderer;

/**
 *
 * @author util2
 */
public class SsbSynchroTable extends javax.swing.JPanel {

    SsbLinkPanel tableLink = new SsbLinkPanel();
    
    // ifrTableOne components and variables
    private static SsbEventTableModel dtmSSB;    
    private SsbEventTableRenderer ssbEventTableRenderer;
    // ifrTableOne stop
    
    public SsbSynchroTable() {
        initComponents();
        init();
    }
    
    private void init(){
        ssbEventTableRenderer = new SsbEventTableRenderer(MainFrame.isDark());
        initializeTableOne(MainFrame.getLanguage(), MainFrame.getISO());   
    }
    
    // <editor-fold defaultstate="collapsed" desc="TableOne">
    
    public void initializeTableOne(Language in, ISO_3166 get){        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(in.isForced() == true){
            get = in.getIso();
        }
        
        // Fill in the table
        dtmSSB = new SsbEventTableModel(in, get);
        
        ssbTable.setModel(dtmSSB);
        
        TableColumn column;
        for (int i = 0; i < 8; i++) {
            column = ssbTable.getColumnModel().getColumn(i);
            switch(i){
                case 0 -> column.setPreferredWidth(40);
                //# (line number)
                case 1 -> column.setPreferredWidth(150);
                //Type
                case 2 -> column.setPreferredWidth(300);
                //Event ID or Time (Start, End)
                case 3 -> column.setPreferredWidth(100);
                //Macro
                case 4 -> column.setPreferredWidth(300);
                //Note
                case 5 -> column.setPreferredWidth(40);
                //CPL
                case 6 -> column.setPreferredWidth(40);
                //CPS
                case 7 -> column.setPreferredWidth(1000);
                //Text
            }
        }
        
        ssbTable.setDefaultRenderer(String.class, ssbEventTableRenderer);
        ssbTable.setDefaultRenderer(SsbEventType.class, ssbEventTableRenderer);
        ssbTable.setDefaultRenderer(SsbMacro.class, ssbEventTableRenderer);
        ssbTable.setDefaultRenderer(Integer.class, ssbEventTableRenderer);
        
        ssbTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    SsbEvent ev = dtmSSB.getEventAt(ssbTable.getSelectedRow());
                    tableLink.alter(ev);                    
                    try{
                        tableLink.displayEventTime(ev);
                        tableLink.updateAreaFrames(ev);
                    }catch(Exception ex){}
                    MainFrame.getAudioFrame().setArea(ev.getStart(), ev.getEnd());
                }
            }
        });
    }
    
    public JTable getTable(){
        return ssbTable;
    }
    
    public SsbEventTableModel getSsbTableModel(){
        return dtmSSB;
    }
    
    public SsbLinkPanel getSsbTableLink(){
        return tableLink;
    }
    
    public void loadSSBTable(File f){        
        SSB loading = SSB.Read(f.getPath());
        ssbTable.removeAll();
        dtmSSB.insertAll(loading.getEvents());
        ssbTable.updateUI();
        tableLink.initSsbComboMacro();
    }
    
    public void saveSSBTable(File f){
        SSB saving = new SSB();
        List<SsbEvent> events = dtmSSB.getAllEvents();
        saving.setEvents(events);
        saving.setMacros(tableLink.getMacros());
        SSB.Save(f.getPath(), saving);
    }
    
    // </editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ssbTable = new javax.swing.JTable();

        ssbTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(ssbTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable ssbTable;
    // End of variables declaration//GEN-END:variables
}
