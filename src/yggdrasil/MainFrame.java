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
package yggdrasil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemColor;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.wingate.timelibrary.Time;
import yggdrasil.util.subtitle.ass.ASS;
import yggdrasil.util.subtitle.ass.Event;
import yggdrasil.util.subtitle.ass.Style;
import yggdrasil.drawing.HistoricListRenderer;
import yggdrasil.drawing.Memories;
import yggdrasil.drawing.Sketchpad;
import yggdrasil.drawing.layers.HistoricalLayersComboRenderer;
import yggdrasil.drawing.layers.Layer;
import yggdrasil.drawing.layers.LayersGroup;
import yggdrasil.fcfilefilter.DrawingFileFilter;
import yggdrasil.fcfilefilter.ImagesFileFilter;
import yggdrasil.fcfilefilter.MoviesFileFilter;
import yggdrasil.fcfilefilter.SoundsFileFilter;
import yggdrasil.fcfilefilter.SubtitlesFileFilter;
import yggdrasil.karaoke.AssKaraokeCollection;
import yggdrasil.languages.ISO_3166;
import yggdrasil.languages.Language;
import yggdrasil.network.UserChatUID;
import yggdrasil.util.AssEventTableModel;
import yggdrasil.util.AssEventTableRenderer;
import yggdrasil.util.audio.AudioWave;
import yggdrasil.util.FFStuffs;
import yggdrasil.util.FramesPanel;
import yggdrasil.util.audio.AudioWavePanel;
import yggdrasil.util.dialog.PropsDialog;
import yggdrasil.util.dialog.StylesDialog;
import yggdrasil.util.subtitle.YGGY;
import yggdrasil.videoplayer.AudioVideoPlayer;
import org.wingate.yinggyongg.io.Client;
import org.wingate.yinggyongg.io.Server;
import org.wingate.yinggyongg.peer.Machine;
import org.wingate.yinggyongg.ruby.Rubygg;
import org.wingate.yinggyongg.ruby.Scripting;
import org.wingate.yinggyongg.ruby.XrbReader;
import yggdrasil.util.Clipboard;

/**
 *
 * @author util2
 */
public class MainFrame extends javax.swing.JFrame {
    
    // Temp folder
    private static final String TEMP_FOLDER = "tempFolder";

    // Language (loading from properties of each component)
    static ISO_3166 wantedIso = ISO_3166.getISO_3166(Locale.getDefault().getISO3Country());
    static Language chosen = new Language();
    
    private static boolean darkUI = false;
    
    private File video = null;
    private ASS ass = ASS.NoFileToLoad();
    private final YGGY yggy = YGGY.create();
    private Event currentEvent = null;
    
    private Time end = Time.create(0L);
    private Time start = Time.create(0L);
    
    // ifrVideo components and variables
    private FramesPanel fp;
    private FFStuffs ffss = null;
    private final AudioVideoPlayer avPanel = new AudioVideoPlayer();
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
    
    // ifrDrawTools2 components and variables
    private ImageIcon iiShapes, iiDraft, iiControls, iiFile, iiSel, iiBool;
    // ifrDrawTools2 stop
    
    // ifrSketchpad components and variables
    private Sketchpad skp;
    // ifrSketchpad stop
    
    // ifrHistoricLayers components and variables
    private final DefaultListModel dlmHistoric = new DefaultListModel();
    private final DefaultComboBoxModel dcbmLayers = new DefaultComboBoxModel();
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Groups of layers");
    private final DefaultTreeModel dtmHistoricLayers = new DefaultTreeModel(root);
    private int layersGroupIndex = 0;
    // ifrHistoricLayers stop
    
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
        File temp_folder = new File(TEMP_FOLDER);
        temp_folder.mkdir();
        temp_folder.deleteOnExit();
        
        setSize(1880, 1058);
        setLocationRelativeTo(null);
        
        // Check if there is a requested language (forced)
        // and choose between posibilities
        if(chosen.isForced() == true){
            wantedIso = chosen.getIso();
        }
        
        // File filters settings
        fcASS.setFileFilter(new SubtitlesFileFilter());
        fcVideo.setFileFilter(new MoviesFileFilter());
        fcAudio.setFileFilter(new SoundsFileFilter());
        fcDrawingYDS.setFileFilter(new DrawingFileFilter());
        fcDrawingImages.setFileFilter(new ImagesFileFilter());
        
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
        ifrVideo.setSize(972, 662);
        ifrVideo.setLocation(5, 5);
        ifrVideo.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ifrVideo.setTitle("Java FFmpeg - " + paneVideo.getWidth() + "x" + paneVideo.getHeight());                
            }            
        });
        paneVideo.add(avPanel.getVideoPanel());
        ifrVideo.setTitle("Java FFmpeg - " + paneVideo.getWidth() + "x" + paneVideo.getHeight());
        deskYGGY.add(ifrVideo);

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
        
        // Drawing (Tools)
//        ifrDrawTools.setSize(170, 750);
//        ifrDrawTools.setLocation(5, 5);
//        deskDrawing.add(ifrDrawTools);
        
        // Drawing (Tools2)
        ifrDrawTools2.setSize(220, 750);
        ifrDrawTools2.setLocation(5, 5);
        deskDrawing.add(ifrDrawTools2);
        iiFile = getTabLeft(chosen.getTranslated("iiFile", wantedIso, "File"));
        tabbedDrawTools2.setTabComponentAt(0, new JLabel(iiFile));
        iiShapes = getTabLeft(chosen.getTranslated("iiShapes", wantedIso, "Shapes"));
        tabbedDrawTools2.setTabComponentAt(1, new JLabel(iiShapes));
        iiDraft = getTabLeft(chosen.getTranslated("iiDraft", wantedIso, "Draft"));
        tabbedDrawTools2.setTabComponentAt(2, new JLabel(iiDraft));
        iiControls = getTabLeft(chosen.getTranslated("iiControls", wantedIso, "Controls"));
        tabbedDrawTools2.setTabComponentAt(3, new JLabel(iiControls));
        iiSel = getTabLeft(chosen.getTranslated("iiSel", wantedIso, "Transform"));
        tabbedDrawTools2.setTabComponentAt(4, new JLabel(iiSel));
        iiBool = getTabLeft(chosen.getTranslated("iiBool", wantedIso, "Boolean Op"));
        tabbedDrawTools2.setTabComponentAt(5, new JLabel(iiBool));
        
        // Drawing (Sketchpad)
        skp = new Sketchpad(this);
        ifrSketchpad.setSize(1350, 750);
        ifrSketchpad.setLocation(225, 5);
        ifrSketchpad.setLayout(new BorderLayout());
        ifrSketchpad.add(skp);
        deskDrawing.add(ifrSketchpad);
        
        // Drawing (Historic and layers)
        ifrHistoricLayers.setSize(280, 750);
        ifrHistoricLayers.setLocation(1575, 5);
        deskDrawing.add(ifrHistoricLayers);
        listHistoric.setModel(dlmHistoric);
        listHistoric.setCellRenderer(new HistoricListRenderer(skp));
        comboHistoricLayers.setModel(dcbmLayers);
        comboHistoricLayers.setRenderer(new HistoricalLayersComboRenderer());
        dcbmLayers.addElement(new Layer());
        treeHistoricLayers.setModel(dtmHistoricLayers);
        
