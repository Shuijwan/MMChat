/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactManager.java
 *
 */

package com.aaron.mmchat.core;

import android.R.interpolator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @Title: ContactManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public interface ContactManager {
    
    public static final int CONTACT_OPERATION_ERROR = 1;
    
    public static interface ContactListCallback {
        public void onContactListUpdate(String clientJid);
        public void onContactRemovedFailed(String contact, int errorcode);
        public void onContactRemoved(String contact);
        public void onContactAdded(String contact);
        public void onContactAddedFailed(String contact, int errorcode);
//        public void onContactGroupAdded(ContactGroup group);
//        public void onContactGroupAddedFailed(ContactGroup group, int errorcode);
//        public void onContactGroupRemoved(ContactGroup group);
//        public void onContactGroupRemovedFailed(ContactGroup group, int errorcode);
    }
    
    public void registerContactListCallback(ContactListCallback callback);
    
    public void unregisterContactListCallback(ContactListCallback callback);
    
    public ArrayList<ContactGroup> getContactList(String clientJid);
    
    public boolean refreshContactList(String clientJid);

}
