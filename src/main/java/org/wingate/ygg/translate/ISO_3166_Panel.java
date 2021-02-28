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
package org.wingate.ygg.translate;

import java.awt.BorderLayout;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.wingate.timelibrary.Time;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.subs.ass.AssStyle;
import org.wingate.ygg.subs.ass.AssEventTableModel;
import org.wingate.ygg.subs.ass.AssEventTableRenderer;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;

/**
 *
 * @author util2
 */
public class ISO_3166_Panel extends JPanel {
    
    private ISO_3166 iso3166 = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());    
    private final JTable table = new JTable();
    private final JScrollPane sp = new JScrollPane(table);
    private AssEventTableModel assEventTableModel = null;
    private final AssEventTableRenderer renderer = new AssEventTableRenderer(MainFrame.isDark());

    public ISO_3166_Panel() {
        init();
    }
    
    private void init(){
        initializeTableOne(MainFrame.getLanguage(), MainFrame.getISO());
        
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        table.updateUI();
    }
    
    public void initializeTableOne(Language in, ISO_3166 get){        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(in.isForced() == true){
            get = in.getIso();
        }
        
        // Fill in the table
        assEventTableModel = new AssEventTableModel(in, get);
        
        table.setModel(assEventTableModel);
        
        TableColumn column;
        for (int i = 0; i < 14; i++) {
            column = table.getColumnModel().getColumn(i);
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
        
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(AssEvent.LineType.class, renderer);
        table.setDefaultRenderer(Time.class, renderer);
        table.setDefaultRenderer(AssStyle.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
    }

    public ISO_3166 getIso3166() {
        return iso3166;
    }

    public void setIso3166(ISO_3166 iso3166) {
        this.iso3166 = iso3166;
    }

    public AssEventTableModel getAssEventTableModel() {
        return assEventTableModel;
    }

    public JTable getTable() {
        return table;
    }

    public JScrollPane getScrollPane() {
        return sp;
    }
    
}
