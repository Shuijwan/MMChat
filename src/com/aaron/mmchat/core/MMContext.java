package com.aaron.mmchat.core;

import android.content.Context;

import com.aaron.mmchat.core.services.ChatManagerService;
import com.aaron.mmchat.core.services.ContactManagerService;
import com.aaron.mmchat.core.services.LoginManagerService;


import org.jivesoftware.smack.SmackAndroid;

import java.util.HashMap;

/**
 *
 * @Title: MMContext.java
 * @Package: com.aaron.mmchat.core
 * @Description: Entry for retrieving the core services,see {@link LoginManager},{@link ContactManager},{@link ChatManager}
 * use getService(String service).
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
        LoginManagerService loginManagerService = new LoginManagerService(appContext);
        mServices.put(LOGIN_SERVICE, loginManagerService);
        mServices.put(CONTACT_SERVICE, new ContactManagerService(loginManagerService));
        mServices.put(CHAT_SERVICE, new ChatManagerService(loginManagerService));
        
        SmackAndroid.init(appContext);
    }
    
    /**
     * get instance of MMContext
     * 
     * */
    public synchronized static MMContext getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new MMContext(context);
        }
        return sInstance;
    }
    
    /**
     * peek instance of MMContext, will return null if getInstance is not called before.
     * 
     * */
    public static MMContext peekInstance() {
        return sInstance;
    }
    
    /**
     * get core service according to given service name.
     * @param service, service name, MMContext.LOGIN_SERVICE, MMContext.CONTACT_SERVICE, .etc
     * 
     * */
    public Object getService(String service) {
        return mServices.get(service);
    }

}
