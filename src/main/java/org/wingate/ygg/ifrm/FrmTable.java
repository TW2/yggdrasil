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
package org.wingate.ygg.ifrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.swing.table.TableColumn;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ass.Event.LineType;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.language.ISO_3166;
import org.wingate.ygg.language.Language;
import org.wingate.ygg.model.AssEventTableModel;
import org.wingate.ygg.renderer.AssEventTableRenderer;
import org.wingate.ygg.util.Time;

/**
 *
 * @author util2
 */
public class FrmTable extends javax.swing.JInternalFrame {
    
    private final AVStudio studio;

    private AssEventTableModel dtmASS;    
    private AssEventTableRenderer assEventTableRenderer;
    
    /**
     * Creates new form FrmTable
     * @param studio
     */
    public FrmTable(AVStudio studio) {
        initComponents();
        this.studio = studio;
        init();
    }
    
    private void init(){
        assEventTableRenderer = new AssEventTableRenderer(studio.isDark());
        initializeTable(MainFrame.getLanguage(), MainFrame.getISOCountry());
        setVisible(true);
    }
    
    public void initializeTable(Language in, ISO_3166 get){        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(in.isForced() == true){
            get = in.getIso();
        }
        
        // Fill in the table
        dtmASS = new AssEventTableModel(in, get);
        
        tableASS.setModel(dtmASS);
        
        TableColumn column;
        for (int i = 0; i < 14; i++) {
            column = tableASS.getColumnModel().getColumn(i);
            switch(i){
                case 0:
                    column.setPreferredWidth(40);
                    break; //# (line number)
                case 1:
                    column.setPreferredWidth(150);
                    break; //Type
                case 2:
                    column.setPreferredWidth(40);
                    break; //Layer
                case 3:
                    column.setPreferredWidth(100);
                    break; //Start
                case 4:
                    column.setPreferredWidth(100);
                    break; //End
                case 5:
                    column.setPreferredWidth(20);
                    break; //ML
                case 6:
                    column.setPreferredWidth(20);
                    break; //MR
                case 7:
                    column.setPreferredWidth(20);
                    break; //MV
                case 8:
                    column.setPreferredWidth(150);
                    break; //Style
                case 9:
                    column.setPreferredWidth(150);
                    break; //Name
                case 10:
                    column.setPreferredWidth(150);
                    break; //Effect
                case 11:
                    column.setPreferredWidth(40);
                    break; //CPL
                case 12:
                    column.setPreferredWidth(40);
                    break; //CPS
                case 13:
                    column.setPreferredWidth(1500);
                    break; //Text
            }
        }
        
        tableASS.setDefaultRenderer(String.class, assEventTableRenderer);
        tableASS.setDefaultRenderer(LineType.class, assEventTableRenderer);
        tableASS.setDefaultRenderer(Time.class, assEventTableRenderer);
        tableASS.setDefaultRenderer(Style.class, assEventTableRenderer);
        tableASS.setDefaultRenderer(Integer.class, assEventTableRenderer);
        
        tableASS.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    Event ev = dtmASS.getEventAt(tableASS.getSelectedRow());
                    studio.tableToCommand(ev);
                }
            }
        });
    }
    
    public void loadASSTable(File f){
        ASS ass = ASS.Read(f.getPath());
        studio.setAss(ass);
        dtmASS.insertAll(ass.getEvents());
        tableASS.updateUI();
        studio.getFrmSC().reinit();
    }
    
    public void saveASSTable(File f){
        ASS ass = new ASS();
        List<Event> events = dtmASS.getAllEvents();
        ass.setEvents(events);
        ASS.Save(f.getPath(), ass);
    }
    
    public void alter(Event ev){
        dtmASS.changeEventAt(ev, tableASS.getSelectedRow());
        tableASS.updateUI();
    }
    
    public void add(Event ev){
        dtmASS.insertOne(ev);
        tableASS.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableASS = new javax.swing.JTable();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Table Swing");

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tableASS.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableASS);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1117, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableASS;
    // End of variables declaration//GEN-END:variables
}
