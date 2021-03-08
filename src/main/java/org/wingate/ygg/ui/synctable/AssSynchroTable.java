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
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.subs.ass.ASS;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.subs.ass.tool.AssEventTableModel;
import org.wingate.ygg.subs.ass.tool.AssEventTableRenderer;
import org.wingate.ygg.subs.ass.AssStyle;
import org.wingate.ygg.ui.IfrTableLink;

/**
 *
 * @author util2
 */
public class AssSynchroTable extends javax.swing.JPanel {
    
    // ifrTableOne components and variables
    private static AssEventTableModel dtmASS;    
    private AssEventTableRenderer assEventTableRenderer;
    // ifrTableOne stop

    public AssSynchroTable() {
        initComponents();
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
        
        assTable.setModel(dtmASS);
        
        TableColumn column;
        for (int i = 0; i < 14; i++) {
            column = assTable.getColumnModel().getColumn(i);
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
        
        assTable.setDefaultRenderer(String.class, assEventTableRenderer);
        assTable.setDefaultRenderer(AssEvent.LineType.class, assEventTableRenderer);
        assTable.setDefaultRenderer(Time.class, assEventTableRenderer);
        assTable.setDefaultRenderer(AssStyle.class, assEventTableRenderer);
        assTable.setDefaultRenderer(Integer.class, assEventTableRenderer);
        
        assTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && MainFrame.getTableLinkFrame() != null){
                    // Récupération du composant maître
                    IfrTableLink link = MainFrame.getTableLinkFrame();
                    
                    // Evénement
                    AssEvent ev = dtmASS.getEventAt(assTable.getSelectedRow());
                    
                    // Altération
                    link.getAssLink().alter(ev);                    
                    try{
                        link.getAssLink().displayEventTime(ev);
                        link.getAssLink().updateAreaFrames(ev);
                    }catch(Exception ex){}
                    MainFrame.getAudioFrame().setArea(ev.getStartTime(), ev.getEndTime());
                }
            }
        });
    }
    
    public JTable getTable(){
        return assTable;
    }
    
    public AssEventTableModel getAssTableModel(){
        return dtmASS;
    }
    
    public void loadASSTable(File f){
        if(MainFrame.getTableLinkFrame() != null){
            // Récupération du composant maître
            IfrTableLink link = MainFrame.getTableLinkFrame();
            
            ASS loading = ASS.Read(f.getPath());
            assTable.removeAll();
            dtmASS.insertAll(loading.getEvents());
            assTable.updateUI();
            link.getAssLink().initAssComboStyle();
            link.getAssLink().initAssComboName();
        }        
    }
    
    public void saveASSTable(File f){
        if(MainFrame.getTableLinkFrame() != null){
            // Récupération du composant maître
            IfrTableLink link = MainFrame.getTableLinkFrame();
            
            ASS saving = new ASS();
            List<AssEvent> events = dtmASS.getAllEvents();
            saving.setEvents(events);
            saving.setStyles(link.getAssLink().getStyles());
            ASS.Save(f.getPath(), saving);
        }
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
        assTable = new javax.swing.JTable();

        assTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(assTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTable assTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
