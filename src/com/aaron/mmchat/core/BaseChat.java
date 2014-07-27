/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseChat.java
 *
 */

package com.aaron.mmchat.core;

import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @Title: BaseChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: Base class for P2PChat and GroupChat
 * 
 * @Author: aaron
 * @Date: 2014-6-21
 *
 */

public abstract class BaseChat {
    
    
    public static interface ChatCallback {
        public void onMessageSent();
        public void onMessageSentFailed();
        public void onMessageReceived();
    }
    
    protected String mClientJid;
    protected LinkedList<Message> mMessages;
    private HashSet<ChatCallback> mCallbacks;
    private ChatHistoryManager mChatHistoryManager;
    
    public BaseChat(String clientJid) {
        mClientJid = clientJid;
        mMessages = new LinkedList<Message>();
        mCallbacks = new HashSet<ChatCallback>();
        mChatHistoryManager = ChatHistoryManager.getChatHistoryManager();
    }
    
    public void registerChatCallback(ChatCallback callback) {
        mCallbacks.add(callback);
    }
    
    public void unregisterChatCallback(ChatCallback callback) {
        mCallbacks.remove(callback);
    }
    
    protected void notifyMessageSentFailed() {
        for(ChatCallback callback : mCallbacks) {
            callback.onMessageSentFailed();
        }
    }
    
    protected void notifyMessageSent() {
        for(ChatCallback callback : mCallbacks) {
            callback.onMessageSent();
        }
    }
    
    protected void notifyMessageReceived() {
        for(ChatCallback callback : mCallbacks) {
            callback.onMessageReceived();
        }
    }
    
    public String getClientJid() {
        return mClientJid;
    }

    public LinkedList<Message> getMessageList() {
        return mMessages;
    }
    
    protected void removeMessage(Message msg) {
        mMessages.remove(msg);
        if(msg instanceof InstantMessage) {
            mChatHistoryManager.deleteInstantMessage((InstantMessage) msg);
        }
    }
    
    protected void addMessage(Message msg) {
        mMessages.add(msg);
        if(msg instanceof InstantMessage) {
            mChatHistoryManager.addInstantMessage((InstantMessage) msg);
        }
    }
    
    public Message getLastMessage() {
        return mMessages.peekLast();
    }
    
    public abstract void sendMessage(String text);
    
    public abstract String getChatName();
}
