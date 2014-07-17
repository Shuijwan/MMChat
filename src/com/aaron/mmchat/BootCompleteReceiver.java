/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BootCompleteReceiver.java
 *
 */

package com.aaron.mmchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aaron.mmchat.core.SettingManager;
import com.aaron.mmchat.service.MMChatService;

/**
 *
 * @Title: BootCompleteReceiver.java
 * @Package: com.aaron.mmchat
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-7-17
 *
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && SettingManager.getDefaultSettingManager().isBootOnStart()) {
            MMChatService.startMMChatService(context);
        }
        
    }

}
