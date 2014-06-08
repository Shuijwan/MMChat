/**
 *
 * Copyright 2013 Cisco Inc. All rights reserved.
 * MenuTest2.java
 *
 */

package com.aaron.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.TextureView;

/**
 *
 * @Title: MenuTest2.java
 * @Package: com.aaron.menu
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2013-11-1
 *
 */

public class MenuTest2 extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("dddd");
        setContentView(view);
    }

}
