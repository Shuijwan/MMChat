package com.aaron.mmchat.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @Title: ContactManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: interface for manager contact list. Retrieve the instance from 
 *               MMContext.getInstance(context).getService(MMContext.CONTACT_SERVICE)
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public interface ContactManager {
    
    public static interface ContactListCallback {
        
        /**
         * callback for clientJid' s contact list is refreshed
         * 
         * */
        public void onContactListAllRefreshed(String clientJid);
        
        /**
         * callback for @ContactGroup are added
         * @param clientJid, the client user these groups belong to
         * @param groups, added groups
         * 
         * */
        public void onContactGroupsAdded(String clientJid, Collection<String> groups);
        
        /**
         * callback for @ContactGroup are removed
         * @param clientJid, the client user these groups belong to
         * @param groups, removed groups
         * 
         * */
        public void onContactGroupsRemoved(String clientJid, Collection<String> groups);
    }
    
    /**
     * register a ContactListCallback
     * 
     * */
    public void registerContactListCallback(ContactListCallback callback);
    
    /**
     * unregister a ContactListCallback
     * */
    public void unregisterContactListCallback(ContactListCallback callback);
    
    /**
     * return clientJid's contact list
     * @param clientJid
     * 
     * */
    public List<ContactGroup> getContactList(String clientJid);
    
    /**
     * return all contact list
     * @return return a contact list map, key is the clientJid, value is this clientJid's contact list
     * 
     * */
    public Map<String, ArrayList<ContactGroup>> getAllContactList();
    
    /**
     * refresh clientJid's contact list
     * @param clientJid
     * 
     * */
    public boolean refreshContactList(String clientJid);

}
