/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * MMChatApplication.java
 *
 */

package com.aaron.mmchat;

import android.app.Application;
import android.content.Context;

import com.aaron.mmchat.core.AccountType;

/**
 *
 * @Title: MMChatApplication.java
 * @Package: com.aaron.mmchat
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class MMChatApplication extends Application {

    private static Context sApplicationContext;
    
    public void onCreate() {
        super.onCreate();
        AccountType.loadAllAccountType(this);
        sApplicationContext = this;
    }
    
    public static Context getAppContext() {
        return sApplicationContext;
    }
}
