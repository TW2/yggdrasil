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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.wingate.ygg.base.AVStudio;
import org.wingate.ygg.karaoke.KaraokeLanguage;
import org.wingate.ygg.language.ISO_3166;
import org.wingate.ygg.language.Language;
import org.wingate.ygg.util.YGGY;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {

    JPanel paneForDesktop;
    JDesktopPane desktop = new JDesktopPane();
    AVStudio studio = null;
    boolean viewStudio = false;
    
    // Langage des karaokés
    static KaraokeLanguage karaokeLanguage = KaraokeLanguage.Romaji;
    
    // Language (loading from properties of each component)
    static ISO_3166 wantedIso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    static Language chosen = new Language();
    
    // Pour avoir des sous-titres
    static YGGY yggy = YGGY.create();
    
    // Pour savoir si on est en mode dark
    boolean dark = false;
    
    // Fichiers vidéos :
    private final static String[] videoFiles = new String[]{".mp4", ".mkv"};
    
    /**
     * Creates new form MainFrame
     * @param dark
     */
    public MainFrame(boolean dark) {
        initComponents();
        this.dark = dark;
        init();
    }
    
    private void init(){
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        
        //======================================================================
        // SplashScreen
        //----------------------------------------------------------------------
        JFrame frm = new JFrame();
        frm.setAlwaysOnTop(frm.isAlwaysOnTopSupported());
        JLabel lbl = new JLabel(new ImageIcon(getClass().getResource("/images/2020 - Yggdrasil.png")));
        frm.getContentPane().add(lbl);        
        frm.pack();
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        frm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frm.dispose();
            }            
        });
        //----------------------------------------------------------------------
        
        //======================================================================
        // Wallpaper Panel
        //----------------------------------------------------------------------
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/Konata_Izumi.jpg"));
        int x = (getWidth() - icon.getIconWidth()) / 2;
        int y = (getHeight() - icon.getIconHeight()) / 2 - 40;
        paneForDesktop = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(icon.getImage(), x, y, null);
            }
        };
        //----------------------------------------------------------------------
        
        //======================================================================
        // Desktop
        //----------------------------------------------------------------------
        desktop.setOpaque(false);        
        //----------------------------------------------------------------------
        
        paneForDesktop.setLayout(new BorderLayout());
        paneForDesktop.add(desktop, BorderLayout.CENTER);
        paneGeneral.setLayout(new BorderLayout());
        paneGeneral.add(paneForDesktop, BorderLayout.CENTER);
        
        studio = new AVStudio(dark);
        desktop.add(studio.getFrmVideo());        
        desktop.add(studio.getFrmWave());
        desktop.add(studio.getFrmTable());
        desktop.add(studio.getFrmSC());
        
        changeStudioPosition();
        displayStudio();
    }
    
    public static Language getLanguage(){
        return chosen;
    }
    
    public static ISO_3166 getISOCountry(){
        return chosen.isForced() ? chosen.getIso() : wantedIso;
    }
    
    public static YGGY getYGGY(){
        return yggy;
    }
    
    public static Image makeColorTransparent(Image im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if (( rgb | 0xFF000000 ) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                }else{
                    // nothing to do
                    return rgb;
                }
            }
        }; 

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    
    public static KaraokeLanguage getKaraokeLanguage(){
        return karaokeLanguage;
    }
    
    private void changeStudioPosition(){
        int availableWidth = getWidth();
        int availableHeight = getHeight();
        
        // Video
        studio.getFrmVideo().setSize(availableWidth * 1/2 - 4, availableHeight * 4/7 - 4);
        studio.getFrmVideo().setLocation(2, 2);
        
        // Audio
        studio.getFrmWave().setSize(availableWidth * 1/2 - 20, availableHeight * 2/7 - 4 - 40);
        studio.getFrmWave().setLocation(availableWidth * 1/2, 2);
        
        // Commands
        studio.getFrmSC().setSize(availableWidth * 1/2 - 20, availableHeight * 2/7 - 4 + 40);
        studio.getFrmSC().setLocation(availableWidth * 1/2, availableHeight * 2/7 + 2 - 40);
        
        // Table
        studio.getFrmTable().setSize(availableWidth - 20, availableHeight * 3/7 - 60);
        studio.getFrmTable().setLocation(2, availableHeight * 4/7);
    }
    
    private void displayStudio(){
        if(viewStudio == false){
            studio.getFrmSC().setVisible(true);
            studio.getFrmTable().setVisible(true);
            studio.getFrmVideo().setVisible(true);
            studio.getFrmWave().setVisible(true);
            viewStudio = true;
        }else{
            studio.getFrmSC().setVisible(false);
            studio.getFrmTable().setVisible(false);
            studio.getFrmVideo().setVisible(false);
            studio.getFrmWave().setVisible(false);
            viewStudio = false;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcAV = new javax.swing.JFileChooser();
        paneGeneral = new javax.swing.JPanel();
        mnuGeneral = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuFileOpen = new javax.swing.JMenuItem();
        mnuFileSave = new javax.swing.JMenuItem();
        mnuFileSep02 = new javax.swing.JPopupMenu.Separator();
        mnuFileScripts = new javax.swing.JMenu();
        mnuFilePlugins = new javax.swing.JMenu();
        mnuFileSep01 = new javax.swing.JPopupMenu.Separator();
        mnuFileExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        mnuMenuVideo = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        mnuRBMenuAVStudioRoumaji = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        paneGeneral.setBackground(new java.awt.Color(255, 255, 204));

        javax.swing.GroupLayout paneGeneralLayout = new javax.swing.GroupLayout(paneGeneral);
        paneGeneral.setLayout(paneGeneralLayout);
        paneGeneralLayout.setHorizontalGroup(
            paneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1105, Short.MAX_VALUE)
        );
        paneGeneralLayout.setVerticalGroup(
            paneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 566, Short.MAX_VALUE)
        );

        jMenu1.setText("File");

        mnuFileOpen.setText("Open...");
        mnuFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileOpenActionPerformed(evt);
            }
        });
        jMenu1.add(mnuFileOpen);

        mnuFileSave.setText("Save...");
        mnuFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileSaveActionPerformed(evt);
            }
        });
        jMenu1.add(mnuFileSave);
        jMenu1.add(mnuFileSep02);

        mnuFileScripts.setText("Scripts");
        jMenu1.add(mnuFileScripts);

        mnuFilePlugins.setText("Plugins");
        jMenu1.add(mnuFilePlugins);
        jMenu1.add(mnuFileSep01);

        mnuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mnuFileExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/20px-Crystal_Clear_action_exit.png"))); // NOI18N
        mnuFileExit.setText("Quit");
        mnuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuFileExit);

        mnuGeneral.add(jMenu1);

        jMenu2.setText("Edit");

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/20 time_studio.png"))); // NOI18N
        jMenu3.setText("AVStudio");

        mnuMenuVideo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
        mnuMenuVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/20 time_studio.png"))); // NOI18N
        mnuMenuVideo.setText("See/Hide the studio... ");
        mnuMenuVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMenuVideoActionPerformed(evt);
            }
        });
        jMenu3.add(mnuMenuVideo);

        jMenu4.setText("Language of subtitles");

        mnuRBMenuAVStudioRoumaji.setSelected(true);
        mnuRBMenuAVStudioRoumaji.setText("Rômaji");
        mnuRBMenuAVStudioRoumaji.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRBMenuAVStudioRoumajiActionPerformed(evt);
            }
        });
        jMenu4.add(mnuRBMenuAVStudioRoumaji);

        jMenu3.add(jMenu4);

        jMenu2.add(jMenu3);

        mnuGeneral.add(jMenu2);

        setJMenuBar(mnuGeneral);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneGeneral, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuMenuVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMenuVideoActionPerformed
        displayStudio();
    }//GEN-LAST:event_mnuMenuVideoActionPerformed

    private void mnuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuFileExitActionPerformed

    private void mnuFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileOpenActionPerformed
        if(studio != null &&
                studio.getFrmVideo().isVisible() |
                studio.getFrmWave().isVisible() |
                studio.getFrmTable().isVisible() |
                studio.getFrmSC().isVisible()){
            if(studio.getFrmVideo().isSelected()){
                for(FileFilter ff : fcAV.getChoosableFileFilters()){
                    fcAV.removeChoosableFileFilter(ff);
                }
                FileFilter ffav = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if(f.isDirectory()) return true;
                        for(String str : videoFiles){
                            if(f.getName().toLowerCase().endsWith(str)){
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Video files";
                    }
                };
                fcAV.addChoosableFileFilter(ffav);
                fcAV.setFileFilter(ffav);
                int z = fcAV.showOpenDialog(this);
                if(z == JFileChooser.APPROVE_OPTION){
                    studio.getFrmVideo().openAudioVideo(fcAV.getSelectedFile());
                    studio.getFrmWave().setFile(fcAV.getSelectedFile());
                }
            }
            if(studio.getFrmTable().isSelected()){
                for(FileFilter ff : fcAV.getChoosableFileFilters()){
                    fcAV.removeChoosableFileFilter(ff);
                }
                FileFilter ffass = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if(f.isDirectory()) return true;
                        return f.getName().endsWith(".ass");
                    }

                    @Override
                    public String getDescription() {
                        return "ASS files";
                    }
                };
                fcAV.addChoosableFileFilter(ffass);
                fcAV.setFileFilter(ffass);
                int z = fcAV.showOpenDialog(this);
                if(z == JFileChooser.APPROVE_OPTION){
                    studio.getFrmTable().loadASSTable(fcAV.getSelectedFile());
                    studio.getFrmVideo().openASS(fcAV.getSelectedFile());
                }
            }
        }
    }//GEN-LAST:event_mnuFileOpenActionPerformed

    private void mnuFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileSaveActionPerformed
        if(studio != null &&
                studio.getFrmVideo().isVisible() |
                studio.getFrmWave().isVisible() |
                studio.getFrmTable().isVisible() |
                studio.getFrmSC().isVisible()){
            if(studio.getFrmTable().isSelected()){
                for(FileFilter ff : fcAV.getChoosableFileFilters()){
                    fcAV.removeChoosableFileFilter(ff);
                }
                FileFilter ffass = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if(f.isDirectory()) return true;
                        return f.getName().endsWith(".ass");
                    }

                    @Override
                    public String getDescription() {
                        return "ASS files";
                    }
                };
                fcAV.addChoosableFileFilter(ffass);
                fcAV.setFileFilter(ffass);
                int z = fcAV.showSaveDialog(this);
                if(z == JFileChooser.APPROVE_OPTION){
                    File file = fcAV.getSelectedFile();
                    if(file.getName().endsWith(".ass") == false){
                        file = new File(file.getParentFile(), file.getName()+".ass");
                    }
                    studio.getFrmTable().saveASSTable(file);
                }
            }
        }
    }//GEN-LAST:event_mnuFileSaveActionPerformed

    private void mnuRBMenuAVStudioRoumajiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRBMenuAVStudioRoumajiActionPerformed
        karaokeLanguage = KaraokeLanguage.Romaji;
    }//GEN-LAST:event_mnuRBMenuAVStudioRoumajiActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame(false).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser fcAV;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem mnuFileExit;
    private javax.swing.JMenuItem mnuFileOpen;
    private javax.swing.JMenu mnuFilePlugins;
    private javax.swing.JMenuItem mnuFileSave;
    private javax.swing.JMenu mnuFileScripts;
    private javax.swing.JPopupMenu.Separator mnuFileSep01;
    private javax.swing.JPopupMenu.Separator mnuFileSep02;
    private javax.swing.JMenuBar mnuGeneral;
    private javax.swing.JMenuItem mnuMenuVideo;
    private javax.swing.JRadioButtonMenuItem mnuRBMenuAVStudioRoumaji;
    private javax.swing.JPanel paneGeneral;
    // End of variables declaration//GEN-END:variables
}
