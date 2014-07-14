/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * SettingManager.java
 *
 */

package com.aaron.mmchat.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.aaron.mmchat.R;

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
    
    private Context mContext;
    
    SettingManager(Context context) {
        mContext = context.getApplicationContext();
    }
    
    //hidden, only seen by core package
    static void initDefaultSettingManager(Context context) {
        if(sSettingManager == null) {
            sSettingManager = new SettingManager(context);
        }
    }
    
    public static synchronized SettingManager getDefaultSettingManager() {
        if(sSettingManager == null) {
            throw new IllegalStateException("SettingManager has not been inited!");
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(mContext.getString(key), def);
    }
}
