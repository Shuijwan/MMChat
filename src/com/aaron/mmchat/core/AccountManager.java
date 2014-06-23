/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * AccountManager.java
 *
 */

package com.aaron.mmchat.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @Title: AccountManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class AccountManager {
    
    private static final String TAG = AccountManager.class.getSimpleName();
    
    private static final String ACCOUNT_FILENAME = "accounts";

    public static class Account {
        public String accountTypeId;
        public String jid;
        public String username;
        public String password;
    }
    
    private AccountManager(Context context) {
        
        mContext = context.getApplicationContext();
        mAccounts = new ArrayList<AccountManager.Account>();
        
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACCOUNT_FILENAME, 0);
        HashMap<String, HashSet<String>> accounts = (HashMap<String, HashSet<String>>) sharedPreferences.getAll();
        Set<Entry<String, HashSet<String>>> entrys = accounts.entrySet();
        Iterator<Entry<String, HashSet<String>>> iterator = entrys.iterator();
        Entry<String, HashSet<String>> entry;
        HashSet<String> info;
        Account account;
        String item;
        while(iterator.hasNext()) {
            entry = iterator.next();
            account = new Account();
            info = entry.getValue();
            Iterator<String> infoIterator = info.iterator();
            while(infoIterator.hasNext()) {
                item = infoIterator.next();
                if(item.startsWith("jid:")) {
                    account.jid = item.substring(4);
                } else if(item.startsWith("id:")) {
                    account.accountTypeId = item.substring(3);
                } else if(item.startsWith("u:")) {
                    account.username = item.substring(2);
                } else if(item.startsWith("p:")) {
                    account.password = item.substring(2);
                }
            }
            
            mAccounts.add(account);
        }
        
    }
    
    private static AccountManager sAccountManager;
    
    private ArrayList<Account> mAccounts;
    private Context mContext;
    
    public synchronized static AccountManager getInstance(Context context) {
        if(sAccountManager == null) {
            sAccountManager = new AccountManager(context);
        }
        return sAccountManager;
    }
    
    public List<Account> getAccounts() {
        return Collections.unmodifiableList(mAccounts);
    }
    
    private Account getAccount(String jid) {
        for(Account account : mAccounts) {
            if(account.jid.equals(jid)) {
                return account;
            }
        }
        return null;
    }
    
    public void addAccount(String jid, String accountTypeId, String username, String password) {
        Log.i(TAG,"addAccount:"+jid);
        if(getAccount(jid) != null) {
            return;
        }
        
        Account account = new Account();
        account.jid = jid;
        account.accountTypeId = accountTypeId;
        account.username = username;
        account.password = password;
        
        mAccounts.add(account);
        
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ACCOUNT_FILENAME, 0);
        Editor editor = sharedPreferences.edit();
        HashSet<String> set = new HashSet<String>();
        
        set.add("jid:"+jid);
        set.add("id:"+accountTypeId);
        set.add("u:"+username);
        set.add("p:"+password);
        
        editor.putStringSet(jid, set);
        editor.commit();
    }
    
    public void deleteAccount(String jid) {
        Account account;
        if((account = getAccount(jid)) == null) {
            return;
        }
        mAccounts.remove(account);
        
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ACCOUNT_FILENAME, 0);
        Editor editor = sharedPreferences.edit();
        editor.remove(jid);
        editor.commit();
    }
    
}
