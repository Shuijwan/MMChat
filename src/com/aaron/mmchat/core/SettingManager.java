/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * SettingManager.java
 *
 */

package com.aaron.mmchat.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aaron.mmchat.R;

import java.util.HashMap;

/**
 *
 * @Title: SettingManager.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-7-14
 *
 */

public class SettingManager {
    
    private static SettingManager sSettingManager;
    
    private SettingManager() {
    }
    
    public static synchronized SettingManager getDefaultSettingManager() {
        if(sSettingManager == null) {
            sSettingManager = new SettingManager();
        }
        return sSettingManager;
    }
    
    public boolean isShowOfflineContacts() {
        return getBoolean(R.string.show_offline_contact, true);
    }
    
    public boolean isSoundOn() {
        return getBoolean(R.string.sound, true);
    }
    
    public boolean isVibrateOn() {
        return getBoolean(R.string.vibrate, true);
    }
    
    public boolean isShowInStatusBar() {
        return getBoolean(R.string.show_in_statusbar, true);
    }
    
    public boolean isBootOnStart() {
        return getBoolean(R.string.bootstart, true);
    }

    private boolean getBoolean(int key, boolean def) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MMContext.getAppContext());
        return preferences.getBoolean(MMContext.getAppContext().getString(key), def);
    }
}
