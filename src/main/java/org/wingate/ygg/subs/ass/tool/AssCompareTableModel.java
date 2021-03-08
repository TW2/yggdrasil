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
package org.wingate.ygg.subs.ass.tool;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.wingate.ygg.subs.ass.AssEvent;

/**
 *
 * @author util2
 */
public class AssCompareTableModel extends AbstractTableModel {
    
    private List<AssEvent> events_1 = new ArrayList<>();
    private List<AssEvent> events_2 = new ArrayList<>();
    private List<AssEvent> events_3 = new ArrayList<>();

    public AssCompareTableModel() {
    }

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<AssEvent> getEvents_1() {
        return events_1;
    }

    public void setEvents_1(List<AssEvent> events_1) {
        this.events_1 = events_1;
    }

    public List<AssEvent> getEvents_2() {
        return events_2;
    }

    public void setEvents_2(List<AssEvent> events_2) {
        this.events_2 = events_2;
    }

    public List<AssEvent> getEvents_3() {
        return events_3;
    }

    public void setEvents_3(List<AssEvent> events_3) {
        this.events_3 = events_3;
    }
    
}
