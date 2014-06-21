/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * P2PChat.java
 *
 */

package com.aaron.mmchat.core;

import android.util.Log;

import com.aaron.mmchat.core.services.ContactManagerService;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;

import java.util.ArrayList;
import java.util.List;

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
    
    private Chat mChat;
    private Contact mContact;
    
    public P2PChat(String clientJid, Chat chat) {
        super(clientJid);
        mChat = chat;
        mChat.addMessageListener(this);
        ContactManagerService contactManager = (ContactManagerService) MMContext.peekInstance().getService(MMContext.CONTACT_SERVICE);
        mContact = contactManager.getContact(clientJid, chat.getParticipant());
    }
    
    public String getParticipantName() {
        
        if(mContact != null) {
            return mContact.getName();
        }
        
        if(mChat != null) {
            return mChat.getParticipant();
        }
        return null;
    }
    
    public Contact getParticipantContact() {
        return mContact;
    }
    
    public String getParticipantJid() {
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
