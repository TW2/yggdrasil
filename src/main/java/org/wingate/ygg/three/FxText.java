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
package org.wingate.ygg.three;

import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Rotate;
import javax.imageio.ImageIO;

/**
 *
 * @author util2
 */
public class FxText {
    
    private final JFXPanel fXPanel = new JFXPanel();
    
    private static final double SIZE = 1;
    private static final double WIDTH = 3;
    private final Group group = new Group();
    private final Rotate rx = new Rotate(0, Rotate.X_AXIS);
    private final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
    
    private Path path;

    public FxText(java.awt.Font font, String character) {
        init(font, character);
    }
    
    private void init(java.awt.Font font, String character){
        Platform.runLater(() -> {
            final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2d = img.createGraphics();
            final GlyphVector gv = font.createGlyphVector(g2d.getFontRenderContext(), character);
            
            path = swingToFXShaper(gv, 1.35d);

            g2d.dispose();

            path.setFill(Color.BLACK);
            path.setStroke(Color.TRANSPARENT);
            path.setStrokeWidth(WIDTH);
            
            group.getChildren().add(path);            

            group.getTransforms().addAll(rz, ry, rx);
            group.setTranslateX(-SIZE / 2);
            group.setTranslateY(-SIZE / 2);
            group.setTranslateZ(SIZE / 2);
            rx.setAngle(45);
            ry.setAngle(-45);
            
            Group root = new Group();
            Scene scene = new Scene(root, SIZE * 2, SIZE * 2);

            StackPane stack = new StackPane();
            stack.getChildren().add(group);
            scene.setRoot(stack);
            
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            WritableImage image = group.snapshot(sp, null);
            BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(bi, "png", new File("C:\\Users\\util2\\Desktop\\yggdrasil.png"));
            } catch (IOException ex) {
                Logger.getLogger(FxText.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            fXPanel.setScene(scene);
            fXPanel.setVisible(true);
        });
    }
    
    private Path swingToFXShaper(GlyphVector gv, double scale){
        // JavaFX
        Path fxpath = new Path();

        // Swing
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        PathIterator pi = gv.getOutline().getPathIterator(at);
        double[] coordinates = new double[6];
        
        boolean initial = true;
        
        boolean hasClosePoint = false, isClosedOneTime = false;
        double closeX = 0d, closeY = 0d;
        
        while(pi.isDone() == false){
            pi.next();
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
        
        // JavaFX
        return fxpath;
    }

    public JFXPanel getfXPanel() {
        return fXPanel;
    }
}
