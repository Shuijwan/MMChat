/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * InstantMessage.java
 *
 */

package com.aaron.mmchat.core;

import android.database.Cursor;

/**
 *
 * @Title: InstantMessage.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-21
 *
 */

public class InstantMessage extends Message {
    
    private String mContent;
    
    public InstantMessage(org.jivesoftware.smack.packet.Message msg, boolean self) {
        super(msg.getFrom(), "sfsdf", self);
        mContent = msg.getBody();
    }
    
    public InstantMessage(Cursor cursor) {
        super(cursor.getString(1), "", true);
        mContent = cursor.getString(2);
    }
    
    @Override
    public String getContent() {
        return mContent;
    }
}