//        // Server launching
//        Server.createServer();
//        
//        // Final touch
//        Rubygg rby = XrbReader.readString(
//                getClass().getResource("/rubygg/chat_activity.xrb").getPath());
//        try {
//            Scripting.runRubyCode(rby.getCode(), "hello");
//        } catch (IOException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
    
    public static String getTempFolder(){
        return TEMP_FOLDER;
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
        avPanel.setAudioVideoFile(video);
    }
    
    private void playAndStop(){
        if(currentEvent != null){
            avPanel.setStart(start);
            avPanel.setStop(end);
            avPanel.audioVideoPlay();
        }
    }
    
    public void displayEventTime(Event ev){
        int startFrame = Time.getFrame(ev.getStartTime(), ffss.getFps());
        int endFrame = Time.getFrame(ev.getEndTime(), ffss.getFps());
        
        int startSamples = aw.getSamplesFromFrame(startFrame);
        int endSamples = aw.getSamplesFromFrame(endFrame);
        
        Point startPoint = new Point(Math.round(startSamples / aw.getSamplesPerPixel()), 0);
        Point stopPoint = new Point(Math.round(endSamples / aw.getSamplesPerPixel()), 0);
        
        aw.getAudioWavePanel().updatePoint(startPoint, stopPoint);
        
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
        
        Time currentTime = Time.create(0L);
        Time startTime = Time.create(0L);
        Time endTime = ffss.getDuration();
        Time durTime = ffss.getDuration();
        
        tfVideoStartTime.setText(startTime.toProgramExtendedTime());
        tfVideoStartFrame.setText(Integer.toString(Time.getFrame(startTime, ffss.getFps())));
        tfVideoEndTime.setText(endTime.toProgramExtendedTime());
        tfVideoEndFrame.setText(Integer.toString(Time.getFrame(endTime, ffss.getFps())));
        tfVideoDurationTime.setText(durTime.toProgramExtendedTime());
        tfVideoDurationFrame.setText(Integer.toString(Time.getFrame(durTime, ffss.getFps())));
        tfVideoCurrentTime.setText(currentTime.toProgramExtendedTime());
        tfVideoCurrentFrame.setText(Integer.toString(Time.getFrame(currentTime, ffss.getFps())));

        fp.updatePosition(currentTime);
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
        Time startTime, endTime, duration;
        startTime = ev.getStartTime();
        endTime = ev.getEndTime();
        duration = Time.substract(startTime, endTime);
        tfStartTime.setText(startTime.toProgramExtendedTime());
        tfEndTime.setText(endTime.toProgramExtendedTime());
        tfDurationTime.setText(duration.toProgramExtendedTime());
        try{
            tfStartFrame.setText(Integer.toString(Time.getFrame(startTime, ffss.getFps())));
            tfEndFrame.setText(Integer.toString(Time.getFrame(endTime, ffss.getFps())));
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
    
    // Refresh ASS rendering by updating ass temporary file
    private void refreshTempASS(){
        saveASSTable(new File(TEMP_FOLDER, "temp.ass"));
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
    
    // <editor-fold defaultstate="collapsed" desc="Drawing tools, historic and layers">
    
    public void setMousePositionFromDrawingTools(int x, int y){
        //lblDrawingCoordinates.setText(x+";"+y);
        lblCoordinates2.setText(x+";"+y);
    }
    
    public boolean isMoveNSelected(){
        return toggleMoveN2.isSelected();
    }
    
    public boolean isMoveMSelected(){
        return toggleMoveM2.isSelected();
    }
    
    public boolean isLineSelected(){
        return toggleLine2.isSelected();
    }
    
    public boolean isQuadraticSelected(){
        return toggleQuadratic2.isSelected();
    }
    
    public boolean isCubicSelected(){
        return toggleCubic2.isSelected();
    }
    
    public boolean isCursorSelected(){
        return toggleCursor2.isSelected();
    }
    
    public boolean isBSplineSelected(){
        return toggleBSpline2.isSelected();
    }
    
    public boolean isGridLockerSelected(){
        return toggleGLock2.isSelected();
    }
    
//    public void reloadHistoric(List<Memories<?>> memories){
//        dlmHistoric.clear();
//        dlmHistoric.addAll(memories);
//    }
    
    public void addToHistoric(Memories<?> mem){
        dlmHistoric.addElement(mem);
        tfDrawingCommands.setText(skp.getAssCommands());
    }
    
    public void removeLastFromHistoric(){
        dlmHistoric.removeElementAt(dlmHistoric.size() - 1);
        tfDrawingCommands.setText(skp.getAssCommands());
    }
    
    public void addLayersGroup(){
        if(treeHistoricLayers.getSelectionCount() > 0){
            TreePath[] paths = treeHistoricLayers.getSelectionPaths();
            if (paths != null) {
                for (TreePath path : paths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                    if(isLayerTreeNode(path) == true 
                            && treeHistoricLayers.getSelectionPath().equals(path) == true){
                        String layerName = JOptionPane.showInputDialog(this, "Type a layer name:");
                        ((DefaultMutableTreeNode)node.getParent()).add(new DefaultMutableTreeNode(layerName));
                        break;
                    }else if(isGroupTreeNode(path) == true
                            && treeHistoricLayers.getSelectionPath().equals(path) == true){
                        String groupName = JOptionPane.showInputDialog(this, "Type a group name:");
                        root.add(new DefaultMutableTreeNode(groupName));
                        break;
                    }
                }
            }            
        }else{
            String groupName = JOptionPane.showInputDialog(this, "Type a group name:");
            LayersGroup layersGroup = LayersGroup.create(groupName, layersGroupIndex);
            layersGroupIndex++;
            Layer lay = new Layer();
            layersGroup.addLayer(lay);
            DefaultMutableTreeNode groupNode = layersGroup.getNode();
            DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(lay);
            root.add(groupNode);
            groupNode.add(layerNode);
        }        
    }
    
    public boolean isLayerTreeNode(TreePath path){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        return node.getUserObject() instanceof Layer;
    }
    
    public boolean isGroupTreeNode(TreePath path){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        return node.getUserObject() instanceof String;
    }
    
    public void deleteLayersGroup(){
        TreePath[] paths = treeHistoricLayers.getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                if(isLayerTreeNode(path) == true 
                        && treeHistoricLayers.getSelectionPath().equals(path) == true){
                    dtmHistoricLayers.removeNodeFromParent(node);
                    break;
                }else if(isGroupTreeNode(path) == true
                        && treeHistoricLayers.getSelectionPath().equals(path) == true){
                    dtmHistoricLayers.removeNodeFromParent(node);
                    break;
                }
            }
        }
    }
    
    private ImageIcon getTabLeft(String text){
        BufferedImage img_1 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img_1.createGraphics();
        int w = g2d.getFontMetrics().stringWidth("   "+text+"   ");
        int b = g2d.getFontMetrics().stringWidth(text);
        int h = g2d.getFontMetrics().getHeight();
        g2d.dispose();
        
        BufferedImage img_2 = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        g2d = img_2.createGraphics();
        g2d.setColor(darkUI ? Color.white : Color.black);
        g2d.rotate(Math.toRadians(-90));
        g2d.drawString(text, -w*13/15, 12);
        
        g2d.dispose();
        return new ImageIcon(img_2);
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
        fcDrawingYDS = new javax.swing.JFileChooser();
        fcDrawingImages = new javax.swing.JFileChooser();
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
        ifrDrawTools = new javax.swing.JInternalFrame();
        lblDrawingFile = new javax.swing.JLabel();
        lblDrawingTools = new javax.swing.JLabel();
        lblDrawingImage = new javax.swing.JLabel();
        lblDrawingCtrls = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        btnDrawNew = new javax.swing.JButton();
        btnDrawOpen = new javax.swing.JButton();
        btnDrawSave = new javax.swing.JButton();
        btnDrawFontOpen = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        toggleCursor = new javax.swing.JToggleButton();
        toggleLine = new javax.swing.JToggleButton();
        toggleQuadratic = new javax.swing.JToggleButton();
        toggleCubic = new javax.swing.JToggleButton();
        toggleGLock = new javax.swing.JToggleButton();
        toggleMoveM = new javax.swing.JToggleButton();
        toggleMoveN = new javax.swing.JToggleButton();
        toggleBSpline = new javax.swing.JToggleButton();
        jPanel13 = new javax.swing.JPanel();
        btnEye = new javax.swing.JButton();
        btnImageUp = new javax.swing.JButton();
        btnOpenImage = new javax.swing.JButton();
        btnClearImage = new javax.swing.JButton();
        btnImageLeft = new javax.swing.JButton();
        btnImageCenter = new javax.swing.JButton();
        btnImageRight = new javax.swing.JButton();
        btnImageBottom = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        btnSeeOneLayer = new javax.swing.JButton();
        btnSeeLayers = new javax.swing.JButton();
        btnDrawingCopy = new javax.swing.JButton();
        btnDrawingPaste = new javax.swing.JButton();
        btnMagicWand = new javax.swing.JButton();
        btnSelection = new javax.swing.JButton();
        btnPara = new javax.swing.JButton();
        btnPerp = new javax.swing.JButton();
        lblAlphaLayer = new javax.swing.JLabel();
        slideAlphaLayer = new javax.swing.JSlider();
        lblDisplaySize = new javax.swing.JLabel();
        slideDisplaySize = new javax.swing.JSlider();
        lblAlphaImage = new javax.swing.JLabel();
        slideAlphaImage = new javax.swing.JSlider();
        lblImageSize = new javax.swing.JLabel();
        slideImageSize = new javax.swing.JSlider();
        lblDrawingCoordinates = new javax.swing.JLabel();
        tfDrawingCommands = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        toggleOpTranslate = new javax.swing.JToggleButton();
        toggleOpRotation = new javax.swing.JToggleButton();
        toggleOpScale = new javax.swing.JToggleButton();
        toggleOpShear = new javax.swing.JToggleButton();
        bgDrawingTools = new javax.swing.ButtonGroup();
        ifrSketchpad = new javax.swing.JInternalFrame();
        ifrHistoricLayers = new javax.swing.JInternalFrame();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        listHistoric = new javax.swing.JList<>();
        btnHistoricRedo = new javax.swing.JButton();
        btnHistoricUndo = new javax.swing.JButton();
        comboHistoricLayers = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        lblHistoricGroupLayers = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        treeHistoricLayers = new javax.swing.JTree();
        btnHistoricAddLayer = new javax.swing.JButton();
        btnHistoricRemoveLayer = new javax.swing.JButton();
        ifrDrawTools2 = new javax.swing.JInternalFrame();
        tabbedDrawTools2 = new javax.swing.JTabbedPane();
        panFile = new javax.swing.JPanel();
        btnDrawNew2 = new javax.swing.JButton();
        btnDrawOpen2 = new javax.swing.JButton();
        btnDrawSave2 = new javax.swing.JButton();
        btnDrawFontOpen2 = new javax.swing.JButton();
        panShapes = new javax.swing.JPanel();
        toggleCursor2 = new javax.swing.JToggleButton();
        toggleLine2 = new javax.swing.JToggleButton();
        toggleQuadratic2 = new javax.swing.JToggleButton();
        toggleCubic2 = new javax.swing.JToggleButton();
        toggleGLock2 = new javax.swing.JToggleButton();
        toggleMoveM2 = new javax.swing.JToggleButton();
        toggleMoveN2 = new javax.swing.JToggleButton();
        toggleBSpline2 = new javax.swing.JToggleButton();
        panDraft = new javax.swing.JPanel();
        btnEye2 = new javax.swing.JButton();
        btnImageUp2 = new javax.swing.JButton();
        btnOpenImage2 = new javax.swing.JButton();
        btnClearImage2 = new javax.swing.JButton();
        btnImageLeft2 = new javax.swing.JButton();
        btnImageCenter2 = new javax.swing.JButton();
        btnImageRight2 = new javax.swing.JButton();
        btnImageBottom2 = new javax.swing.JButton();
        panControls = new javax.swing.JPanel();
        toggleSeeOneLayer2 = new javax.swing.JToggleButton();
        toggleSeeLayers2 = new javax.swing.JToggleButton();
        btnDrawingCopy2 = new javax.swing.JButton();
        btnDrawingPaste2 = new javax.swing.JButton();
        btnMagicWand2 = new javax.swing.JButton();
        btnPara2 = new javax.swing.JButton();
        btnPerp2 = new javax.swing.JButton();
        lblAlphaLayer2 = new javax.swing.JLabel();
        slideAlphaLayer2 = new javax.swing.JSlider();
        lblDisplaySize2 = new javax.swing.JLabel();
        slideDisplaySize2 = new javax.swing.JSlider();
        lblAlphaImage2 = new javax.swing.JLabel();
        slideAlphaImage2 = new javax.swing.JSlider();
        lblImageSize2 = new javax.swing.JLabel();
        slideImageSize2 = new javax.swing.JSlider();
        toggleOpTranslate2 = new javax.swing.JToggleButton();
        toggleOpRotation2 = new javax.swing.JToggleButton();
        toggleOpScale2 = new javax.swing.JToggleButton();
        toggleOpShear2 = new javax.swing.JToggleButton();
        panSelection = new javax.swing.JPanel();
        lblSelection2 = new javax.swing.JLabel();
        spSelection2 = new javax.swing.JScrollPane();
        listSelection2 = new javax.swing.JList<>();
        btnDoSelection2 = new javax.swing.JButton();
        panBooleanOp = new javax.swing.JPanel();
        btnBOpIntersect = new javax.swing.JButton();
        btnBOpSubstract = new javax.swing.JButton();
        btnBOpXOR = new javax.swing.JButton();
        btnBOpUnion = new javax.swing.JButton();
        panDisplay2 = new javax.swing.JPanel();
        lblCoordinates2 = new javax.swing.JLabel();
        tfCommands2 = new javax.swing.JTextField();
        popCommands = new javax.swing.JPopupMenu();
        popmCopy = new javax.swing.JMenuItem();
        popmPaste = new javax.swing.JMenuItem();
        bgDrawingTools2 = new javax.swing.ButtonGroup();
        bgLayers2 = new javax.swing.ButtonGroup();
        splitMain = new javax.swing.JSplitPane();
        tabbedOptions = new javax.swing.JTabbedPane();
        paneChat = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnRegister = new javax.swing.JButton();
        btnSmiley = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        lblCom = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tpChatChannel = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        tpChatEntry = new javax.swing.JTextPane();
        panePTP = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        tabbedMainFunctions = new javax.swing.JTabbedPane();
        tabStudio = new javax.swing.JPanel();
        deskYGGY = new javax.swing.JDesktopPane();
        tabTrans = new javax.swing.JPanel();
        tabEditing = new javax.swing.JPanel();
        tabDrawing = new javax.swing.JPanel();
        deskDrawing = new javax.swing.JDesktopPane();
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

        btnVideoPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play.png"))); // NOI18N
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

        btnVideoPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs pause.png"))); // NOI18N
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

        btnVideoStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs stop.png"))); // NOI18N
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

        btnVideoPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play out 01.png"))); // NOI18N
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

        btnVideoPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play in 01.png"))); // NOI18N
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

        btnVideoPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs in.png"))); // NOI18N
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

        btnVideoPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play in 02.png"))); // NOI18N
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

        btnVideoPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play out 02.png"))); // NOI18N
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
                .addComponent(paneVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
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
            .addGap(0, 290, Short.MAX_VALUE)
        );

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnAudioPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play.png"))); // NOI18N
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

        btnAudioPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs pause.png"))); // NOI18N
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

        btnAudioStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs stop.png"))); // NOI18N
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

        btnAudioPlayBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play out 01.png"))); // NOI18N
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

        btnAudioPlayBegin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play in 01.png"))); // NOI18N
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

        btnAudioPlayArea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs in.png"))); // NOI18N
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

        btnAudioPlayEnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play in 02.png"))); // NOI18N
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

        btnAudioPlayAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_timer_stuffs play out 02.png"))); // NOI18N
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

        btnAudioAccept.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32 accept.png"))); // NOI18N
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

        toggleAssKaraoke.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/ass yves karaoke (ass).png"))); // NOI18N
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

        toggleYvesKaraoke.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/ass yves karaoke (ves).png"))); // NOI18N
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
        ifrtableOne.setTitle("ASS Table");
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
        toggleCmdDialogue.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub dialogue.png"))); // NOI18N
        toggleCmdDialogue.setSelected(true);
        toggleCmdDialogue.setToolTipText("Dialogue");

        bgAssType.add(toggleCmdComment);
        toggleCmdComment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment.png"))); // NOI18N
        toggleCmdComment.setToolTipText("Comment");

        bgAssType.add(toggleCmdProposal);
        toggleCmdProposal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment yellow.png"))); // NOI18N
        toggleCmdProposal.setToolTipText("Proposal");

        bgAssType.add(toggleCmdRequest);
        toggleCmdRequest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/40 funsub comment blue.png"))); // NOI18N
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

        ifrDrawTools.setMaximizable(true);
        ifrDrawTools.setResizable(true);
        ifrDrawTools.setTitle("Tools");
        ifrDrawTools.setVisible(true);

        lblDrawingFile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDrawingFile.setText("File");

        lblDrawingTools.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDrawingTools.setText("Drawing tools");

        lblDrawingImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDrawingImage.setText("Image tools");

        lblDrawingCtrls.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDrawingCtrls.setText("Controls");

        jPanel11.setLayout(null);

        btnDrawNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_newdocument.png"))); // NOI18N
        btnDrawNew.setToolTipText("New drawing");
        btnDrawNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawNewActionPerformed(evt);
            }
        });
        jPanel11.add(btnDrawNew);
        btnDrawNew.setBounds(0, 0, 40, 38);

        btnDrawOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_folder.png"))); // NOI18N
        btnDrawOpen.setToolTipText("Open drawing");
        btnDrawOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawOpenActionPerformed(evt);
            }
        });
        jPanel11.add(btnDrawOpen);
        btnDrawOpen.setBounds(40, 0, 40, 40);

        btnDrawSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_floppydisk.png"))); // NOI18N
        btnDrawSave.setToolTipText("Save drawing");
        btnDrawSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawSaveActionPerformed(evt);
            }
        });
        jPanel11.add(btnDrawSave);
        btnDrawSave.setBounds(80, 0, 40, 40);

        btnDrawFontOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_mimetype_font_type1.png"))); // NOI18N
        btnDrawFontOpen.setToolTipText("Open outline");
        btnDrawFontOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawFontOpenActionPerformed(evt);
            }
        });
        jPanel11.add(btnDrawFontOpen);
        btnDrawFontOpen.setBounds(120, 0, 40, 40);

        jPanel12.setLayout(null);

        bgDrawingTools.add(toggleCursor);
        toggleCursor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_cur.png"))); // NOI18N
        toggleCursor.setSelected(true);
        toggleCursor.setToolTipText("Cursor");
        jPanel12.add(toggleCursor);
        toggleCursor.setBounds(0, 0, 40, 40);

        bgDrawingTools.add(toggleLine);
        toggleLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingLine.png"))); // NOI18N
        toggleLine.setToolTipText("Line");
        jPanel12.add(toggleLine);
        toggleLine.setBounds(40, 0, 40, 40);

        bgDrawingTools.add(toggleQuadratic);
        toggleQuadratic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/afm splines 02.png"))); // NOI18N
        toggleQuadratic.setToolTipText("Quadratic");
        jPanel12.add(toggleQuadratic);
        toggleQuadratic.setBounds(80, 0, 40, 40);

        bgDrawingTools.add(toggleCubic);
        toggleCubic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingBezier.png"))); // NOI18N
        toggleCubic.setToolTipText("Cubic");
        jPanel12.add(toggleCubic);
        toggleCubic.setBounds(120, 0, 40, 40);

        toggleGLock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/gridlocker.png"))); // NOI18N
        toggleGLock.setToolTipText("Grid locker");
        jPanel12.add(toggleGLock);
        toggleGLock.setBounds(0, 40, 40, 40);

        bgDrawingTools.add(toggleMoveM);
        toggleMoveM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew_m_32.png"))); // NOI18N
        toggleMoveM.setToolTipText("Move M");
        jPanel12.add(toggleMoveM);
        toggleMoveM.setBounds(40, 40, 40, 40);

        bgDrawingTools.add(toggleMoveN);
        toggleMoveN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew_n_32.png"))); // NOI18N
        toggleMoveN.setToolTipText("Move N");
        jPanel12.add(toggleMoveN);
        toggleMoveN.setBounds(80, 40, 40, 40);

        bgDrawingTools.add(toggleBSpline);
        toggleBSpline.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingBSpline.png"))); // NOI18N
        toggleBSpline.setToolTipText("BSpline");
        jPanel12.add(toggleBSpline);
        toggleBSpline.setBounds(120, 40, 40, 40);

        jPanel13.setLayout(null);

        btnEye.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_eye.png"))); // NOI18N
        btnEye.setToolTipText("Eye");
        btnEye.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEyeActionPerformed(evt);
            }
        });
        jPanel13.add(btnEye);
        btnEye.setBounds(0, 0, 40, 40);

        btnImageUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew8_32.png"))); // NOI18N
        btnImageUp.setToolTipText("Move the image to the up");
        btnImageUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageUpActionPerformed(evt);
            }
        });
        jPanel13.add(btnImageUp);
        btnImageUp.setBounds(40, 0, 40, 40);

        btnOpenImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_app_kpaint.png"))); // NOI18N
        btnOpenImage.setToolTipText("Open an image...");
        btnOpenImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenImageActionPerformed(evt);
            }
        });
        jPanel13.add(btnOpenImage);
        btnOpenImage.setBounds(80, 0, 40, 40);

        btnClearImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_app_windows_users.png"))); // NOI18N
        btnClearImage.setToolTipText("Clear the image");
        btnClearImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImageActionPerformed(evt);
            }
        });
        jPanel13.add(btnClearImage);
        btnClearImage.setBounds(120, 0, 40, 40);

        btnImageLeft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew4_32.png"))); // NOI18N
        btnImageLeft.setToolTipText("Move the image to the left");
        btnImageLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageLeftActionPerformed(evt);
            }
        });
        jPanel13.add(btnImageLeft);
        btnImageLeft.setBounds(0, 40, 40, 40);

        btnImageCenter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew5_32.png"))); // NOI18N
        btnImageCenter.setToolTipText("Center the image");
        btnImageCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageCenterActionPerformed(evt);
            }
        });
        jPanel13.add(btnImageCenter);
        btnImageCenter.setBounds(40, 40, 40, 40);

        btnImageRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew6_32.png"))); // NOI18N
        btnImageRight.setToolTipText("Move the image to the right");
        btnImageRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageRightActionPerformed(evt);
            }
        });
        jPanel13.add(btnImageRight);
        btnImageRight.setBounds(80, 40, 40, 40);

        btnImageBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew2_32.png"))); // NOI18N
        btnImageBottom.setToolTipText("Move the image to the bottom");
        btnImageBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageBottomActionPerformed(evt);
            }
        });
        jPanel13.add(btnImageBottom);
        btnImageBottom.setBounds(40, 80, 40, 40);

        jPanel14.setLayout(null);

        btnSeeOneLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_layers_just_one.png"))); // NOI18N
        btnSeeOneLayer.setToolTipText("See one layer");
        jPanel14.add(btnSeeOneLayer);
        btnSeeOneLayer.setBounds(0, 0, 40, 40);

        btnSeeLayers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_layers_three.png"))); // NOI18N
        btnSeeLayers.setToolTipText("See all layers");
        jPanel14.add(btnSeeLayers);
        btnSeeLayers.setBounds(40, 0, 40, 40);

        btnDrawingCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_action_editcopy.png"))); // NOI18N
        btnDrawingCopy.setToolTipText("Copy");
        jPanel14.add(btnDrawingCopy);
        btnDrawingCopy.setBounds(80, 0, 40, 40);

        btnDrawingPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_action_editpaste.png"))); // NOI18N
        btnDrawingPaste.setToolTipText("Paste");
        jPanel14.add(btnDrawingPaste);
        btnDrawingPaste.setBounds(120, 0, 40, 40);

        btnMagicWand.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/magic-wand.png"))); // NOI18N
        btnMagicWand.setToolTipText("Magic wand");
        jPanel14.add(btnMagicWand);
        btnMagicWand.setBounds(0, 40, 40, 40);

        btnSelection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Selection.png"))); // NOI18N
        btnSelection.setToolTipText("Selection");
        jPanel14.add(btnSelection);
        btnSelection.setBounds(40, 40, 40, 40);

        btnPara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_parallele.png"))); // NOI18N
        btnPara.setToolTipText("Parallel");
        jPanel14.add(btnPara);
        btnPara.setBounds(80, 40, 40, 40);

        btnPerp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_perpendiculaire.png"))); // NOI18N
        btnPerp.setToolTipText("Perpendicular");
        jPanel14.add(btnPerp);
        btnPerp.setBounds(120, 40, 40, 40);

        lblAlphaLayer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlphaLayer.setText("Alpha of layer");
        jPanel14.add(lblAlphaLayer);
        lblAlphaLayer.setBounds(0, 80, 80, 16);
        jPanel14.add(slideAlphaLayer);
        slideAlphaLayer.setBounds(0, 100, 80, 11);

        lblDisplaySize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDisplaySize.setText("Display size");
        jPanel14.add(lblDisplaySize);
        lblDisplaySize.setBounds(80, 80, 80, 16);
        jPanel14.add(slideDisplaySize);
        slideDisplaySize.setBounds(80, 100, 80, 11);

        lblAlphaImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAlphaImage.setText("Alpha of image");
        jPanel14.add(lblAlphaImage);
        lblAlphaImage.setBounds(0, 130, 80, 16);
        jPanel14.add(slideAlphaImage);
        slideAlphaImage.setBounds(0, 150, 80, 11);

        lblImageSize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImageSize.setText("Image size");
        jPanel14.add(lblImageSize);
        lblImageSize.setBounds(80, 130, 80, 16);
        jPanel14.add(slideImageSize);
        slideImageSize.setBounds(80, 150, 80, 11);

        lblDrawingCoordinates.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblDrawingCoordinates.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDrawingCoordinates.setText("0,0");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Operations");
        jLabel9.setToolTipText("");

        jPanel15.setLayout(null);

        toggleOpTranslate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-translate.png"))); // NOI18N
        toggleOpTranslate.setToolTipText("Translate");
        jPanel15.add(toggleOpTranslate);
        toggleOpTranslate.setBounds(0, 0, 40, 40);

        toggleOpRotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-rotate.png"))); // NOI18N
        toggleOpRotation.setToolTipText("Rotate");
        jPanel15.add(toggleOpRotation);
        toggleOpRotation.setBounds(40, 0, 40, 40);

        toggleOpScale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-scale.png"))); // NOI18N
        toggleOpScale.setToolTipText("Scale");
        jPanel15.add(toggleOpScale);
        toggleOpScale.setBounds(80, 0, 40, 40);

        toggleOpShear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-shear.png"))); // NOI18N
        toggleOpShear.setToolTipText("Shear");
        jPanel15.add(toggleOpShear);
        toggleOpShear.setBounds(120, 0, 40, 40);

        javax.swing.GroupLayout ifrDrawToolsLayout = new javax.swing.GroupLayout(ifrDrawTools.getContentPane());
        ifrDrawTools.getContentPane().setLayout(ifrDrawToolsLayout);
        ifrDrawToolsLayout.setHorizontalGroup(
            ifrDrawToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDrawingCoordinates, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tfDrawingCommands)
            .addComponent(lblDrawingFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblDrawingTools, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblDrawingImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblDrawingCtrls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ifrDrawToolsLayout.setVerticalGroup(
            ifrDrawToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ifrDrawToolsLayout.createSequentialGroup()
                .addComponent(lblDrawingCoordinates)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfDrawingCommands, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDrawingFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDrawingTools)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDrawingImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDrawingCtrls)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        ifrSketchpad.setMaximizable(true);
        ifrSketchpad.setResizable(true);
        ifrSketchpad.setTitle("Sketchpad");
        ifrSketchpad.setVisible(true);

        javax.swing.GroupLayout ifrSketchpadLayout = new javax.swing.GroupLayout(ifrSketchpad.getContentPane());
        ifrSketchpad.getContentPane().setLayout(ifrSketchpadLayout);
        ifrSketchpadLayout.setHorizontalGroup(
            ifrSketchpadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 835, Short.MAX_VALUE)
        );
        ifrSketchpadLayout.setVerticalGroup(
            ifrSketchpadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 587, Short.MAX_VALUE)
        );

        ifrHistoricLayers.setMaximizable(true);
        ifrHistoricLayers.setResizable(true);
        ifrHistoricLayers.setTitle("Historic and layers");
        ifrHistoricLayers.setVisible(true);

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listHistoric.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(listHistoric);

        btnHistoricRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew6_32.png"))); // NOI18N
        btnHistoricRedo.setToolTipText("Redo");
        btnHistoricRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricRedoActionPerformed(evt);
            }
        });

        btnHistoricUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew4_32.png"))); // NOI18N
        btnHistoricUndo.setToolTipText("Undo");
        btnHistoricUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricUndoActionPerformed(evt);
            }
        });

        comboHistoricLayers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(btnHistoricUndo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHistoricRedo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboHistoricLayers, 0, 158, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHistoricUndo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHistoricRedo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboHistoricLayers, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Historic", jPanel16);

        lblHistoricGroupLayers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHistoricGroupLayers.setText("Group or layer in course");

        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane7.setViewportView(treeHistoricLayers);

        btnHistoricAddLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_plus.png"))); // NOI18N
        btnHistoricAddLayer.setToolTipText("Add layer");
        btnHistoricAddLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricAddLayerActionPerformed(evt);
            }
        });

        btnHistoricRemoveLayer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_minus.png"))); // NOI18N
        btnHistoricRemoveLayer.setToolTipText("Remove a layer");
        btnHistoricRemoveLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoricRemoveLayerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHistoricAddLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnHistoricRemoveLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHistoricGroupLayers, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHistoricAddLayer, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnHistoricRemoveLayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHistoricGroupLayers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Layers", jPanel17);

        javax.swing.GroupLayout ifrHistoricLayersLayout = new javax.swing.GroupLayout(ifrHistoricLayers.getContentPane());
        ifrHistoricLayers.getContentPane().setLayout(ifrHistoricLayersLayout);
        ifrHistoricLayersLayout.setHorizontalGroup(
            ifrHistoricLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        ifrHistoricLayersLayout.setVerticalGroup(
            ifrHistoricLayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        ifrDrawTools2.setVisible(true);

        tabbedDrawTools2.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        panFile.setLayout(null);

        btnDrawNew2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_newdocument.png"))); // NOI18N
        btnDrawNew2.setToolTipText("New/Clear");
        btnDrawNew2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawNew2ActionPerformed(evt);
            }
        });
        panFile.add(btnDrawNew2);
        btnDrawNew2.setBounds(0, 0, 40, 40);

        btnDrawOpen2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_folder.png"))); // NOI18N
        btnDrawOpen2.setToolTipText("Open");
        btnDrawOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawOpen2ActionPerformed(evt);
            }
        });
        panFile.add(btnDrawOpen2);
        btnDrawOpen2.setBounds(40, 0, 40, 40);

        btnDrawSave2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_floppydisk.png"))); // NOI18N
        btnDrawSave2.setToolTipText("Save as.../Save");
        btnDrawSave2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawSave2ActionPerformed(evt);
            }
        });
        panFile.add(btnDrawSave2);
        btnDrawSave2.setBounds(80, 0, 40, 40);

        btnDrawFontOpen2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_mimetype_font_type1.png"))); // NOI18N
        btnDrawFontOpen2.setToolTipText("Open a font character");
        btnDrawFontOpen2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrawFontOpen2ActionPerformed(evt);
            }
        });
        panFile.add(btnDrawFontOpen2);
        btnDrawFontOpen2.setBounds(120, 0, 40, 40);

        tabbedDrawTools2.addTab("File", panFile);

        panShapes.setLayout(null);

        bgDrawingTools2.add(toggleCursor2);
        toggleCursor2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_cur.png"))); // NOI18N
        toggleCursor2.setSelected(true);
        toggleCursor2.setToolTipText("Cursor");
        panShapes.add(toggleCursor2);
        toggleCursor2.setBounds(0, 0, 40, 40);

        bgDrawingTools2.add(toggleLine2);
        toggleLine2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingLine.png"))); // NOI18N
        toggleLine2.setToolTipText("Path line");
        panShapes.add(toggleLine2);
        toggleLine2.setBounds(40, 0, 40, 40);

        bgDrawingTools2.add(toggleQuadratic2);
        toggleQuadratic2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/afm splines 02.png"))); // NOI18N
        toggleQuadratic2.setToolTipText("Path Quadratic Bézier");
        panShapes.add(toggleQuadratic2);
        toggleQuadratic2.setBounds(80, 0, 40, 40);

        bgDrawingTools2.add(toggleCubic2);
        toggleCubic2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingBezier.png"))); // NOI18N
        toggleCubic2.setToolTipText("Path Cubic Bézier");
        panShapes.add(toggleCubic2);
        toggleCubic2.setBounds(120, 0, 40, 40);

        toggleGLock2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/gridlocker.png"))); // NOI18N
        toggleGLock2.setToolTipText("Grid locker");
        panShapes.add(toggleGLock2);
        toggleGLock2.setBounds(0, 40, 40, 40);

        bgDrawingTools2.add(toggleMoveM2);
        toggleMoveM2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew_m_32.png"))); // NOI18N
        panShapes.add(toggleMoveM2);
        toggleMoveM2.setBounds(40, 40, 40, 40);

        bgDrawingTools2.add(toggleMoveN2);
        toggleMoveN2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew_n_32.png"))); // NOI18N
        panShapes.add(toggleMoveN2);
        toggleMoveN2.setBounds(80, 40, 40, 40);

        bgDrawingTools2.add(toggleBSpline2);
        toggleBSpline2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-DrawingBSpline.png"))); // NOI18N
        panShapes.add(toggleBSpline2);
        toggleBSpline2.setBounds(120, 40, 40, 40);

        tabbedDrawTools2.addTab("Shapes", panShapes);

        panDraft.setLayout(null);

        btnEye2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_eye.png"))); // NOI18N
        btnEye2.setToolTipText("Eye");
        btnEye2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEye2ActionPerformed(evt);
            }
        });
        panDraft.add(btnEye2);
        btnEye2.setBounds(0, 0, 40, 40);

        btnImageUp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew8_32.png"))); // NOI18N
        btnImageUp2.setToolTipText("");
        btnImageUp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageUp2ActionPerformed(evt);
            }
        });
        panDraft.add(btnImageUp2);
        btnImageUp2.setBounds(40, 0, 40, 40);

        btnOpenImage2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_app_kpaint.png"))); // NOI18N
        btnOpenImage2.setToolTipText("Open an image");
        btnOpenImage2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenImage2ActionPerformed(evt);
            }
        });
        panDraft.add(btnOpenImage2);
        btnOpenImage2.setBounds(80, 0, 40, 40);

        btnClearImage2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_app_windows_users.png"))); // NOI18N
        btnClearImage2.setToolTipText("Clear");
        btnClearImage2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearImage2ActionPerformed(evt);
            }
        });
        panDraft.add(btnClearImage2);
        btnClearImage2.setBounds(120, 0, 40, 40);

        btnImageLeft2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew4_32.png"))); // NOI18N
        btnImageLeft2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageLeft2ActionPerformed(evt);
            }
        });
        panDraft.add(btnImageLeft2);
        btnImageLeft2.setBounds(0, 40, 40, 40);

        btnImageCenter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew5_32.png"))); // NOI18N
        btnImageCenter2.setToolTipText("Center");
        btnImageCenter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageCenter2ActionPerformed(evt);
            }
        });
        panDraft.add(btnImageCenter2);
        btnImageCenter2.setBounds(40, 40, 40, 40);

        btnImageRight2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew6_32.png"))); // NOI18N
        btnImageRight2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageRight2ActionPerformed(evt);
            }
        });
        panDraft.add(btnImageRight2);
        btnImageRight2.setBounds(80, 40, 40, 40);

        btnImageBottom2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/fnew2_32.png"))); // NOI18N
        btnImageBottom2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageBottom2ActionPerformed(evt);
            }
        });
        panDraft.add(btnImageBottom2);
        btnImageBottom2.setBounds(40, 80, 40, 40);

        tabbedDrawTools2.addTab("Draft", panDraft);

        panControls.setLayout(null);

        bgLayers2.add(toggleSeeOneLayer2);
        toggleSeeOneLayer2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_layers_just_one.png"))); // NOI18N
        toggleSeeOneLayer2.setSelected(true);
        toggleSeeOneLayer2.setToolTipText("One layer");
        panControls.add(toggleSeeOneLayer2);
        toggleSeeOneLayer2.setBounds(0, 0, 40, 40);

        bgLayers2.add(toggleSeeLayers2);
        toggleSeeLayers2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_layers_three.png"))); // NOI18N
        toggleSeeLayers2.setToolTipText("All layers");
        panControls.add(toggleSeeLayers2);
        toggleSeeLayers2.setBounds(40, 0, 40, 40);

        btnDrawingCopy2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_action_editcopy.png"))); // NOI18N
        btnDrawingCopy2.setToolTipText("Copy");
        panControls.add(btnDrawingCopy2);
        btnDrawingCopy2.setBounds(80, 0, 40, 40);

        btnDrawingPaste2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Crystal_Clear_action_editpaste.png"))); // NOI18N
        btnDrawingPaste2.setToolTipText("Paste");
        panControls.add(btnDrawingPaste2);
        btnDrawingPaste2.setBounds(120, 0, 40, 40);

        btnMagicWand2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/magic-wand.png"))); // NOI18N
        btnMagicWand2.setToolTipText("Magic wand");
        panControls.add(btnMagicWand2);
        btnMagicWand2.setBounds(0, 40, 40, 40);

        btnPara2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_parallele.png"))); // NOI18N
        btnPara2.setToolTipText("Parallel");
        panControls.add(btnPara2);
        btnPara2.setBounds(40, 40, 40, 40);

        btnPerp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32_perpendiculaire.png"))); // NOI18N
        btnPerp2.setToolTipText("Perpendicular");
        panControls.add(btnPerp2);
        btnPerp2.setBounds(80, 40, 40, 40);

        lblAlphaLayer2.setText("Layer transparency :");
        panControls.add(lblAlphaLayer2);
        lblAlphaLayer2.setBounds(10, 90, 150, 16);
        panControls.add(slideAlphaLayer2);
        slideAlphaLayer2.setBounds(0, 110, 160, 11);

        lblDisplaySize2.setText("Display size :");
        panControls.add(lblDisplaySize2);
        lblDisplaySize2.setBounds(10, 130, 150, 16);
        panControls.add(slideDisplaySize2);
        slideDisplaySize2.setBounds(0, 150, 160, 11);

        lblAlphaImage2.setText("Image transparency :");
        panControls.add(lblAlphaImage2);
        lblAlphaImage2.setBounds(10, 170, 150, 16);
        panControls.add(slideAlphaImage2);
        slideAlphaImage2.setBounds(0, 190, 160, 11);

        lblImageSize2.setText("Image size :");
        panControls.add(lblImageSize2);
        lblImageSize2.setBounds(10, 210, 150, 16);
        panControls.add(slideImageSize2);
        slideImageSize2.setBounds(0, 230, 160, 11);

        bgDrawingTools2.add(toggleOpTranslate2);
        toggleOpTranslate2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-translate.png"))); // NOI18N
        toggleOpTranslate2.setToolTipText("Translate");
        panControls.add(toggleOpTranslate2);
        toggleOpTranslate2.setBounds(0, 250, 40, 40);

        bgDrawingTools2.add(toggleOpRotation2);
        toggleOpRotation2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-rotate.png"))); // NOI18N
        toggleOpRotation2.setToolTipText("Rotate");
        panControls.add(toggleOpRotation2);
        toggleOpRotation2.setBounds(40, 250, 40, 40);

        bgDrawingTools2.add(toggleOpScale2);
        toggleOpScale2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-scale.png"))); // NOI18N
        toggleOpScale2.setToolTipText("Resize");
        panControls.add(toggleOpScale2);
        toggleOpScale2.setBounds(80, 250, 40, 40);

        bgDrawingTools2.add(toggleOpShear2);
        toggleOpShear2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/AFM-shear.png"))); // NOI18N
        toggleOpShear2.setToolTipText("Shear");
        panControls.add(toggleOpShear2);
        toggleOpShear2.setBounds(120, 250, 40, 40);

        tabbedDrawTools2.addTab("Controls", panControls);

        panSelection.setLayout(null);

        lblSelection2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/32px-Selection.png"))); // NOI18N
        lblSelection2.setText("Transformation :");
        panSelection.add(lblSelection2);
        lblSelection2.setBounds(0, 0, 160, 40);

        spSelection2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spSelection2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        listSelection2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        spSelection2.setViewportView(listSelection2);

        panSelection.add(spSelection2);
        spSelection2.setBounds(0, 40, 160, 146);

        btnDoSelection2.setText("Do transformation");
        panSelection.add(btnDoSelection2);
        btnDoSelection2.setBounds(0, 190, 160, 22);

        tabbedDrawTools2.addTab("Selection", panSelection);

        panBooleanOp.setLayout(null);

        btnBOpIntersect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/intersect.PNG"))); // NOI18N
        panBooleanOp.add(btnBOpIntersect);
        btnBOpIntersect.setBounds(0, 0, 160, 90);

        btnBOpSubstract.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/substract.PNG"))); // NOI18N
        panBooleanOp.add(btnBOpSubstract);
        btnBOpSubstract.setBounds(0, 90, 160, 90);

        btnBOpXOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/XOR.PNG"))); // NOI18N
        panBooleanOp.add(btnBOpXOR);
        btnBOpXOR.setBounds(0, 270, 160, 90);

        btnBOpUnion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/documents/images/union.PNG"))); // NOI18N
        panBooleanOp.add(btnBOpUnion);
        btnBOpUnion.setBounds(0, 180, 160, 90);

        tabbedDrawTools2.addTab("Boolean Op", panBooleanOp);

        ifrDrawTools2.getContentPane().add(tabbedDrawTools2, java.awt.BorderLayout.CENTER);

        lblCoordinates2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblCoordinates2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCoordinates2.setText("0;0");

        tfCommands2.setComponentPopupMenu(popCommands);

        javax.swing.GroupLayout panDisplay2Layout = new javax.swing.GroupLayout(panDisplay2);
        panDisplay2.setLayout(panDisplay2Layout);
        panDisplay2Layout.setHorizontalGroup(
            panDisplay2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDisplay2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panDisplay2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCoordinates2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfCommands2, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                .addContainerGap())
        );
        panDisplay2Layout.setVerticalGroup(
            panDisplay2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panDisplay2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCoordinates2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfCommands2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ifrDrawTools2.getContentPane().add(panDisplay2, java.awt.BorderLayout.NORTH);

        popmCopy.setText("Copy");
        popmCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmCopyActionPerformed(evt);
            }
        });
        popCommands.add(popmCopy);

        popmPaste.setText("Paste");
        popmPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmPasteActionPerformed(evt);
            }
        });
        popCommands.add(popmPaste);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Yggdrasil 1.1");

        splitMain.setDividerLocation(400);
        splitMain.setDividerSize(15);
        splitMain.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitMain.setOneTouchExpandable(true);

        btnRegister.setText("Register");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        btnSmiley.setText("SMILEY");
        btnSmiley.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSmileyActionPerformed(evt);
            }
        });

        btnSend.setText("SEND");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        lblCom.setBackground(new java.awt.Color(153, 153, 153));
        lblCom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCom.setOpaque(true);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setViewportView(tpChatChannel);

        jScrollPane4.setViewportView(tpChatEntry);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSmiley, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lblCom, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnRegister)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSmiley)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCom, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout paneChatLayout = new javax.swing.GroupLayout(paneChat);
        paneChat.setLayout(paneChatLayout);
        paneChatLayout.setHorizontalGroup(
            paneChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        paneChatLayout.setVerticalGroup(
            paneChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
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

        tabbedMainFunctions.addTab("ASS", tabStudio);

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

        javax.swing.GroupLayout deskDrawingLayout = new javax.swing.GroupLayout(deskDrawing);
        deskDrawing.setLayout(deskDrawingLayout);
        deskDrawingLayout.setHorizontalGroup(
            deskDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 846, Short.MAX_VALUE)
        );
        deskDrawingLayout.setVerticalGroup(
            deskDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout tabDrawingLayout = new javax.swing.GroupLayout(tabDrawing);
        tabDrawing.setLayout(tabDrawingLayout);
        tabDrawingLayout.setHorizontalGroup(
            tabDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(deskDrawing)
        );
        tabDrawingLayout.setVerticalGroup(
            tabDrawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(deskDrawing)
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
        avPanel.setStart(Time.create(0L));
        avPanel.setStop(Time.create(0L));
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayActionPerformed

    private void btnVideoPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPauseActionPerformed
        avPanel.setStart(Time.create(0L));
        avPanel.setStop(Time.create(0L));
        avPanel.audioVideoPause();
    }//GEN-LAST:event_btnVideoPauseActionPerformed

    private void btnVideoStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoStopActionPerformed
        avPanel.setStart(Time.create(0L));
        avPanel.setStop(Time.create(0L));
        avPanel.audioVideoStop();
    }//GEN-LAST:event_btnVideoStopActionPerformed

    private void btnVideoPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayBeforeActionPerformed
        end = fp.getAreaStartTime();
        start = Time.substract(end, Time.create(500L));
        avPanel.setStart(start);
        avPanel.setStop(end);
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayBeforeActionPerformed

    private void btnVideoPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayBeginActionPerformed
        start = fp.getAreaStartTime();
        end = Time.addition(start, Time.create(500L));
        avPanel.setStart(start);
        avPanel.setStop(end);
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayBeginActionPerformed

    private void btnVideoPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayAreaActionPerformed
        start = fp.getAreaStartTime();
        end = fp.getAreaEndTime();
        avPanel.setStart(start);
        avPanel.setStop(end);
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayAreaActionPerformed

    private void btnVideoPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayEndActionPerformed
        end = fp.getAreaEndTime();
        start = Time.substract(end, Time.create(500L));
        avPanel.setStart(start);
        avPanel.setStop(end);
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayEndActionPerformed

    private void btnVideoPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVideoPlayAfterActionPerformed
        start = fp.getAreaEndTime();
        end = Time.addition(start, Time.create(500L));
        avPanel.setStart(start);
        avPanel.setStop(end);
        avPanel.audioVideoPlay();
    }//GEN-LAST:event_btnVideoPlayAfterActionPerformed

    private void btnAudioPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.play(Time.create(0L), Time.create(0L));
        }
    }//GEN-LAST:event_btnAudioPlayActionPerformed

    private void btnAudioPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPauseActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.pause();
        }
    }//GEN-LAST:event_btnAudioPauseActionPerformed

    private void btnAudioStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioStopActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            aw.stop();
        }
    }//GEN-LAST:event_btnAudioStopActionPerformed

    private void btnAudioPlayBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayBeforeActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStop = aw.getTimeOfStartArea();
            Time msStart = Time.substract(msStop, Time.create(500L));
            aw.play(msStart, msStop);
        }
    }//GEN-LAST:event_btnAudioPlayBeforeActionPerformed

    private void btnAudioPlayBeginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayBeginActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStartArea();
            Time msStop = Time.addition(msStart, Time.create(500L));
            aw.play(msStart, msStop);
        }
    }//GEN-LAST:event_btnAudioPlayBeginActionPerformed

    private void btnAudioPlayAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayAreaActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStartArea();
            Time msStop = aw.getTimeOfStopArea();
            aw.play(msStart, msStop);
        }
    }//GEN-LAST:event_btnAudioPlayAreaActionPerformed

    private void btnAudioPlayEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayEndActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStop = aw.getTimeOfStopArea();
            Time msStart = Time.substract(msStop, Time.create(500L));
            aw.play(msStart, msStop);
        }
    }//GEN-LAST:event_btnAudioPlayEndActionPerformed

    private void btnAudioPlayAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioPlayAfterActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            Time msStart = aw.getTimeOfStopArea();
            Time msStop = Time.addition(msStart, Time.create(500L));
            aw.play(msStart, msStop);
        }
    }//GEN-LAST:event_btnAudioPlayAfterActionPerformed

    private void btnAudioAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudioAcceptActionPerformed
        if(aw != null && aw.getVideoFilePath() != null){
            start = aw.getTimeOfStartArea();
            end = aw.getTimeOfStopArea();
            changeTime(start, end);
        }
    }//GEN-LAST:event_btnAudioAcceptActionPerformed

    private void mnuFileYGGYAudioOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYAudioOpenActionPerformed
        // See yggdrasil.fcfilefiter.SoundsFileFilter
        int z = fcAudio.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            openAudio(fcAudio.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYAudioOpenActionPerformed

    private void mnuFileYGGYAVOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYAVOpenActionPerformed
        // See yggdrasil.fcfilefiter.MoviesFileFilter
        int z = fcVideo.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            openAudioVideo(fcVideo.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYAVOpenActionPerformed

    private void mnuFileYGGYVideoOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYVideoOpenActionPerformed
        // See yggdrasil.fcfilefiter.MoviesFileFilter
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
        refreshTempASS();
    }//GEN-LAST:event_btnCmdStyleAddActionPerformed

    private void btnCmdStyleChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCmdStyleChangeActionPerformed
        if(tableOne.getSelectedRow() != -1){
            Event nv = getFromAssSubCommands();
            dtmASS.changeEventAt(nv, tableOne.getSelectedRow());
            tableOne.updateUI();
            refreshTempASS();
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
            refreshTempASS();
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
            refreshTempASS();
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
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            loadASSTable(fcASS.getSelectedFile());
            
        }
    }//GEN-LAST:event_mnuFileYGGYOpenASSActionPerformed

    private void mnuFileYGGYSaveASSAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYSaveASSAsActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
        int z = fcASS.showSaveDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            saveASSTable(fcASS.getSelectedFile());
        }
    }//GEN-LAST:event_mnuFileYGGYSaveASSAsActionPerformed

    private void mnuFileYGGYSaveASSAgainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFileYGGYSaveASSAgainActionPerformed
        // See yggdrasil.fcfilefiter.SubtitlesFileFilter
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

    private void btnDrawNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawNewActionPerformed
        // Drawing tools (Create BLANK drawing area)
        skp.createNewDrawing();
        dlmHistoric.clear();
        tfDrawingCommands.setText(skp.getAssCommands());
    }//GEN-LAST:event_btnDrawNewActionPerformed

    private void btnDrawOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawOpenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawOpenActionPerformed

    private void btnDrawSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawSaveActionPerformed

    private void btnDrawFontOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawFontOpenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawFontOpenActionPerformed

    private void btnEyeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEyeActionPerformed
        // Drawing tools (Open a background image from video)
        if(ffss.hasVideo() == true){            
            try {
                try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video)) {
                    grabber.start();
                    
                    boolean found = false;
                    Frame fr = null;
                    
                    while(found == false){
                        fr = grabber.grabImage();
                        
                        if(grabber.getFrameNumber() >= Time.getFrame(aw.getTimeOfStartArea(), grabber.getFrameRate())){
                            found = true;
                        }
                    }
                    
                    try{
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        ImageIcon icon = new ImageIcon(converter.getBufferedImage(fr));
                        skp.setDrawingBackgroundImage(icon);
                    }catch(Exception ex){
                        System.err.println("Error while converting to image!");
                    }
                    
                    grabber.stop();
                    grabber.release();    
                }
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnEyeActionPerformed

    private void btnOpenImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenImageActionPerformed
        // Drawing tools (Open a background image)
        // See yggdrasil.fcfilefiter.ImagesFileFilter
        int z = fcDrawingImages.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            skp.setDrawingBackgroundImage(new ImageIcon(fcDrawingImages.getSelectedFile().getPath()));
        }
    }//GEN-LAST:event_btnOpenImageActionPerformed

    private void btnClearImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImageActionPerformed
        // Drawing tools (Clear a background image)
        if(skp.hasDrawingBackgroundImage() == true){
            int z = JOptionPane.showConfirmDialog(this, 
                    chosen.getTranslated(
                            "btnClearImageMessage",
                            wantedIso,
                            "Do you really want to clear the background image?"),
                    chosen.getTranslated(
                            "btnClearImageTitle",
                            wantedIso,
                            "Remove background image"),
                    JOptionPane.YES_NO_OPTION
            );
            if(z == JOptionPane.YES_OPTION){
                skp.setDrawingBackgroundImage(null);
            }
        }        
    }//GEN-LAST:event_btnClearImageActionPerformed

    private void btnImageUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageUpActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(0, -10);
        }        
    }//GEN-LAST:event_btnImageUpActionPerformed

    private void btnImageLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageLeftActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(-10, 0);
        }        
    }//GEN-LAST:event_btnImageLeftActionPerformed

    private void btnImageCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageCenterActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.centerDrawingBackgroundImage();
        }
    }//GEN-LAST:event_btnImageCenterActionPerformed

    private void btnImageRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageRightActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(10, 0);
        }        
    }//GEN-LAST:event_btnImageRightActionPerformed

    private void btnImageBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageBottomActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(0, 10);
        }        
    }//GEN-LAST:event_btnImageBottomActionPerformed

    private void btnHistoricUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricUndoActionPerformed
        int size = skp.getMemories().size();
        if(size < 1) return;
        int lastUndoIndex = -1;
        for(int i = size - 1; i >= 1; i--){
            if(skp.getMemories().get(i).isUndo() == true){
                lastUndoIndex = i;
            }
        }
        int index = lastUndoIndex == -1 ? size - 1 : lastUndoIndex - 1;
        skp.getMemories().get(index).setUndo(true);
        skp.refreshDrawingAfterUndo(skp.getMemories().get(index));
        dlmHistoric.clear();
        dlmHistoric.addAll(skp.getMemories());
        tfDrawingCommands.setText(skp.getAssCommands());
    }//GEN-LAST:event_btnHistoricUndoActionPerformed

    private void btnHistoricRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricRedoActionPerformed
        int size = skp.getMemories().size();
        if(size == 0) return;
        int lastUndoIndex = -1;
        for(int i = size - 1; i >= 0; i--){
            if(skp.getMemories().get(i).isUndo() == true){
                lastUndoIndex = i;
            }
        }
        if(lastUndoIndex == -1) return;
        skp.getMemories().get(lastUndoIndex).setUndo(false);
        skp.refreshDrawingAfterRedo(skp.getMemories().get(lastUndoIndex));
        dlmHistoric.clear();
        dlmHistoric.addAll(skp.getMemories());
        tfDrawingCommands.setText(skp.getAssCommands());
    }//GEN-LAST:event_btnHistoricRedoActionPerformed

    private void btnHistoricAddLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricAddLayerActionPerformed
        addLayersGroup();
    }//GEN-LAST:event_btnHistoricAddLayerActionPerformed

    private void btnHistoricRemoveLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoricRemoveLayerActionPerformed
        if(treeHistoricLayers.getSelectionCount() > 0){
            deleteLayersGroup();
        }
    }//GEN-LAST:event_btnHistoricRemoveLayerActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        // Your Machine to upload to database
        String surname = JOptionPane.showInputDialog(this, "Type your surname:");
        String codename = JOptionPane.showInputDialog(this, "Type your codename to help identification:");
        String password = JOptionPane.showInputDialog(this, "Type your password for account manipulation:");
        Machine m = Machine.createMachine(surname, password);
        m.setCodename(codename);
        
        // Trying to save your data to database
        // TODO Set server url
        Machine server = m;
        try {
            Client.save(server, m);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnSmileyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSmileyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSmileyActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSendActionPerformed

    private void popmCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmCopyActionPerformed
        Clipboard.CCopy(tfCommands2.getText());
    }//GEN-LAST:event_popmCopyActionPerformed

    private void popmPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popmPasteActionPerformed
        String s = Clipboard.CPaste();
        if(s.isEmpty() == false){
            if(tfCommands2.getSelectedText() != null){
                String opening = tfCommands2.getText().substring(0, tfCommands2.getSelectionStart());
                String ending = tfCommands2.getText().substring(tfCommands2.getSelectionEnd()+1);
                tfCommands2.setText(opening + s + ending);
            }else{
                tfCommands2.setText(s);
            }
        }        
    }//GEN-LAST:event_popmPasteActionPerformed

    private void btnDrawNew2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawNew2ActionPerformed
        // Drawing tools (Create BLANK drawing area)
        skp.createNewDrawing();
        dlmHistoric.clear();
        tfCommands2.setText(skp.getAssCommands());
    }//GEN-LAST:event_btnDrawNew2ActionPerformed

    private void btnDrawOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawOpen2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawOpen2ActionPerformed

    private void btnDrawSave2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawSave2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawSave2ActionPerformed

    private void btnDrawFontOpen2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrawFontOpen2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDrawFontOpen2ActionPerformed

    private void btnEye2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEye2ActionPerformed
        // Drawing tools (Open a background image from video)
        if(ffss.hasVideo() == true){            
            try {
                try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video)) {
                    grabber.start();
                    
                    boolean found = false;
                    Frame fr = null;
                    
                    while(found == false){
                        fr = grabber.grabImage();
                        
                        if(grabber.getFrameNumber() >= Time.getFrame(aw.getTimeOfStartArea(), grabber.getFrameRate())){
                            found = true;
                        }
                    }
                    
                    try{
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        ImageIcon icon = new ImageIcon(converter.getBufferedImage(fr));
                        skp.setDrawingBackgroundImage(icon);
                    }catch(Exception ex){
                        System.err.println("Error while converting to image!");
                    }
                    
                    grabber.stop();
                    grabber.release();    
                }
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnEye2ActionPerformed

    private void btnImageUp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageUp2ActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(0, -10);
        }
    }//GEN-LAST:event_btnImageUp2ActionPerformed

    private void btnOpenImage2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenImage2ActionPerformed
        // Drawing tools (Open a background image)
        // See yggdrasil.fcfilefiter.ImagesFileFilter
        int z = fcDrawingImages.showOpenDialog(this);
        if(z == JFileChooser.APPROVE_OPTION){
            skp.setDrawingBackgroundImage(new ImageIcon(fcDrawingImages.getSelectedFile().getPath()));
        }
    }//GEN-LAST:event_btnOpenImage2ActionPerformed

    private void btnClearImage2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImage2ActionPerformed
        // Drawing tools (Clear a background image)
        if(skp.hasDrawingBackgroundImage() == true){
            int z = JOptionPane.showConfirmDialog(this, 
                    chosen.getTranslated(
                            "btnClearImageMessage",
                            wantedIso,
                            "Do you really want to clear the background image?"),
                    chosen.getTranslated(
                            "btnClearImageTitle",
                            wantedIso,
                            "Remove background image"),
                    JOptionPane.YES_NO_OPTION
            );
            if(z == JOptionPane.YES_OPTION){
                skp.setDrawingBackgroundImage(null);
            }
        }
    }//GEN-LAST:event_btnClearImage2ActionPerformed

    private void btnImageLeft2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageLeft2ActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(-10, 0);
        }
    }//GEN-LAST:event_btnImageLeft2ActionPerformed

    private void btnImageCenter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageCenter2ActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.centerDrawingBackgroundImage();
        }
    }//GEN-LAST:event_btnImageCenter2ActionPerformed

    private void btnImageRight2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageRight2ActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(10, 0);
        }
    }//GEN-LAST:event_btnImageRight2ActionPerformed

    private void btnImageBottom2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageBottom2ActionPerformed
        if(skp.hasDrawingBackgroundImage()){
            skp.setTranslateBackImage(0, 10);
        }
    }//GEN-LAST:event_btnImageBottom2ActionPerformed

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
    private javax.swing.ButtonGroup bgDrawingTools;
    private javax.swing.ButtonGroup bgDrawingTools2;
    private javax.swing.ButtonGroup bgLayers2;
    private javax.swing.JButton btnAudioAccept;
    private javax.swing.JButton btnAudioPause;
    private javax.swing.JButton btnAudioPlay;
    private javax.swing.JButton btnAudioPlayAfter;
    private javax.swing.JButton btnAudioPlayArea;
    private javax.swing.JButton btnAudioPlayBefore;
    private javax.swing.JButton btnAudioPlayBegin;
    private javax.swing.JButton btnAudioPlayEnd;
    private javax.swing.JButton btnAudioStop;
    private javax.swing.JButton btnBOpIntersect;
    private javax.swing.JButton btnBOpSubstract;
    private javax.swing.JButton btnBOpUnion;
    private javax.swing.JButton btnBOpXOR;
    private javax.swing.JButton btnClearImage;
    private javax.swing.JButton btnClearImage2;
    private javax.swing.JButton btnCmdStyleAdd;
    private javax.swing.JButton btnCmdStyleAfter;
    private javax.swing.JButton btnCmdStyleBefore;
    private javax.swing.JButton btnCmdStyleChange;
    private javax.swing.JButton btnCmdStyleParam;
    private javax.swing.JButton btnCmdStyleProperties;
    private javax.swing.JButton btnDoSelection2;
    private javax.swing.JButton btnDrawFontOpen;
    private javax.swing.JButton btnDrawFontOpen2;
    private javax.swing.JButton btnDrawNew;
    private javax.swing.JButton btnDrawNew2;
    private javax.swing.JButton btnDrawOpen;
    private javax.swing.JButton btnDrawOpen2;
    private javax.swing.JButton btnDrawSave;
    private javax.swing.JButton btnDrawSave2;
    private javax.swing.JButton btnDrawingCopy;
    private javax.swing.JButton btnDrawingCopy2;
    private javax.swing.JButton btnDrawingPaste;
    private javax.swing.JButton btnDrawingPaste2;
    private javax.swing.JButton btnEye;
    private javax.swing.JButton btnEye2;
    private javax.swing.JButton btnHistoricAddLayer;
    private javax.swing.JButton btnHistoricRedo;
    private javax.swing.JButton btnHistoricRemoveLayer;
    private javax.swing.JButton btnHistoricUndo;
    private javax.swing.JButton btnImageBottom;
    private javax.swing.JButton btnImageBottom2;
    private javax.swing.JButton btnImageCenter;
    private javax.swing.JButton btnImageCenter2;
    private javax.swing.JButton btnImageLeft;
    private javax.swing.JButton btnImageLeft2;
    private javax.swing.JButton btnImageRight;
    private javax.swing.JButton btnImageRight2;
    private javax.swing.JButton btnImageUp;
    private javax.swing.JButton btnImageUp2;
    private javax.swing.JButton btnMagicWand;
    private javax.swing.JButton btnMagicWand2;
    private javax.swing.JButton btnOpenImage;
    private javax.swing.JButton btnOpenImage2;
    private javax.swing.JButton btnPara;
    private javax.swing.JButton btnPara2;
    private javax.swing.JButton btnPerp;
    private javax.swing.JButton btnPerp2;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnSeeLayers;
    private javax.swing.JButton btnSeeOneLayer;
    private javax.swing.JButton btnSelection;
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
    private javax.swing.JComboBox<String> comboHistoricLayers;
    private javax.swing.JComboBox<String> comboName;
    private javax.swing.JComboBox<String> comboStyle;
    private javax.swing.JDesktopPane deskDrawing;
    private javax.swing.JDesktopPane deskYGGY;
    private javax.swing.JFileChooser fcASS;
    private javax.swing.JFileChooser fcAudio;
    private javax.swing.JFileChooser fcDrawingImages;
    private javax.swing.JFileChooser fcDrawingYDS;
    private javax.swing.JFileChooser fcVideo;
    private javax.swing.JInternalFrame ifrAssTableCommands;
    private javax.swing.JInternalFrame ifrDrawTools;
    private javax.swing.JInternalFrame ifrDrawTools2;
    private javax.swing.JInternalFrame ifrHistoricLayers;
    private javax.swing.JInternalFrame ifrSketchpad;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
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
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblAlphaImage;
    private javax.swing.JLabel lblAlphaImage2;
    private javax.swing.JLabel lblAlphaLayer;
    private javax.swing.JLabel lblAlphaLayer2;
    private javax.swing.JLabel lblCom;
    private javax.swing.JLabel lblCoordinates2;
    private javax.swing.JLabel lblDisplaySize;
    private javax.swing.JLabel lblDisplaySize2;
    private javax.swing.JLabel lblDrawingCoordinates;
    private javax.swing.JLabel lblDrawingCtrls;
    private javax.swing.JLabel lblDrawingFile;
    private javax.swing.JLabel lblDrawingImage;
    private javax.swing.JLabel lblDrawingTools;
    private javax.swing.JLabel lblHistoricGroupLayers;
    private javax.swing.JLabel lblImageSize;
    private javax.swing.JLabel lblImageSize2;
    private javax.swing.JLabel lblSelection2;
    private javax.swing.JList<String> listHistoric;
    private javax.swing.JList<String> listSelection2;
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
    private javax.swing.JPanel panBooleanOp;
    private javax.swing.JPanel panControls;
    private javax.swing.JPanel panDisplay2;
    private javax.swing.JPanel panDraft;
    private javax.swing.JPanel panFile;
    private javax.swing.JPanel panSelection;
    private javax.swing.JPanel panShapes;
    private javax.swing.JPanel paneChat;
    private javax.swing.JPanel paneControls;
    private javax.swing.JPanel paneEmbedWave;
    private javax.swing.JPanel panePTP;
    private javax.swing.JPanel paneTimeline;
    private javax.swing.JPanel paneVideo;
    private javax.swing.JPopupMenu popCommands;
    private javax.swing.JMenuItem popmCopy;
    private javax.swing.JMenuItem popmPaste;
    private javax.swing.JSlider slideAlphaImage;
    private javax.swing.JSlider slideAlphaImage2;
    private javax.swing.JSlider slideAlphaLayer;
    private javax.swing.JSlider slideAlphaLayer2;
    private javax.swing.JSlider slideDisplaySize;
    private javax.swing.JSlider slideDisplaySize2;
    private javax.swing.JSlider slideImageSize;
    private javax.swing.JSlider slideImageSize2;
    private javax.swing.JScrollPane spSelection2;
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
    private javax.swing.JTabbedPane tabbedDrawTools2;
    private javax.swing.JTabbedPane tabbedMainFunctions;
    private javax.swing.JTabbedPane tabbedOptions;
    private javax.swing.JTabbedPane tabbedSubsCmd;
    private javax.swing.JTable tableOne;
    private javax.swing.JTextField tfCommands2;
    private javax.swing.JTextField tfDrawingCommands;
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
    private javax.swing.JToggleButton toggleBSpline;
    private javax.swing.JToggleButton toggleBSpline2;
    private javax.swing.JToggleButton toggleCmdCommands;
    private javax.swing.JToggleButton toggleCmdComment;
    private javax.swing.JToggleButton toggleCmdDialogue;
    private javax.swing.JToggleButton toggleCmdMovie;
    private javax.swing.JToggleButton toggleCmdPicture;
    private javax.swing.JToggleButton toggleCmdProposal;
    private javax.swing.JToggleButton toggleCmdRequest;
    private javax.swing.JToggleButton toggleCmdSound;
    private javax.swing.JToggleButton toggleCubic;
    private javax.swing.JToggleButton toggleCubic2;
    private javax.swing.JToggleButton toggleCursor;
    private javax.swing.JToggleButton toggleCursor2;
    private javax.swing.JToggleButton toggleGLock;
    private javax.swing.JToggleButton toggleGLock2;
    private javax.swing.JToggleButton toggleLine;
    private javax.swing.JToggleButton toggleLine2;
    private javax.swing.JToggleButton toggleMoveM;
    private javax.swing.JToggleButton toggleMoveM2;
    private javax.swing.JToggleButton toggleMoveN;
    private javax.swing.JToggleButton toggleMoveN2;
    private javax.swing.JToggleButton toggleOpRotation;
    private javax.swing.JToggleButton toggleOpRotation2;
    private javax.swing.JToggleButton toggleOpScale;
    private javax.swing.JToggleButton toggleOpScale2;
    private javax.swing.JToggleButton toggleOpShear;
    private javax.swing.JToggleButton toggleOpShear2;
    private javax.swing.JToggleButton toggleOpTranslate;
    private javax.swing.JToggleButton toggleOpTranslate2;
    private javax.swing.JToggleButton toggleQuadratic;
    private javax.swing.JToggleButton toggleQuadratic2;
    private javax.swing.JToggleButton toggleSeeLayers2;
    private javax.swing.JToggleButton toggleSeeOneLayer2;
    private javax.swing.JToggleButton toggleYvesKaraoke;
    private javax.swing.JTextPane tpChatChannel;
    private javax.swing.JTextPane tpChatEntry;
    private javax.swing.JTextPane tpText;
    private javax.swing.JTree treeHistoricLayers;
    // End of variables declaration//GEN-END:variables
}
