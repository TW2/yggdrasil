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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wingate.ygg.MainFrame;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygglock.YggLock;

/**
 *
 * @author util2
 */
public class Handler implements Runnable {
    private final ServerSocket serverSocket;
    private Socket socket;
    
    private Protocol mainProtocol = Protocol.Unknown;
    private String innerProtocol = null;
    
    public Handler(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while(true){
            try{
            socket = serverSocket.accept();                

            InputStream in = socket.getInputStream();
            dispatcher(in);
            
            } catch (IOException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    private void dispatcher(InputStream in) throws IOException{        
        
        if(mainProtocol == Protocol.Unknown){
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            if(line == null) return;
            mainProtocol = Protocol.from(line);
        }else if(innerProtocol == null){
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            if(line == null){
                mainProtocol = Protocol.Unknown;
                return;
            };
            innerProtocol = line;
        }else{
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();            
            if(line == null){
                mainProtocol = Protocol.Unknown;
                innerProtocol = null;
                return;
            }
            switch(mainProtocol){
                case Message -> { getChatMessage(line); }
                case Proposal, Request -> { getPRMessage(line); }
            }
            mainProtocol = Protocol.Unknown;
            innerProtocol = null;
        }
    }
    
    private YggLock.CryptObj getSender(){
        String ip = socket.getInetAddress().getHostAddress();
        YggLock.CryptObj cObj = null;
        for(YggLock.CryptObj co : MainFrame.getCryptObjs()){
            if(co.getIp().equals(ip) == true){
                cObj = co;
                break;
            }
        }
        return cObj;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Chat">
    
    private void getChatMessage(String data) throws IOException{
        YggLock.CryptObj cObj = getSender();
        
        switch(ChatMessage.InnerChatType.find(innerProtocol)){
            case Text -> {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int m = calendar.get(Calendar.MINUTE);
                int s = calendar.get(Calendar.SECOND);
                String sh = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
                String sm = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
                String ss = s < 10 ? "0" + Integer.toString(s) : Integer.toString(s);
                String time = sh + ":" + sm + ":" + ss;
                String toPrint = "[" + time + "] <" + cObj.getSurname() + "> " + data;
                MainFrame.getChat().addToDoc(toPrint);
            }
            case Smiley -> {}
            case Proposal -> {}
            case Request -> {}
        }
    }
    
    private void getChatMessage(byte[] data) throws IOException{
        YggLock.CryptObj cObj = getSender();
        
        switch(ChatMessage.InnerChatType.find(innerProtocol)){
            case Image -> {}
            case Sound -> {}
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="PR">
    
    private void getPRMessage(String data) throws IOException{
        YggLock.CryptObj cObj = getSender();
        
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String sh = h < 10 ? "0" + Integer.toString(h) : Integer.toString(h);
        String sm = m < 10 ? "0" + Integer.toString(m) : Integer.toString(m);
        String ss = s < 10 ? "0" + Integer.toString(s) : Integer.toString(s);
        String time = sh + ":" + sm + ":" + ss;
        String toPrint = "[" + time + "] <" + cObj.getSurname() + "> ";
        
        AssEvent ev = AssEvent.createFromASS(data);
        
        switch(InSubsMessage.InSubsType.find(innerProtocol)){
            case Proposal -> { MainFrame.getChat().addToDoc(toPrint + "{Proposal}: " + ev.getText()); }
            case Request -> { MainFrame.getChat().addToDoc(toPrint + "{Request}: " + ev.getText()); }
        }
    }
    
    // </editor-fold>
}
