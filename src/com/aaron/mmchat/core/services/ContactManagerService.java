/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ContactManagerService.java
 *
 */

package com.aaron.mmchat.core.services;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aaron.mmchat.core.Contact;
import com.aaron.mmchat.core.ContactGroup;
import com.aaron.mmchat.core.ContactManager;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.RosterListener2;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Title: ContactManagerService.java
 * @Package: com.aaron.mmchat.core.services
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class ContactManagerService extends BaseManagerService implements ContactManager, LoginCallback {

    private class ContactListListener2 implements RosterListener2 {

        private String clientJid;
        
        public ContactListListener2(String clientJid) {
            this.clientJid = clientJid;
        }
        
        @Override
        public void onRosterRefreshed() {
            Message msg = Message.obtain(mUIHandler, MSG_CONTACTLIST_REFRESHED, clientJid);
            msg.sendToTarget();
        }

        @Override
        public void onGroupAdded(Collection<String> addedGroups) {
            if(!addedGroups.isEmpty()) {
                Object[] param = new Object[2];
                param[0] = clientJid;
                param[1] = addedGroups;
                
                Message msg = Message.obtain(mUIHandler, MSG_CONTACTGROUP_ADDED, param);
                msg.sendToTarget();   
            }
            
        }

        @Override
        public void onGroupRemoved(Collection<String> removedGroups) {
            if(!removedGroups.isEmpty()) {
                
                Object[] param = new Object[2];
                param[0] = clientJid;
                param[1] = removedGroups;
                
                Message msg = Message.obtain(mUIHandler, MSG_CONTACTGROUP_REMOVED, param);
                msg.sendToTarget();
            }
            
        }

        @Override
        public void onRostersAdded(HashMap<String, Set<String>> addedEntries) {
            if(addedEntries.isEmpty()) {
                return;
            }
            
            Object[] param = new Object[2];
            param[0] = clientJid;
            param[1] = addedEntries;
            
            Message msg = Message.obtain(mUIHandler, MSG_CONTACT_ADDED, param);
            msg.sendToTarget();
        }

        @Override
        public void onRostersRemoved(HashMap<String, Set<String>> removedEntries) {
            if(removedEntries.isEmpty()) {
                return;
            }
            
            Object[] param = new Object[2];
            param[0] = clientJid;
            param[1] = removedEntries;
            
            Message msg = Message.obtain(mUIHandler, MSG_CONTACT_REMOVED, param);
            msg.sendToTarget();       
        }
        
    }
    
    private class ContactListListener implements RosterListener {

        private String clientJid;
        
        public ContactListListener(String clientJid) {
            this.clientJid = clientJid;
        }
        
        @Override
        public void entriesAdded(Collection<String> addresses) {
            
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            if(addresses.isEmpty()) {
                return;
            }
            
            Object[] param = new Object[2];
            param[0] = clientJid;
            param[1] = addresses;
            
            Message msg = Message.obtain(mUIHandler, MSG_CONTACT_UPDATED, param);
            msg.sendToTarget();
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            
        }

        @Override
        public void presenceChanged(Presence presence) {
            String[] param = new String[2];
            param[0] = clientJid;
            param[1] = StringUtils.parseBareAddress(presence.getFrom());
            
            Message msg = Message.obtain(mUIHandler, MSG_CONTACT_PRESENCE_UPDATED, param);
            msg.sendToTarget();
        }
        
    }
    
    private HashMap<String, HashMap<String, ContactGroup>> mAllContactListsMap;
    private HashMap<String, ArrayList<ContactGroup>> mAllContactLists;
    private HashMap<String, Roster> mRosters;
    private HashMap<String, HashMap<String,Contact>> mAllContactsMap;
    private ArrayList<ContactListCallback> mCallbacks;
    private LoginManager mLoginManager;
    private ArrayList<String> mPendingAddContacts;
    private ArrayList<String> mPendingRemoveContacts;
    
    private static final int MSG_CONTACTLIST_REFRESHED = 1;
    private static final int MSG_CONTACTGROUP_ADDED = 2;
    private static final int MSG_CONTACTGROUP_REMOVED = 3;
    private static final int MSG_CONTACT_ADDED = 4;
    private static final int MSG_CONTACT_REMOVED = 5;
    private static final int MSG_CONTACT_UPDATED = 6;
    private static final int MSG_CONTACT_PRESENCE_UPDATED = 7;
    
    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        
        private void handleContactListRefreshed(String clientJid) {
            
            Roster roster = mRosters.get(clientJid);
            Collection<RosterGroup> groups = roster.getGroups();
            ArrayList<ContactGroup> contactlist = mAllContactLists.get(clientJid);
            if(contactlist == null) {
                contactlist = new ArrayList<ContactGroup>();
                mAllContactLists.put(clientJid, contactlist);
            } else {
                contactlist.clear();
            }
            
            HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
            if(contactlistMap == null) {
                contactlistMap = new HashMap<String, ContactGroup>();
                mAllContactListsMap.put(clientJid, contactlistMap);
            } else {
                contactlistMap.clear();
            }
            
            ContactGroup contactGroup;
            for(RosterGroup group : groups) {
                contactGroup = new ContactGroup(clientJid, group);
                contactlist.add(contactGroup);
                contactlistMap.put(group.getName(), contactGroup);
            }
            
            Collection<RosterEntry> unfiledEntries = roster.getUnfiledEntries();
            if(!unfiledEntries.isEmpty()) {
                RosterGroup tmp = roster.createGroup("Contact");
                for(RosterEntry entry : unfiledEntries) {
                    tmp.addEntryLocal(entry);
                }
                ContactGroup tmpGroup = new ContactGroup(clientJid, tmp);
                contactlist.add(tmpGroup);
                contactlistMap.put(tmpGroup.getName(), tmpGroup);
            }
            notifyContactListAllRefreshed(clientJid);
        }
        
        private void handleContactGroupAdded(String clientJid, Collection<String> addedGroups) {
            
            ArrayList<ContactGroup> contactlist = mAllContactLists.get(clientJid);
            if(contactlist == null) {
                contactlist = new ArrayList<ContactGroup>();
                mAllContactLists.put(clientJid, contactlist);
            }
            
            HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
            if(contactlistMap == null) {
                contactlistMap = new HashMap<String, ContactGroup>();
                mAllContactListsMap.put(clientJid, contactlistMap);
            }
            
            Roster roster = mRosters.get(clientJid);
            
            RosterGroup group;
            ContactGroup contactGroup;
            for(String groupName : addedGroups) {
                group = roster.getGroup(groupName);
                contactGroup = new ContactGroup(clientJid, group);
                contactlist.add(contactGroup);
                contactlistMap.put(groupName, contactGroup);
            }
            notifyContactGroupsAdded(clientJid, addedGroups);  
        }
        
        private void handleContactGroupRemoved(String clientJid,Collection<String> removedGroups) {
            ArrayList<ContactGroup> contactlist = mAllContactLists.get(clientJid);
            if(contactlist != null) {
                Iterator<ContactGroup> iterator = contactlist.iterator();
                ContactGroup contactGroup;
                while(iterator.hasNext()) {
                    contactGroup = iterator.next();
                    if(removedGroups.contains(contactGroup.getName())) {
                        iterator.remove();
                    }
                }
            }
            
            HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
            if(contactlistMap != null) {
                for(String groupName : removedGroups) {
                    contactlistMap.remove(groupName);
                }
            }
            notifyContactGroupsRemoved(clientJid, removedGroups);  
        }
        
        private void handleContactAdded(String clientJid, HashMap<String, Set<String>> addedEntries) {
            Set<String> addedRosters = addedEntries.keySet();
            Roster roster = mRosters.get(clientJid);
            for(String jid : addedRosters) {
                if(mPendingAddContacts != null) {
                    removePendingAddContact(jid);
                }
                
                HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
                Set<String> groups = addedEntries.get(jid);
                
                ContactGroup group;
                
                for(String groupName : groups) {
                    group = contactlistMap.get(groupName);
                    group.addContactInternal(roster.getEntry(jid));  
                }
            }
        }
        
        private void handleContactRemoved(String clientJid, HashMap<String, Set<String>> removedEntries) {
            Set<String> deletedRosters = removedEntries.keySet();
            
            for(String jid : deletedRosters) {
                if(mPendingRemoveContacts != null) {
                    removePendingRemoveContact(jid);
                }
                
                HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
                Set<String> groups = removedEntries.get(jid);
                
                ContactGroup group;
                
                for(String groupName : groups) {
                    group = contactlistMap.get(groupName);
                    if(group != null) {
                        group.removeContactInternal(jid);  
                    }
                }   
            }
        }
        
        private void handleContactUpdated(String clientJid, Collection<String> addresses) {
            Roster roster = mRosters.get(clientJid);
            HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
            ContactGroup contactGroup;
            for(String jid : addresses) {
                RosterEntry entry = roster.getEntry(jid);
                Collection<RosterGroup> groups = entry.getGroups();
                if(groups.size() > 0) { // same entry share same contact, so just update 1 group
                    Iterator<RosterGroup> groupIterator = groups.iterator();
                    contactGroup = contactlistMap.get(groupIterator.next().getName());
                    contactGroup.updateContactRosterEntry(jid, entry);  
                }
                
//                for(RosterGroup group : groups) {
//                    contactGroup = contactlistMap.get(group.getName());
//                    contactGroup.updateContactRosterEntry(jid, entry);  
//                }
            }
        }
        
        private void handleContactPresenceUpdated(String clientJid, String jid) {
            if(clientJid.equals(jid.split("/")[0])) {
                return;
            }
            
            Roster roster = mRosters.get(clientJid);
            RosterEntry entry = roster.getEntry(jid);
            
            if(entry == null) {
                return;
            }
            Collection<RosterGroup> groups = entry.getGroups();
            HashMap<String, ContactGroup> contactlistMap = mAllContactListsMap.get(clientJid);
            ContactGroup contactGroup;
            Contact contact;
            
            if(groups.size() > 0) { // same entry share same contact, so just update 1 group
                Iterator<RosterGroup> groupIterator = groups.iterator();
                contactGroup = contactlistMap.get(groupIterator.next().getName());
                contact = contactGroup.getContact(jid);
                contact.updatePresence();
            }
            
//            for(RosterGroup group : groups) {
//                contactGroup = contactlistMap.get(group.getName());
//                contact = contactGroup.getContact(jid);
//                contact.updatePresence();
//            }
        }
        
        @Override
        public void handleMessage(Message msg) {
            Object[] param;
            switch(msg.what) {
                case MSG_CONTACTLIST_REFRESHED:
                    handleContactListRefreshed((String)msg.obj);
                    break;
                case MSG_CONTACTGROUP_ADDED:
                    param = (Object[]) msg.obj;
                    handleContactGroupAdded((String)param[0], (Collection<String>)param[1]);
                    break;
                case MSG_CONTACTGROUP_REMOVED:
                    param = (Object[]) msg.obj;
                    handleContactGroupRemoved((String)param[0], (Collection<String>)param[1]);
                    break;
                case MSG_CONTACT_ADDED:
                    param = (Object[]) msg.obj;
                    handleContactAdded((String)param[0], (HashMap<String, Set<String>>)param[1]);
                    break;
                case MSG_CONTACT_REMOVED:
                    param = (Object[]) msg.obj;
                    handleContactRemoved((String)param[0], (HashMap<String, Set<String>>)param[1]);
                    break;
                case MSG_CONTACT_UPDATED:
                    param = (Object[]) msg.obj;
                    handleContactUpdated((String)param[0], (Collection<String>)param[1]);
                    break;
                case MSG_CONTACT_PRESENCE_UPDATED:
                    param = (String[]) msg.obj;
                    handleContactPresenceUpdated((String)param[0], (String)param[1]);
                    break;
                default:
                    break;
            }
        }
    };
    
    public ContactManagerService(LoginManager manager) {
        mCallbacks = new ArrayList<ContactManager.ContactListCallback>();
        mRosters = new HashMap<String, Roster>();
        mAllContactLists = new HashMap<String, ArrayList<ContactGroup>>();
        mAllContactListsMap = new HashMap<String, HashMap<String,ContactGroup>>();
        mAllContactsMap = new HashMap<String, HashMap<String,Contact>>();
        mLoginManager = manager;
        mLoginManager.registerLoginCallback(this);
    }

    @Override
    public void registerContactListCallback(ContactListCallback callback) {
        if(!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterContactListCallback(ContactListCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public List<ContactGroup> getContactList(String clientJid) {
        return Collections.unmodifiableList(mAllContactLists.get(clientJid));
    }
    
    public Contact getOrCreateContact(String clientJid, RosterEntry entry) {
        HashMap<String, Contact> map = mAllContactsMap.get(clientJid);
        if(map == null) {
            map = new HashMap<String, Contact>();
            mAllContactsMap.put(clientJid, map);
        }
        Contact contact = map.get(entry.getUser());
        if(contact == null) {
            contact = new Contact(entry);
            map.put(entry.getUser(), contact);
        }
        return contact;
    }
    
    public Contact getContact(String clientJid, String jid) {
        HashMap<String, Contact> map = mAllContactsMap.get(clientJid);
        return map.get(jid);
    }
    
    @Override
    public Map<String, ArrayList<ContactGroup>> getAllContactList() {
        return mAllContactLists;
    }

    @Override
    public boolean refreshContactList(String clientJid) {
        Log.i("TTT", "refreshlist:"+clientJid);
        Roster roster = mRosters.get(clientJid);
        try {
            roster.reload();
            return true;
        } catch (NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Roster getRoster(String clientJid) {
        return mRosters.get(clientJid);
    }
    
    public void addPendingAddContact(String jid) {
        if(mPendingAddContacts == null) {
            mPendingAddContacts = new ArrayList<String>();
        }
        if(!mPendingAddContacts.contains(jid)) {
            mPendingAddContacts.add(jid);
        }
    }
    
    public void removePendingAddContact(String jid) {
        mPendingAddContacts.remove(jid);
    }
    
    public void addPendingRemoveContact(String jid) {
        if(mPendingRemoveContacts == null) {
            mPendingRemoveContacts = new ArrayList<String>();
        }
        if(!mPendingRemoveContacts.contains(jid)) {
            mPendingRemoveContacts.add(jid);
        }
    }
    
    public void removePendingRemoveContact(String jid) {
        mPendingRemoveContacts.remove(jid);
    }
    
    public void notifyContactListAllRefreshed(String clientJid) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactListAllRefreshed(clientJid);
        }
    }
      
    public void notifyContactGroupsAdded(String clientJid, Collection<String> groups) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactGroupsAdded(clientJid, groups);
        }
    }
    
    public void notifyContactGroupsRemoved(String clientJid, Collection<String> groups) {
        for(ContactListCallback callback : mCallbacks) {
            callback.onContactGroupsRemoved(clientJid, groups);
        }
    }

    @Override
    public void onLoginSuccessed(String clientJid) {
        XMPPConnection connection = getXmppConnection(clientJid);
        Roster roster = connection.getRoster();
        mRosters.put(clientJid, roster);
        roster.addRosterListener(new ContactListListener(clientJid));
        roster.addRosterListener2(new ContactListListener2(clientJid));
        
        refreshContactList(clientJid);
    }

    @Override
    public void onLoginFailed(String clientJid, int errorcode) {
        
        
    }

    @Override
    public void onLogoutFinished(String clientJid, boolean remove) {
        if(remove) {
            mRosters.remove(clientJid);
            mAllContactLists.remove(clientJid);
            mAllContactListsMap.remove(clientJid);
            mAllContactsMap.remove(clientJid);
            notifyContactListAllRefreshed(clientJid);
        }
        
    }
}
