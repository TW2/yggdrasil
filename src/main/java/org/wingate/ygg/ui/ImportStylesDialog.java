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
package org.wingate.ygg.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.wingate.ygg.io.AssFileFilter;
import org.wingate.ygg.subs.ASS;
import org.wingate.ygg.subs.AssStyle;

/**
 *
 * @author util2
 */
public class ImportStylesDialog extends javax.swing.JDialog {
    
    public enum DialogResult{
        None, Ok, Cancel;
    }
    
    private DefaultTableModel assStylesModel = null;
    private final Map<String, AssStyle> styles = new HashMap<>();
    private DialogResult dialogResult = DialogResult.None;

    public ImportStylesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }
    
    private void init(){
        for(FileFilter ff : fcImportStyles.getChoosableFileFilters()){
            fcImportStyles.removeChoosableFileFilter(ff);
        }
        fcImportStyles.addChoosableFileFilter(new AssFileFilter());
        fcImportStyles.setMultiSelectionEnabled(true);
        
        // Tableau
        assStylesModel = new DefaultTableModel(
                null,
                new String[]{"Selected ?", "Style"}
        ){
            Class[] types = new Class [] {Boolean.class, String.class};
            boolean[] canEdit = new boolean [] {true, false};
            @Override
            public Class getColumnClass(int columnIndex) {return types [columnIndex];}
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        };
        
        tableStyles.setModel(assStylesModel);
        
        TableColumn column;
        for (int i = 0; i < 2; i++) {
            column = tableStyles.getColumnModel().getColumn(i);
            switch(i){
                case 0 -> column.setPreferredWidth(60);
                case 1 -> column.setPreferredWidth(600);
            }
        }
    }
    
    public void showDialog(){        
        int z = fcImportStyles.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            // On charge tous les styles de tous les fichiers sélectionnés
            for(File f : fcImportStyles.getSelectedFiles()){
                ASS ass = ASS.Read(f.getPath());
                Map<String, AssStyle> newStyles = ass.getStyles();
                styles.putAll(newStyles);
            }
            
            // On sort de la fonction si le nombre d'élément est nul
            if(styles.isEmpty()) return;
            
            // On remplit le tableau
            for(Map.Entry<String, AssStyle> entry : styles.entrySet()){
                assStylesModel.addRow(new Object[]{
                    false, entry.getKey()
                });
            }            
            tableStyles.updateUI();
            
            // On affiche la dialog
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    public Map<String, AssStyle> getStyles() {
        // On crée un objet pour la valeur de retour
        Map<String, AssStyle> selectedStyles = new HashMap<>();
        
        // Pour chaque élément du tableau
        for(int i=0; i<tableStyles.getRowCount(); i++){            
            // On obtient un état de sélection
            boolean value = (boolean)tableStyles.getValueAt(i, 0);
            
            // Si la ligne est cochée
            if(value == true){                
                // Pour chaque entrée
                for(Map.Entry<String, AssStyle> entry : styles.entrySet()){                    
                    // Si cela coïncide avec un style dans le tableau
                    if(tableStyles.getValueAt(i, 1).toString().equals(entry.getKey())){
                        // On ajoue la valeur à la map de la valeur de retour
                        selectedStyles.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
            }
        }
        
        // Retourne les styles sélectionés
        return selectedStyles;
    }
    
    public String getCollection(){
        return tfCollection.getText().isEmpty() ? "Default movie" : tfCollection.getText();
    }

    public DialogResult getDialogResult() {
        return dialogResult;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcImportStyles = new javax.swing.JFileChooser();
        lblImportStyles = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableStyles = new javax.swing.JTable();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblCollection = new javax.swing.JLabel();
        tfCollection = new org.wingate.freectrl.PlaceholderTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblImportStyles.setText("Choose the styles to import : ");

        tableStyles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Selected ?", "Style"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableStyles);
        if (tableStyles.getColumnModel().getColumnCount() > 0) {
            tableStyles.getColumnModel().getColumn(0).setResizable(false);
            tableStyles.getColumnModel().getColumn(1).setResizable(false);
            tableStyles.getColumnModel().getColumn(1).setPreferredWidth(600);
        }

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblCollection.setText("Collection name : ");

        tfCollection.setPlaceholder("Choose a collection name (Default is 'Default movie')");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblImportStyles)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCollection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCollection, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOK)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImportStyles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel)
                    .addComponent(lblCollection)
                    .addComponent(tfCollection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        dialogResult = DialogResult.Ok;
        dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dialogResult = DialogResult.Cancel;
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImportStylesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            ImportStylesDialog dialog = new ImportStylesDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JFileChooser fcImportStyles;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCollection;
    private javax.swing.JLabel lblImportStyles;
    private javax.swing.JTable tableStyles;
    private org.wingate.freectrl.PlaceholderTextField tfCollection;
    // End of variables declaration//GEN-END:variables
}
