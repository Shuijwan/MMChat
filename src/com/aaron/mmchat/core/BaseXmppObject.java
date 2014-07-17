/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * BaseXmppObject.java
 *
 */

package com.aaron.mmchat.core;

/**
 *
 * @Title: BaseXmppObject.java
 * @Package: com.aaron.mmchat.core
 * @Description: All smack sync api, should extends from BaseXmppObject to do async call,
 * for example, @BaseManagerService, @ContactGroup 
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public abstract class BaseXmppObject {
    
    public final void enqueneTask(Runnable runnable) {
        MMContext.getInstance().getCoreThreadPool().submitTask(runnable);
    }
}
