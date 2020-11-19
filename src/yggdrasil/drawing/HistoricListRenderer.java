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
package yggdrasil.drawing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import yggdrasil.util.DrawColor;

/**
 *
 * @author util2
 */
public class HistoricListRenderer extends JPanel implements ListCellRenderer {

    private final JLabel lblShape = new JLabel();
    private final JLabel lblDetails = new JLabel();
    private final JLabel lblColor = new JLabel();
    private final JPanel paneInfos = new JPanel();
    
    private Sketchpad sketchpad = null;
    
    public HistoricListRenderer(Sketchpad sketchpad) {
        this.sketchpad = sketchpad;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        add(lblColor, BorderLayout.WEST);
        add(paneInfos, BorderLayout.CENTER);
        lblColor.setOpaque(true);
        lblColor.setPreferredSize(new Dimension(10, getBounds().height));
        
        paneInfos.setLayout(new BoxLayout(paneInfos, BoxLayout.Y_AXIS));
        paneInfos.add(lblShape);
        paneInfos.add(lblDetails);
        
        lblDetails.setForeground(Color.gray);
        lblDetails.setFont(lblDetails.getFont().deriveFont(Font.ITALIC));
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
        
        if(value instanceof Memories<?>){
            Memories<?> purpose = (Memories<?>)value;
            if(purpose.getObject() instanceof Move){
                Move move = (Move)purpose.getObject();
                lblShape.setText("Move " + (move.isMoveM()? "(M)" : "(N)"));
                lblDetails.setText(getCoordinates(move.getPointA()));
                lblColor.setBackground(move.isMoveM()?
                        DrawColor.dark_orange.getColor(isUndo(purpose) ? .2f : 1f) : // M
                        DrawColor.blue_violet.getColor(isUndo(purpose) ? .2f : 1f)); // N
                paneInfos.setBackground(move.isMoveM()?
                        DrawColor.dark_orange.getColor(.2f) : // M
                        DrawColor.blue_violet.getColor(.2f)); // N
            }else if(purpose.getObject() instanceof Line){
                Line line = (Line)purpose.getObject();
                lblShape.setText("Line");
                lblDetails.setText(getCoordinates(line.getPointA(), line.getPointB()));
                lblColor.setBackground(DrawColor.red.getColor(isUndo(purpose) ? .2f : 1f));
                paneInfos.setBackground(DrawColor.red.getColor(.2f));
            }else if(purpose.getObject() instanceof Bezier){
                Bezier bezier = (Bezier)purpose.getObject();
                lblShape.setText("Bezier " + (bezier.isCubic() ? "(cubic)" : "(quadratic)"));
                Point2D pXo = new Point2D.Double(bezier.getXo(), bezier.getYo());
                Point2D pX = new Point2D.Double(bezier.getX(), bezier.getY());
                lblDetails.setText(getCoordinates(pXo, pX));
                lblColor.setBackground(bezier.isCubic() ?
                        DrawColor.magenta.getColor(isUndo(purpose) ? .2f : 1f) : // Cubic
                        DrawColor.cyan.getColor(isUndo(purpose) ? .2f : 1f)); // Quadratic
                paneInfos.setBackground(bezier.isCubic()?
                        DrawColor.magenta.getColor(.2f) : // Cubic
                        DrawColor.cyan.getColor(.2f)); // Quadratic
            }else if(purpose.getObject() instanceof BSpline){
                BSpline bspline = (BSpline)purpose.getObject();
                lblShape.setText("BSpline");
                lblDetails.setText(getCoordinates(bspline.getBSplineCurve().getControlPoints().get(0)));
                lblColor.setBackground(DrawColor.burly_wood.getColor(isUndo(purpose) ? .2f : 1f));
                paneInfos.setBackground(DrawColor.burly_wood.getColor(.2f));
            }else if(purpose.getObject() instanceof HistoricalPointChange){
                HistoricalPointChange hpc = (HistoricalPointChange)purpose.getObject();
                lblShape.setText("A point has changed");
                lblDetails.setText(getCoordinates(hpc.getOldLocation(), hpc.getNewLocation()));
                lblColor.setBackground(DrawColor.medium_sea_green.getColor(isUndo(purpose) ? .2f : 1f));
                paneInfos.setBackground(DrawColor.medium_sea_green.getColor(.2f));
            }
        }
        
        return this;
    }
    
    private String getCoordinates(Point2D p1, Point2D p2){
        Point pa = sketchpad.getSheetCoordinatesToDisplay(new Point((int)p1.getX(), (int)p1.getY()));
        Point pb = sketchpad.getSheetCoordinatesToDisplay(new Point((int)p2.getX(), (int)p2.getY()));
        StringBuilder sb = new StringBuilder();
        sb.append("From");
        sb.append(" ");
        sb.append("[");
        sb.append(pa.x);
        sb.append(";");
        sb.append(pa.y);
        sb.append("]");
        sb.append(" ");
        sb.append("to");
        sb.append(" ");
        sb.append("[");
        sb.append(pb.x);
        sb.append(";");
        sb.append(pb.y);
        sb.append("]");
        return sb.toString();
    }
    
    private String getCoordinates(Point2D p1){
        Point pa = sketchpad.getSheetCoordinatesToDisplay(new Point((int)p1.getX(), (int)p1.getY()));
        StringBuilder sb = new StringBuilder();
        sb.append("From");
        sb.append(" ");
        sb.append("[");
        sb.append(pa.x);
        sb.append(";");
        sb.append(pa.y);
        sb.append("]");
        return sb.toString();
    }
    
    private boolean isUndo(Object obj){
        boolean undo = false;
        
        List<Memories<?>> mms = sketchpad.getMemories();
        
        if(mms.isEmpty() == false){
            for(Memories<?> mem : mms){
                if(obj.equals(mem) && mem.isUndo() == true){
                    undo = true;
                    break;
                }
            }
        }
        
        return undo;
    }
}
