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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ASS;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.AssEvent;
import org.wingate.ygg.subs.AssStyle;
import org.wingate.ygg.subs.AssEventTableModel;
import org.wingate.ygg.subs.AssEventTableRenderer;

/**
 *
 * @author util2
 */
public class IfrTable extends javax.swing.JInternalFrame {

    IfrTableLink tableLink = null;
    
    // ifrTableOne components and variables
    private static AssEventTableModel dtmASS;    
    private AssEventTableRenderer assEventTableRenderer;
    // ifrTableOne stop
    
    /**
     * Creates new form ifrTable
     * @param tableLink
     */
    public IfrTable(IfrTableLink tableLink) {
        initComponents();
        this.tableLink = tableLink;
        init();
    }
    
    private void init(){
        assEventTableRenderer = new AssEventTableRenderer(MainFrame.isDark());
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
        dtmASS = new AssEventTableModel(in, get);
        
        tableV1.setModel(dtmASS);
        
        TableColumn column;
        for (int i = 0; i < 14; i++) {
            column = tableV1.getColumnModel().getColumn(i);
            switch(i){
                case 0 -> column.setPreferredWidth(40);
                //# (line number)
                case 1 -> column.setPreferredWidth(150);
                //Type
                case 2 -> column.setPreferredWidth(40);
                //Layer
                case 3 -> column.setPreferredWidth(100);
                //Start
                case 4 -> column.setPreferredWidth(100);
                //End
                case 5 -> column.setPreferredWidth(20);
                //ML
                case 6 -> column.setPreferredWidth(20);
                //MR
                case 7 -> column.setPreferredWidth(20);
                //MV
                case 8 -> column.setPreferredWidth(150);
                //Style
                case 9 -> column.setPreferredWidth(150);
                //Name
                case 10 -> column.setPreferredWidth(150);
                //Effect
                case 11 -> column.setPreferredWidth(40);
                //CPL
                case 12 -> column.setPreferredWidth(40);
                //CPS
                case 13 -> column.setPreferredWidth(1000);
                //Text
            }
        }
        
        tableV1.setDefaultRenderer(String.class, assEventTableRenderer);
        tableV1.setDefaultRenderer(AssEvent.LineType.class, assEventTableRenderer);
        tableV1.setDefaultRenderer(Time.class, assEventTableRenderer);
        tableV1.setDefaultRenderer(AssStyle.class, assEventTableRenderer);
        tableV1.setDefaultRenderer(Integer.class, assEventTableRenderer);
        
        tableV1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    AssEvent ev = dtmASS.getEventAt(tableV1.getSelectedRow());
                    MainFrame.getTableLinkFrame().alter(ev);                    
                    try{
                        MainFrame.getTableLinkFrame().displayEventTime(ev);
                        MainFrame.getTableLinkFrame().updateAreaFrames(ev);
                    }catch(Exception ex){}
                    MainFrame.getAudioFrame().setArea(ev.getStartTime(), ev.getEndTime());
                }
            }
        });
    }
    
    public static JTable getTableV1(){
        return tableV1;
    }
    
    public static JTable getTableV2(){
        return tableV2;
    }
    
    public static AssEventTableModel getAssTableModel(){
        return dtmASS;
    }
    
    public void loadASSTable(File f){        
        ASS loading = ASS.Read(f.getPath());
        tableV1.removeAll();
        dtmASS.insertAll(loading.getEvents());
        tableV1.updateUI();
        tableLink.initAssComboStyle();
        tableLink.initAssComboName();
    }
    
    public void saveASSTable(File f){
        ASS saving = new ASS();
        List<AssEvent> events = dtmASS.getAllEvents();
        saving.setEvents(events);
        saving.setStyles(MainFrame.getTableLinkFrame().getStyles());
        ASS.Save(f.getPath(), saving);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        panAss1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableV1 = new javax.swing.JTable();
        panAss2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableV2 = new javax.swing.JTable();
        panAssCompare = new javax.swing.JPanel();

        setMaximizable(true);
        setResizable(true);
        setTitle("Table");

        panAss1.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tableV1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tableV1);

        panAss1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("ASS #1", panAss1);

        panAss2.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tableV2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tableV2);

        panAss2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("ASS #2", panAss2);

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

        jTabbedPane1.addTab("ASS Compare", panAssCompare);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panAss1;
    private javax.swing.JPanel panAss2;
    private javax.swing.JPanel panAssCompare;
    private static javax.swing.JTable tableV1;
    private static javax.swing.JTable tableV2;
    // End of variables declaration//GEN-END:variables
}
