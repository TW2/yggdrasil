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
package org.wingate.ygg.chat;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.ui.IfrTable;
import org.wingate.ygg.ui.PRChatSeeDialog;
import org.wingate.ygg.ui.synctable.SelectedFormat;

/**
 *
 * @author util2
 */
public class PRStyles {

    public PRStyles() {
    }
    
    public static void ASS_addProposalToDoc(JTextPane textPane, AssEvent ev){
        // SEE BUTTON -> Dialog with text
        JButton btnSee = new JButton("See");
        btnSee.addActionListener((ActionEvent e) -> {
            PRChatSeeDialog dialog = new PRChatSeeDialog(new javax.swing.JFrame(), true);
            dialog.showDialog(ev, PRChatSeeDialog.Type.Proposal);
        });
        textPane.insertComponent(btnSee);
        
        // ADD BEFORE BUTTON
        JButton btnBefore = new JButton("Add before");
        btnBefore.addActionListener((ActionEvent e) -> {
            if(MainFrame.getTableFrame() == null) return;
            if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
                //===---
                IfrTable table = MainFrame.getTableFrame();
                //===---
                if(table.getTable().getSelectedRow() != -1){
                    table.getLastAssSynchroTable().getAssTableModel().insertOneAt(ev, table.getTable().getSelectedRow());
                    table.getLastAssSynchroTable().getTable().updateUI();
                }
            }
        });
        textPane.insertComponent(btnBefore);
        
        // ADD AFTER BUTTON
        JButton btnAfter = new JButton("Add after");
        btnAfter.addActionListener((ActionEvent e) -> {
            if(MainFrame.getTableFrame() == null) return;
            if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
                //===---
                IfrTable table = MainFrame.getTableFrame();
                //===---
                if(table.getTable().getSelectedRow() != -1){
                    if(table.getTable().getRowCount() - 1 == table.getTable().getSelectedRow()){
                        // We are at last event
                        table.getLastAssSynchroTable().getAssTableModel().insertOne(ev);
                    }else{
                        // We are inside the events cosmos
                        table.getLastAssSynchroTable().getAssTableModel().insertOneAt(ev, table.getTable().getSelectedRow() + 1);
                    }
                    table.getLastAssSynchroTable().getTable().updateUI();
                }
            }
        });
        textPane.insertComponent(btnAfter);
    }
    
    public static void ASS_addRequestToDoc(JTextPane textPane, AssEvent ev){
        // SEE BUTTON -> Dialog with text
        JButton btnSee = new JButton("See");
        btnSee.addActionListener((ActionEvent e) -> {
            PRChatSeeDialog dialog = new PRChatSeeDialog(new javax.swing.JFrame(), true);
            dialog.showDialog(ev, PRChatSeeDialog.Type.Request);
        });
        textPane.insertComponent(btnSee);
        // ADD BEFORE BUTTON
        JButton btnBefore = new JButton("Add before");
        btnBefore.addActionListener((ActionEvent e) -> {
            if(MainFrame.getTableFrame() == null) return;
            if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
                //===---
                IfrTable table = MainFrame.getTableFrame();
                //===---
                if(table.getTable().getSelectedRow() != -1){
                    table.getLastAssSynchroTable().getAssTableModel().insertOneAt(ev, table.getTable().getSelectedRow());
                    table.getLastAssSynchroTable().getTable().updateUI();
                }
            }
        });
        textPane.insertComponent(btnBefore);
        // ADD AFTER BUTTON
        JButton btnAfter = new JButton("Add after");
        btnAfter.addActionListener((ActionEvent e) -> {
            if(MainFrame.getTableFrame() == null) return;
            if(MainFrame.getTableFrame().getFormat() == SelectedFormat.ASS){
                //===---
                IfrTable table = MainFrame.getTableFrame();
                //===---
                if(table.getTable().getSelectedRow() != -1){
                    if(table.getTable().getRowCount() - 1 == table.getTable().getSelectedRow()){
                        // We are at last event
                        table.getLastAssSynchroTable().getAssTableModel().insertOne(ev);
                    }else{
                        // We are inside the events cosmos
                        table.getLastAssSynchroTable().getAssTableModel().insertOneAt(ev, table.getTable().getSelectedRow() + 1);
                    }
                    table.getLastAssSynchroTable().getTable().updateUI();
                }
            }
        });
        textPane.insertComponent(btnAfter);
    }
}
