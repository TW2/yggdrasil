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
public class InSubsMessage {
    
    public enum InSubsType {
        Request("rqt"),
        Proposal("prp"),
        Exit("ext"),
        Unknown("ukn");
        
        String protocol;
        
        private InSubsType(String protocol){
            this.protocol = protocol;
        }

        public String getProtocol() {
            return protocol;
        }
        
        public static InSubsType find(String s){
            InSubsType p = Unknown;
            
            for(InSubsType pr : values()){
                if(pr.getProtocol().equalsIgnoreCase(s) == true){
                    p = pr;
                    break;
                }
            }
            
            return p;
        }
    }
    
    private final InSubsType protocol;
    private final Object object;

    public InSubsMessage(InSubsType protocol, Object object) {
        this.protocol = protocol;
        this.object = object;
    }
    
    public InSubsType getType() {
        return protocol;
    }

    public Object getObject() {
        return object;
    }
    
    private static Class decodeClass(InSubsType isp){
        Class c = Object.class;
        
        switch(isp){
            case Proposal, Request, Exit -> { return String.class; }
        }
        
        return c;
    }
    
    public static String tryGetString(InSubsType isp, byte[] bytes){
        if(decodeClass(isp) == String.class){
            return new String(bytes);
        }
        return null;
    }
    
    public static byte[] tryGetBytes(InSubsType isp, byte[] bytes){
        if(decodeClass(isp) == byte[].class){
            return bytes;
        }
        return null;
    }
    
    
}
