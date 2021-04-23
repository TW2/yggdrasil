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
package org.wingate.ygg.p2p;

/**
 *
 * @author util2
 */
public enum Protocol {
    Unknown("xxx"),
    Message("msg"),
    Download("ddl"),
    DownLoadFinished("ldd"),
    Proposal("prp"),
    Request("req");
    
    String protocolMessage;
    
    private Protocol(String protocolMessage){
        this.protocolMessage = protocolMessage;
    }
    
    public static Protocol from(String raw){
        for(Protocol p : values()){
            if(p.protocolMessage.equalsIgnoreCase(raw) == true){
                return p;
            }
        }
        return Unknown;
    }

    public String getProtocolMessage() {
        return protocolMessage;
    }
    
}
