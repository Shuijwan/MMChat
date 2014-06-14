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
    
    private static final String ACCOUNT_FILENAME = "accounts";

    public static class Account {
        String accountTypeId;
        public String username;
        String password;
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
                if(item.startsWith("id:")) {
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
    
    private boolean containAccount(String accountTypeId, String username) {
        for(Account account : mAccounts) {
            if(account.accountTypeId.equals(accountTypeId) && account.username.equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    public void addAccount(String accountTypeId, String username, String password) {
        
        if(containAccount(accountTypeId, username)) {
            return;
        }
        
        Account account = new Account();
        account.accountTypeId = accountTypeId;
        account.username = username;
        account.password = password;
        
        mAccounts.add(account);
        
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(ACCOUNT_FILENAME, 0);
        Editor editor = sharedPreferences.edit();
        HashSet<String> set = new HashSet<String>();
        set.add("id:"+accountTypeId);
        set.add("u:"+username);
        set.add("p:"+password);
        editor.putStringSet(accountTypeId, set);
        editor.commit();
    }
    
}
