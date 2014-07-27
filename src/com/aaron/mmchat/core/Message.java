/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * Message.java
 *
 */

package com.aaron.mmchat.core;

/**
 *
 * @Title: Message.java
 * @Package: com.aaron.mmchat.core
 * @Description:  base class for all message
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public abstract class Message {
    protected String mFrom;
    protected String mTo;
    protected String mDate;
    protected String mPacketId;
    protected boolean mSelfMessage;
    protected boolean mReaded;
    
    public Message(String from, String to,  String date, String packetId, boolean self) {
        mFrom = from;
        mTo = to;
        mDate = date;
        mPacketId = packetId;
        mSelfMessage = self;
        mReaded = self;
    }
    
    /**
     * return the content of this message
     * 
     * */
    public abstract Object getContent();
    
    /**
     * return the date of this message
     * 
     * */
    public String getDate() {
        return mDate;
    }
    
    /**
     * return the message sender
     * 
     * */
    public String getFrom() {
        return mFrom;
    }
    
    /**
     * return if sent by clientUser
     * 
     * */
    public boolean isSelfMessage() {
        return mSelfMessage;
    }
    
    /**
     * return if has been readed
     * 
     * */
    public boolean isReaded() {
        return mReaded;
    }
    
    /**
     * mark this message as readed
     * 
     * */
    public void markAsReaded() {
        mReaded = true;
    }
    
    /**
     * return the packetId
     * 
     * */
    public String getPacketId() {
        return mPacketId;
    }
}
