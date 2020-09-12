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
package org.wingate.ygg.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Rotate;
import org.wingate.ygg.ass.ASS;
import org.wingate.ygg.ass.Event;

/**
 *
 * @author util2
 */
public class GlyphInfo {
    
    // Swing
    private final List<GlyphMetrics> metrics = new ArrayList<>();
    private final List<String> words = new ArrayList<>();
    private BufferedImage image;
    private Alignment align = new Alignment();
    
    // JavaFX
    private final Path fxpath = new Path();    
    private static final double SIZE = 1;
    private final Group group = new Group();
    private final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    private final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    
    private GlyphInfo() {
    }
    
    public static GlyphInfo create(ASS ass, Event ev){
        GlyphInfo glyphInfo = new GlyphInfo();
        
        // On récupère la phrase
        final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = img.createGraphics();
        final GlyphVector gv = ev.getStyle().getFont().createGlyphVector(g2d.getFontRenderContext(), ev.getText());
        final GlyphMetrics gm = gv.getGlyphMetrics(0);
        glyphInfo.align = Alignment.create(ass, ev, Margins.create(ass, ev));
        
        glyphInfo.metrics.add(gm);
        glyphInfo.words.add(ev.getText());
        
        glyphInfo.swingToFXShaper(gv, 1d); //glyphInfo.swingToFXShaper(gv, 1.35d);
        
        g2d.dispose();
        
        glyphInfo.fxpath.setFill(glyphInfo.swingColorToFX(ev.getStyle().getTextColor()));
        glyphInfo.fxpath.setStroke(glyphInfo.swingColorToFX(ev.getStyle().getBordColor()));
        glyphInfo.fxpath.setStrokeWidth(ev.getStyle().getOutline());

        glyphInfo.group.getChildren().add(glyphInfo.fxpath);

        glyphInfo.group.getTransforms().addAll(glyphInfo.rz, glyphInfo.ry, glyphInfo.rx);
        glyphInfo.group.setTranslateX(-SIZE / 2);
        glyphInfo.group.setTranslateY(-SIZE / 2);
        glyphInfo.group.setTranslateZ(SIZE / 2);
//        glyphInfo.rx.setAngle(45);
//        glyphInfo.ry.setAngle(-45);
//        glyphInfo.rz.setAngle(-45);

        glyphInfo.image = new BufferedImage(
                Integer.parseInt(ass.getResX()),
                Integer.parseInt(ass.getResY()),
                BufferedImage.TYPE_INT_ARGB
        );
        
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage wi = glyphInfo.group.snapshot(sp, null);
        glyphInfo.image = SwingFXUtils.fromFXImage(wi, glyphInfo.image);
        
        return glyphInfo;
    }
    
    private void swingToFXShaper(GlyphVector gv, double scale){
        // Swing
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        PathIterator pi = gv.getOutline().getPathIterator(at);
        double[] coordinates = new double[6];
        
        boolean initial = true;
        
        boolean hasClosePoint = false, isClosedOneTime = false;
        double closeX = 0d, closeY = 0d;
        
        for (;!pi.isDone(); pi.next()){
            int type = pi.currentSegment(coordinates);
            
            if(hasClosePoint == false){
                closeX = coordinates[0];
                closeY = coordinates[1];
                hasClosePoint = true;
            }
            
            if(initial == true && type != PathIterator.SEG_MOVETO){
                // Java FX ------------------------
                MoveTo moveTo = new MoveTo();
                moveTo.setX(coordinates[0]);
                moveTo.setY(coordinates[1]);
                fxpath.getElements().add(moveTo);
                // --------------------------------
            }
            
            switch(type){
                case PathIterator.SEG_MOVETO:
                    // Java FX ------------------------
                    MoveTo moveTo = new MoveTo();
                    moveTo.setX(coordinates[0]);
                    moveTo.setY(coordinates[1]);
                    fxpath.getElements().add(moveTo);
                    // --------------------------------
                    // Swing
                    initial = false;
                    break;
                case PathIterator.SEG_LINETO:
                    // Java FX ------------------------
                    LineTo lineTo = new LineTo();
                    lineTo.setX(coordinates[0]);
                    lineTo.setY(coordinates[1]);
                    fxpath.getElements().add(lineTo);
                    // --------------------------------
                    // Swing
                    initial = false;
                    break;
                case PathIterator.SEG_QUADTO:
                    // Java FX ------------------------
                    QuadCurveTo quadTo = new QuadCurveTo();
                    quadTo.setControlX(coordinates[0]);
                    quadTo.setControlY(coordinates[1]);
                    quadTo.setX(coordinates[2]);
                    quadTo.setY(coordinates[3]);
                    fxpath.getElements().add(quadTo);
                    // --------------------------------
                    // Swing
                    initial = false;
                    break;
                case PathIterator.SEG_CUBICTO:
                    // Java FX ------------------------
                    CubicCurveTo cubicTo = new CubicCurveTo();
                    cubicTo.setControlX1(coordinates[0]);
                    cubicTo.setControlY1(coordinates[1]);
                    cubicTo.setControlX2(coordinates[2]);
                    cubicTo.setControlY2(coordinates[3]);
                    cubicTo.setX(coordinates[4]);
                    cubicTo.setY(coordinates[5]);
                    fxpath.getElements().add(cubicTo);
                    // --------------------------------
                    // Swing
                    initial = false;
                    break;
                case PathIterator.SEG_CLOSE:
                    // Swing hack
                    if(isClosedOneTime == false){
                        // Java FX ------------------------
                        LineTo closeTo = new LineTo();
                        closeTo.setX(closeX);
                        closeTo.setY(closeY);
                        fxpath.getElements().add(closeTo);
                        // --------------------------------
                        isClosedOneTime = true;
                    }
                    // Swing
                    initial = false;
                    break;
                default:
                    // Swing                    
                    initial = false;
                    break;
            }
        }
    }
    
