/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatHistoryProvider.java
 *
 */

package com.aaron.mmchat.core.provider;

/**
 *
 * @Title: ChatHistoryProvider.java
 * @Package: com.aaron.mmchat.core.provider
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-20
 *
 */


import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class ChatHistoryProvider extends ContentProvider {

    public static final String AUTHORITY = "com.aaron.mmchat.core.provider.ChatHistory";
    public static final String P2PCHAT_TABLE_NAME = "p2pchat";
    public static final String GROUPCHAT_TABLE_NAME = "groupchat";
    
    public static final Uri P2PCHAT_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + P2PCHAT_TABLE_NAME);
    public static final Uri GROUPCHAT_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + GROUPCHAT_TABLE_NAME); 

    private static final UriMatcher URI_MATCHER = new UriMatcher(
            UriMatcher.NO_MATCH);

    private static final int P2PCHAT_MESSAGES = 1;
    private static final int P2PCHAT_MESSAGE_ID = 2;
    private static final int GROUPCHAT_MESSAGES = 3;
    private static final int GROUPCHAT_MESSAGE_ID = 4;

    static {
        URI_MATCHER.addURI(AUTHORITY, "p2pchat", P2PCHAT_MESSAGES);
        URI_MATCHER.addURI(AUTHORITY, "p2pchat/#", P2PCHAT_MESSAGE_ID);
        URI_MATCHER.addURI(AUTHORITY, "groupchat", GROUPCHAT_MESSAGES);
        URI_MATCHER.addURI(AUTHORITY, "groupchat/#", GROUPCHAT_MESSAGE_ID);
    }

    private static final String TAG = ChatHistoryProvider.class.getSimpleName();

    private SQLiteOpenHelper mOpenHelper;

    public ChatHistoryProvider() {
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String segment;
        
        switch (URI_MATCHER.match(url)) {

        case P2PCHAT_MESSAGES:
            count = db.delete(P2PCHAT_TABLE_NAME, where, whereArgs);
            break;
        case P2PCHAT_MESSAGE_ID:
            segment = url.getPathSegments().get(1);

            if (TextUtils.isEmpty(where)) {
                where = "_id=" + segment;
            } else {
                where = "_id=" + segment + " AND (" + where + ")";
            }

            count = db.delete(P2PCHAT_TABLE_NAME, where, whereArgs);
            break;
        case GROUPCHAT_MESSAGES:
            count = db.delete(GROUPCHAT_TABLE_NAME, where, whereArgs);
            break;
        case GROUPCHAT_MESSAGE_ID:
            segment = url.getPathSegments().get(1);

            if (TextUtils.isEmpty(where)) {
                where = "_id=" + segment;
            } else {
                where = "_id=" + segment + " AND (" + where + ")";
            }

            count = db.delete(GROUPCHAT_TABLE_NAME, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Cannot delete from URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public String getType(Uri url) {
        int match = URI_MATCHER.match(url);
        switch (match) {
        case P2PCHAT_MESSAGES:
            return P2PChatHistoryColumns.CONTENT_TYPE;
        case P2PCHAT_MESSAGE_ID:
            return P2PChatHistoryColumns.CONTENT_ITEM_TYPE;
        case GROUPCHAT_MESSAGES:
            return GroupChatHistoryColumns.CONTENT_TYPE;
        case GROUPCHAT_MESSAGE_ID:
            return GroupChatHistoryColumns.CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        int match = URI_MATCHER.match(url);
        switch (match) {
            case P2PCHAT_MESSAGES:
            case GROUPCHAT_MESSAGES:
                break;
            default:
                throw new IllegalArgumentException("Cannot insert into URL: " + url);
        }

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

        ArrayList<String> requiredColumns;
        if(match == P2PCHAT_MESSAGES) {
            requiredColumns = P2PChatHistoryColumns.getRequiredColumns();
        } else {
            requiredColumns = GroupChatHistoryColumns.getRequiredColumns();
        }
        
        for (String colName : requiredColumns) {
            if (!values.containsKey(colName)) {
                throw new IllegalArgumentException("Missing column: " + colName);
            }
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId;
        if(match == P2PCHAT_MESSAGES) {
            rowId = db.insert(P2PCHAT_TABLE_NAME, BaseChatHistoryColumns.DATE, values);
        } else {
            rowId = db.insert(GROUPCHAT_TABLE_NAME, BaseChatHistoryColumns.DATE, values);
        }

        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + url);
        }

        Uri noteUri;
        if(match == P2PCHAT_MESSAGES) {
            noteUri = ContentUris.withAppendedId(P2PCHAT_CONTENT_URI, rowId);
        } else {
            noteUri = ContentUris.withAppendedId(GROUPCHAT_CONTENT_URI, rowId);
        }
        getContext().getContentResolver().notifyChange(noteUri, null);
        return noteUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ChatDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        int match = URI_MATCHER.match(url);

        switch (match) {
        case P2PCHAT_MESSAGES:
            qBuilder.setTables(P2PCHAT_TABLE_NAME);
            break;
        case P2PCHAT_MESSAGE_ID:
            qBuilder.setTables(P2PCHAT_TABLE_NAME);
            qBuilder.appendWhere("_id=");
            qBuilder.appendWhere(url.getPathSegments().get(1));
            break;
        case GROUPCHAT_MESSAGES:
            qBuilder.setTables(GROUPCHAT_TABLE_NAME);
            break;
        case GROUPCHAT_MESSAGE_ID:
            qBuilder.setTables(GROUPCHAT_TABLE_NAME);
            qBuilder.appendWhere("_id=");
            qBuilder.appendWhere(url.getPathSegments().get(1));
            break;
        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = BaseChatHistoryColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qBuilder.query(db, projectionIn, selection, selectionArgs,
                null, null, orderBy);

        if (ret == null) {
            
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), url);
        }

        return ret;
    }

    @Override
    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = URI_MATCHER.match(url);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String segment;
        switch (match) {
        case P2PCHAT_MESSAGES:
            count = db.update(P2PCHAT_TABLE_NAME, values, where, whereArgs);
            break;
        case P2PCHAT_MESSAGE_ID:
            segment = url.getPathSegments().get(1);
            rowId = Long.parseLong(segment);
            count = db.update(P2PCHAT_TABLE_NAME, values, "_id=" + rowId, null);
            break;
        case GROUPCHAT_MESSAGES:
            count = db.update(GROUPCHAT_TABLE_NAME, values, where, whereArgs);
            break;
        case GROUPCHAT_MESSAGE_ID:
            segment = url.getPathSegments().get(1);
            rowId = Long.parseLong(segment);
            count = db.update(GROUPCHAT_TABLE_NAME, values, "_id=" + rowId, null);
            break;
        default:
            throw new UnsupportedOperationException("Cannot update URL: " + url);
        }

        getContext().getContentResolver().notifyChange(url, null);
        return count;

    }

    private static class ChatDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mmchat.db";
        private static final int DATABASE_VERSION = 1;

        public ChatDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + P2PCHAT_TABLE_NAME + " (" + P2PChatHistoryColumns._ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + P2PChatHistoryColumns.DATE + " INTEGER,"
                    + P2PChatHistoryColumns.DIRECTION + " INTEGER,"
                    + P2PChatHistoryColumns.JID + " TEXT,"
                    + P2PChatHistoryColumns.CONTENT + " TEXT,"
                    + P2PChatHistoryColumns.DELIVERY_STATUS + " INTEGER,"
                    + P2PChatHistoryColumns.PACKET_ID + " TEXT);");
            
            db.execSQL("CREATE TABLE " + GROUPCHAT_TABLE_NAME + " (" + GroupChatHistoryColumns._ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + GroupChatHistoryColumns.DATE + " INTEGER,"
                    + GroupChatHistoryColumns.DIRECTION + " INTEGER,"
                    + GroupChatHistoryColumns.JID + " TEXT,"
                    + GroupChatHistoryColumns.CONTENT + " TEXT,"
                    + GroupChatHistoryColumns.DELIVERY_STATUS + " INTEGER,"
                    + GroupChatHistoryColumns.PACKET_ID + " TEXT,"
                    + GroupChatHistoryColumns.CHAT_TYPE + " INTEGER,"
                    + GroupChatHistoryColumns.ROOM_ID + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            
        }

    }

    
    
    static class BaseChatHistoryColumns implements BaseColumns {

        private BaseChatHistoryColumns() {
        }
        
        public static final String DEFAULT_SORT_ORDER = "_id ASC"; // sort by auto-id

        public static final String DATE = "date";
        public static final String DIRECTION = "direction";
        public static final String JID = "jid";
        public static final String CONTENT = "content";
        public static final String DELIVERY_STATUS = "status"; // SQLite can not rename columns, reuse old name
        public static final String PACKET_ID = "pid";

        // boolean mappings
        public static final int INCOMING = 0;
        public static final int OUTGOING = 1;
        
        public static final int DS_NEW = 0; //< this message has not been sent/displayed yet
        public static final int DS_SENT_OR_READ = 1; //< this message was sent but not yet acked, or it was received and read
        public static final int DS_ACKED = 2; //< this message was XEP-0184 acknowledged
        public static final int DS_FAILED = 3; //< this message was returned as failed

    } 
    
    public static final class P2PChatHistoryColumns extends BaseChatHistoryColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mmchat.p2pchat";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mmchat.p2pchat";
        
        public static ArrayList<String> getRequiredColumns() {
            ArrayList<String> tmpList = new ArrayList<String>();
            tmpList.add(DATE);
            tmpList.add(DIRECTION);
            tmpList.add(JID);
            tmpList.add(CONTENT);
            return tmpList;
        }
    }

    public static final class GroupChatHistoryColumns extends BaseChatHistoryColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mmchat.groupchat";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mmchat.groupchat";
        
        public static final String CHAT_TYPE = "chat_type";
        public static final String ROOM_ID = "room_id";
        
        public static final int TYPE_TEMPORARY = 0;
        public static final int TYPE_PERSISTENT = 1;
        
        public static ArrayList<String> getRequiredColumns() {
            ArrayList<String> tmpList = new ArrayList<String>();
            tmpList.add(DATE);
            tmpList.add(DIRECTION);
            tmpList.add(JID);
            tmpList.add(CONTENT);
            tmpList.add(CHAT_TYPE);
            tmpList.add(ROOM_ID);
            return tmpList;
        }
    }
}
