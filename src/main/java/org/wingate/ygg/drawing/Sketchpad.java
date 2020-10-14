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
package org.wingate.ygg.drawing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.util.DrawColor;

/**
 *
 * @author util2
 */
public class Sketchpad extends JScrollPane {
    
    MainFrame mf;    
    
    // Size (minimum)
    int widthSize = 500;
    int heightSize = 500;
    
    // Drawing components
    Sheet sheet = new Sheet();
    SheetHBorder hrule;
    SheetVBorder vrule;
    
    // Image offset
    int offsetX = 0;
    int offsetY = 0;
    
    // Historic and layers
    List<Memories<?>> memoriesArray = new ArrayList<>();
    Point2D beforeEditingPoint = null;
    Point2D afterEditingPoint = null;
    List<AShape> editingShapes = new ArrayList<>();
    
    // While editing a point and finally a shape
    boolean editing = false;
    boolean editingButtonUsed = false; // <- trigger on mouse button 2
    Point2D editingPoint2DTemp = null;
    
    // Aide à repérer le point d'insertion (ajout)
    Point lastClicked = null;

    public Sketchpad(MainFrame mf) {
        this.mf = mf;
        init();
    }
    
    private void init(){
        setViewportView(sheet);
        sheet.setPreferredSize(new java.awt.Dimension(widthSize, heightSize));
        sheet.revalidate();        
        
        hrule = new SheetHBorder(widthSize);
        vrule = new SheetVBorder(heightSize);        
        setColumnHeaderView(hrule);
        setRowHeaderView(vrule);
        setOpaque(true);
        setBackground(MainFrame.isDark() ? new Color(70,70,70) : Color.white);
        
        addMouseListener(new MouseAdapter() {
            
            // <editor-fold defaultstate="collapsed" desc="mouseClicked">
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    
                    Point pCurrent = new Point(
                            e.getPoint().x - vrule.getWidth() + getHorizontalScrollBar().getValue(),
                            e.getPoint().y - hrule.getHeight() + getVerticalScrollBar().getValue()
                    );
                    
                    pCurrent = autoGridLocker(pCurrent);

                    if(mf.isLineSelected()){
                        rerouteElement(); // Undoable now are broken!
                        addElement(
                                memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                pCurrent,
                                Line.class,
                                Line.create(memoriesArray.isEmpty() ? pCurrent : lastClicked, pCurrent)
                        );
                        lastClicked = pCurrent;
                    }else if(mf.isCubicSelected()){
                        rerouteElement(); // Undoable now are broken!
                        addElement(
                                memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                pCurrent,
                                Bezier.class,
                                Bezier.createCubic(memoriesArray.isEmpty() ? pCurrent : lastClicked, pCurrent)
                        );
                        lastClicked = pCurrent;
                    }else if(mf.isQuadraticSelected()){
                        rerouteElement(); // Undoable now are broken!
                        addElement(
                                memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                pCurrent,
                                Bezier.class,
                                Bezier.createQuadratic(memoriesArray.isEmpty() ? pCurrent : lastClicked, pCurrent)
                        );
                        lastClicked = pCurrent;
                    }else if(mf.isMoveMSelected()){
                        rerouteElement(); // Undoable now are broken!
                        addElement(
                                memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                pCurrent,
                                Move.class,
                                Move.create(memoriesArray.isEmpty() ? pCurrent : lastClicked, true)
                        );
                        lastClicked = pCurrent;
                    }else if(mf.isMoveNSelected()){
                        rerouteElement(); // Undoable now are broken!
                        addElement(
                                memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                pCurrent,
                                Move.class,
                                Move.create(memoriesArray.isEmpty() ? pCurrent : lastClicked, false)
                        );
                        lastClicked = pCurrent;
                    }else if(mf.isBSplineSelected()){
                        if(memoriesArray.isEmpty() == false 
                                && memoriesArray.get(memoriesArray.size() - 1).getObject() instanceof BSpline){
                            // Si on a une BSpline en cours de dessin
                            ((BSpline)memoriesArray.get(memoriesArray.size() - 1).getObject()).addPoint(pCurrent);
                        }else{
                            BSpline bs = new BSpline();
                            if(memoriesArray.isEmpty() == false){
                                bs.addPoint(lastClicked);
                            }                            
                            bs.addPoint(pCurrent);
                            rerouteElement(); // Undoable now are broken!
                            addElement(
                                    memoriesArray.isEmpty() ? pCurrent : lastClicked, 
                                    pCurrent,
                                    BSpline.class,
                                    bs
                            );
                        }
                        lastClicked = pCurrent;
                    }
                    sheet.updateDrawing();
                }
                
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="mousePressed">
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    editingButtonUsed = true;
                    
                    //==========================================================
                    // Prepare la modification d'un point
                    //----------------------------------------------------------
                    Point pCurrent = new Point(
                            e.getPoint().x - vrule.getWidth() + getHorizontalScrollBar().getValue(),
                            e.getPoint().y - hrule.getHeight() + getVerticalScrollBar().getValue()
                    );

                    pCurrent = autoGridLocker(pCurrent);
                    
                    beforeEditingPoint = pCurrent;
                    editingShapes.clear();
                    //==========================================================
                }
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="mouseReleased">
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    
                    editingButtonUsed = false;
                    
                    //==========================================================
                    // Clôt la modification d'un point
                    //----------------------------------------------------------
                    Point pCurrent = new Point(
                            e.getPoint().x - vrule.getWidth() + getHorizontalScrollBar().getValue(),
                            e.getPoint().y - hrule.getHeight() + getVerticalScrollBar().getValue()
                    );

                    pCurrent = autoGridLocker(pCurrent);
                    
                    afterEditingPoint = pCurrent;
                    
                    if(beforeEditingPoint.distance(afterEditingPoint) != 0d){
                        HistoricalPointChange hpc = null;
                        if(editingShapes.size() == 2){
                            AShape sh1 = editingShapes.get(0);
                            AShape sh2 = editingShapes.get(1);
                            hpc = new HistoricalPointChange(sh1, sh2, beforeEditingPoint, afterEditingPoint);
                        }else if(editingShapes.size() == 1){
                            AShape sh = editingShapes.get(0);
                            hpc = new HistoricalPointChange(sh, beforeEditingPoint, afterEditingPoint);                            
                        }
                        if(hpc != null){
                            addElement(beforeEditingPoint, afterEditingPoint, HistoricalPointChange.class, hpc);
                        }                        
                    }                    
                    //==========================================================
                }
            }
            // </editor-fold>
            
        });

        
        addMouseMotionListener(new MouseMotionListener() {
            
            // <editor-fold defaultstate="collapsed" desc="mouseMoved">
            @Override
            public void mouseMoved(MouseEvent e) {
                Point pCurrent = new Point(
                        e.getPoint().x - vrule.getWidth() + getHorizontalScrollBar().getValue(),
                        e.getPoint().y - hrule.getHeight() + getVerticalScrollBar().getValue()
                );

                pCurrent = autoGridLocker(pCurrent);
                
                editing = mf.isCursorSelected();
                
                if(editing == true){
                    // Traitement de modification (avant médian et après médian)
                    for(Memories<?> mms : memoriesArray){
                        if(mms.getObject().getClass().equals(HistoricalPointChange.class) == true){
                            continue;
                        }
                        AShape sh = (AShape)mms.getObject();
                        if(sh instanceof Line){
                            Line line = (Line)sh;
                            if(isClosePoint(pCurrent, line.getPointA())){
                                editingPoint2DTemp = line.getPointA();
                            }else if(isClosePoint(pCurrent, line.getPointB())){
                                editingPoint2DTemp = line.getPointB();
                            }else{
                                editingPoint2DTemp = null;                                
                            }
                            if(editingPoint2DTemp != null){
                                break;
                            }
                        }else if(sh instanceof Bezier){
                            Bezier bezier = (Bezier)sh;
                            Point2D po = new Point2D.Double(bezier.getXo(), bezier.getYo());
                            Point2D pCpa = new Point2D.Double(bezier.getCpxa(), bezier.getCpya());
                            Point2D pCpb = new Point2D.Double(bezier.getCpxb(), bezier.getCpyb());
                            Point2D pCp = new Point2D.Double(bezier.getCpx(), bezier.getCpy());
                            Point2D p = new Point2D.Double(bezier.getX(), bezier.getY());
                            if(isClosePoint(pCurrent, po)){
                                editingPoint2DTemp = po;
                            }else if(isClosePoint(pCurrent, pCpa) && bezier.isCubic() == true){
                                editingPoint2DTemp = pCpa;
                            }else if(isClosePoint(pCurrent, pCpb) && bezier.isCubic() == true){
                                editingPoint2DTemp = pCpb;
                            }else if(isClosePoint(pCurrent, pCp) && bezier.isQuadratic() == true){
                                editingPoint2DTemp = pCp;
                            }else if(isClosePoint(pCurrent, p)){
                                editingPoint2DTemp = p;
                            }else{
                                editingPoint2DTemp = null;
                            }
                            if(editingPoint2DTemp != null){
                                break;
                            }
                        }else if(sh instanceof Move){
                            Move move = (Move)sh;
                            if(isClosePoint(pCurrent, move.getPointA())){
                                editingPoint2DTemp = move.getPointA();
                            }else{
                                editingPoint2DTemp = null;
                            }
                            if(editingPoint2DTemp != null){
                                break;
                            }
                        }else if(sh instanceof BSpline){
                            BSpline bs = (BSpline)sh;
                            for(int i=0; i<bs.getBSplineCurve().getControlPoints().size(); i++){
                                Point2D p = bs.getBSplineCurve().getControlPoints().get(i);
                                if(isClosePoint(pCurrent, p)){
                                    editingPoint2DTemp = p;
                                    break;
                                }else{
                                    editingPoint2DTemp = null;
                                }
                            }
                            if(editingPoint2DTemp != null){
                                break;
                            }
                        }
                    }
                    
                }
                
                sheet.updateMousePosition(pCurrent.x, pCurrent.y);
                sheet.updateDrawing();
            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="mouseDragged">
            @Override
            public void mouseDragged(MouseEvent e) {
                Point pCurrent = new Point(
                        e.getPoint().x - vrule.getWidth() + getHorizontalScrollBar().getValue(),
                        e.getPoint().y - hrule.getHeight() + getVerticalScrollBar().getValue()
                );

                pCurrent = autoGridLocker(pCurrent);
                
                if(editing == true){
                    if(editingButtonUsed == true && editingPoint2DTemp != null){
                        // Traitement médian modification (pendant)
                        for(Memories<?> mms : memoriesArray){
                            if(mms.getObject().getClass().equals(HistoricalPointChange.class) == true){
                                continue;
                            }
                            AShape sh = (AShape)mms.getObject();
                            boolean state = sh.quickModify(editingPoint2DTemp, pCurrent);
                            if(state == true && editingShapes.contains(sh) == false){
                                editingShapes.add(sh);
                            }                            
                        }
                        if(lastClicked.distance(editingPoint2DTemp) == 0){
                            lastClicked = pCurrent;
                        } 
                        editingPoint2DTemp = pCurrent;
                    }
                    sheet.updateMousePosition(pCurrent.x, pCurrent.y); // Axe souris + aide
                    sheet.updateDrawing(); // repaint
                }
            }
            // </editor-fold>
            
        });
    }
    
    // <editor-fold defaultstate="collapsed" desc="Functions">
    public void createNewDrawing(){
        memoriesArray.clear();
        sheet.updateDrawing();
    }
    
    private boolean isSamePoint(Point2D a, Point2D b){
        return a.distance(b) == 0d;
    }
    
    private boolean isClosePoint(Point2D a, Point2D b){
        return isClosePoint(a, b, 10d);
    }
    
    private boolean isClosePoint(Point2D a, Point2D b, double distance){
        return a.distance(b) < distance;
    }
    
    /**
     * Set a background image for help at drawing task.
     * If <span style="color: blue;"><code>null</code></span> then there is no image.
     * @param img The image to load or blank if <span style="color: blue;"><code>null</code></span>
     */
    public void setDrawingBackgroundImage(ImageIcon img){
        sheet.updateImage(img);
    }
    
    public boolean hasDrawingBackgroundImage(){
        return sheet.hasDrawingBackgroundImage();
    }
    
    public void setTranslateBackImage(int x, int y){
        sheet.updateImagePosition(x, y);
        sheet.repaint();
    }
    
    public void centerDrawingBackgroundImage(){
        sheet.centerDrawingBackgroundImage();
    }
    
    private Point autoGridLocker(Point oldPoint){
        if(mf.isGridLockerSelected()){
            // On rassemble les informations
            int w = sheet.getWidth();
            int h = sheet.getHeight();
            int scale = sheet.getScaleXY();
            
            // On trouve le milieu réel
            int xMiddle = w/scale/2;
            int yMiddle = h/scale/2;
            
            // Variable à retourner
            Point newPoint = oldPoint;
            
            // Variable de contrôle
            boolean hasPoint = false;
            
            // Attribution
            int xSearch = xMiddle;
            int ySearch = yMiddle; 
            
            // On cherche entre 0,0 et -w,-h
            for(int y = ySearch; y >= 0; y -= 25*scale){
                for(int x = xSearch; x >= 0;  x -= 25*scale){
                    Point p = new Point(x, y);
                    if(isClosePoint(p, oldPoint, 25*scale/2) == true){
                        newPoint = p;
                        hasPoint = true;
                        break;
                    }
                }
                if(hasPoint == true){
                    break;
                }
            }
            
            // Continuer ou retourner ?
            if(hasPoint == true){
                return newPoint;
            }
            
            // On cherche entre 0,0 et +w,+h
            for(int y = ySearch; y <= h; y += 25*scale){
                for(int x = xSearch; x <= w;  x += 25*scale){
                    Point p = new Point(x, y);
                    if(isClosePoint(p, oldPoint, 25*scale/2) == true){
                        newPoint = p;
                        hasPoint = true;
                        break;
                    }
                }
                if(hasPoint == true){
                    break;
                }
            }
            
            // Continuer ou retourner ?
            if(hasPoint == true){
                return newPoint;
            }
            
            // On cherche entre 0,0 et -w,+h
            for(int y = ySearch; y <= h; y += 25*scale){
                for(int x = xSearch; x >= 0;  x -= 25*scale){
                    Point p = new Point(x, y);
                    if(isClosePoint(p, oldPoint, 25*scale/2) == true){
                        newPoint = p;
                        hasPoint = true;
                        break;
                    }
                }
                if(hasPoint == true){
                    break;
                }
            }
            
            // Continuer ou retourner ?
            if(hasPoint == true){
                return newPoint;
            }
            
            // On cherche entre 0,0 et +w,-h
            for(int y = ySearch; y >= 0; y -= 25*scale){
                for(int x = xSearch; x <= w;  x += 25*scale){
                    Point p = new Point(x, y);
                    if(isClosePoint(p, oldPoint, 25*scale/2) == true){
                        newPoint = p;
                        hasPoint = true;
                        break;
                    }
                }
                if(hasPoint == true){
                    break;
                }
            }
            
            // Continuer ou retourner ?
            if(hasPoint == true){
                return newPoint;
            }
        }
        return oldPoint;
    }
    
    public void refreshDrawing(){
        sheet.updateDrawing();
    }
    
    public void refreshDrawingAfterUndo(Memories mem){
        if(mem.getObject() instanceof HistoricalPointChange){
            HistoricalPointChange hpc = (HistoricalPointChange)mem.getObject();
            hpc.getShape().quickModify(hpc.getNewLocation(), hpc.getOldLocation());
            if(hpc.hasSecondShape() == true){
                hpc.getAnotherShape().quickModify(hpc.getNewLocation(), hpc.getOldLocation());
            }
        }
        sheet.updateDrawing();
    }
    
    public void refreshDrawingAfterRedo(Memories mem){
        if(mem.getObject() instanceof HistoricalPointChange){
            HistoricalPointChange hpc = (HistoricalPointChange)mem.getObject();
            hpc.getShape().quickModify(hpc.getOldLocation(), hpc.getNewLocation());
            if(hpc.hasSecondShape() == true){
                hpc.getAnotherShape().quickModify(hpc.getOldLocation(), hpc.getNewLocation());
            }
        }
        sheet.updateDrawing();
    }
    
    /**
     * Reroute (to do before addElement)
     * @see addElement
     */
    private void rerouteElement(){
        //======================================================================
        // Check about undo
        //----------------------------------------------------------------------
        int index = -1;
        for(int i=0; i<memoriesArray.size(); i++){
            if(memoriesArray.get(i).isUndo()){
                index = i;
                break;
            }
        }
        if(index != -1){
            for(int i=memoriesArray.size() - 1; i >= 0; i--){
                if(i >= index){
                    if(memoriesArray.get(i).getObject() instanceof AShape){
                        Point2D p = (Point2D)memoriesArray.get(i).getOldState();
                        lastClicked = new Point((int)p.getX(),(int)p.getY());
                    }
                    memoriesArray.remove(i);
                    mf.removeLastFromHistoric();
                }                
            }
        }
    }
    
    public void addElement(Point2D oldPoint, Point2D newPoint, Class cls, Object obj){
        //======================================================================
        // Add element
        //----------------------------------------------------------------------
        Memories mms = new Memories();
        mms.setOldState(oldPoint);
        mms.setNewState(newPoint);
        mms.setObjectClass(cls);
        mms.setObject(obj);
        
        if(obj instanceof AShape){
            if(memoriesArray.isEmpty()){
                // Any AShape replace by a Move (M)                    
                mms.setObjectClass(Move.class);
                mms.setObject(Move.create(newPoint, true));
                memoriesArray.add(mms);
            }else{
                memoriesArray.add(mms);
            }
        }else if(obj instanceof HistoricalPointChange){
            if(memoriesArray.isEmpty() == false){
                memoriesArray.add(mms);
            }
        }
        
        // Mise à jour de l'historique
        mf.addToHistoric(mms);
    }
    
    public List<Memories<?>> getMemories(){
        return memoriesArray;
    }
    
    public List<AShape> getShapes(){
        List<AShape> shapes = new ArrayList<>();
        
        for(Memories mms : memoriesArray){
            if(mms.getObject() instanceof AShape && mms.isUndo() == false){
                shapes.add((AShape)mms.getObject());
            }
        }        
        
        return shapes;
    }
    
    public String getAssCommands(){
        StringBuilder sb = new StringBuilder();
        boolean start = true; 
        
        for(Memories mms : memoriesArray){
            if(mms.getObject() instanceof AShape && mms.isUndo() == false){
                AShape sh = (AShape)mms.getObject();
                if(start == false){
                    sb.append(" ");
                }
                if(sh instanceof Move){
                    Move move = (Move)sh;
                    Point p = getSheetCoordinatesToDisplay(new Point((int)move.getXa(), (int)move.getYa()));
                    sb.append(move.isMoveM() ? "m " : "n ");
                    sb.append(p.x);
                    sb.append(" ");
                    sb.append(p.y);
                }else if(sh instanceof Line){
                    Line line = (Line)sh;
                    Point p = getSheetCoordinatesToDisplay(new Point((int)line.getXb(), (int)line.getYb())); 
                    sb.append("l ");
                    sb.append(p.x);
                    sb.append(" ");
                    sb.append(p.y);
                }else if(sh instanceof Bezier){
                    Bezier bezier = (Bezier)sh;
                    Point cpa, cpb, p;
                    if(bezier.isCubic() == true){
                        cpa = getSheetCoordinatesToDisplay(
                                new Point((int)bezier.getCpxa(), (int)bezier.getCpya()));
                        cpb = getSheetCoordinatesToDisplay(
                                new Point((int)bezier.getCpxb(), (int)bezier.getCpyb()));
                        p = getSheetCoordinatesToDisplay(
                                new Point((int)bezier.getX(), (int)bezier.getY()));                    
                    }else{
                        double[] coordinates = bezier.toCubic();
                        cpa = getSheetCoordinatesToDisplay(
                                new Point((int)coordinates[2], (int)coordinates[3]));
                        cpb = getSheetCoordinatesToDisplay(
                                new Point((int)coordinates[4], (int)coordinates[5]));
                        p = getSheetCoordinatesToDisplay(
                                new Point((int)coordinates[6], (int)coordinates[7]));
                    }
                    sb.append("b ");
                    sb.append(cpa.x);
                    sb.append(" ");
                    sb.append(cpa.y);
                    sb.append(" ");
                    sb.append(cpb.x);
                    sb.append(" ");
                    sb.append(cpb.y);
                    sb.append(" ");
                    sb.append(p.x);
                    sb.append(" ");
                    sb.append(p.y);
                }else if(sh instanceof BSpline){
                    BSpline bs = (BSpline)sh;
                    sb.append("s");
                    for(Point2D point : bs.getBSplineCurve().getControlPoints()){
                        Point p = getSheetCoordinatesToDisplay(
                                new Point((int)point.getX(), (int)point.getY()));
                        sb.append(" ");
                        sb.append(p.x);
                        sb.append(" ");
                        sb.append(p.y);
                    }
                }                
                if(start == true){
                    start = false;
                }
            }
        }
        
        return sb.toString();
    }
    
    public Point getSheetCoordinatesToDisplay(Point p){
        int mX = p.x -(sheet.getWidth()/sheet.getScaleXY())/2;
        int mY = p.y -(sheet.getHeight()/sheet.getScaleXY())/2;
        return new Point(mX, mY);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Sheet and rules">
    class Sheet extends JPanel {
        //Pour visualiser une ligne aux coordonnées du pointeur de la souris.
        private int mouseX = -1;
        private int mouseY = -1;
        //Pour pouvoir déplacer l'image de fond (s'il y en a une).
        private int imageX = 0;
        private int imageY = 0;
        //Pour connaitre l'image à dessiner (s'il y en a une).
        private ImageIcon img = null;
        //Pour connaitre la transparence de l'image (0f = transparent ; 1f=opaque).
        private Float alpha = 1f;
        //Pour remplir la zone avec une couleur
        private GeneralPath gp = null;
        //Pour rendre transparente la zone
        private Float gpAlpha = 0.2f;

        private Image imgDraft = null;

        private int scale = 1;
        private Thickness thickness = Thickness.Big;

    //    private java.util.List<Layer> layerList = null;
    //    
    //    private Selection selection = new Selection();
        private List<Center> centers = new ArrayList<>();
        private List<Resize> resizes = new ArrayList<>();
        private List<Shear> shears = new ArrayList<>();
        private List<Translation> translations = new ArrayList<>();
    //    private java.util.List<feuille.drawing.ornament.IShape> oml = new java.util.ArrayList<feuille.drawing.ornament.IShape>();

        // On veut obtenir l'état de l'édition (inEdition@MainFrame)
        List<AShape> pvShapes = null;

        public Sheet() {
            init();
        }

        private void init(){
//            // Hide the cursor
//            // Transparent 16 x 16 pixel cursor image.
//            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);        
//            // Create a new blank cursor.
//            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
//            // Set the blank cursor to the Sheet.
//            setCursor(blankCursor);
//
//            addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    repaint();
//                }            
//            });
//
//            mainFrame.addEditionCursorListener(new EditionCursorAdapter() {
//                @Override
//                public void cursorMoved(EditionCursorEvent event) {
//                    pvShapes = event.getNow(true);
//                    repaint();
//                }
//
//                @Override
//                public void cursorEndClick() {
//                    pvShapes = null;
//                    repaint();
//                }
//            });
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            
            Stroke oldStroke = g2d.getStroke();

            g2d.setColor(MainFrame.isDark() ? new Color(70,70,70) : Color.white);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.scale(scale, scale);

            //Essaie de charger puis dessiner une image et changer sa transparence et sa position.
            if(img!=null){
                Composite originalComposite = g2d.getComposite();
                g2d.setComposite(makeComposite(alpha));
                g2d.drawImage(img.getImage(), imageX, imageY, null);
                g2d.setComposite(originalComposite);
            }

            //Essaie de charger puis dessiner une image et changer sa transparence et sa position.
            if(imgDraft!=null){
                Composite originalComposite = g2d.getComposite();
                g2d.setComposite(makeComposite(alpha));
                g2d.drawImage(imgDraft, 0, 0, null);
                g2d.setComposite(originalComposite);
            }

            //Définit et dessine les axes du milieu et le quadrillage.
            g2d.setColor(MainFrame.isDark() ? DrawColor.slate_gray.getColor(0.5f) : new Color(216,255,253));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine((getWidth()/scale)/2, 0, (getWidth()/scale)/2, getHeight()); //vertical
            g2d.drawLine(0, (getHeight()/scale)/2, getWidth(), (getHeight()/scale)/2); //horizontal
            g2d.setColor(MainFrame.isDark() ? DrawColor.cadet_blue.getColor(0.5f) : new Color(234,216,255));
            g2d.setStroke(new BasicStroke(1f));
            int i = getWidth()/2;
            while(i>=0){ i=i-25; g2d.drawLine(i, 0, i, getHeight()); }
            i = getWidth()/2;
            while(i<=getWidth()){ i=i+25; g2d.drawLine(i, 0, i, getHeight()); }
            i = getHeight()/2;
            while(i>=0){ i=i-25; g2d.drawLine(0, i, getWidth(), i); }
            i = getHeight()/2;
            while(i<=getHeight()){ i=i+25; g2d.drawLine(0, i, getWidth(), i); }



            // Montre le path en remplissant la zone
    //        if(layerList!=null){
    //            Composite originalComposite = g2d.getComposite();//Get default
    //            g2d.setComposite(makeComposite(gpAlpha));//Change the transparency
    //            for(Layer lay : layerList){
    //                if(lay!=null && lay.isSelected()==false){
    //                    g2d.setColor(lay.getColor());
    //                    g2d.fill(lay.getGeneralPath());
    //                }else if(lay!=null && lay.isSelected()==true){
    //                    g2d.setColor(lay.getColor());
    //                    g2d.fill(lay.getGeneralPath());
    //                }
    //            }
    //            g2d.setComposite(originalComposite);//Reset default
    //        }else{
    //            if(gp!=null){
    //                Composite originalComposite = g2d.getComposite();//Get default
    //                g2d.setComposite(makeComposite(gpAlpha));//Change the transparency
    //                g2d.setColor(Color.green);
    //                g2d.fill(gp);
    //                g2d.setComposite(originalComposite);//Reset default
    //            }
    //        }


            //Dessine les axes correspondant au curseur de la souris.
            g2d.setColor(MainFrame.isDark() ? DrawColor.chocolate.getColor() : Color.pink);        
            g2d.drawLine(mouseX, 0, mouseX, getHeight());
            g2d.drawLine(0, mouseY, getWidth(), mouseY);

            //Dessine les coordonnées près des axes correspondant au curseur de la souris.
            int mX = mouseX -(getWidth()/scale)/2;
            int mY = mouseY -(getHeight()/scale)/2;
            g2d.drawString(mX + ";" + mY, mouseX+5, mouseY-5);
            // Met à jour les coordonnées sur le label
            mf.setMousePositionFromDrawingTools(mX, mY);
            // Dessine l'aide
            if(mf.isLineSelected() | mf.isCubicSelected() | mf.isQuadraticSelected()
                    | mf.isMoveMSelected() | mf.isMoveNSelected() | mf.isBSplineSelected()){
                g2d.drawImage(MouseHelpers.getAddPoint(), mouseX+5, mouseY+5, null);
            }
            

            //Change la couleur en rouge (cette ligne est inutile, merci de ne pas en tenir compte.)
            g2d.setColor(Color.red);

            // Consulte la liste des formes et les dessine en utilisant des couleurs différentes.
            for(Memories<?> mms : memoriesArray){
                if(mms.getObject() instanceof AShape && mms.isUndo() == false){
                    AShape sh = (AShape)mms.getObject();
                    sh.draw(g2d);
                }
            }
            
            if(editingPoint2DTemp != null){
                double x_e = editingPoint2DTemp.getX() - 15d;
                double y_e = editingPoint2DTemp.getY() - 15d;
                double size_e = 30d;
                g2d.setStroke(new BasicStroke(4f));
                g2d.setColor(Color.red);
                g2d.draw(new Ellipse2D.Double(x_e, y_e, size_e, size_e));
                g2d.setStroke(oldStroke);
                // Dessine l'aide
                g2d.drawImage(MouseHelpers.getMovePoint(), mouseX+5, mouseY+5, null);
            }

            if(pvShapes != null){
                for(AShape sh : pvShapes){
                    sh.draw(g2d);
                }

                g2d.setStroke(new BasicStroke(3f));
                g2d.setColor(Color.magenta.darker().darker());
                Ellipse2D el = new Ellipse2D.Double(mouseX-5d, mouseY-5d, 10d, 10d);
                g2d.draw(el);
            }        

            //Dessine le centre de rotation s'il existe
            for(Resize resize : resizes){
                if(resize.isSet()){
                    g2d.setColor(Color.magenta);
                    g2d.fillOval(resize.getX()-15, resize.getY()-15, 30, 30);
                    g2d.setColor(new Color(199,0,255));
                    if(resize.getPreviewShapes().isEmpty()==false){
                        for(AShape s : resize.getPreviewShapes()){
                            s.drawOperations(g2d);
                        }
                    }
                }   
            }


            //Dessine le centre de rotation s'il existe
            for(Center center : centers){
                if(center.isSet()){
                    g2d.setColor(Color.pink);
                    g2d.fillOval(center.getX()-15, center.getY()-15, 30, 30);
                    g2d.setColor(new Color(199,0,255));
                    if(center.getPreviewShapes().isEmpty()==false){
                        for(AShape s : center.getPreviewShapes()){
                            s.drawOperations(g2d);
                        }
                    }
                }
            }


            //Dessine le rectangle de sélection de groupe
    //        if(selection.exists()){
    //            selection.drawSelection(g2d);
    //        }

            // RE-Consulte la liste des formes et entoure les formes sélectionnées.
    //        for(IShape s : slist.getShapes()){
    //            if(s.isFirstInSelection()){
    //                g2d.setColor(new Color(147,71,255));
    //            }else{
    //                g2d.setColor(Color.red);
    //            }            
    //            g2d.setStroke(new BasicStroke(3f));
    //            if(s instanceof Point){
    //                Point p = (Point)s;
    //                if(p.isInSelection()){
    //                    int x = (int)p.getOriginPoint().getX();
    //                    int y = (int)p.getOriginPoint().getY();
    //                    int delta = thickness.getThickness()+7;
    //                    g2d.drawOval(x-delta/2, y-delta/2, delta, delta);
    //                }                
    //            }
    //            if(s instanceof ControlPoint){
    //                ControlPoint cp = (ControlPoint)s;
    //                if(cp.isInSelection()){
    //                    int x = (int)cp.getOriginPoint().getX();
    //                    int y = (int)cp.getOriginPoint().getY();
    //                    int delta = thickness.getThickness()+7;
    //                    g2d.drawOval(x-delta/2, y-delta/2, delta, delta);
    //                }                
    //            }
    //            if(s instanceof ReStart){
    //                ReStart m = (ReStart)s;
    //                if(m.isInSelection()){
    //                    int x = (int)m.getLastPoint().getX();
    //                    int y = (int)m.getLastPoint().getY();
    //                    int delta = thickness.getThickness()+7;
    //                    g2d.drawOval(x-delta/2, y-delta/2, delta, delta);
    //                }                
    //            }
    //            if(s instanceof Move){
    //                Move n = (Move)s;
    //                if(n.isInSelection()){
    //                    int x = (int)n.getLastPoint().getX();
    //                    int y = (int)n.getLastPoint().getY();
    //                    int delta = thickness.getThickness()+7;
    //                    g2d.drawOval(x-delta/2, y-delta/2, delta, delta);
    //                }
    //            }
    //            if(s instanceof BSpline){
    //                BSpline bs = (BSpline)s;
    //                for(ControlPoint cp : bs.getControlPoints()){
    //                    if(cp.isInSelection()){
    //                        int x = (int)cp.getOriginPoint().getX();
    //                        int y = (int)cp.getOriginPoint().getY();
    //                        int delta = thickness.getThickness()+7;
    //                        g2d.drawOval(x-delta/2, y-delta/2, delta, delta);
    //                    }
    //                }
    //            }
    //        }

            //Dessine le rectangle génénal du shearing
    //        for(Shear shear : shears){
    //            if(shear.isSet()){
    //                shear.drawLimits(g2d);
    //                g2d.setColor(new Color(199,0,255));
    //                if(shear.getPreviewShapes().isEmpty()==false){
    //                    for(IShape s : shear.getPreviewShapes()){
    //                        if (s instanceof Line){
    //                            Line l = (Line)s;
    //                            g2d.drawLine((int)l.getOriginPoint().getX(),(int)l.getOriginPoint().getY(),
    //                                    (int)l.getLastPoint().getX(),(int)l.getLastPoint().getY());
    //                        }else if (s instanceof Bezier){
    //                            Bezier b = (Bezier)s;
    //                            CubicCurve2D c = new CubicCurve2D.Double();
    //                            c.setCurve((int)b.getOriginPoint().getX(), (int)b.getOriginPoint().getY(),
    //                                    (int)b.getControl1Point().getX(), (int)b.getControl1Point().getY(),
    //                                    (int)b.getControl2Point().getX(), (int)b.getControl2Point().getY(),
    //                                    (int)b.getLastPoint().getX(), (int)b.getLastPoint().getY());
    //                            g2d.draw(c);
    //
    //                            //fait en sorte de mieux voir les points de contrôle
    //                            java.awt.Stroke stroke = g2d.getStroke();
    //                            float[] dash = {5, 5};
    //                            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
    //                                              BasicStroke.JOIN_MITER, 5,
    //                                              dash, 0));
    //                            g2d.drawLine((int)b.getOriginPoint().getX(),(int)b.getOriginPoint().getY(),
    //                                    (int)b.getControl1Point().getX(),(int)b.getControl1Point().getY());
    //                            g2d.drawLine((int)b.getControl1Point().getX(),(int)b.getControl1Point().getY(),
    //                                    (int)b.getControl2Point().getX(), (int)b.getControl2Point().getY());
    //                            g2d.drawLine((int)b.getControl2Point().getX(), (int)b.getControl2Point().getY(),
    //                                    (int)b.getLastPoint().getX(), (int)b.getLastPoint().getY());
    //                            g2d.setStroke(stroke);
    //                        }else if (s instanceof Point){
    //                            Point p = (Point)s;
    //                            int x = (int)p.getOriginPoint().getX();
    //                            int y = (int)p.getOriginPoint().getY();
    //                            g2d.fillRect(
    //                                x-thickness.getThickness()/2,
    //                                y-thickness.getThickness()/2,
    //                                thickness.getThickness(),
    //                                thickness.getThickness());
    //                        }else if(s instanceof ControlPoint){
    //                            ControlPoint cp = (ControlPoint)s;
    //                            int x = (int)cp.getOriginPoint().getX();
    //                            int y = (int)cp.getOriginPoint().getY();
    //                            g2d.fillOval(
    //                                x-thickness.getThickness()/2,
    //                                y-thickness.getThickness()/2,
    //                                thickness.getThickness(),
    //                                thickness.getThickness());
    //                        }else if (s instanceof BSpline){
    //                            BSpline bs = (BSpline)s;
    //                            ControlPoint c = null;
    //                            for(ControlPoint cp : bs.getControlPoints()){
    //                                int x1 = (int)cp.getOriginPoint().getX();
    //                                int y1 = (int)cp.getOriginPoint().getY();
    //                                g2d.drawOval(
    //                                    x1-thickness.getThickness()/2,
    //                                    y1-thickness.getThickness()/2,
    //                                    thickness.getThickness(),
    //                                    thickness.getThickness());
    //                                g2d.fillOval(
    //                                    x1-thickness.getThickness()/2,
    //                                    y1-thickness.getThickness()/2,
    //                                    thickness.getThickness(),
    //                                    thickness.getThickness());
    //                                if(c!=null){
    //                                    java.awt.Stroke stroke = g2d.getStroke();
    //                                    float[] dash = {5, 5};
    //                                    g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
    //                                                    BasicStroke.JOIN_MITER, 5,
    //                                                    dash, 0));
    //                                    g2d.drawLine((int)c.getOriginPoint().getX(),(int)c.getOriginPoint().getY(),
    //                                            (int)cp.getOriginPoint().getX(),(int)cp.getOriginPoint().getY());
    //                                    g2d.setStroke(stroke);
    //                                }
    //                                c = cp;
    //                            }
    //                            bs.getBSplineCurve().paintCurve(g2d, 5, true);
    //                            if(bs.isNextExist()){
    //                                g2d.fillRect(
    //                                (int)bs.getNextPoint().getX()-thickness.getThickness()/2,
    //                                (int)bs.getNextPoint().getY()-thickness.getThickness()/2,
    //                                thickness.getThickness(),
    //                                thickness.getThickness());
    //                            }
    //                        }else if (s instanceof Move){
    //                            Move m = (Move)s;
    //                            int x = (int)m.getLastPoint().getX();
    //                            int y = (int)m.getLastPoint().getY();
    //                            g2d.fillRect(
    //                                x-thickness.getThickness()/2,
    //                                y-thickness.getThickness()/2,
    //                                thickness.getThickness(),
    //                                thickness.getThickness());
    //                        }else if (s instanceof ReStart){
    //                            ReStart m = (ReStart)s;
    //                            int x = (int)m.getLastPoint().getX();
    //                            int y = (int)m.getLastPoint().getY();
    //                            g2d.fillRect(
    //                                x-thickness.getThickness()/2,
    //                                y-thickness.getThickness()/2,
    //                                thickness.getThickness(),
    //                                thickness.getThickness());
    //                        }
    //                    }
    //                }
    //            } 
    //        }

            g2d.setStroke(new BasicStroke(1f));

            for(Translation translation : translations){
                if(translation.isSet()){
                    g2d.setColor(Color.pink);
                    Ellipse2D el = new Ellipse2D.Double(translation.getX()-15, translation.getY()-15, 30, 30);
                    g2d.fill(el);
                    g2d.setColor(new Color(199,0,255));
                    if(translation.getPreviewShapes().isEmpty()==false){
                        for(AShape s : translation.getPreviewShapes()){
                            s.drawOperations(g2d);
                        }
                    }
                }
            }
        }

        public void updateThickness(Thickness thickness){
            this.thickness = thickness;
        }

        /* Demande une mise à jour de l'affichage. */
        public void updateDrawing(){
            repaint();
        }

        /* Met à jour les coordonnées de position de la souris. */
        public void updateMousePosition(int mouseX, int mouseY){
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        /* Met à jour les coordonnées de position de l'image
         * en ajoutant la valeur de déplacement. */
        public void updateImagePosition(int x, int y){
            imageX = imageX+x;
            imageY = imageY+y;
        }

        /* Met à jour l'image. */
        public void updateImage(ImageIcon img){
            this.img = img;
        }

        /* Met à jour la transparence de l'image. */
        public void updateImageTransparency(Float alpha){
            this.alpha = alpha;
        }

        /* Met à jour le chemin de la zone. */
        public void updateGeneralPath(GeneralPath gp){
            this.gp = gp;
        }

        /* Met à jour la transparence pour la zone. */
        public void updateGeneralPathTransparency(Float gpAlpha){
            this.gpAlpha = gpAlpha;
        }

        // Gestion de la transparence
        private AlphaComposite makeComposite(float alpha) {
            int type = AlphaComposite.SRC_OVER;
            return(AlphaComposite.getInstance(type, alpha));
        }

        public void setScaleXY(int scale){
            this.scale = scale;
            setPreferredSize(new java.awt.Dimension(setSizeOfDrawing()*scale,setSizeOfDrawing()*scale));
            revalidate();
            repaint();
        }

        public int getScaleXY(){
            return scale;
        }

        public void updateImageRealPosition(int x, int y){
            imageX = x;
            imageY = y;
        }

        private int setSizeOfDrawing(){
            java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
            java.awt.Dimension dim = toolkit.getScreenSize();
            int big = (int)dim.getWidth();
            if(dim.getWidth()<dim.getHeight()){
                big = (int)dim.getHeight();
            }
            int size = 1000;
            while(size<big){
                size+=500;
            }
            return size;
        }

    //    public void setLayerList(java.util.List<Layer> layerList){
    //        this.layerList = layerList;
    //    }

        public void updateDraft(Image imgDraft){
            this.imgDraft = imgDraft;
        }

        public int getImagePositionX(){
            return imageX;
        }

        public int getImagePositionY(){
            return imageY;
        }

        public int getImageWidth(){
            return img.getIconWidth();
        }

        public int getImageHeight(){
            return img.getIconHeight();
        }

    //    public void updateSelection(Selection selection, boolean clear){
    //        if(clear==true){
    //            selection.cleanList(slist);
    //        }
    //        this.selection = selection;
    //        repaint();
    //    }

        public void updateCenter(Center center){
            centers.clear();
            centers.add(center);
            repaint();
        }

        public void updateResize(Resize resize){
            resizes.clear();
            resizes.add(resize);
            repaint();
        }

        public void updateShear(Shear shear){
            shears.clear();
            shears.add(shear);
            repaint();
        }

        public void updateTranslation(Translation translation){
            translations.clear();
            translations.add(translation);
            repaint();
        }

        public void updateCenter(java.util.List<Center> c){
            centers.clear();
            centers.addAll(c);
            repaint();
        }

        public void updateResize(java.util.List<Resize> r){
            resizes.clear();
            resizes.addAll(r);
            repaint();
        }

        public void updateShear(java.util.List<Shear> s){
            shears.clear();
            shears.addAll(s);
            repaint();
        }

        public void updateTranslation(java.util.List<Translation> t){
            translations.clear();
            translations.addAll(t);
            repaint();
        }
        
        public boolean hasDrawingBackgroundImage(){
            return img != null;
        }
        
        public void centerDrawingBackgroundImage(){
            int x = ((getWidth()/scale) - img.getIconWidth())/2;
            int y = ((getHeight()/scale) - img.getIconHeight())/2;
            updateImageRealPosition(x, y);
            repaint();
        }

    }
    
    class SheetHBorder extends JPanel {
    
        private int scale = 1, width = 0;
    
        public SheetHBorder(int width){
            this.width = width;
            setLayout(null);
            setPreferredSize(new Dimension(width, 40));
            revalidate();
        }

        @Override
        public void paint(Graphics g){
            //Charge la classe Graphics2D pour pouvoir avoir accès à ses méthodes.
            Graphics2D g2d = (Graphics2D)g;

            g2d.setColor(MainFrame.isDark() ? new Color(70,70,70) : Color.white);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.scale(scale, scale);

            //Définit et dessine les axes du milieu et le quadrillage.
            g2d.setColor(MainFrame.isDark() ? DrawColor.slate_gray.getColor(0.5f) : new Color(216,255,253));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine((getWidth()/scale)/2, 0, (getWidth()/scale)/2, getHeight()); //vertical
            g2d.setColor(MainFrame.isDark() ? DrawColor.cadet_blue.getColor(0.5f) : new Color(234,216,255));
            g2d.setStroke(new BasicStroke(1f));
            int i = getWidth()/2;
            while(i>=0){ i=i-25; g2d.drawLine(i, 0, i, getHeight()); }
            i = getWidth()/2;
            while(i<=getWidth()){ i=i+25; g2d.drawLine(i, 0, i, getHeight()); }

            //Dessine les nombres aux bords ; ils correspondent aux coordonnées.
            g2d.setColor(MainFrame.isDark() ? DrawColor.white.getColor(0.3f) : Color.darkGray);
            i = (getWidth()/scale)/2;
            while(i>=0){ i=i-50; g2d.drawString(Integer.toString(i-(getWidth()/scale)/2), i-10, 10); }
            i = (getWidth()/scale)/2;
            while(i<=getWidth()){ i=i+50; g2d.drawString(Integer.toString(i-(getWidth()/scale)/2), i-10, 10); }
        }

        public void setScaleXY(int scale){
            this.scale = scale;
            setPreferredSize(new Dimension(width*scale, 40));
            revalidate();
            repaint();
        }

        public int getScaleXY(){
            return scale;
        }

        public void update(){
            repaint();
        }
    }
    
    class SheetVBorder extends JPanel {
    
        private int scale = 1, height = 0;

        public SheetVBorder(int height){
            this.height = height;
            setLayout(null);
            setPreferredSize(new Dimension(40, height));
            revalidate();
        }

        @Override
        public void paint(Graphics g){
            //Charge la classe Graphics2D pour pouvoir avoir accès à ses méthodes.
            Graphics2D g2d = (Graphics2D)g;

            g2d.setColor(MainFrame.isDark() ? new Color(70,70,70) : Color.white);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.scale(scale, scale);

            //Définit et dessine les axes du milieu et le quadrillage.
            g2d.setColor(MainFrame.isDark() ? DrawColor.slate_gray.getColor(0.5f) : new Color(216,255,253));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawLine(0, (getHeight()/scale)/2, getWidth(), (getHeight()/scale)/2); //horizontal
            g2d.setColor(MainFrame.isDark() ? DrawColor.cadet_blue.getColor(0.5f) : new Color(234,216,255));
            g2d.setStroke(new BasicStroke(1f));
            int i = getHeight()/2;
            while(i>=0){ i=i-25; g2d.drawLine(0, i, getWidth(), i); }
            i = getHeight()/2;
            while(i<=getHeight()){ i=i+25; g2d.drawLine(0, i, getWidth(), i); }

            //Dessine les nombres aux bords ; ils correspondent aux coordonnées.
            g2d.setColor(MainFrame.isDark() ? DrawColor.white.getColor(0.3f) : Color.darkGray);
            g2d.rotate(Math.toRadians(-90));
            i = (getHeight()/scale)/2;
            while(i>=0){ i=i-50; g2d.drawString(Integer.toString((i-(getHeight()/scale)/2)), -i-10, 10); }
            i = (getHeight()/scale)/2;
            while(i<=getHeight()){ i=i+50; g2d.drawString(Integer.toString((i-(getHeight()/scale)/2)), -i-10, 10); }
            g2d.rotate(Math.toRadians(0));
        }

        public void setScaleXY(int scale){
            this.scale = scale;
            setPreferredSize(new Dimension(40, height*scale));
            revalidate();
            repaint();
        }

        public int getScaleXY(){
            return scale;
        }

        public void update(){
            repaint();
        }
    }
    // </editor-fold>
    
}