    private Color swingColorToFX(java.awt.Color color){
        double red = color.getRed() / 255d;
        double green = color.getBlue() / 255d;
        double blue = color.getBlue()/ 255d;
        double opacity = 1d - (color.getAlpha() / 255d);
        return new Color(red, green, blue, opacity);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Marges / Margins -- MarginL MarginR MarginV">
    
    private static class Margins {

        private int top = 0;
        private int bottom = 0;        
        private int left = 0;
        private int right = 0;

        private Margins() {
        }
        
        public static Margins create(ASS ass, Event event){
            Margins m = new Margins();
            
            // Marges (bottom and top)
            m.top = event.getMarginV() == 0 ? event.getStyle().getMarginV() : event.getMarginV();
            m.bottom = m.top;
            
            // Marges (left)
            m.left = event.getMarginL() == 0 ? event.getStyle().getMarginL() : event.getMarginL();
            
            // Marges (right)
            m.right = event.getMarginR() == 0 ? event.getStyle().getMarginR() : event.getMarginR();
            
            return m;
        }

        public int getTop() {
            return top;
        }

        public int getBottom() {
            return bottom;
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Alignement / Alignment -- \a \an \pos \move">
    
    public static class Alignment {
        
        // Attention l'alignement est composé de :
        // - l'alignement (numpad) du style 
        // - \a (legacy)
        // - \an (numpad)
        // - \pos(x,y) (override)
        
        public enum Numpad {
            NUMPAD_1(1, 1, "Numpad 1, Bottom-Left"),
            NUMPAD_2(2, 2, "Numpad 2, Bottom-Center"),
            NUMPAD_3(3, 3, "Numpad 3, Bottom-Right"),
            NUMPAD_4(9, 4, "Numpad 4, Middle-Left"),
            NUMPAD_5(10, 5, "Numpad 5, Middle-Center"),
            NUMPAD_6(11, 6, "Numpad 6, Middle-Right"),
            NUMPAD_7(5, 7, "Numpad 7, Top-Left"),
            NUMPAD_8(6, 8, "Numpad 8, Top-Center"),
            NUMPAD_9(7, 9, "Numpad 9, Top-Right");
            
            int legacy, numpad;
            String placement;
            
            private Numpad(int legacy, int numpad, String placement){
                this.legacy = legacy;
                this.numpad = numpad;
                this.placement = placement;
            }
            
            public static Numpad get(int alignment, boolean legacy){
                Numpad numpad = NUMPAD_2;                
                
                for(Numpad n : values()){
                    if(legacy == true){
                        if(alignment == n.legacy){
                            numpad = n;
                            break;
                        }
                    }else{
                        if(alignment == n.numpad){
                            numpad = n;
                            break;
                        }
                    }
                }
                
                return numpad;
            }
        }
        
        private Point position;
        private Numpad numpad = Numpad.NUMPAD_2;

        private Alignment() {
        }
        
        public static Alignment create(ASS ass, Event event, Margins margins){
            Alignment a = new Alignment();
            
            a.numpad = Numpad.get(ass.getStyles().get(event.getStyle().getName()).getAlignment(), false);
            
            if(event.getText().contains("\\a") & !event.getText().contains("\\an")){
                Pattern p = Pattern.compile("\\\\a(\\d+)");
                Matcher m = p.matcher(event.getText());
                
                if(m.find() == true){
                    a.numpad = Numpad.get(Integer.parseInt(m.group(1)), true);
                }                
            }
            
            if(event.getText().contains("\\an") & !event.getText().contains("\\a")){
                Pattern p = Pattern.compile("\\\\an(\\d+)");
                Matcher m = p.matcher(event.getText());
                
                if(m.find() == true){
                    a.numpad = Numpad.get(Integer.parseInt(m.group(1)), false);
                }                
            }
            
            int assWidth = Integer.parseInt(ass.getResX());
            int assHeight = Integer.parseInt(ass.getResY());
            
            if(event.getText().contains("\\pos")){
                Pattern p = Pattern.compile("\\\\pos\\((\\d+),(\\d+)\\)");
                Matcher m = p.matcher(event.getText());
                
                if(m.find() == true){
                    a.position = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                }                
            }if(event.getText().contains("\\move")){
                Pattern p = Pattern.compile("\\\\move\\((\\d+),(\\d+),");
                Matcher m = p.matcher(event.getText());
                
                if(m.find() == true){
                    a.position = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                }
            }else{
                int x = 0, y = 0;
                
                // Définition de X :
                if(a.numpad == Numpad.NUMPAD_1 | a.numpad == Numpad.NUMPAD_4 | a.numpad == Numpad.NUMPAD_7){
                    x = margins.getLeft();
                }else if(a.numpad == Numpad.NUMPAD_2 | a.numpad == Numpad.NUMPAD_5 | a.numpad == Numpad.NUMPAD_8){
                    x = ((assWidth - margins.getRight()) - margins.getLeft()) / 2;
                }else if(a.numpad == Numpad.NUMPAD_3 | a.numpad == Numpad.NUMPAD_6 | a.numpad == Numpad.NUMPAD_9){
                    x = assWidth - margins.getRight();
                }
                
                // Définition de Y :
                if(a.numpad == Numpad.NUMPAD_1 | a.numpad == Numpad.NUMPAD_2 | a.numpad == Numpad.NUMPAD_3){
                    y = assHeight - margins.getBottom();
                }else if(a.numpad == Numpad.NUMPAD_4 | a.numpad == Numpad.NUMPAD_5 | a.numpad == Numpad.NUMPAD_6){
                    y = ((assHeight - margins.getBottom()) - margins.getTop()) / 2;
                }else if(a.numpad == Numpad.NUMPAD_7 | a.numpad == Numpad.NUMPAD_8 | a.numpad == Numpad.NUMPAD_9){
                    y = margins.getTop();
                }
                
                a.position = new Point(x, y);
            }
            
            return a;
        }

        public Point getPosition() {
            return position;
        }

        public Numpad getNumpad() {
            return numpad;
        }
        
        public Point2D getXY(Dimension size){
            double xa = 0, ya = 0;
            
            // Calcul de X
            if(numpad == Numpad.NUMPAD_1 | numpad == Numpad.NUMPAD_4 | numpad == Numpad.NUMPAD_7){
                    xa = position.getX();
                }else if(numpad == Numpad.NUMPAD_2 | numpad == Numpad.NUMPAD_5 | numpad == Numpad.NUMPAD_8){
                    xa = position.getX() - size.getWidth() / 2d;
                }else if(numpad == Numpad.NUMPAD_3 | numpad == Numpad.NUMPAD_6 | numpad == Numpad.NUMPAD_9){
                    xa = position.getX() - size.getWidth();
                }
            
            // Calcul de Y
            if(numpad == Numpad.NUMPAD_1 | numpad == Numpad.NUMPAD_2 | numpad == Numpad.NUMPAD_3){
                    ya = position.getY();
                }else if(numpad == Numpad.NUMPAD_4 | numpad == Numpad.NUMPAD_5 | numpad == Numpad.NUMPAD_6){
                    ya = position.getY() + size.getHeight() / 2d;
                }else if(numpad == Numpad.NUMPAD_7 | numpad == Numpad.NUMPAD_8 | numpad == Numpad.NUMPAD_9){
                    ya = position.getY() + size.getHeight();
                }
            
            return new Point2D.Double(xa, ya);
        }
        
    }
    
    // </editor-fold>

    public List<GlyphMetrics> getMetrics() {
        return metrics;
    }

    public List<String> getWords() {
        return words;
    }

    public Group getGroup() {
        return group;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Alignment getAlign() {
        return align;
    }
    
    
}
