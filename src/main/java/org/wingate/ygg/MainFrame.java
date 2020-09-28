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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import org.wingate.timelibrary.Time;
import org.wingate.videocmp.ImageEvent;
import org.wingate.videocmp.ImageListener;
import org.wingate.videocmp.Player;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;
import org.wingate.ygg.ass.Style;
import org.wingate.ygg.karaoke.AssKaraokeCollection;
import org.wingate.ygg.languages.ISO_3166;
import org.wingate.ygg.languages.Language;
import org.wingate.ygg.network.UserChatUID;
import org.wingate.ygg.util.AssEventTableModel;
import org.wingate.ygg.util.AssEventTableRenderer;
import org.wingate.ygg.util.audio.AudioWave;
import org.wingate.ygg.util.FFStuffs;
import org.wingate.ygg.util.FramesPanel;
import org.wingate.ygg.util.audio.AudioWavePanel;
import org.wingate.ygg.util.dialog.PropsDialog;
import org.wingate.ygg.util.dialog.StylesDialog;
import org.wingate.ygg.util.subtitle.YGGY;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {

    // Language (loading from properties of each component)
    static ISO_3166 wantedIso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    static Language chosen = new Language();
    
    private static boolean darkUI = false;
    
    private boolean fcVideoReady = false;
    private boolean fcAudioReady = false;
    private boolean fcAssReady = false;
    
    private Player player = new Player();
    private File video = null;
    private ASS ass = ASS.NoFileToLoad();
    private final YGGY yggy = YGGY.create();
    private Event currentEvent = null;
    
    // ifrVideo components and variables
    private final JLabel lblOnTopOfOverlay = new JLabel();
    private FramesPanel fp;
    private FFStuffs ffss = null;
    // ifrVideo stop
    
    // ifrWave components and variables
    private AudioWave aw = null;
    // ifrWave stop
    
    // ifrTableOne components and variables
    private AssEventTableModel dtmASS;    
    private AssEventTableRenderer assEventTableRenderer;
    // ifrTableOne stop
    
    // ifrAssSubCommands components and variables
    private final SpinnerNumberModel snmLayer = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmLeft = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmRight = new SpinnerNumberModel(0, 0, 100000, 1);
    private final SpinnerNumberModel snmVertical = new SpinnerNumberModel(0, 0, 100000, 1);
    private final DefaultComboBoxModel dcbmStyle = new DefaultComboBoxModel();
    private final DefaultComboBoxModel dcbmName = new DefaultComboBoxModel();    
    private float tpTextFontSize;
    private Font tpTextNormalFont;
    private final float tpTextScaleMin = 1f;
    private final float tpTextScaleMax = 5f;
    private float tpTextScaleCur = 1f;
    // ifrAssSubCommands stop
    
    private final UserChatUID myselfUID = new UserChatUID();
    private List<UserChatUID> userChatUIDList = new ArrayList<>();
    
    /**
     * Creates new form MainFrame
     * @param darkUI
     */
    public MainFrame(boolean darkUI) {
        initComponents();
        MainFrame.darkUI = darkUI;
        init();
        resizeComponents();
    }
    
    private void init(){
        setSize(1880, 1058);
        setLocationRelativeTo(null);
        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(chosen.isForced() == true){
            wantedIso = chosen.getIso();
        }
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
            
        });
        
        // Peers
        userChatUIDList = UserChatUID.load();
        
        // Video Internal Frame
        fp = new FramesPanel(darkUI);
        paneTimeline.add(fp, BorderLayout.CENTER);
        
        paneVideo.add(lblOnTopOfOverlay, BorderLayout.CENTER);
        lblOnTopOfOverlay.setHorizontalAlignment(SwingConstants.CENTER);
        lblOnTopOfOverlay.setFont(lblOnTopOfOverlay.getFont().deriveFont(50f));
        
        ifrVideo.setSize(972, 662);
        ifrVideo.setLocation(5, 5);
        ifrVideo.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ifrVideo.setTitle("Java VCMP - " + paneVideo.getWidth() + "x" + paneVideo.getHeight());                
            }            
        });
        ifrVideo.setTitle("Java VCMP");
        deskYGGY.add(ifrVideo);
        player.addImageListener(new ImageListener() {
            @Override
            public void imageChanged(ImageEvent event) {
                Dimension imgSize = new Dimension(
                        event.getImage().getWidth(),
                        event.getImage().getHeight()
                );
                Dimension d = getScaledDimension(imgSize, lblOnTopOfOverlay.getSize());
                BufferedImage bufimg = new BufferedImage(
                        lblOnTopOfOverlay.getWidth(),
                        lblOnTopOfOverlay.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D gr = bufimg.createGraphics();
                gr.setColor(isDark() ? Color.black : Color.white);
                gr.fillRect(0, 0, lblOnTopOfOverlay.getWidth(), lblOnTopOfOverlay.getHeight());
                gr.drawImage(
                        event.getImage(), 
                        (lblOnTopOfOverlay.getWidth() - d.width) / 2, 
                        (lblOnTopOfOverlay.getHeight()- d.height) / 2, 
                        d.width, 
                        d.height, 
                        null);
                gr.dispose();
                lblOnTopOfOverlay.setIcon(new ImageIcon(bufimg));
                
                
            }
        });

        // Audio WAVE
        ifrWave.setSize(880, 250);
        ifrWave.setLocation(977, 5);
        deskYGGY.add(ifrWave);
        
        // ASS Style Commands
        ifrAssTableCommands.setSize(880, 215);
        ifrAssTableCommands.setLocation(977, 255);
        deskYGGY.add(ifrAssTableCommands);
        
        // ASS Table (One)
        ifrtableOne.setSize(880, 280);
        ifrtableOne.setLocation(977, 470);
        deskYGGY.add(ifrtableOne);
        
        // ASS Style Commands
        spinLayer.setModel(snmLayer);
        spinL.setModel(snmLeft);
        spinR.setModel(snmRight);
        spinV.setModel(snmVertical);
        comboStyle.setModel(dcbmStyle);
        comboName.setModel(dcbmName);
        tpTextFontSize = tpText.getFont().getSize2D();
        tpTextNormalFont = tpText.getFont();
        tpText.addMouseWheelListener((MouseWheelEvent e) -> {
            if(e.isControlDown()){
                if(e.getPreciseWheelRotation() < 0){
                    // On augmente la taille
                    tpTextScaleCur += 0.2f;
                    tpTextScaleCur = Math.min(tpTextScaleMax, tpTextScaleCur);
                    float fontSize = tpTextScaleCur * tpTextFontSize;
                    tpText.setFont(tpTextNormalFont.deriveFont(fontSize));
                }
                if(e.getPreciseWheelRotation() > 0){
                    // On réduit la taille
                    tpTextScaleCur -= 0.2f;
                    tpTextScaleCur = Math.max(tpTextScaleMin, tpTextScaleCur);
                    float fontSize = tpTextScaleCur * tpTextFontSize;
                    tpText.setFont(tpTextNormalFont.deriveFont(fontSize));
                }
            }
        });
        initAssComboStyle();
        initAssComboName();
        
        // Audio WAVE
        paneEmbedWave.setBackground(AudioWavePanel.getBackgroundColor(darkUI));
        
        // ASS Table (One)
        assEventTableRenderer = new AssEventTableRenderer(darkUI);
        initializeTableOne(chosen, wantedIso);
        
    }
    
    // <editor-fold defaultstate="collapsed" desc="All">
    
    public void setDarkUI(boolean darkUI) {
        MainFrame.darkUI = darkUI;
    }
    
    public static boolean isDark(){
        return darkUI;
    }
    
    private void resizeComponents(){
        splitMain.setDividerLocation(getHeight() * 3 / 4);
    }
    
    public static Language getLanguage(){
        return chosen;
    }
    
    public static ISO_3166 getISO(){
        return wantedIso;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ASS Subtitles">
    
    public void openASS(File assFile){
        ass = assFile != null ? ASS.Read(assFile.getPath()) : null;
    }
    
    private Image getAssShape(ASS ass, Time current){
        // On définit le fichier PNG cible
        String pngPath = new File(video.getParentFile(), "temp.png").getPath();
        
        // On définit le fichier ASS source
        String assPath = ass.getAssFile().getPath();
        
        // On définit le temps actuel en secondes flottantes
        String time = Double.toString(Time.getLengthInSeconds(current));
        
        // On définit la taille de la vidéo
        int width = Integer.parseInt(ass.getResX());
        int height = Integer.parseInt(ass.getResY());
        
        // On lance YGGY (libass)
        int result = yggy.getYggy().executor(pngPath, assPath, time, width, height);
        
        // Si on a pas de sous-titres à cet endroit de la vidéo
        if(result == 1){
            return null;
        }
        
        // On crée une image avec alpha
        Image img = makeColorTransparent(new ImageIcon(pngPath).getImage(), new Color(63, 63, 63));
        
        // On retourne une image avec alpha
        return img;
    }
    
    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    public Image makeColorTransparent(Image im, final Color color) {
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
    
    public void loadASSTable(File f){        
        ASS loading = ASS.Read(f.getPath());
        tableOne.removeAll();
        dtmASS.insertAll(loading.getEvents());
        tableOne.updateUI();
        initAssComboStyle();
        initAssComboName();
    }
    
    public void saveASSTable(File f){
        ASS saving = new ASS();
        List<Event> events = dtmASS.getAllEvents();
        saving.setEvents(events);
        ASS.Save(f.getPath(), saving);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Audio and video">
    
    private void openAudioVideo(File video){
        openVideo(video);
        openAudio(video);
        player.setFile(video);
    }
    
    private void playAndStop(Time startTime, Time endTime){
        if(currentEvent != null){
            player.setStartAt(startTime);
            player.setStopAt(endTime);
            player.play();
        }
    }
    
    public void displayEventTime(Event ev){
        int startFrame = Time.getFrame(ev.getStartTime(), ffss.getFps());
        int endFrame = Time.getFrame(ev.getEndTime(), ffss.getFps());
        
        int startSamples = aw.getSamplesFromFrame(startFrame);
        int endSamples = aw.getSamplesFromFrame(endFrame);
        
        Point start = new Point(Math.round(startSamples / aw.getSamplesPerPixel()), 0);
        Point stop = new Point(Math.round(endSamples / aw.getSamplesPerPixel()), 0);
        
        aw.getAudioWavePanel().updatePoint(start, stop);
        
        try{
            int index = aw.getAudioWavePanel().getAssKaraokeCollectionIndex(ev);
            if(index != -1){
                AssKaraokeCollection akc = aw.getAudioWavePanel().getAssKaraokeCollections().get(index);
            }
        }catch(Exception exc){
            // Nothing to do but not mention it!
        }
                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Audio only">
    
    private void openAudio(File audio){
        aw = AudioWave.create(audio, ffss, darkUI);
        
        paneEmbedWave.setLayout(new BorderLayout());
        paneEmbedWave.add(aw, BorderLayout.CENTER);
        aw.setWaveHeight(paneEmbedWave.getHeight());
        aw.updateDisplayWithOffset(0, Time.create(0L), Time.create(0L));
        
        ifrWave.updateUI();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Video only">
    
    private void openVideo(File video){
        this.video = video;
        
        ffss = FFStuffs.create(video);
        
        fp.configure(ffss);
        
        Time current = Time.create(0L);
        Time start = Time.create(0L);
        Time end = ffss.getDuration();
        Time dur = ffss.getDuration();
        
        tfVideoStartTime.setText(start.toProgramExtendedTime());
        tfVideoStartFrame.setText(Integer.toString(Time.getFrame(start, ffss.getFps())));
        tfVideoEndTime.setText(end.toProgramExtendedTime());
        tfVideoEndFrame.setText(Integer.toString(Time.getFrame(end, ffss.getFps())));
        tfVideoDurationTime.setText(dur.toProgramExtendedTime());
        tfVideoDurationFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
        tfVideoCurrentTime.setText(current.toProgramExtendedTime());
        tfVideoCurrentFrame.setText(Integer.toString(Time.getFrame(current, ffss.getFps())));

        fp.updatePosition(current);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="ASS Cmd Panel">
    
    private void changeTime(Time start, Time end){
        if(currentEvent != null){
            currentEvent.setStartTime(start);
            currentEvent.setEndTime(end);
        }            
            
        tfStartTime.setText(start.toProgramExtendedTime());
        tfEndTime.setText(end.toProgramExtendedTime());
        Time duration = Time.substract(start, end);
        tfDurationTime.setText(duration.toProgramExtendedTime());
        if(ffss != null){
            tfStartFrame.setText(Integer.toString(Time.getFrame(start, ffss.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, ffss.getFps())));
            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, ffss.getFps())));
        }
    }
    
    public void alter(Event ev){
        currentEvent = ev;
        
        // Type de ligne
        switch(ev.getLineType()){
            case Dialogue -> toggleCmdDialogue.setSelected(true);
            case Comment -> toggleCmdComment.setSelected(true);
            case Proposal -> toggleCmdProposal.setSelected(true);
            case Request -> toggleCmdRequest.setSelected(true);
            case Sound -> toggleCmdSound.setSelected(true);
            case Movie -> toggleCmdMovie.setSelected(true);
            case Picture -> toggleCmdPicture.setSelected(true);
            case Commands -> toggleCmdCommands.setSelected(true);         }
        
        // Couche
        snmLayer.setValue(ev.getLayer());
        
        // Start - End - Duration
        Time start, end, duration;
        start = ev.getStartTime();
        end = ev.getEndTime();
        duration = Time.substract(start, end);
        tfStartTime.setText(start.toProgramExtendedTime());
        tfEndTime.setText(end.toProgramExtendedTime());
        tfDurationTime.setText(duration.toProgramExtendedTime());
        try{
            tfStartFrame.setText(Integer.toString(Time.getFrame(start, ffss.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(end, ffss.getFps())));
            tfDurationFrame.setText(Integer.toString(Time.getFrame(duration, ffss.getFps())));
        }catch(Exception ex){
            
        }        
        
        // ML
        snmLeft.setValue(ev.getMarginL());
        
        // MR
        snmRight.setValue(ev.getMarginR());
        
        // MV
        snmVertical.setValue(ev.getMarginV());
        
        // Style
        comboStyle.setSelectedItem(ev.getStyle());
        
        // Name
        comboName.setSelectedItem(ev.getName());
        
        // Effect
        tfSFX.setText(ev.getEffect());
        
        // Text
        tpText.setText(ev.getText());
    }
    
    public void updateAreaFrames(Event ev){
        currentEvent = ev;
        
        fp.updateArea(ev.getStartTime(), ev.getEndTime());
        
        Time dur = Time.substract(ev.getStartTime(), ev.getEndTime());
        
        tfStartTime.setText(ev.getStartTime().toProgramExtendedTime());
        tfStartFrame.setText(Integer.toString(Time.getFrame(ev.getStartTime(), ffss.getFps())));
        tfEndTime.setText(ev.getEndTime().toProgramExtendedTime());
        tfEndFrame.setText(Integer.toString(Time.getFrame(ev.getEndTime(), ffss.getFps())));
        tfDurationTime.setText(dur.toProgramExtendedTime());
        tfDurationFrame.setText(Integer.toString(Time.getFrame(dur, ffss.getFps())));
    }
    
    private void initAssComboStyle(){
        dcbmStyle.removeAllElements();
        ass.getStyles().entrySet().forEach((entry) -> {
            dcbmStyle.addElement(entry.getValue());
        });
    }
    
    private void initAssComboName(){
        dcbmName.removeAllElements();
        dcbmName.addElement("");
        ass.getNames().forEach((name) -> {
            dcbmName.addElement(name);
        });
    }
    
    private Event getFromAssSubCommands(){
        Event nv = new Event();
        
        // Type
        if(toggleCmdDialogue.isSelected()) nv.setLineType(Event.LineType.Dialogue);
        if(toggleCmdComment.isSelected()) nv.setLineType(Event.LineType.Comment);
        if(toggleCmdProposal.isSelected()) nv.setLineType(Event.LineType.Proposal);
        if(toggleCmdRequest.isSelected()) nv.setLineType(Event.LineType.Request);
        if(toggleCmdPicture.isSelected()) nv.setLineType(Event.LineType.Picture);
        if(toggleCmdSound.isSelected()) nv.setLineType(Event.LineType.Sound);
        if(toggleCmdMovie.isSelected()) nv.setLineType(Event.LineType.Movie);
        if(toggleCmdCommands.isSelected()) nv.setLineType(Event.LineType.Commands);
        
        // Layer
        nv.setLayer(snmLayer.getNumber().intValue());
        
        // Start time
        String strStart = tfStartTime.getText();
        Time tStart = new Time();
        Pattern ps = Pattern.compile("(\\d+).{1}(\\d+).{1}(\\d+).{1}(\\d+)");
        Matcher ms = ps.matcher(strStart);
        if(ms.find()){
            tStart.setHours(Integer.parseInt(ms.group(1)));
            tStart.setMinutes(Integer.parseInt(ms.group(2)));
            tStart.setSeconds(Integer.parseInt(ms.group(3)));
            tStart.setMilliseconds(Integer.parseInt(ms.group(4)));
        }
        nv.setStartTime(tStart);
        
        // End time
        String strEnd = tfEndTime.getText();
        Time tEnd = new Time();
        Pattern pe = Pattern.compile("(\\d+).{1}(\\d+).{1}(\\d+).{1}(\\d+)");
        Matcher me = pe.matcher(strEnd);
        if(me.find()){
            tEnd.setHours(Integer.parseInt(me.group(1)));
            tEnd.setMinutes(Integer.parseInt(me.group(2)));
            tEnd.setSeconds(Integer.parseInt(me.group(3)));
            tEnd.setMilliseconds(Integer.parseInt(me.group(4)));
        }
        nv.setEndTime(tEnd);
        
        // Margin L
        nv.setMarginL(snmLeft.getNumber().intValue());
        
        // Margin R
        nv.setMarginR(snmRight.getNumber().intValue());
        
        // Margin V
        nv.setMarginV(snmVertical.getNumber().intValue());
        
        // Style        
        nv.setStyle(dcbmStyle.getSize() == 0 ? new Style() : (Style)dcbmStyle.getSelectedItem());
        
        // Name
        nv.setName(dcbmName.getSize() == 0 ? "" : (String)dcbmName.getSelectedItem());
        
        // Effect
        nv.setEffect(tfSFX.getText());
        
        // Text
        nv.setText(tpText.getText());
        
        return nv;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="TableOne">
    
    public void initializeTableOne(Language in, ISO_3166 get){        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(in.isForced() == true){
            get = in.getIso();
        }
        
        // Fill in the table
        dtmASS = new AssEventTableModel(in, get);
        
        tableOne.setModel(dtmASS);
        
        TableColumn column;
        for (int i = 0; i < 14; i++) {
            column = tableOne.getColumnModel().getColumn(i);
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
        
        tableOne.setDefaultRenderer(String.class, assEventTableRenderer);
        tableOne.setDefaultRenderer(Event.LineType.class, assEventTableRenderer);
        tableOne.setDefaultRenderer(Time.class, assEventTableRenderer);
        tableOne.setDefaultRenderer(Style.class, assEventTableRenderer);
        tableOne.setDefaultRenderer(Integer.class, assEventTableRenderer);
        
        tableOne.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    Event ev = dtmASS.getEventAt(tableOne.getSelectedRow());
                    alter(ev);
                    try{
                        displayEventTime(ev);
                        updateAreaFrames(ev);
                    }catch(Exception ex){}
                }
            }
        });
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

        fcASS = new javax.swing.JFileChooser();
        fcAudio = new javax.swing.JFileChooser();
        fcVideo = new javax.swing.JFileChooser();
        ifrVideo = new javax.swing.JInternalFrame();
        paneVideo = new javax.swing.JPanel();
        paneControls = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnVideoPlay = new javax.swing.JButton();
        btnVideoPause = new javax.swing.JButton();
        btnVideoStop = new javax.swing.JButton();
        btnVideoPlayBefore = new javax.swing.JButton();
        btnVideoPlayBegin = new javax.swing.JButton();
        btnVideoPlayArea = new javax.swing.JButton();
        btnVideoPlayEnd = new javax.swing.JButton();
        btnVideoPlayAfter = new javax.swing.JButton();
        tfVideoStartTime = new javax.swing.JTextField();
        tfVideoEndTime = new javax.swing.JTextField();
        tfVideoDurationTime = new javax.swing.JTextField();
        tfVideoCurrentTime = new javax.swing.JTextField();
        tfVideoStartFrame = new javax.swing.JTextField();
        tfVideoEndFrame = new javax.swing.JTextField();
        tfVideoDurationFrame = new javax.swing.JTextField();
        tfVideoCurrentFrame = new javax.swing.JTextField();
        paneTimeline = new javax.swing.JPanel();
        ifrWave = new javax.swing.JInternalFrame();
        paneEmbedWave = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnAudioPlay = new javax.swing.JButton();
        btnAudioPause = new javax.swing.JButton();
        btnAudioStop = new javax.swing.JButton();
        btnAudioPlayBefore = new javax.swing.JButton();
        btnAudioPlayBegin = new javax.swing.JButton();
        btnAudioPlayArea = new javax.swing.JButton();
        btnAudioPlayEnd = new javax.swing.JButton();
        btnAudioPlayAfter = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAudioAccept = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        toggleAssKaraoke = new javax.swing.JToggleButton();
        toggleYvesKaraoke = new javax.swing.JToggleButton();
        ifrtableOne = new javax.swing.JInternalFrame();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOne = new javax.swing.JTable();
        ifrAssTableCommands = new javax.swing.JInternalFrame();
        tabbedSubsCmd = new javax.swing.JTabbedPane();
        tabAssSubsCmd = new javax.swing.JPanel();
        toggleCmdDialogue = new javax.swing.JToggleButton();
        toggleCmdComment = new javax.swing.JToggleButton();
        toggleCmdProposal = new javax.swing.JToggleButton();
        toggleCmdRequest = new javax.swing.JToggleButton();
        toggleCmdPicture = new javax.swing.JToggleButton();
        toggleCmdSound = new javax.swing.JToggleButton();
        toggleCmdMovie = new javax.swing.JToggleButton();
        toggleCmdCommands = new javax.swing.JToggleButton();
        comboStyle = new javax.swing.JComboBox<>();
        spinLayer = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        tfStartTime = new javax.swing.JTextField();
        tfStartFrame = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tfEndTime = new javax.swing.JTextField();
        tfEndFrame = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        tfDurationTime = new javax.swing.JTextField();
        tfDurationFrame = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        spinL = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        spinV = new javax.swing.JSpinner();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        spinR = new javax.swing.JSpinner();
        tabAssSfxMarg = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        comboName = new javax.swing.JComboBox<>();
        tfSFX = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        btnCmdStyleAdd = new javax.swing.JButton();
        btnCmdStyleChange = new javax.swing.JButton();
        btnCmdStyleAfter = new javax.swing.JButton();
        btnCmdStyleBefore = new javax.swing.JButton();
        btnCmdStyleParam = new javax.swing.JButton();
        btnCmdStyleProperties = new javax.swing.JButton();
        bgAssType = new javax.swing.ButtonGroup();
        splitMain = new javax.swing.JSplitPane();
        tabbedOptions = new javax.swing.JTabbedPane();
        paneChat = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnSend = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tpChatEntry = new javax.swing.JTextPane();
        btnSmiley = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tpChatChannel = new javax.swing.JTextPane();
        panePTP = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tabbedMainFunctions = new javax.swing.JTabbedPane();
        tabStudio = new javax.swing.JPanel();
        deskYGGY = new javax.swing.JDesktopPane();
        tabTrans = new javax.swing.JPanel();
        tabEditing = new javax.swing.JPanel();
        tabDrawing = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        menuBarYGGY = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuFileYGGY = new javax.swing.JMenu();
        mnuFileYGGYOpenASS = new javax.swing.JMenuItem();
        mnuFileYGGYSaveASSAs = new javax.swing.JMenuItem();
        mnuFileYGGYSaveASSAgain = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuFileYGGYVideoOpen = new javax.swing.JMenuItem();
        mnuFileYGGYAudioOpen = new javax.swing.JMenuItem();
        mnuFileYGGYAVOpen = new javax.swing.JMenuItem();
        mnuFileChat = new javax.swing.JMenu();
        mnuFileChatAddUserToDB = new javax.swing.JMenuItem();
        mnuFileChatGetUID = new javax.swing.JMenuItem();

        ifrVideo.setMaximizable(true);
        ifrVideo.setResizable(true);
        ifrVideo.setTitle("Video-FX :: VLCj");
        ifrVideo.setVisible(true);

        paneVideo.setBackground(new java.awt.Color(0, 153, 255));
        paneVideo.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnVideoPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play.png"))); // NOI18N
        btnVideoPlay.setText("Play");
        btnVideoPlay.setFocusable(false);
        btnVideoPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlay);

        btnVideoPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs pause.png"))); // NOI18N
        btnVideoPause.setText("Pause");
        btnVideoPause.setFocusable(false);
        btnVideoPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPauseActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPause);

        btnVideoStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs stop.png"))); // NOI18N
        btnVideoStop.setText("Stop");
        btnVideoStop.setFocusable(false);
        btnVideoStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoStopActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoStop);

        btnVideoPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnVideoPlayBefore.setText("Before");
        btnVideoPlayBefore.setFocusable(false);
        btnVideoPlayBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlayBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlayBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayBeforeActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlayBefore);

        btnVideoPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 01.png"))); // NOI18N
        btnVideoPlayBegin.setText("Begin");
        btnVideoPlayBegin.setFocusable(false);
        btnVideoPlayBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlayBegin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlayBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayBeginActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlayBegin);

        btnVideoPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnVideoPlayArea.setText("Area");
        btnVideoPlayArea.setFocusable(false);
        btnVideoPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayAreaActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlayArea);

        btnVideoPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 02.png"))); // NOI18N
        btnVideoPlayEnd.setText("End");
        btnVideoPlayEnd.setFocusable(false);
        btnVideoPlayEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlayEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlayEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayEndActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlayEnd);

        btnVideoPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnVideoPlayAfter.setText("After");
        btnVideoPlayAfter.setFocusable(false);
        btnVideoPlayAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVideoPlayAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVideoPlayAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVideoPlayAfterActionPerformed(evt);
            }
        });
        jToolBar2.add(btnVideoPlayAfter);

        tfVideoStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoStartTime.setText("0.00.00.000");

        tfVideoEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoEndTime.setText("0.00.00.000");

        tfVideoDurationTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoDurationTime.setText("0.00.00.000");

        tfVideoCurrentTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoCurrentTime.setText("0.00.00.000");

        tfVideoStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoStartFrame.setText("0");

        tfVideoEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoEndFrame.setText("0");

        tfVideoDurationFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoDurationFrame.setText("0");

        tfVideoCurrentFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfVideoCurrentFrame.setText("0");

        javax.swing.GroupLayout paneControlsLayout = new javax.swing.GroupLayout(paneControls);
        paneControls.setLayout(paneControlsLayout);
        paneControlsLayout.setHorizontalGroup(
            paneControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneControlsLayout.createSequentialGroup()
                        .addComponent(tfVideoStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoCurrentTime, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paneControlsLayout.createSequentialGroup()
                        .addComponent(tfVideoStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfVideoCurrentFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        paneControlsLayout.setVerticalGroup(
            paneControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfVideoStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoCurrentTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(paneControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfVideoStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVideoCurrentFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneControlsLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        paneTimeline.setBackground(new java.awt.Color(204, 204, 204));
        paneTimeline.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout ifrVideoLayout = new javax.swing.GroupLayout(ifrVideo.getContentPane());
        ifrVideo.getContentPane().setLayout(ifrVideoLayout);
        ifrVideoLayout.setHorizontalGroup(
            ifrVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneVideo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(paneControls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(paneTimeline, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ifrVideoLayout.setVerticalGroup(
            ifrVideoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ifrVideoLayout.createSequentialGroup()
                .addComponent(paneVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneTimeline, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ifrWave.setMaximizable(true);
        ifrWave.setResizable(true);
        ifrWave.setTitle("Waveform");
        ifrWave.setVisible(true);

        paneEmbedWave.setBackground(new java.awt.Color(0, 153, 204));

        javax.swing.GroupLayout paneEmbedWaveLayout = new javax.swing.GroupLayout(paneEmbedWave);
        paneEmbedWave.setLayout(paneEmbedWaveLayout);
        paneEmbedWaveLayout.setHorizontalGroup(
            paneEmbedWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        paneEmbedWaveLayout.setVerticalGroup(
            paneEmbedWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 326, Short.MAX_VALUE)
        );

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnAudioPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play.png"))); // NOI18N
        btnAudioPlay.setText("Play");
        btnAudioPlay.setFocusable(false);
        btnAudioPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlay);

        btnAudioPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs pause.png"))); // NOI18N
        btnAudioPause.setText("Pause");
        btnAudioPause.setFocusable(false);
        btnAudioPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPauseActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPause);

        btnAudioStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs stop.png"))); // NOI18N
        btnAudioStop.setText("Stop");
        btnAudioStop.setFocusable(false);
        btnAudioStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioStopActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioStop);

        btnAudioPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 01.png"))); // NOI18N
        btnAudioPlayBefore.setText("Before");
        btnAudioPlayBefore.setFocusable(false);
        btnAudioPlayBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlayBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlayBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayBeforeActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlayBefore);

        btnAudioPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 01.png"))); // NOI18N
        btnAudioPlayBegin.setText("Begin");
        btnAudioPlayBegin.setFocusable(false);
        btnAudioPlayBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlayBegin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlayBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayBeginActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlayBegin);

        btnAudioPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs in.png"))); // NOI18N
        btnAudioPlayArea.setText("Area");
        btnAudioPlayArea.setFocusable(false);
        btnAudioPlayArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlayArea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlayArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayAreaActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlayArea);

        btnAudioPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play in 02.png"))); // NOI18N
        btnAudioPlayEnd.setText("End");
        btnAudioPlayEnd.setFocusable(false);
        btnAudioPlayEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlayEnd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlayEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayEndActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlayEnd);

        btnAudioPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32_timer_stuffs play out 02.png"))); // NOI18N
        btnAudioPlayAfter.setText("After");
        btnAudioPlayAfter.setFocusable(false);
        btnAudioPlayAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioPlayAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioPlayAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioPlayAfterActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioPlayAfter);
        jToolBar3.add(jSeparator1);

        btnAudioAccept.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/32 accept.png"))); // NOI18N
        btnAudioAccept.setText("Accept");
        btnAudioAccept.setFocusable(false);
        btnAudioAccept.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAudioAccept.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAudioAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudioAcceptActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAudioAccept);
        jToolBar3.add(jSeparator2);

        toggleAssKaraoke.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ass yves karaoke (ass).png"))); // NOI18N
        toggleAssKaraoke.setText("Karaoke");
        toggleAssKaraoke.setFocusable(false);
        toggleAssKaraoke.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleAssKaraoke.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleAssKaraoke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleAssKaraokeActionPerformed(evt);
            }
        });
        jToolBar3.add(toggleAssKaraoke);

        toggleYvesKaraoke.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ass yves karaoke (ves).png"))); // NOI18N
        toggleYvesKaraoke.setText("Karaoke");
        toggleYvesKaraoke.setFocusable(false);
        toggleYvesKaraoke.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleYvesKaraoke.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleYvesKaraoke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleYvesKaraokeActionPerformed(evt);
            }
        });
        jToolBar3.add(toggleYvesKaraoke);

        javax.swing.GroupLayout ifrWaveLayout = new javax.swing.GroupLayout(ifrWave.getContentPane());
        ifrWave.getContentPane().setLayout(ifrWaveLayout);
        ifrWaveLayout.setHorizontalGroup(
            ifrWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneEmbedWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE)
        );
        ifrWaveLayout.setVerticalGroup(
            ifrWaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ifrWaveLayout.createSequentialGroup()
                .addComponent(paneEmbedWave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ifrtableOne.setMaximizable(true);
        ifrtableOne.setResizable(true);
        ifrtableOne.setTitle("ASS YGGY Table");
        ifrtableOne.setVisible(true);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tableOne.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableOne);

        javax.swing.GroupLayout ifrtableOneLayout = new javax.swing.GroupLayout(ifrtableOne.getContentPane());
        ifrtableOne.getContentPane().setLayout(ifrtableOneLayout);
        ifrtableOneLayout.setHorizontalGroup(
            ifrtableOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
        );
        ifrtableOneLayout.setVerticalGroup(
            ifrtableOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
        );

        ifrAssTableCommands.setMaximizable(true);
        ifrAssTableCommands.setResizable(true);
        ifrAssTableCommands.setTitle("ASS Subtitles Commands");
        ifrAssTableCommands.setVisible(true);

        bgAssType.add(toggleCmdDialogue);
        toggleCmdDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub dialogue.png"))); // NOI18N
        toggleCmdDialogue.setSelected(true);
        toggleCmdDialogue.setToolTipText("Dialogue");

        bgAssType.add(toggleCmdComment);
        toggleCmdComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment.png"))); // NOI18N
        toggleCmdComment.setToolTipText("Comment");

        bgAssType.add(toggleCmdProposal);
        toggleCmdProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment yellow.png"))); // NOI18N
        toggleCmdProposal.setToolTipText("Proposal");

        bgAssType.add(toggleCmdRequest);
        toggleCmdRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/40 funsub comment blue.png"))); // NOI18N
        toggleCmdRequest.setToolTipText("Request");

        bgAssType.add(toggleCmdPicture);
        toggleCmdPicture.setText("P");

        bgAssType.add(toggleCmdSound);
        toggleCmdSound.setText("S");

        bgAssType.add(toggleCmdMovie);
        toggleCmdMovie.setText("M");

        bgAssType.add(toggleCmdCommands);
        toggleCmdCommands.setText("C");

        comboStyle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout tabAssSubsCmdLayout = new javax.swing.GroupLayout(tabAssSubsCmd);
        tabAssSubsCmd.setLayout(tabAssSubsCmdLayout);
        tabAssSubsCmdLayout.setHorizontalGroup(
            tabAssSubsCmdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabAssSubsCmdLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toggleCmdDialogue, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdComment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdProposal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdSound, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toggleCmdCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabAssSubsCmdLayout.setVerticalGroup(
            tabAssSubsCmdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabAssSubsCmdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabAssSubsCmdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(toggleCmdDialogue, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabAssSubsCmdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(toggleCmdPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(toggleCmdSound, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(toggleCmdMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(toggleCmdCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(toggleCmdRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toggleCmdProposal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toggleCmdComment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinLayer)
                    .addComponent(comboStyle))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedSubsCmd.addTab("Type, Layer and Style", tabAssSubsCmd);

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));

        tfStartTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartTime.setText("0.00.00.000");

        tfStartFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfStartFrame.setText("0");

        jLabel1.setText("Start");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfStartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 153, 153));

        tfEndTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndTime.setText("0.00.00.000");

        tfEndFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfEndFrame.setText("0");

        jLabel2.setText("Stop");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfEndFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(153, 204, 255));

        tfDurationTime.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurationTime.setText("0.00.00.000");

        tfDurationFrame.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDurationFrame.setText("0");

        jLabel3.setText("Duration");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDurationTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfDurationFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 204, 255));

        jLabel4.setText("L");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(spinL, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 204, 255));

        jLabel5.setText("V");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(spinV, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 204, 255));

        jLabel6.setText("R");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        tabbedSubsCmd.addTab("Sync and Margins", jPanel2);

        jLabel7.setText("Name");

        comboName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel8.setText("Effect");

        javax.swing.GroupLayout tabAssSfxMargLayout = new javax.swing.GroupLayout(tabAssSfxMarg);
        tabAssSfxMarg.setLayout(tabAssSfxMargLayout);
        tabAssSfxMargLayout.setHorizontalGroup(
            tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabAssSfxMargLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboName, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(tfSFX, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabAssSfxMargLayout.setVerticalGroup(
            tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabAssSfxMargLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabAssSfxMargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfSFX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabbedSubsCmd.addTab("Name and Effect", tabAssSfxMarg);

        ifrAssTableCommands.getContentPane().add(tabbedSubsCmd, java.awt.BorderLayout.NORTH);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setViewportView(tpText);

        ifrAssTableCommands.getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        btnCmdStyleAdd.setForeground(new java.awt.Color(51, 255, 51));
        btnCmdStyleAdd.setText("ADD AT END");
        btnCmdStyleAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStyleAddActionPerformed(evt);
            }
        });

        btnCmdStyleChange.setForeground(new java.awt.Color(255, 0, 0));
        btnCmdStyleChange.setText("CHANGE");
        btnCmdStyleChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStyleChangeActionPerformed(evt);
            }
        });

        btnCmdStyleAfter.setForeground(new java.awt.Color(0, 0, 255));
        btnCmdStyleAfter.setText("ADD AFTER");
        btnCmdStyleAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStyleAfterActionPerformed(evt);
            }
        });

        btnCmdStyleBefore.setForeground(new java.awt.Color(204, 0, 204));
        btnCmdStyleBefore.setText("ADD BEFORE");
        btnCmdStyleBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStyleBeforeActionPerformed(evt);
            }
        });

        btnCmdStyleParam.setText("STYLES");
        btnCmdStyleParam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStyleParamActionPerformed(evt);
            }
        });

        btnCmdStyleProperties.setText("PROPERTIES");
        btnCmdStyleProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCmdStylePropertiesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCmdStyleProperties)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCmdStyleParam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 361, Short.MAX_VALUE)
                .addComponent(btnCmdStyleChange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCmdStyleBefore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCmdStyleAfter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCmdStyleAdd)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCmdStyleAdd)
                    .addComponent(btnCmdStyleChange)
                    .addComponent(btnCmdStyleAfter)
                    .addComponent(btnCmdStyleBefore)
                    .addComponent(btnCmdStyleParam)
                    .addComponent(btnCmdStyleProperties))
                .addContainerGap())
        );

        ifrAssTableCommands.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yggdrasil 1.1");

        splitMain.setDividerLocation(400);
        splitMain.setDividerSize(15);
        splitMain.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitMain.setOneTouchExpandable(true);

        btnSend.setText("SEND");

        jScrollPane4.setViewportView(tpChatEntry);

        btnSmiley.setText("SMILEY");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(btnSend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSmiley)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnSend)
                .addComponent(btnSmiley))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setViewportView(tpChatChannel);

        javax.swing.GroupLayout paneChatLayout = new javax.swing.GroupLayout(paneChat);
        paneChat.setLayout(paneChatLayout);
        paneChatLayout.setHorizontalGroup(
            paneChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
        );
        paneChatLayout.setVerticalGroup(
            paneChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneChatLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedOptions.addTab("Chat", paneChat);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane5.setViewportView(jTable1);

        javax.swing.GroupLayout panePTPLayout = new javax.swing.GroupLayout(panePTP);
        panePTP.setLayout(panePTPLayout);
        panePTPLayout.setHorizontalGroup(
            panePTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
        );
        panePTPLayout.setVerticalGroup(
            panePTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
        );

        tabbedOptions.addTab("Files", panePTP);

        splitMain.setBottomComponent(tabbedOptions);

        javax.swing.GroupLayout deskYGGYLayout = new javax.swing.GroupLayout(deskYGGY);
        deskYGGY.setLayout(deskYGGYLayout);
        deskYGGYLayout.setHorizontalGroup(
            deskYGGYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        deskYGGYLayout.setVerticalGroup(
            deskYGGYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout tabStudioLayout = new javax.swing.GroupLayout(tabStudio);
        tabStudio.setLayout(tabStudioLayout);
        tabStudioLayout.setHorizontalGroup(
            tabStudioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(deskYGGY)
        );
        tabStudioLayout.setVerticalGroup(
            tabStudioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(deskYGGY, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        tabbedMainFunctions.addTab("YGGY", tabStudio);

        javax.swing.GroupLayout tabTransLayout = new javax.swing.GroupLayout(tabTrans);
        tabTrans.setLayout(tabTransLayout);
        tabTransLayout.setHorizontalGroup(
            tabTransLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        tabTransLayout.setVerticalGroup(
            tabTransLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        tabbedMainFunctions.addTab("Translate", tabTrans);

        javax.swing.GroupLayout tabEditingLayout = new javax.swing.GroupLayout(tabEditing);
        tabEditing.setLayout(tabEditingLayout);
        tabEditingLayout.setHorizontalGroup(
            tabEditingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        tabEditingLayout.setVerticalGroup(
            tabEditingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        tabbedMainFunctions.addTab("Editing", tabEditing);

        javax.swing.GroupLayout tabDrawingLayout = new javax.swing.GroupLayout(tabDrawing);
        tabDrawing.setLayout(tabDrawingLayout);
        tabDrawingLayout.setHorizontalGroup(
            tabDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        tabDrawingLayout.setVerticalGroup(
            tabDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        tabbedMainFunctions.addTab("Drawing", tabDrawing);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        tabbedMainFunctions.addTab("tab5", jPanel5);

        splitMain.setLeftComponent(tabbedMainFunctions);

        getContentPane().add(splitMain, java.awt.BorderLayout.CENTER);

        mnuFile.setText("File");

        mnuFileYGGY.setText("YGGY");

        mnuFileYGGYOpenASS.setText("Open an ASS file...");
        mnuFileYGGYOpenASS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYOpenASSActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYOpenASS);

        mnuFileYGGYSaveASSAs.setText("Save an ASS file as...");
        mnuFileYGGYSaveASSAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYSaveASSAsActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYSaveASSAs);

        mnuFileYGGYSaveASSAgain.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuFileYGGYSaveASSAgain.setText("Save an ASS");
        mnuFileYGGYSaveASSAgain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYSaveASSAgainActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYSaveASSAgain);
        mnuFileYGGY.add(jSeparator3);

        mnuFileYGGYVideoOpen.setText("Open a video only...");
        mnuFileYGGYVideoOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYVideoOpenActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYVideoOpen);

        mnuFileYGGYAudioOpen.setText("Open an audio only...");
        mnuFileYGGYAudioOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYAudioOpenActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYAudioOpen);

        mnuFileYGGYAVOpen.setText("Open a media...");
        mnuFileYGGYAVOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileYGGYAVOpenActionPerformed(evt);
            }
        });
        mnuFileYGGY.add(mnuFileYGGYAVOpen);

        mnuFile.add(mnuFileYGGY);

        mnuFileChat.setText("Chat");

        mnuFileChatAddUserToDB.setText("Paste a contact to database");
        mnuFileChatAddUserToDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileChatAddUserToDBActionPerformed(evt);
            }
        });
        mnuFileChat.add(mnuFileChatAddUserToDB);

        mnuFileChatGetUID.setText("Copy my UID to clipboard");
        mnuFileChatGetUID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFileChatGetUIDActionPerformed(evt);
            }
        });
        mnuFileChat.add(mnuFileChatGetUID);

        mnuFile.add(mnuFileChat);

        menuBarYGGY.add(mnuFile);

        setJMenuBar(menuBarYGGY);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVideoPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayActionPerformed
        player.play();
        if(ffss.hasAudio()){
            aw.play(Time.create(0L), Time.create(0L));
        }
    }//GEN-LAST:event_btnVideoPlayActionPerformed

    private void btnVideoPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPauseActionPerformed
        player.pause();
        if(ffss.hasAudio()){
            aw.pause();
        }
    }//GEN-LAST:event_btnVideoPauseActionPerformed

    private void btnVideoStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoStopActionPerformed
        player.stop();
        if(ffss.hasAudio()){
            aw.stop();
        }
    }//GEN-LAST:event_btnVideoStopActionPerformed

    private void btnVideoPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayBeforeActionPerformed
        Time end = currentEvent.getStartTime();
        Time start = Time.substract(end, Time.create(500L));
        playAndStop(start, end);
        if(ffss.hasAudio()){
            aw.play(start, end);
        }
    }//GEN-LAST:event_btnVideoPlayBeforeActionPerformed

    private void btnVideoPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayBeginActionPerformed
        Time start = currentEvent.getStartTime();
        Time end = Time.addition(start, Time.create(500L));
        playAndStop(start, end);
        if(ffss.hasAudio()){
            aw.play(start, end);
        }
    }//GEN-LAST:event_btnVideoPlayBeginActionPerformed

    private void btnVideoPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayAreaActionPerformed
        Time start = currentEvent.getStartTime();
        Time end = currentEvent.getEndTime();
        playAndStop(start, end);
        if(ffss.hasAudio()){
            aw.play(start, end);
        }
    }//GEN-LAST:event_btnVideoPlayAreaActionPerformed

    private void btnVideoPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayEndActionPerformed
        Time end = currentEvent.getEndTime();
        Time start = Time.substract(end, Time.create(500L));
        playAndStop(start, end);
        if(ffss.hasAudio()){
            aw.play(start, end);
        }
    }//GEN-LAST:event_btnVideoPlayEndActionPerformed

    private void btnVideoPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayAfterActionPerformed
        Time start = currentEvent.getEndTime();
        Time end = Time.addition(start, Time.create(500L));
        playAndStop(start, end);
        if(ffss.hasAudio()){
            aw.play(start, end);
        }
    }//GEN-LAST:event_btnVideoPlayAfterActionPerformed

    private void btnAudioPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.play(Time.create(0L), Time.create(0L));
            if(ffss.hasVideo()){
                player.setStartAt(Time.create(0L));
                player.setStopAt(Time.create(0L));
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayActionPerformed

    private void btnAudioPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPauseActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.pause();
            if(ffss.hasVideo()){
                player.pause();
            }
        }
    }//GEN-LAST:event_btnAudioPauseActionPerformed

    private void btnAudioStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioStopActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.stop();
            if(ffss.hasVideo()){
                player.stop();
            }
        }
    }//GEN-LAST:event_btnAudioStopActionPerformed

    private void btnAudioPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayBeforeActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStop = aw.getTimeOfStartArea();
            Time msStart = Time.substract(msStop, Time.create(500L));
            aw.play(msStart, msStop);
            if(ffss.hasVideo()){
                player.setStartAt(msStart);
                player.setStopAt(msStart);
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayBeforeActionPerformed

    private void btnAudioPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayBeginActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStartArea();
            Time msStop = Time.addition(msStart, Time.create(500L));
            aw.play(msStart, msStop);
            if(ffss.hasVideo()){
                player.setStartAt(msStart);
                player.setStopAt(msStart);
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayBeginActionPerformed

    private void btnAudioPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayAreaActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStartArea();
            Time msStop = aw.getTimeOfStopArea();
            aw.play(msStart, msStop);
            if(ffss.hasVideo()){
                player.setStartAt(msStart);
                player.setStopAt(msStart);
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayAreaActionPerformed

    private void btnAudioPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayEndActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStop = aw.getTimeOfStopArea();
            Time msStart = Time.substract(msStop, Time.create(500L));
            aw.play(msStart, msStop);
            if(ffss.hasVideo()){
                player.setStartAt(msStart);
                player.setStopAt(msStart);
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayEndActionPerformed

    private void btnAudioPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayAfterActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStopArea();
            Time msStop = Time.addition(msStart, Time.create(500L));
            aw.play(msStart, msStop);
            if(ffss.hasVideo()){
                player.setStartAt(msStart);
                player.setStopAt(msStart);
                player.play();
            }
        }
    }//GEN-LAST:event_btnAudioPlayAfterActionPerformed

    private void btnAudioAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioAcceptActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time start = aw.getTimeOfStartArea();
            Time end = aw.getTimeOfStopArea();
            changeTime(start, end);
        }
    }//GEN-LAST:event_btnAudioAcceptActionPerformed

    private void mnuFileYGGYAudioOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYAudioOpenActionPerformed
        if(fcAudioReady == false){
            for(FileFilter ff : fcAudio.getChoosableFileFilters()){
                fcAudio.removeChoosableFileFilter(ff);
            }
            
            fcAudio.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "m4a", "wav", "mka", "wma", "ogg", 
                            "mp2", "mp3", "aac", "opus", "tta", 
                            "aiff" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "Audio files";
                }
            });
            fcAudioReady = true;
        }
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            openAudio(fcAudio.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYAudioOpenActionPerformed

    private void mnuFileYGGYAVOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYAVOpenActionPerformed
        if(fcVideoReady == false){
            for(FileFilter ff : fcVideo.getChoosableFileFilters()){
                fcVideo.removeChoosableFileFilter(ff);
            }
            
            fcVideo.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "avi", "mov", "mkv", "asf", "ts", 
                            "mpeg", "m2ts", "vob", "mp4", "divx", 
                            "ogm" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "Video files";
                }
            });
            fcVideoReady = true;
        }
        int z = fcVideo.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            openAudioVideo(fcVideo.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYAVOpenActionPerformed

    private void mnuFileYGGYVideoOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYVideoOpenActionPerformed
        if(fcVideoReady == false){
            for(FileFilter ff : fcVideo.getChoosableFileFilters()){
                fcVideo.removeChoosableFileFilter(ff);
            }
            
            fcVideo.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "avi", "mov", "mkv", "asf", "ts", 
                            "mpeg", "m2ts", "vob", "mp4", "divx", 
                            "ogm" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "Video files";
                }
            });
            fcVideoReady = true;
        }
        int z = fcVideo.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            openVideo(fcVideo.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYVideoOpenActionPerformed

    private void toggleAssKaraokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleAssKaraokeActionPerformed
        if(toggleYvesKaraoke.isSelected() == true){
            toggleYvesKaraoke.setSelected(false);
        }
    }//GEN-LAST:event_toggleAssKaraokeActionPerformed

    private void toggleYvesKaraokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleYvesKaraokeActionPerformed
        if(toggleAssKaraoke.isSelected() == true){
            toggleAssKaraoke.setSelected(false);
        }
    }//GEN-LAST:event_toggleYvesKaraokeActionPerformed

    private void btnCmdStyleAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleAddActionPerformed
        Event nv = getFromAssSubCommands();
        dtmASS.insertOne(nv);
        tableOne.updateUI();
    }//GEN-LAST:event_btnCmdStyleAddActionPerformed

    private void btnCmdStyleChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleChangeActionPerformed
        if(tableOne.getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            dtmASS.changeEventAt(nv, tableOne.getSelectedRow());
            tableOne.updateUI();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnCmdStyleChangeActionPerformed

    private void btnCmdStyleBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleBeforeActionPerformed
        if(tableOne.getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            dtmASS.insertOneAt(nv, tableOne.getSelectedRow());
            tableOne.updateUI();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnCmdStyleBeforeActionPerformed

    private void btnCmdStyleAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleAfterActionPerformed
        if(tableOne.getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            if(tableOne.getRowCount() - 1 == tableOne.getSelectedRow()){
                // We are at last event
                dtmASS.insertOne(nv);
            }else{
                // We are inside the events cosmos
                dtmASS.insertOneAt(nv, tableOne.getSelectedRow() + 1);
            }
            tableOne.updateUI();
        }else{
            JOptionPane.showConfirmDialog(
                    this,                           // Parent
                    "No selected row!",             // Text
                    "Error",                        // Title
                    JOptionPane.OK_CANCEL_OPTION,   // Option
                    JOptionPane.ERROR_MESSAGE);     // Message
        }
    }//GEN-LAST:event_btnCmdStyleAfterActionPerformed

    private void mnuFileYGGYOpenASSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYOpenASSActionPerformed
        if(fcAssReady == false){
            for(FileFilter ff : fcASS.getChoosableFileFilters()){
                fcASS.removeChoosableFileFilter(ff);
            }
            
            fcASS.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "ass" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "ASS files";
                }
            });
            fcAssReady = true;
        }
        int z = fcASS.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            loadASSTable(fcASS.getSelectedFile());
            
        }
    }//GEN-LAST:event_mnuFileYGGYOpenASSActionPerformed

    private void mnuFileYGGYSaveASSAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYSaveASSAsActionPerformed
        if(fcAssReady == false){
            for(FileFilter ff : fcASS.getChoosableFileFilters()){
                fcASS.removeChoosableFileFilter(ff);
            }
            
            fcASS.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "ass" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "ASS files";
                }
            });
            fcAssReady = true;
        }
        int z = fcASS.showSaveDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            saveASSTable(fcASS.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYSaveASSAsActionPerformed

    private void mnuFileYGGYSaveASSAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYSaveASSAgainActionPerformed
        if(ass.getAssFile() != null){
            saveASSTable(fcASS.getSelectedFile());
            return;
        }        
        if(fcAssReady == false){
            for(FileFilter ff : fcASS.getChoosableFileFilters()){
                fcASS.removeChoosableFileFilter(ff);
            }
            
            fcASS.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory()) return true;
                    String ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
                    return switch (ext) {
                        case "ass" -> true;
                        default -> false;
                    };
                }

                @Override
                public String getDescription() {
                    return "ASS files";
                }
            });
            fcAssReady = true;
        }
        int z = fcASS.showSaveDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            saveASSTable(fcASS.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYSaveASSAgainActionPerformed

    private void mnuFileChatAddUserToDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileChatAddUserToDBActionPerformed
        UserChatUID uid = UserChatUID.decodeUID();
        UserChatUID.save(userChatUIDList, uid);
    }//GEN-LAST:event_mnuFileChatAddUserToDBActionPerformed

    private void mnuFileChatGetUIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileChatGetUIDActionPerformed
        UserChatUID.encodeUID(myselfUID);        
    }//GEN-LAST:event_mnuFileChatGetUIDActionPerformed

    private void btnCmdStylePropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStylePropertiesActionPerformed
        PropsDialog prd = new PropsDialog(this, true);
        prd.setAssInfos(ass);
        prd.setVideoInfos(ffss);
        prd.showDialog(this);
        if(prd.getDialogResult() == PropsDialog.DialogResult.OK){
            prd.getAssInfos(ass);
        }
    }//GEN-LAST:event_btnCmdStylePropertiesActionPerformed

    private void btnCmdStyleParamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleParamActionPerformed
        StylesDialog sd = new StylesDialog(this, true);
        sd.showDialog(ass.getStyles());
        if(sd.getDialogResult() == StylesDialog.DialogResult.Ok){
            System.out.println("Ok");
        }
    }//GEN-LAST:event_btnCmdStyleParamActionPerformed

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
    private javax.swing.ButtonGroup bgAssType;
    private javax.swing.JButton btnAudioAccept;
    private javax.swing.JButton btnAudioPause;
    private javax.swing.JButton btnAudioPlay;
    private javax.swing.JButton btnAudioPlayAfter;
    private javax.swing.JButton btnAudioPlayArea;
    private javax.swing.JButton btnAudioPlayBefore;
    private javax.swing.JButton btnAudioPlayBegin;
    private javax.swing.JButton btnAudioPlayEnd;
    private javax.swing.JButton btnAudioStop;
    private javax.swing.JButton btnCmdStyleAdd;
    private javax.swing.JButton btnCmdStyleAfter;
    private javax.swing.JButton btnCmdStyleBefore;
    private javax.swing.JButton btnCmdStyleChange;
    private javax.swing.JButton btnCmdStyleParam;
    private javax.swing.JButton btnCmdStyleProperties;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnSmiley;
    private javax.swing.JButton btnVideoPause;
    private javax.swing.JButton btnVideoPlay;
    private javax.swing.JButton btnVideoPlayAfter;
    private javax.swing.JButton btnVideoPlayArea;
    private javax.swing.JButton btnVideoPlayBefore;
    private javax.swing.JButton btnVideoPlayBegin;
    private javax.swing.JButton btnVideoPlayEnd;
    private javax.swing.JButton btnVideoStop;
    private javax.swing.JComboBox<String> comboName;
    private javax.swing.JComboBox<String> comboStyle;
    private javax.swing.JDesktopPane deskYGGY;
    private javax.swing.JFileChooser fcASS;
    private javax.swing.JFileChooser fcAudio;
    private javax.swing.JFileChooser fcVideo;
    private javax.swing.JInternalFrame ifrAssTableCommands;
    private javax.swing.JInternalFrame ifrVideo;
    private javax.swing.JInternalFrame ifrWave;
    private javax.swing.JInternalFrame ifrtableOne;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JMenuBar menuBarYGGY;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuFileChat;
    private javax.swing.JMenuItem mnuFileChatAddUserToDB;
    private javax.swing.JMenuItem mnuFileChatGetUID;
    private javax.swing.JMenu mnuFileYGGY;
    private javax.swing.JMenuItem mnuFileYGGYAVOpen;
    private javax.swing.JMenuItem mnuFileYGGYAudioOpen;
    private javax.swing.JMenuItem mnuFileYGGYOpenASS;
    private javax.swing.JMenuItem mnuFileYGGYSaveASSAgain;
    private javax.swing.JMenuItem mnuFileYGGYSaveASSAs;
    private javax.swing.JMenuItem mnuFileYGGYVideoOpen;
    private javax.swing.JPanel paneChat;
    private javax.swing.JPanel paneControls;
    private javax.swing.JPanel paneEmbedWave;
    private javax.swing.JPanel panePTP;
    private javax.swing.JPanel paneTimeline;
    private javax.swing.JPanel paneVideo;
    private javax.swing.JSpinner spinL;
    private javax.swing.JSpinner spinLayer;
    private javax.swing.JSpinner spinR;
    private javax.swing.JSpinner spinV;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JPanel tabAssSfxMarg;
    private javax.swing.JPanel tabAssSubsCmd;
    private javax.swing.JPanel tabDrawing;
    private javax.swing.JPanel tabEditing;
    private javax.swing.JPanel tabStudio;
    private javax.swing.JPanel tabTrans;
    private javax.swing.JTabbedPane tabbedMainFunctions;
    private javax.swing.JTabbedPane tabbedOptions;
    private javax.swing.JTabbedPane tabbedSubsCmd;
    private javax.swing.JTable tableOne;
    private javax.swing.JTextField tfDurationFrame;
    private javax.swing.JTextField tfDurationTime;
    private javax.swing.JTextField tfEndFrame;
    private javax.swing.JTextField tfEndTime;
    private javax.swing.JTextField tfSFX;
    private javax.swing.JTextField tfStartFrame;
    private javax.swing.JTextField tfStartTime;
    private javax.swing.JTextField tfVideoCurrentFrame;
    private javax.swing.JTextField tfVideoCurrentTime;
    private javax.swing.JTextField tfVideoDurationFrame;
    private javax.swing.JTextField tfVideoDurationTime;
    private javax.swing.JTextField tfVideoEndFrame;
    private javax.swing.JTextField tfVideoEndTime;
    private javax.swing.JTextField tfVideoStartFrame;
    private javax.swing.JTextField tfVideoStartTime;
    private javax.swing.JToggleButton toggleAssKaraoke;
    private javax.swing.JToggleButton toggleCmdCommands;
    private javax.swing.JToggleButton toggleCmdComment;
    private javax.swing.JToggleButton toggleCmdDialogue;
    private javax.swing.JToggleButton toggleCmdMovie;
    private javax.swing.JToggleButton toggleCmdPicture;
    private javax.swing.JToggleButton toggleCmdProposal;
    private javax.swing.JToggleButton toggleCmdRequest;
    private javax.swing.JToggleButton toggleCmdSound;
    private javax.swing.JToggleButton toggleYvesKaraoke;
    private javax.swing.JTextPane tpChatChannel;
    private javax.swing.JTextPane tpChatEntry;
    private javax.swing.JTextPane tpText;
    // End of variables declaration//GEN-END:variables
}
