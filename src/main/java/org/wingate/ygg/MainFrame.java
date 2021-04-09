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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.MenuElement;
import javax.swing.filechooser.FileFilter;
import org.wingate.freectrl.VCheckBoxMenuItem;
import org.wingate.freectrl.VMenu;
import org.wingate.freectrl.VMenuItem;
import org.wingate.ygg.chat.Chat;
import org.wingate.ygg.io.AssFileFilter;
import org.wingate.ygg.io.Client;
import org.wingate.ygg.io.MainFileFilter;
import org.wingate.ygg.io.Server;
import org.wingate.ygg.io.SrtFileFilter;
import org.wingate.ygg.io.SsbFileFilter;
import org.wingate.ygg.io.VesFileFilter;
import org.wingate.ygg.io.YggConf;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.ui.IfrTable;
import org.wingate.ygg.ui.IfrTableLink;
import org.wingate.ygg.ui.IfrVideo;
import org.wingate.ygg.ui.IfrWave;
import org.wingate.ygg.io.VideoFileChooserFileFilter;
import org.wingate.ygg.io.WebVTTFileFilter;
import org.wingate.ygg.subs.SubsData;
import org.wingate.ygg.ui.IfrTranslation;
import org.wingate.ygg.ui.SubsChoiceDialog;
import org.wingate.ygglock.YggLock;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {
    private final YggConf yggConf = new YggConf();

    private static boolean darkUI = false;
    
    private static final List<YggLock.CryptObj> cryptObjs = new ArrayList<>();
    private YggLock.CryptObj localObj = null;
    private Server myServer = null;
    private static Chat chat = null;
    
    // Language (loading from properties of each component)
    static ISO_3166 wantedIso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    static Language chosen = null;
    
    // Subs collection
    SubsData subsData = new SubsData();
    
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
        setTitle("Yggdrasil v1.2.4 alpha - \"Happy Go Ducky\"");
        setIconImage(new ImageIcon(getClass().getResource("/images/YGG_Icone.png")).getImage());
        
        // Chargement de la configuration...
        yggConf.load();
        
        // Chargement de la langue (à faire après le YggConf.load() et avant les init)
        chosen = new Language();
        
        // Init des objets
        wave = new IfrWave(); // Wave must be init before video cause video needs for it in its init
        video = new IfrVideo(wave);
        tableLink = new IfrTableLink(); // tl must be init before table cause table needs for it in its init
        table = new IfrTable();        
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
        fcASS.addChoosableFileFilter(new AssFileFilter());
        fcASS.addChoosableFileFilter(new SsbFileFilter());
        fcASS.addChoosableFileFilter(new SrtFileFilter());
        fcASS.addChoosableFileFilter(new VesFileFilter());
        fcASS.addChoosableFileFilter(new WebVTTFileFilter());
        // On redéfinit le fcNetworkConnect
        for(FileFilter ff : fcNetworkConnect.getChoosableFileFilters()){
            fcNetworkConnect.removeChoosableFileFilter(ff);
        }
        fcNetworkConnect.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.isDirectory()) return true;
                return file.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Text file containing a key";
            }
        });        
        
        //----------------------------------------------------------------------
        // Timing configuration
        //----------------------------------------------------------------------
        table.setSize(1864, 232);
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
        
        // On initialise des classes
        chat = new Chat(chatTextPane.getStyledDocument());
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
    
    public static List<YggLock.CryptObj> getCryptObjs() {
        return cryptObjs;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Affichage des éléments">
    
    private void cleanDesktop(){
        deskMain.removeAll();
        deskMain.updateUI();
    }
    
    private void displayTime(){
        cleanDesktop();
        deskMain.add(wave);
        deskMain.add(video);
        deskMain.add(table);
        deskMain.add(tableLink);
        deskMain.updateUI();
    }
    
    private void displayTranslation(){
        cleanDesktop();
        deskMain.add(translation);
        deskMain.updateUI();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Réseau">
    
    public static void setMessageToChat(String author, String message){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String sh = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
        String sm = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
        String ss = s < 10 ? "0" + Integer.toString(s) : Integer.toString(s);
        String time = sh + ":" + sm + ":" + ss;
        String toPrint = "[" + time + "] <" + author + "> " + message;
        chat.addToDoc(toPrint);
    }
    
    public static void setProposalToChat(String author, String message){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String sh = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
        String sm = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
        String ss = s < 10 ? "0" + Integer.toString(s) : Integer.toString(s);
        String time = sh + ":" + sm + ":" + ss;
        String toPrint = "[" + time + "] " + author + " " + message;
        chat.addToDoc(toPrint);
    }
    
    public static void setRequestToChat(String author, String message){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String sh = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
        String sm = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
        String ss = s < 10 ? "0" + Integer.toString(s) : Integer.toString(s);
        String time = sh + ":" + sm + ":" + ss;
        String toPrint = "[" + time + "] " + author + " " + message;
        chat.addToDoc(toPrint);
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
        fcNetworkConnect = new javax.swing.JFileChooser();
        deskMain = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextPane = new javax.swing.JTextPane();
        btnSendText = new javax.swing.JButton();
        chatTextField = new org.wingate.freectrl.PlaceholderTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        vmnFile = new org.wingate.freectrl.VMenu();
        vmnFileTime = new org.wingate.freectrl.VMenu();
        vmnFileTimeNewSubs = new org.wingate.freectrl.VMenuItem();
        vmnFileTimeOpenSubs = new org.wingate.freectrl.VMenuItem();
        vmnFileTimeSaveSubs = new org.wingate.freectrl.VMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        vmnFileTimeOpenVideo = new org.wingate.freectrl.VMenuItem();
        vmnFileTimeOpenAudio = new org.wingate.freectrl.VMenuItem();
        vmnFileTranslate = new org.wingate.freectrl.VMenu();
        vmnFileTranslateOpenSubs = new org.wingate.freectrl.VMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        vmnFileTranslateOpenVideo = new org.wingate.freectrl.VMenuItem();
        vmnFileTranslateOpenAudio = new org.wingate.freectrl.VMenuItem();
        vmnFileNetwork = new org.wingate.freectrl.VMenu();
        vmnNetworkMyServ = new org.wingate.freectrl.VMenuItem();
        vmnNetworkAddClients = new org.wingate.freectrl.VMenuItem();
        vmnDisplay = new org.wingate.freectrl.VMenu();
        vmnDisplayHideAll = new org.wingate.freectrl.VMenuItem();
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
            .addGap(0, 718, Short.MAX_VALUE)
        );
        deskMainLayout.setVerticalGroup(
            deskMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        chatTextPane.setEditable(false);
        jScrollPane1.setViewportView(chatTextPane);

        btnSendText.setText("Send");
        btnSendText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendTextActionPerformed(evt);
            }
        });

        chatTextField.setPlaceholder("Type a text here");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSendText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSendText)
                    .addComponent(chatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        vmnFile.setText("File");
        vmnFile.setVariableName("vmnFile");

        vmnFileTime.setText("Timing");

        vmnFileTimeNewSubs.setText("New subtitles...");
        vmnFileTimeNewSubs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTimeNewSubsActionPerformed(evt);
            }
        });
        vmnFileTime.add(vmnFileTimeNewSubs);

        vmnFileTimeOpenSubs.setText("Open subtitles...");
        vmnFileTimeOpenSubs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTimeOpenSubsActionPerformed(evt);
            }
        });
        vmnFileTime.add(vmnFileTimeOpenSubs);

        vmnFileTimeSaveSubs.setText("Save subtitles...");
        vmnFileTimeSaveSubs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTimeSaveSubsActionPerformed(evt);
            }
        });
        vmnFileTime.add(vmnFileTimeSaveSubs);
        vmnFileTime.add(jSeparator2);

        vmnFileTimeOpenVideo.setText("Open a video...");
        vmnFileTimeOpenVideo.setVariableName("vmnFileOpenVideo");
        vmnFileTimeOpenVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTimeOpenVideoActionPerformed(evt);
            }
        });
        vmnFileTime.add(vmnFileTimeOpenVideo);

        vmnFileTimeOpenAudio.setText("Open an audio file...");
        vmnFileTimeOpenAudio.setVariableName("vmnFileOpenAudio");
        vmnFileTimeOpenAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTimeOpenAudioActionPerformed(evt);
            }
        });
        vmnFileTime.add(vmnFileTimeOpenAudio);

        vmnFile.add(vmnFileTime);

        vmnFileTranslate.setText("Translation");

        vmnFileTranslateOpenSubs.setText("Open subtitles...");
        vmnFileTranslateOpenSubs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTranslateOpenSubsActionPerformed(evt);
            }
        });
        vmnFileTranslate.add(vmnFileTranslateOpenSubs);
        vmnFileTranslate.add(jSeparator1);

        vmnFileTranslateOpenVideo.setText("Open a video...");
        vmnFileTranslateOpenVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTranslateOpenVideoActionPerformed(evt);
            }
        });
        vmnFileTranslate.add(vmnFileTranslateOpenVideo);

        vmnFileTranslateOpenAudio.setText("Open an audio file...");
        vmnFileTranslateOpenAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnFileTranslateOpenAudioActionPerformed(evt);
            }
        });
        vmnFileTranslate.add(vmnFileTranslateOpenAudio);

        vmnFile.add(vmnFileTranslate);

        vmnFileNetwork.setText("Network");

        vmnNetworkMyServ.setText("Connect my server...");
        vmnNetworkMyServ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnNetworkMyServActionPerformed(evt);
            }
        });
        vmnFileNetwork.add(vmnNetworkMyServ);

        vmnNetworkAddClients.setText("Add clients...");
        vmnNetworkAddClients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vmnNetworkAddClientsActionPerformed(evt);
            }
        });
        vmnFileNetwork.add(vmnNetworkAddClients);

        vmnFile.add(vmnFileNetwork);

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
            .addComponent(deskMain)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(deskMain)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vmnFileTimeOpenAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTimeOpenAudioActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){            
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            try{
                wave.openAudio(videoFile);
            }catch(Exception exc){

            }
        }
    }//GEN-LAST:event_vmnFileTimeOpenAudioActionPerformed

    private void vmnFileTimeOpenVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTimeOpenVideoActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            try{
                wave.openAudio(videoFile);
                video.setVideoPath(videoFile.getPath());                        
            }catch(Exception exc){

            }
        }
    }//GEN-LAST:event_vmnFileTimeOpenVideoActionPerformed

    private void vmnDisplayHideAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnDisplayHideAllActionPerformed
        cleanDesktop();
    }//GEN-LAST:event_vmnDisplayHideAllActionPerformed

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

    private void vmnFileTimeNewSubsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTimeNewSubsActionPerformed
        SubsChoiceDialog subsChoice = new SubsChoiceDialog(this, true);
        subsChoice.showDialog();
        if(subsChoice.getDialogResult() == SubsChoiceDialog.DialogResult.OK){
            switch(subsChoice.getChoice()){
                case ASS -> {                    
                    try {
                        File temp = new File("temporary.ass");
                        temp.createNewFile();
                        table.addASSTable(temp);
                    } catch (IOException ex) { }
                }
                case SSB -> {
                    try {
                        File temp = new File("temporary.ssb");
                        temp.createNewFile();
                        table.addSSBTable(temp);
                    } catch (IOException ex) { }
                }
                case SRT -> {
                    try {
                        File temp = new File("temporary.srt");
                        temp.createNewFile();
                        table.addSRTTable(temp);
                    } catch (IOException ex) { }
                }
                case WebVTT -> {
                    try {
                        File temp = new File("temporary.vtt");
                        temp.createNewFile();
                        table.addVTTTable(temp);
                    } catch (IOException ex) { }
                }
                case VES -> {
                    try {
                        File temp = new File("temporary.ves");
                        temp.createNewFile();
                        table.addVESTable(temp);
                    } catch (IOException ex) { }
                }
            }
            displayTime();
        }
    }//GEN-LAST:event_vmnFileTimeNewSubsActionPerformed

    private void vmnFileTimeOpenSubsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTimeOpenSubsActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            try{
                File file = fcASS.getSelectedFile();
                if(file.getName().endsWith(".ass")) table.addASSTable(file);
                if(file.getName().endsWith(".ssb")) table.addSSBTable(file);
                if(file.getName().endsWith(".srt")) table.addSRTTable(file);
                if(file.getName().endsWith(".ves")) table.addVESTable(file);
                if(file.getName().endsWith(".vtt")) table.addVTTTable(file);

                video.setSubtitlesFile(file);
            }catch(Exception exc){

            }
        }
    }//GEN-LAST:event_vmnFileTimeOpenSubsActionPerformed

    private void vmnFileTimeSaveSubsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTimeSaveSubsActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showSaveDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            try{
                // On recherche l'extension (ASS, SSB, SRT, VES, WebVTT)
                MainFileFilter mff;
                if(fcASS.getFileFilter() instanceof MainFileFilter){
                    mff = (MainFileFilter)fcASS.getFileFilter();
                }else{
                    return;
                }

                // On identifie le fichier
                File save = fcASS.getSelectedFile();

                // On met l'extension du fichier si il n'y en a pas
                if(save.getName().contains(".") == false){
                    save = new File(save.getParentFile(), save.getName() + mff.getExtension());
                }

                // On sauvegarde le fichier en fonction de l'extension
                table.save(save, mff.getExtension());                        
            }catch(Exception exc){

            }
        }
    }//GEN-LAST:event_vmnFileTimeSaveSubsActionPerformed

    private void vmnFileTranslateOpenSubsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTranslateOpenSubsActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            translation.loadASS(fcASS.getSelectedFile());
        }
    }//GEN-LAST:event_vmnFileTranslateOpenSubsActionPerformed

    private void vmnFileTranslateOpenVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTranslateOpenVideoActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            translation.loadAudio(videoFile);
        }
    }//GEN-LAST:event_vmnFileTranslateOpenVideoActionPerformed

    private void vmnFileTranslateOpenAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnFileTranslateOpenAudioActionPerformed
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){            
            // Préparation de variables
            File videoFile = fcAudio.getSelectedFile();
            
            translation.loadAudio(videoFile);
        }
    }//GEN-LAST:event_vmnFileTranslateOpenAudioActionPerformed

    private void btnSendTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendTextActionPerformed
        if(chatTextField.getText().isEmpty() == false){
            setMessageToChat(localObj.getSurname(), chatTextField.getText());
            for(YggLock.CryptObj co : cryptObjs){
                try {
                    Client nakama = new Client(co);
                    nakama.connect();
                    nakama.sendMessage(chatTextField.getText());
                    nakama.close();
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_btnSendTextActionPerformed

    private void vmnNetworkMyServActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnNetworkMyServActionPerformed
        int z = fcNetworkConnect.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            boolean update = false;
            File f = fcNetworkConnect.getSelectedFile();
            try(FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);){
                // On récupère une clé et on la décrypte
                YggLock.CryptObj cObj = YggLock.decrypt(br.readLine());
                
                // On regarde si elle n'apparaît pas déjà
                if(localObj != null && localObj.getIp().equalsIgnoreCase(cObj.getIp()) == true){
                    if(localObj.getPort() != cObj.getPort()){
                        update = true;
                        localObj = cObj;                        
                    }
                }
                
                // On fait l'action requise
                if(update == false){
                    localObj = cObj;
                }
                
                if(myServer != null){
                    myServer.stopThread();
                }
                myServer = new Server(localObj);
                myServer.startServer();
                vmnNetworkMyServ.setEnabled(false);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_vmnNetworkMyServActionPerformed

    private void vmnNetworkAddClientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vmnNetworkAddClientsActionPerformed
        fcNetworkConnect.setMultiSelectionEnabled(true);
        int z = fcNetworkConnect.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            for(File f : fcNetworkConnect.getSelectedFiles()){
                boolean add = true;
                boolean update = false;
                YggLock.CryptObj obsolete = null;
                try(FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr);){
                    // On récupère une clé et on la décrypte
                    YggLock.CryptObj cObj = YggLock.decrypt(br.readLine());

                    // On regarde si elle n'apparaît pas déjà
                    for(YggLock.CryptObj co : cryptObjs){
                        if(co.getIp().equalsIgnoreCase(cObj.getIp()) == true){
                            if(co.getPort() != cObj.getPort()){
                                update = true;
                                obsolete = co;
                                break;
                            }else{
                                add = false;
                                break;
                            }
                        } 
                    }

                    // On fait l'action requise
                    if(update == true && obsolete != null){
                        cryptObjs.remove(obsolete);
                        cryptObjs.add(cObj);
                    }else if(add == true){
                        cryptObjs.add(cObj);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    fcNetworkConnect.setMultiSelectionEnabled(false);
                }
            }
        }
        fcNetworkConnect.setMultiSelectionEnabled(false);
    }//GEN-LAST:event_vmnNetworkAddClientsActionPerformed

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
    private javax.swing.JButton btnSendText;
    private org.wingate.freectrl.PlaceholderTextField chatTextField;
    private javax.swing.JTextPane chatTextPane;
    private javax.swing.JDesktopPane deskMain;
    private javax.swing.JFileChooser fcASS;
    private javax.swing.JFileChooser fcAudio;
    private javax.swing.JFileChooser fcNetworkConnect;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private org.wingate.freectrl.VMenu vmnDisplay;
    private org.wingate.freectrl.VMenuItem vmnDisplayDrawing;
    private org.wingate.freectrl.VMenuItem vmnDisplayEdit;
    private org.wingate.freectrl.VMenuItem vmnDisplayFiles;
    private org.wingate.freectrl.VMenuItem vmnDisplayHideAll;
    private org.wingate.freectrl.VMenuItem vmnDisplayTime;
    private org.wingate.freectrl.VMenuItem vmnDisplayTranslate;
    private org.wingate.freectrl.VMenu vmnFile;
    private org.wingate.freectrl.VMenu vmnFileNetwork;
    private org.wingate.freectrl.VMenu vmnFileTime;
    private org.wingate.freectrl.VMenuItem vmnFileTimeNewSubs;
    private org.wingate.freectrl.VMenuItem vmnFileTimeOpenAudio;
    private org.wingate.freectrl.VMenuItem vmnFileTimeOpenSubs;
    private org.wingate.freectrl.VMenuItem vmnFileTimeOpenVideo;
    private org.wingate.freectrl.VMenuItem vmnFileTimeSaveSubs;
    private org.wingate.freectrl.VMenu vmnFileTranslate;
    private org.wingate.freectrl.VMenuItem vmnFileTranslateOpenAudio;
    private org.wingate.freectrl.VMenuItem vmnFileTranslateOpenSubs;
    private org.wingate.freectrl.VMenuItem vmnFileTranslateOpenVideo;
    private org.wingate.freectrl.VMenuItem vmnNetworkAddClients;
    private org.wingate.freectrl.VMenuItem vmnNetworkMyServ;
    // End of variables declaration//GEN-END:variables
}
