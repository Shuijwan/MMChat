/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * MMChatService.java
 *
 */

package com.aaron.mmchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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

    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

}
