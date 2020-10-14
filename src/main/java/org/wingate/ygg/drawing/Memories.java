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

/**
 *
 * @author util2
 */
public class Memories<T> {
        
    private T           oldState = null;
    private T           newState = null;
    private Class       objectClass = null;
    private boolean     undo = false;
    private Object      object = null;

    public Memories() {
    }

    public T getOldState() {
        return oldState;
    }

    public void setOldState(T oldState) {
        this.oldState = oldState;
    }

    public T getNewState() {
        return newState;
    }

    public void setNewState(T newState) {
        this.newState = newState;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }

    public boolean isUndo() {
        return undo;
    }

    public void setUndo(boolean undo) {
        this.undo = undo;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
    
}
