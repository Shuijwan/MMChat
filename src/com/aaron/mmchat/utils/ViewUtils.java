/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ViewUtils.java
 *
 */

package com.aaron.mmchat.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *
 * @Title: ViewUtils.java
 * @Package: com.aaron.mmchat.utils
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-20
 *
 */

public class ViewUtils {

    
    public static void hideKeyboard(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(context.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }
    
    public static void showKeyboard(Activity context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }
}
