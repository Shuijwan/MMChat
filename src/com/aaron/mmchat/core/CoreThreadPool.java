/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * CoreThreadPool.java
 *
 */

package com.aaron.mmchat.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @Title: CoreThreadPool.java
 * @Package: com.aaron.mmchat.core
 * @Description: all xmpp object's async task is executed in this CoreThreadPool, it only be inited
 * by {@link MMContext} to make it singleton
 * 
 * @Author: aaron
 * @Date: 2014-7-17
 *
 */

class CoreThreadPool {
    
    private ExecutorService mThreadPool;
    
    /**
     * hidden in core package
     * 
     * */
    CoreThreadPool() {
        
    }
    
    /**
     * submit a Task
     * 
     * */
    public void submitTask(Runnable task) {
        if(mThreadPool == null) {
            mThreadPool = Executors.newFixedThreadPool(3);
        }
        mThreadPool.submit(task);
    }
    
    /**
     * stop the thread pool
     * 
     * */
    public void stopThreadPool() {
        if(mThreadPool != null) {
            mThreadPool.shutdown();
            mThreadPool = null;
        }
    }

}
