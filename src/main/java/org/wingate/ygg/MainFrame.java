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
package org.wingate.ygg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.MenuElement;
import javax.swing.filechooser.FileFilter;
import org.wingate.freectrl.VCheckBoxMenuItem;
import org.wingate.freectrl.VMenu;
import org.wingate.freectrl.VMenuItem;
import org.wingate.replica.ygg.PeerServer;
import org.wingate.ygg.io.SubtitlesFileFilter;
import org.wingate.ygg.io.YggConf;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.ui.IfrChat;
import org.wingate.ygg.ui.IfrTable;
import org.wingate.ygg.ui.IfrTableLink;
import org.wingate.ygg.ui.IfrVideo;
import org.wingate.ygg.ui.IfrWave;
import org.wingate.ygg.io.VideoFileChooserFileFilter;
import org.wingate.ygg.ui.IfrTranslation;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {
    private final YggConf yggConf = new YggConf();

    private static boolean darkUI = false;
    
    // Language (loading from properties of each component)
    static ISO_3166 wantedIso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    static Language chosen = null;
    
    public enum Section {
        None, Chat, Files, Time, Edit, Drawing, Translate;
    }
    private Section section = Section.None;
    
    // Chat components
    private static IfrChat chat;
    
    // Timing components
    private static IfrTable table;
    private static IfrVideo video;
    private static IfrWave wave;
    private static IfrTableLink tableLink;
    
    // Translation components
    private static IfrTranslation translation;
    
    public MainFrame(boolean dark) {        
        initComponents();
        darkUI = dark;
        init();
    }
    
    private void init(){
        // Chargement du titre de l'application
        setTitle("Yggdrasil v1.2.2 alpha - \"Happy Go Ducky\"");
        
        // Chargement de la configuration...
        yggConf.load();
        
        // Chargement de la langue (à faire après le YggConf.load() et avant les init)
        chosen = new Language();
        
        // Init des objets
        chat = new IfrChat();
        wave = new IfrWave(); // Wave must be init before video cause video needs for it in its init
        video = new IfrVideo(wave);
        tableLink = new IfrTableLink(); // tl must be init before table cause table needs for it in its init
        table = new IfrTable(tableLink);
        tableLink.externallyInitAfterRealInit(wave, video);
        translation = new IfrTranslation();
        
        // Changer la taille de la fenêtre
        setSize(1880, 1058);
        
        // Centrer la fenêtre
        setLocationRelativeTo(null);
        
        // Vérification de forçage de langue (forced) et réattribution ssi
        if(chosen.isForced() == true){
            wantedIso = chosen.getIso();
        }
        
        // Traduction du logiciel dans une autre langue que l'anglais
        translations();
        
        // Chargements des filtres
        for(FileFilter ff : fcAudio.getChoosableFileFilters()){
            fcAudio.removeChoosableFileFilter(ff);
        }
        fcAudio.addChoosableFileFilter(new VideoFileChooserFileFilter());
        for(FileFilter ff : fcASS.getChoosableFileFilters()){
            fcASS.removeChoosableFileFilter(ff);
        }
        fcASS.addChoosableFileFilter(new SubtitlesFileFilter());
        
        //----------------------------------------------------------------------
        // Chat configuration
        //----------------------------------------------------------------------
        chat.setSize(1500, 915);
        chat.setLocation(0, 0);
        chat.setVisible(true);
        //--------------------------------------------------------- CHAT END ---
        
        //----------------------------------------------------------------------
        // Timing configuration
        //----------------------------------------------------------------------
        table.setSize(1864, 300);
        table.setLocation(0, 665);
        table.setVisible(true);
        
        video.setSize(1864/2, 665);
        video.setLocation(0, 0);
        video.setVisible(true);
        
        wave.setSize(1864/2, 300);
        wave.setLocation(1864/2, 0);
        wave.setVisible(true);
        
        tableLink.setSize(1864/2, 365);
        tableLink.setLocation(1864/2, 300);
        tableLink.setVisible(true);
        //------------------------------------------------------- TIMING END ---
        
        //----------------------------------------------------------------------
        // Translation configuration
        //----------------------------------------------------------------------
        translation.setSize(1865, 965);
        translation.setLocation(0, 0);
        translation.setVisible(true);
        //--------------------------------------------------------- CHAT END ---
        
        // Affiche le mode de synchronisation par défaut
        displayTime();
        
        PeerServer.createServer();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Traduction">
    
    private void translations(){
        // Menu        
        // On obtient tous les sous-menus de la barre
        List<MenuElement> out = new ArrayList<>();
        out = getAllChildrenMenu(out, jMenuBar1);
        // On charge la traduction de tous les menus
        for(MenuElement me : out){
            if(me instanceof VMenu){
                VMenu menu = (VMenu)me;
                menu.setText(chosen.getTranslated(menu.getVariableName(), wantedIso, menu.getText()));
            }else if(me instanceof VMenuItem){
                VMenuItem item = (VMenuItem)me;
                item.setText(chosen.getTranslated(item.getVariableName(), wantedIso, item.getText()));
            }else if(me instanceof VCheckBoxMenuItem){
                VCheckBoxMenuItem chck = (VCheckBoxMenuItem)me;
                chck.setText(chosen.getTranslated(chck.getVariableName(), wantedIso, chck.getText()));
            }
        }
        
        // Boutons
        btnMessageTop.setText(chosen.getTranslated("btnMessageTop", wantedIso, btnMessageTop.getText()));
    }
    
    private List<MenuElement> getAllChildrenMenu(List<MenuElement> res, MenuElement me){
        
        for(MenuElement ref : me.getSubElements()){
            res.add(ref);
            if(ref.getSubElements().length > 0){
                getAllChildrenMenu(res, ref);
            }
        }
        
        return res;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Accès statique">
    
    public void setDarkUI(boolean darkUI) {
        MainFrame.darkUI = darkUI;
    }
    
    public static boolean isDark(){
        return darkUI;
    }
    
    public static Language getLanguage(){
        return chosen;
    }
    
    public static ISO_3166 getISO(){
        return wantedIso;
    }
    
    public static IfrVideo getVideoFrame(){
        return video;
    }
    
    public static IfrWave getAudioFrame(){
        return wave;
    }
    
    public static IfrTable getTableFrame(){
        return table;
    }
    
    public static IfrTableLink getTableLinkFrame(){
        return tableLink;
    }
    
    public static IfrChat getChatFrame(){
        return chat;
    }
    
    public static void setProgress(float value, String text){
        progressTask.setValue(Math.round(value * 100f));
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Affichage des éléments">
    
    private void cleanDesktop(){
        deskMain.removeAll();
        deskMain.updateUI();
    }
    
    private void displayChat(){
        cleanDesktop();
        deskMain.add(chat);
        deskMain.updateUI();
        section = Section.Chat;
    }
    
    private void displayTime(){
        cleanDesktop();
        deskMain.add(wave);
        deskMain.add(video);
        deskMain.add(table);
        deskMain.add(tableLink);
        deskMain.updateUI();
        section = Section.Time;
    }
    
    private void displayTranslation(){
        cleanDesktop();
        deskMain.add(translation);
        deskMain.updateUI();
        section = Section.Translate;
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

        fcAudio = new javax.swing.JFileChooser();
        fcASS = new javax.swing.JFileChooser();
        deskMain = new javax.swing.JDesktopPane();
        btnMessageTop = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        progressP2P = new javax.swing.JProgressBar();
        progressTask = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        vmnFile = new org.wingate.freectrl.VMenu();
        vmnFileOpenSubtitles = new org.wingate.freectrl.VMenuItem();
        vmnFileSaveSubtitles = new org.wingate.freectrl.VMenuItem();
        vmnFileOpenVideo = new org.wingate.freectrl.VMenuItem();
        vmnFileOpenAudio = new org.wingate.freectrl.VMenuItem();
        vmnDisplay = new org.wingate.freectrl.VMenu();
        vmnDisplayHideAll = new org.wingate.freectrl.VMenuItem();
        vmnDisplayChat = new org.wingate.freectrl.VMenuItem();
        vmnDisplayFiles = new org.wingate.freectrl.VMenuItem();
        vmnDisplayTranslate = new org.wingate.freectrl.VMenuItem();
        vmnDisplayTime = new org.wingate.freectrl.VMenuItem();
        vmnDisplayEdit = new org.wingate.freectrl.VMenuItem();
        vmnDisplayDrawing = new org.wingate.freectrl.VMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yggdrasil v1.2.1 alpha - \"Happy go Ducky\"");

        javax.swing.GroupLayout deskMainLayout = new javax.swing.GroupLayout(deskMain);
        deskMain.setLayout(deskMainLayout);
        deskMainLayout.setHorizontalGroup(
            deskMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        deskMainLayout.setVerticalGroup(
            deskMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 361, Short.MAX_VALUE)
        );

        btnMessageTop.setText("Send message");

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jPanel1.add(jTextField3);

        jTextField4.setEditable(false);
        jPanel1.add(jTextField4);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        jPanel2.add(progressP2P);
        jPanel2.add(progressTask);

        vmnFile.setText("File");
        vmnFile.setVariableName("vmnFile");

        vmnFileOpenSubtitles.setText("Open subtitles...");
        vmnFileOpenSubtitles.setVariableName("vmnFileOpenSubtitles");
        vmnFileOpenSubtitles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileOpenSubtitlesActionPerformed(evt);
            }
        });
        vmnFile.add(vmnFileOpenSubtitles);

        vmnFileSaveSubtitles.setText("Save subtitles...");
        vmnFileSaveSubtitles.setVariableName("vmnFileSaveSubtitles");
        vmnFileSaveSubtitles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileSaveSubtitlesActionPerformed(evt);
            }
        });
        vmnFile.add(vmnFileSaveSubtitles);

        vmnFileOpenVideo.setText("Open a video...");
        vmnFileOpenVideo.setVariableName("vmnFileOpenVideo");
        vmnFileOpenVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileOpenVideoActionPerformed(evt);
            }
        });
        vmnFile.add(vmnFileOpenVideo);

        vmnFileOpenAudio.setText("Open an audio file...");
        vmnFileOpenAudio.setVariableName("vmnFileOpenAudio");
        vmnFileOpenAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileOpenAudioActionPerformed(evt);
            }
        });
        vmnFile.add(vmnFileOpenAudio);

        jMenuBar1.add(vmnFile);

        vmnDisplay.setText("Display");
        vmnDisplay.setVariableName("vmnDisplay");

        vmnDisplayHideAll.setText("Hide all frames");
        vmnDisplayHideAll.setVariableName("vmnDisplayHideAll");
        vmnDisplayHideAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayHideAllActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayHideAll);

        vmnDisplayChat.setText("Display chat");
        vmnDisplayChat.setVariableName("vmnDisplayChat");
        vmnDisplayChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayChatActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayChat);

        vmnDisplayFiles.setMnemonic('D');
        vmnDisplayFiles.setText("Display files");
        vmnDisplayFiles.setVariableName("vmnDisplayFiles");
        vmnDisplayFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayFilesActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayFiles);

        vmnDisplayTranslate.setText("Display translate");
        vmnDisplayTranslate.setVariableName("vmnDisplayTranslate");
        vmnDisplayTranslate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayTranslateActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayTranslate);

        vmnDisplayTime.setText("Display timing");
        vmnDisplayTime.setToolTipText("");
        vmnDisplayTime.setVariableName("vmnDisplayTime");
        vmnDisplayTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayTimeActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayTime);

        vmnDisplayEdit.setText("Display editing");
        vmnDisplayEdit.setVariableName("vmnDisplayEdit");
        vmnDisplayEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayEditActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayEdit);

        vmnDisplayDrawing.setText("Display drawing");
        vmnDisplayDrawing.setVariableName("vmnDisplayDrawing");
        vmnDisplayDrawing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnDisplayDrawingActionPerformed(evt);
            }
        });
        vmnDisplay.add(vmnDisplayDrawing);

        jMenuBar1.add(vmnDisplay);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMessageTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(deskMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMessageTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deskMain))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vmnFileOpenAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileOpenAudioActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){            
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            switch(section){
                case Time -> {
                    // Si synchronisation est activée (Time)
                    try{
                        wave.openAudio(videoFile);
                    }catch(Exception exc){
                        
                    }
                }
                case Translate -> {
                    // Si traduction est activée (Translation)
                    translation.loadAudio(videoFile);
                }
            }
        }
    }//GEN-LAST:event_vmnFileOpenAudioActionPerformed

    private void vmnFileOpenVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileOpenVideoActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            switch(section){
                case Time -> {
                    // Si synchronisation est activée (Time)
                    try{
                        wave.openAudio(videoFile);
                        video.setVideoPath(videoFile.getPath());
                    }catch(Exception exc){
                        
                    }
                }
                case Translate -> {
                    // Si traduction est activée (Translation)
                    translation.loadAudio(videoFile);
                }
            }
        }
    }//GEN-LAST:event_vmnFileOpenVideoActionPerformed

    private void vmnFileOpenSubtitlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileOpenSubtitlesActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){            
            switch(section){
                case Time -> {
                    // Si synchronisation est activée (Time)
                    try{
                        table.loadASSTable(fcASS.getSelectedFile());
                        video.setSubtitlesFile(fcASS.getSelectedFile());
                    }catch(Exception exc){
                        
                    }
                }
                case Translate -> {
                    // Si traduction est activée (Translation)
                    translation.loadASS(fcASS.getSelectedFile());
                }
            }
        }
    }//GEN-LAST:event_vmnFileOpenSubtitlesActionPerformed

    private void vmnFileSaveSubtitlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileSaveSubtitlesActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showSaveDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            switch(section){
                case Time -> {
                    // Si synchronisation est activée (Time)
                    try{
                        table.saveASSTable(fcASS.getSelectedFile());
                    }catch(Exception exc){
                        
                    }
                }
                case Translate -> {
                    // Si traduction est activée (Translation)
                    
                }
            }
        }
    }//GEN-LAST:event_vmnFileSaveSubtitlesActionPerformed

    private void vmnDisplayHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayHideAllActionPerformed
        cleanDesktop();
    }//GEN-LAST:event_vmnDisplayHideAllActionPerformed

    private void vmnDisplayChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayChatActionPerformed
        displayChat();
    }//GEN-LAST:event_vmnDisplayChatActionPerformed

    private void vmnDisplayFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayFilesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_vmnDisplayFilesActionPerformed

    private void vmnDisplayTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayTimeActionPerformed
        displayTime();
    }//GEN-LAST:event_vmnDisplayTimeActionPerformed

    private void vmnDisplayEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_vmnDisplayEditActionPerformed

    private void vmnDisplayDrawingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayDrawingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_vmnDisplayDrawingActionPerformed

    private void vmnDisplayTranslateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayTranslateActionPerformed
        displayTranslation();
    }//GEN-LAST:event_vmnDisplayTranslateActionPerformed

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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame(false).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMessageTop;
    private javax.swing.JDesktopPane deskMain;
    private javax.swing.JFileChooser fcASS;
    private javax.swing.JFileChooser fcAudio;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JProgressBar progressP2P;
    private static javax.swing.JProgressBar progressTask;
    private org.wingate.freectrl.VMenu vmnDisplay;
    private org.wingate.freectrl.VMenuItem vmnDisplayChat;
    private org.wingate.freectrl.VMenuItem vmnDisplayDrawing;
    private org.wingate.freectrl.VMenuItem vmnDisplayEdit;
    private org.wingate.freectrl.VMenuItem vmnDisplayFiles;
    private org.wingate.freectrl.VMenuItem vmnDisplayHideAll;
    private org.wingate.freectrl.VMenuItem vmnDisplayTime;
    private org.wingate.freectrl.VMenuItem vmnDisplayTranslate;
    private org.wingate.freectrl.VMenu vmnFile;
    private org.wingate.freectrl.VMenuItem vmnFileOpenAudio;
    private org.wingate.freectrl.VMenuItem vmnFileOpenSubtitles;
    private org.wingate.freectrl.VMenuItem vmnFileOpenVideo;
    private org.wingate.freectrl.VMenuItem vmnFileSaveSubtitles;
    // End of variables declaration//GEN-END:variables
}
