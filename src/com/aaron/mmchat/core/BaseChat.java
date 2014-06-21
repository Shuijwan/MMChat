/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseChat.java
 *
 */

package com.aaron.mmchat.core;

import java.util.LinkedList;

/**
 *
 * @Title: BaseChat.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-21
 *
 */

public abstract class BaseChat {
    
    protected String mClientJid;
    protected LinkedList<Message> mMessages;
    
    public BaseChat(String clientJid) {
        mClientJid = clientJid;
        mMessages = new LinkedList<Message>();
    }
    
    public String getClientJid() {
        return mClientJid;
    }

    public LinkedList<Message> getMessageList() {
        return mMessages;
    }
    
    public void removeMessage(Message msg) {
        mMessages.remove(msg);
    }
    
    public void addMessage(Message msg) {
        mMessages.add(msg);
    }
}
