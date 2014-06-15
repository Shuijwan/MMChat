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
    
    public static final int CONTACT_OPERATION_ERROR = 1;
    
    public static interface ContactListCallback {
        
        /**
         * callback for clientJid' s contact list is refreshed
         * 
         * */
        public void onContactListAllRefreshed(String clientJid);
        
        /**
         * callback for @Contact is removed failed
         * @param clientJid, the client user this contact belongs to
         * @param contact, the failed removed Contact's jid
         * @param errorcode, fail reason
         * */
        public void onContactRemovedFailed(String clientJid, String contact, int errorcode);
        
        /**
         * callback for @Contact is removed successfully
         * @param clientJid, the client user this contact belongs to
         * @param contact, the removed Contact's jid
         * 
         * */
        public void onContactRemoved(String clientJid, String contact);
        
        /**
         * callback for @Contact is added successfully
         * @param clientJid, the client user this contact belongs to
         * @param contact, the added Contact's jid
         * 
         * */
        public void onContactAdded(String clientJid, String contact);
        
        /**
         * callback for @Contact is added failed
         * @param clientJid, the client user this contact belongs to
         * @param contact, the fail added Contact's jid
         * @param errorcode, fail reason
         * 
         * */
        public void onContactAddedFailed(String clientJid, String contact, int errorcode);
        
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
        
        /**
         * callback for @Contact' info changed
         * @param clientJid, the client user this contact belongs to
         * @param contact, jid of this contact
         * */
        public void onContactUpdated(String clientJid, String contact);
        
        /**
         * callback for @Contact's presence changed
         * @param clientJid, the client user this contact belongs to
         * @param contact, jid of this contact
         * 
         * */
        public void onContactPresenceUpdated(String clientJid, String contact);
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
