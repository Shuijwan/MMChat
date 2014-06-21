/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * P2PChat.java
 *
 */

package com.aaron.mmchat.core;

import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.util.ArrayList;

/**
 *
 * @Title: P2PChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class P2PChat extends BaseChat implements MessageListener {
    
    public static interface P2PChatCallback {
        public void onMessageSent();
        public void onMessageSentFailed();
        public void onMessageReceived();
    }
    
    private Chat mChat;
    private ArrayList<P2PChatCallback> mCallbacks;
    
    public P2PChat(String clientJid, Chat chat) {
        super(clientJid);
        mChat = chat;
        mChat.addMessageListener(this);
    }
    
    private void notifyMessageSentFailed() {
        for(P2PChatCallback callback : mCallbacks) {
            callback.onMessageSentFailed();
        }
    }
    
    private void notifyMessageSent() {
        for(P2PChatCallback callback : mCallbacks) {
            callback.onMessageSent();
        }
    }
    
    private void notifyMessageReceived() {
        for(P2PChatCallback callback : mCallbacks) {
            callback.onMessageReceived();
        }
    }
    
    public String getParticipant() {
        if(mChat != null) {
            return mChat.getParticipant();
        }
        return null;
    }
    
    public void sendMessage(String text) {
        Message msg = new Message();
        msg.setBody(text);
        msg.addExtension(new DeliveryReceiptRequest());
        String packedId = msg.getPacketID();
        try {
            mChat.sendMessage(msg);
            addMessage(new InstantMessage(msg, true));
            notifyMessageSent();
        } catch (NotConnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            notifyMessageSentFailed();
        }
    }
    
    @Override
    public void processMessage(Chat chat, Message message) {
        Log.i("TTT", "msg:"+message.toString());
        addMessage(new InstantMessage(message, false));
        notifyMessageReceived();
        
    }

}
