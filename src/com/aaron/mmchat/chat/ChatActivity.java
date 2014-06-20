/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatActivity.java
 *
 */

package com.aaron.mmchat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.utils.ViewUtils;

/**
 *
 * @Title: ChatActivity.java
 * @Package: com.aaron.mmchat.chat
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-15
 *
 */

public class ChatActivity extends Activity implements OnRefreshListener, OnClickListener {
    
    public static void startChatActivity(Context context, String jid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("jid", jid);
        context.startActivity(intent);
    }
    

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ImageView mMultiMediaButton;
    private GridView mMultiMediaPanel;
    private EditText mTextInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mListView = (ListView) findViewById(R.id.list_view);
        mMultiMediaButton = (ImageView) findViewById(R.id.multimedia_button);
        mMultiMediaPanel = (GridView) findViewById(R.id.multimedia_input_panel);
        mTextInput = (EditText) findViewById(R.id.text_input);
        
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_dark, android.R.color.holo_green_dark,  
                android.R.color.holo_blue_dark, android.R.color.holo_orange_dark); 
        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        mMultiMediaButton.setOnClickListener(this);
        mTextInput.setOnClickListener(this);
 
        
    }

    @Override
    public void onRefresh() {
        
        
    }

    @Override
    public void onClick(View v) {
        if(v == mMultiMediaButton) {
            int visibility = mMultiMediaPanel.getVisibility();
            if(visibility == View.GONE) {
                ViewUtils.hideKeyboard(this);
                showMultiMedialPanel();             
            } else {
                hideMultiMediaPanel();
                ViewUtils.showKeyboard(this, mTextInput);
                
            }
        } else if(v == mTextInput) {
            hideMultiMediaPanel();
        }
        
    }

    private void hideMultiMediaPanel() {
        if(mMultiMediaPanel.getVisibility() != View.GONE) {
            mMultiMediaPanel.setVisibility(View.GONE);
        }
    }
    
    private void showMultiMedialPanel() {
        if(mMultiMediaPanel.getVisibility() != View.VISIBLE) {
            mMultiMediaPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideMultiMediaPanel();
        ViewUtils.hideKeyboard(this);
        return false;
    }
    
    
    
    
}
