/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * MMContext.java
 *
 */

package com.aaron.mmchat.core;

import android.content.Context;

import com.aaron.mmchat.core.services.ChatManagerServices;
import com.aaron.mmchat.core.services.ContactManagerService;
import com.aaron.mmchat.core.services.LoginManagerService;


import org.jivesoftware.smack.SmackAndroid;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @Title: MMContext.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-10
 *
 */

public class MMContext {
    
    public static final String LOGIN_SERVICE = "login";
    public static final String CONTACT_SERVICE = "contact";
    public static final String CHAT_SERVICE = "chat";
    public static final String CONFIG_SERVICE = "config";
    
    private static MMContext sInstance; 
    
    private HashMap<String, Object> mServices;
      
    private MMContext(Context context) {
        Context appContext = context.getApplicationContext();
        mServices = new HashMap<String, Object>();
        mServices.put(LOGIN_SERVICE, new LoginManagerService(appContext));
        mServices.put(CONTACT_SERVICE, new ContactManagerService());
        mServices.put(CHAT_SERVICE, new ChatManagerServices());
        
        SmackAndroid.init(appContext);
    }
    
    public synchronized static MMContext getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new MMContext(context);
        }
        return sInstance;
    }
    
    public static MMContext peekInstance() {
        return sInstance;
    }
    
    public Object getService(String service) {
        return mServices.get(service);
    }

}
