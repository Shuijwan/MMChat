/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * MMChatService.java
 *
 */

package com.aaron.mmchat.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.AccountManager.Account;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.MMContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Title: MMChatService.java
 * @Package: com.aaron.mmchat.service
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class MMChatService extends Service {
    
    public static void startMMChatService(Context context) {
        Intent intent = new Intent(context, MMChatService.class);
        context.startService(intent);
    }

    public void onCreate() {
        super.onCreate();
        AccountManager accountManager = AccountManager.getInstance(this);
        List<Account> accounts = accountManager.getAccounts();
        LoginManager loginManager = (LoginManager) MMContext.getInstance(this).getService(MMContext.LOGIN_SERVICE);
        for(Account account : accounts) {
            loginManager.relogin(account);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
