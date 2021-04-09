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
package org.wingate.ygg.io;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import org.wingate.ygg.subs.ass.AssEvent;
import org.wingate.ygg.ui.synctable.SelectedFormat;
import org.wingate.ygglock.YggLock;

/**
 *
 * @author util2
 */
public class Client {
    
    SocketChannel client;
    YggLock.CryptObj cObj = new YggLock.CryptObj();
    
    public Client(){
        
    }
    
    public Client(YggLock.CryptObj cObj){
        this.cObj = cObj;
    }
    
    // On se connecte
    public void connect() throws IOException{
        InetSocketAddress hostAddress = new InetSocketAddress(cObj.getIp(), cObj.getPort());
        client = SocketChannel.open(hostAddress);
    }
    
    // On arrête la connexion
    public void close() throws IOException{
        client.close();
    }
    
    // On envoie un message
    public void sendMessage(String message) throws IOException{
        String up = Protocol.Message.getProtocolMessage() + message;
        ByteBuffer buffer = ByteBuffer.wrap(up.getBytes());
        client.write(buffer);
        buffer.clear();
    }
    
    // On envoie un message
    public void sendFile(File file) throws IOException{
        String upWithName = Protocol.Download.getProtocolMessage() + file.getName();
        
        //Send file name
        byte[] nameBytes = upWithName.getBytes();
        ByteBuffer nameBuffer = ByteBuffer.wrap(nameBytes);
        client.write(nameBuffer);
        nameBuffer.clear();
        
        //Send file bytes
        FileChannel filechannel = FileChannel.open(file.toPath());
        ByteBuffer buffer = ByteBuffer.allocate(10000000);
        
        int bytesread = filechannel.read(buffer);
        
        while(bytesread != -1){
            buffer.flip();
            client.write(buffer);
            buffer.compact();
            bytesread = filechannel.read(buffer);
        }
        
        buffer.clear();
        
        //Send file stop
        byte[] endBytes = "end-of-file".getBytes();
        ByteBuffer endBuffer = ByteBuffer.wrap(endBytes);
        client.write(endBuffer);
        endBuffer.clear();
    }
    
    // On envoie un message
    public void sendProposal(Object raw, SelectedFormat choice) throws IOException{
        String message = "";
        switch(choice){
            case ASS -> {
                if(raw instanceof AssEvent){
                    AssEvent ev = (AssEvent)raw;
                    message = "ASS;" + AssEvent.getAssEventLine(ev);
                }
            }
            default -> { return; }
        }
        String up = Protocol.Proposal.getProtocolMessage() + message;
        ByteBuffer buffer = ByteBuffer.wrap(up.getBytes());
        client.write(buffer);
        buffer.clear();
    }
    
    // On envoie un message
    public void sendRequest(Object raw, SelectedFormat choice) throws IOException{
        String message = "";
        switch(choice){
            case ASS -> {
                if(raw instanceof AssEvent){
                    AssEvent ev = (AssEvent)raw;
                    message = "ASS;" + AssEvent.getAssEventLine(ev);
                }
            }
            default -> { return; }
        }
        String up = Protocol.Request.getProtocolMessage() + message;
        ByteBuffer buffer = ByteBuffer.wrap(up.getBytes());
        client.write(buffer);
        buffer.clear();
    }
    
//    private String getApplicationDirectory(){
//        if(System.getProperty("os.name").equalsIgnoreCase("Mac OS X")){
//            java.io.File file = new java.io.File("");
//            return file.getAbsolutePath();
//        }
//        String path = System.getProperty("user.dir");
//        if(path.toLowerCase().contains("jre")){
//            File f = new File(getClass().getProtectionDomain()
//                    .getCodeSource().getLocation().toString()
//                    .substring(6));
//            path = f.getParent();
//        }
//        return path;
//    }
}
