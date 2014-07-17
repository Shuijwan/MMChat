/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * MMChatApplication.java
 *
 */

package com.aaron.mmchat;

import android.app.Application;
import com.aaron.mmchat.core.AccountType;
import com.aaron.mmchat.core.MMContext;

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
    
    public void onCreate() {
        super.onCreate();
        AccountType.loadAllAccountType(this);
        MMContext.init(this);
    }
}
