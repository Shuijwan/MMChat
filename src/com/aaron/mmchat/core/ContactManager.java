/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactManager.java
 *
 */

package com.aaron.mmchat.core;

import android.R.interpolator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public void onContactListAllRefreshed(String clientJid);
        public void onContactRemovedFailed(String contact, int errorcode);
        public void onContactRemoved(String contact);
        public void onContactAdded(String contact);
        public void onContactAddedFailed(String contact, int errorcode);
        public void onContactGroupsAdded(Collection<String> groups);
        public void onContactGroupsRemoved(Collection<String> groups);
        public void onContactUpdated(String contact);
        public void onContactPresenceUpdated(String contact);
    }
    
    public void registerContactListCallback(ContactListCallback callback);
    
    public void unregisterContactListCallback(ContactListCallback callback);
    
    public List<ContactGroup> getContactList(String clientJid);
    
    public Map<String, ArrayList<ContactGroup>> getAllContactList();
    
    public boolean refreshContactList(String clientJid);

}
