/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatHistoryManager.java
 *
 */

package com.aaron.mmchat.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.aaron.mmchat.core.provider.ChatHistoryProvider;
import com.aaron.mmchat.core.provider.ChatHistoryProvider.P2PChatHistoryColumns;

/**
 *
 * @Title: ChatHistoryManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-7-20
 *
 */

class ChatHistoryManager {
    
    private static ChatHistoryManager sChatHistoryManager = new ChatHistoryManager();
    
    private Context mContext;
    private ChatHistoryManager() {
        mContext = MMContext.getAppContext();
    }
    
    public static ChatHistoryManager getChatHistoryManager() {
        return sChatHistoryManager;
    }
    
    public void addInstantMessage(InstantMessage message) {
        ContentValues values = new ContentValues();
        values.put(P2PChatHistoryColumns.DATE, message.getDate());
        values.put(P2PChatHistoryColumns.CONTENT, message.getContent());
        values.put(P2PChatHistoryColumns.DIRECTION, message.isSelfMessage() ?  P2PChatHistoryColumns.OUTGOING : P2PChatHistoryColumns.INCOMING);
        values.put(P2PChatHistoryColumns.JID, message.getFrom());
        values.put(P2PChatHistoryColumns.DELIVERY_STATUS, message.isReaded() ? P2PChatHistoryColumns.DS_SENT_OR_READ : P2PChatHistoryColumns.DS_NEW);
        values.put(P2PChatHistoryColumns.PACKET_ID, message.getPacketId());
        mContext.getContentResolver().insert(ChatHistoryProvider.P2PCHAT_CONTENT_URI, values);
    }
    
    public void deleteInstantMessage(InstantMessage message) {
        mContext.getContentResolver().delete(ChatHistoryProvider.P2PCHAT_CONTENT_URI, P2PChatHistoryColumns.PACKET_ID +" = ?", new String[] {message.getPacketId()});
    }
    
    public Cursor getP2PChatInstantMessages(String participantJid) {
        Cursor cursor = mContext.getContentResolver().query(ChatHistoryProvider.P2PCHAT_CONTENT_URI, null, P2PChatHistoryColumns.JID + " = ?", new String[] { participantJid }, null);
        return cursor;
    }

}
