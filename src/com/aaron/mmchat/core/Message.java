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
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public abstract class Message {
    protected String mFrom;
    protected String mDate;
    protected boolean mSelfMessage;
    protected boolean mReaded;
    
    public Message(String from, String date, boolean self) {
        mFrom = from;
        mDate = date;
        mSelfMessage = self;
        mReaded = self;
    }
    
    public abstract Object getContent();
    
    public String getDate() {
        return mDate;
    }
    
    public String getFrom() {
        return mFrom;
    }
    
    public boolean isSelfMessage() {
        return mSelfMessage;
    }
    
    public boolean isReaded() {
        return mReaded;
    }
    
    public void markAsReaded() {
        mReaded = true;
    }
}
