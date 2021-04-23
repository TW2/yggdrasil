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
public class ChatMessage {
    
    public enum InnerChatType {
        None("nil"),
        Text("txt"),
        Image("img"),
        Smiley("smy"),
        Sound("snd"),
        Proposal("prp"),
        Request("rqt"),
        Exit("end");
        
        String protocol;
        
        private InnerChatType(String protocol){
            this.protocol = protocol;
        }

        public String getProtocol() {
            return protocol;
        }
        
        public static InnerChatType find(String code){
            InnerChatType ict = None;
            
            for(InnerChatType ct : values()){
                if(ct.getProtocol().equalsIgnoreCase(code) == true){
                    ict = ct;
                    break;
                }
            }
            
            return ict;
        }
    }
    
    private final InnerChatType innerChatType;
    private final Object object;

    public ChatMessage(InnerChatType innerChatType, Object object) {
        this.innerChatType = innerChatType;
        this.object = object;
    }
    
    public InnerChatType getInnerChatType() {
        return innerChatType;
    }

    public Object getObject() {
        return object;
    }
    
    private static Class decodeClass(InnerChatType ict){
        Class c = Object.class;
        
        switch(ict){
            case Text, Proposal, Request, Smiley, Exit -> { return String.class; }
            case Image, Sound -> { return byte[].class; }
        }
        
        return c;
    }
    
    public static String tryGetString(InnerChatType ict, byte[] bytes){
        if(decodeClass(ict) == String.class){
            return new String(bytes);
        }
        return null;
    }
    
    public static String tryGetString(InnerChatType ict, String s){
        if(decodeClass(ict) == String.class){
            return s;
        }
        return null;
    }
    
    public static byte[] tryGetBytes(InnerChatType ict, byte[] bytes){
        if(decodeClass(ict) == byte[].class){
            return bytes;
        }
        return null;
    }
    
    public static byte[] tryGetBytes(InnerChatType ict, String s){
        if(decodeClass(ict) == byte[].class){
            return s.getBytes();
        }
        return null;
    }
}
