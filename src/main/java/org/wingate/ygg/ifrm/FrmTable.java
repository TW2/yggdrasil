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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.wingate.ygg.util.Clipboard;
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

        popTable = new javax.swing.JPopupMenu();
        popTableRadioSeeAll = new javax.swing.JRadioButtonMenuItem();
        popTableRadioHideTags = new javax.swing.JRadioButtonMenuItem();
        popTableRadioStripAll = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        popTableCut = new javax.swing.JMenuItem();
        popTableCopy = new javax.swing.JMenuItem();
        popTablePaste = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        popTableDuplicate = new javax.swing.JMenuItem();
        popTableRemove = new javax.swing.JMenuItem();
        bgPopTableTags = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableASS = new javax.swing.JTable();

        bgPopTableTags.add(popTableRadioSeeAll);
        popTableRadioSeeAll.setSelected(true);
        popTableRadioSeeAll.setText("See tags (Normal)");
        popTableRadioSeeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableRadioSeeAllActionPerformed(evt);
            }
        });
        popTable.add(popTableRadioSeeAll);

        bgPopTableTags.add(popTableRadioHideTags);
        popTableRadioHideTags.setText("View closed tags (With Items)");
        popTableRadioHideTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableRadioHideTagsActionPerformed(evt);
            }
        });
        popTable.add(popTableRadioHideTags);

        bgPopTableTags.add(popTableRadioStripAll);
        popTableRadioStripAll.setText("See text only (Strip all)");
        popTableRadioStripAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableRadioStripAllActionPerformed(evt);
            }
        });
        popTable.add(popTableRadioStripAll);
        popTable.add(jSeparator1);

        popTableCut.setText("Cut");
        popTableCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableCutActionPerformed(evt);
            }
        });
        popTable.add(popTableCut);

        popTableCopy.setText("Copy");
        popTableCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableCopyActionPerformed(evt);
            }
        });
        popTable.add(popTableCopy);

        popTablePaste.setText("Paste");
        popTablePaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTablePasteActionPerformed(evt);
            }
        });
        popTable.add(popTablePaste);
        popTable.add(jSeparator2);

        popTableDuplicate.setText("Duplicate");
        popTableDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableDuplicateActionPerformed(evt);
            }
        });
        popTable.add(popTableDuplicate);

        popTableRemove.setText("Remove");
        popTableRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popTableRemoveActionPerformed(evt);
            }
        });
        popTable.add(popTableRemove);

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
        tableASS.setComponentPopupMenu(popTable);
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

    private void popTableRadioSeeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableRadioSeeAllActionPerformed
        assEventTableRenderer.setTexttype(AssEventTableRenderer.TextType.Normal);
        tableASS.updateUI();
    }//GEN-LAST:event_popTableRadioSeeAllActionPerformed

    private void popTableRadioHideTagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableRadioHideTagsActionPerformed
        assEventTableRenderer.setTexttype(AssEventTableRenderer.TextType.WithItems);
        tableASS.updateUI();
    }//GEN-LAST:event_popTableRadioHideTagsActionPerformed

    private void popTableRadioStripAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableRadioStripAllActionPerformed
        assEventTableRenderer.setTexttype(AssEventTableRenderer.TextType.StripAll);
        tableASS.updateUI();
    }//GEN-LAST:event_popTableRadioStripAllActionPerformed

    private void popTableCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableCutActionPerformed
        if(tableASS.getSelectedRow() != -1){
            int[] rows = tableASS.getSelectedRows();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<rows.length; i++){
                sb.append(Event.getAssEventLine(dtmASS.getEventAt(rows[i])));
                if(i<rows.length - 1){                    
                    sb.append("\n");
                }
            }
            boolean result = Clipboard.CCopy(sb.toString());
            if(result == true){
                for(int i = rows.length - 1; i >= 0; i--){
                    dtmASS.removeOne(rows[i]);
                }
            }
            tableASS.updateUI();
        }
    }//GEN-LAST:event_popTableCutActionPerformed

    private void popTableCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableCopyActionPerformed
        if(tableASS.getSelectedRow() != -1){
            int[] rows = tableASS.getSelectedRows();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<rows.length; i++){
                sb.append(Event.getAssEventLine(dtmASS.getEventAt(rows[i])));
                if(i<rows.length - 1){                    
                    sb.append("\n");
                }
            }
            Clipboard.CCopy(sb.toString());
        }
    }//GEN-LAST:event_popTableCopyActionPerformed

    private void popTablePasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTablePasteActionPerformed
        String lines = Clipboard.CPaste();
        if(lines.isEmpty() == false){
            List<Event> evts = new ArrayList<>();
            try(StringReader sr = new StringReader(lines); BufferedReader br = new BufferedReader(sr);){
                String line;
                while((line = br.readLine()) != null){
                    Event ev = Event.createFromASS(line);
                    evts.add(ev);
                }
            } catch (IOException ex) {
                return;
            }
            
            if(tableASS.getSelectedRow() != -1){
                int selectedRow = tableASS.getSelectedRow();
                for(int i = evts.size() - 1; i>= 0; i--){
                    Event ev = evts.get(i);
                    dtmASS.insertOneAt(ev, selectedRow);
                }
            }else{
                dtmASS.insertAll(evts);
            }
            tableASS.updateUI();
        }        
    }//GEN-LAST:event_popTablePasteActionPerformed

    private void popTableDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableDuplicateActionPerformed
        if(tableASS.getSelectedRow() != -1){
            int[] rows = tableASS.getSelectedRows();
            int selectedRow = rows[rows.length - 1];
            
            List<Event> evts = new ArrayList<>();
            for(int i=0; i<rows.length; i++){
                evts.add(dtmASS.getEventAt(rows[i]));
            }            
            
            for(int i = evts.size() - 1; i>= 0; i--){
                Event ev = evts.get(i);
                dtmASS.insertOneAt(ev, selectedRow);
            }
            tableASS.updateUI();
        }
    }//GEN-LAST:event_popTableDuplicateActionPerformed

    private void popTableRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popTableRemoveActionPerformed
        if(tableASS.getSelectedRow() != -1){
            int[] rows = tableASS.getSelectedRows();
            for(int i = rows.length - 1; i >= 0; i--){
                dtmASS.removeOne(rows[i]);
            }
            tableASS.updateUI();
        }
    }//GEN-LAST:event_popTableRemoveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgPopTableTags;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu popTable;
    private javax.swing.JMenuItem popTableCopy;
    private javax.swing.JMenuItem popTableCut;
    private javax.swing.JMenuItem popTableDuplicate;
    private javax.swing.JMenuItem popTablePaste;
    private javax.swing.JRadioButtonMenuItem popTableRadioHideTags;
    private javax.swing.JRadioButtonMenuItem popTableRadioSeeAll;
    private javax.swing.JRadioButtonMenuItem popTableRadioStripAll;
    private javax.swing.JMenuItem popTableRemove;
    private javax.swing.JTable tableASS;
    // End of variables declaration//GEN-END:variables
}
