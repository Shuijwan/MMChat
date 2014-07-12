/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatActivity.java
 *
 */

package com.aaron.mmchat.chat;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.BaseChat;
import com.aaron.mmchat.core.BaseChat.ChatCallback;
import com.aaron.mmchat.core.ChatManager;
import com.aaron.mmchat.core.InstantMessage;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.Message;
import com.aaron.mmchat.core.P2PChat;
import com.aaron.mmchat.utils.ViewUtils;
import com.aaron.mmchat.widget.AvatarView;

import java.util.ArrayList;

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

public class ChatActivity extends Activity implements OnRefreshListener, OnClickListener, ChatCallback, TextWatcher, OnNavigationListener {
    
    public static void startP2PChatActivity(Context context, String clientJid, String jid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("clientJid", clientJid);
        intent.putExtra("jid", jid);
        context.startActivity(intent);
    }
    
    public static void startGroupChatActivity(Context context, String clientJid, String jid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("clientJid", clientJid);
        intent.putExtra("jid", jid);
        context.startActivity(intent);
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ImageView mMultiMediaButton;
    private GridView mMultiMediaPanel;
    private EditText mTextInput;
    private MessageAdapter mAdapter;
    private BaseChat mCurrentChat;
    private ChatManager mChatManager;
    private LayoutInflater mInflater;
    private ChatSessionListAdapter mChatSessionListAdapter;
    
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
        mTextInput.addTextChangedListener(this);
 
        mChatManager = (ChatManager) MMContext.peekInstance().getService(MMContext.CHAT_SERVICE);
        String clientJid = getIntent().getStringExtra("clientJid");
        String jid = getIntent().getStringExtra("jid");
        
        mCurrentChat = mChatManager.getOrCreateP2PChat(clientJid, jid);
        mCurrentChat.registerChatCallback(this);
        
        mInflater = LayoutInflater.from(this);
        
        mAdapter = new MessageAdapter();
        mListView.setAdapter(mAdapter);
        
        initActionBar();
        
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mChatSessionListAdapter = new ChatSessionListAdapter();
        actionBar.setListNavigationCallbacks(mChatSessionListAdapter, this);
        
        ArrayList<P2PChat> list = mChatManager.getP2PChatList();
        actionBar.setSelectedNavigationItem(list.indexOf(mCurrentChat));
        
    }
    
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
    
    @Override
    public void onRefresh() {
        
        
    }

    @Override
    public void onClick(View v) {
        if(v == mMultiMediaButton) {
            if(mTextInput.getText().length() > 0) {
                sendMessage(mTextInput.getText().toString());
            } else {
                switchMultiMediaPanel();
            }
            
        } else if(v == mTextInput) {
            hideMultiMediaPanel();
        }
        
    }

    private void sendMessage(String text) {
        mCurrentChat.sendMessage(text);
        mTextInput.setText("");
    }
    
    private void switchMultiMediaPanel() {
        int visibility = mMultiMediaPanel.getVisibility();
        if(visibility == View.GONE) {
            ViewUtils.hideKeyboard(this);
            showMultiMedialPanel();             
        } else {
            hideMultiMediaPanel();
            ViewUtils.showKeyboard(this, mTextInput);
            
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
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            hideMultiMediaPanel();
            ViewUtils.hideKeyboard(this);
        }
        return super.onTouchEvent(event);
    }
    
    class ChatSessionListAdapter extends BaseAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            
            SessionViewHolder holder;
           if(convertView == null) {
               convertView = mInflater.inflate(R.layout.chat_session_dropdown_item, null);
               holder = new SessionViewHolder();
               
               holder.name = (TextView) convertView.findViewById(R.id.username);
               holder.lastmessage = (TextView) convertView.findViewById(R.id.last_message);
               holder.unreadcount = (TextView) convertView.findViewById(R.id.unread_message_count);
               convertView.setTag(holder);
           } else {
               holder = (SessionViewHolder) convertView.getTag();
           }
           
           P2PChat chat = (P2PChat) getItem(position);

           holder.name.setText(chat.getParticipantName());
           Message lastMessage = chat.getLastMessage();
           if(lastMessage != null) {
               holder.lastmessage.setText((String)lastMessage.getContent());
           } else {
               holder.lastmessage.setText("");
           }
           return convertView;
        }

        @Override
        public int getCount() {
            return mChatManager.getP2PChatList().size();
        }

        @Override
        public Object getItem(int position) {
            return mChatManager.getP2PChatList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_contact_list_contact, parent, false);
                
                holder = new ViewHolder();

                holder.avator = (AvatarView) convertView.findViewById(R.id.contact_avatar);

                holder.name = (TextView) convertView.findViewById(R.id.contact_display_name);
                holder.presence = (TextView) convertView.findViewById(R.id.contact_presence_text);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final P2PChat chat = (P2PChat) mCurrentChat;

            holder.name.setText(chat.getParticipantName());
            holder.presence.setText("");
            return convertView;
        }
        
    }
    
    static class ViewHolder {
        AvatarView avator;
        TextView name;
        TextView presence;
    }
    
    static class SessionViewHolder {
        TextView name;
        TextView lastmessage;
        TextView unreadcount;
    }
    
    class MessageAdapter extends BaseAdapter {

        private static final int MESSAGE_TYPE_SELF = 0;
        private static final int MESSAGE_TYPE_REMOTE = 1;
        
        @Override
        public int getItemViewType(int position) {
            InstantMessage msg = (InstantMessage) getItem(position);
            if(msg.isSelfMessage()) {
                return MESSAGE_TYPE_SELF;
            }
            return MESSAGE_TYPE_REMOTE;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return mCurrentChat.getMessageList().size();
        }

        @Override
        public Object getItem(int position) {
            return mCurrentChat.getMessageList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InstantMessage msg = (InstantMessage) getItem(position);
            int type = getItemViewType(position);
            switch (type) {
                case MESSAGE_TYPE_SELF:
                    SelfMessageViewHolder holder;
                    if(convertView == null) {
                        convertView = mInflater.inflate(R.layout.instant_message_self, null);
                        holder = new SelfMessageViewHolder();
                        holder.content = (TextView) convertView.findViewById(R.id.message);
                        
                        convertView.setTag(holder);
                    } else {
                        holder = (SelfMessageViewHolder) convertView.getTag();
                    }
                    
                    holder.content.setText(msg.getContent());
                    break;
                case MESSAGE_TYPE_REMOTE:
                    RemoteMessageViewHolder holder2;
                    if(convertView == null) {
                        convertView = mInflater.inflate(R.layout.instant_message_remote, null);
                        holder2 = new RemoteMessageViewHolder();
                        holder2.name = (TextView) convertView.findViewById(R.id.username);
                        holder2.content = (TextView) convertView.findViewById(R.id.message);
                        
                        convertView.setTag(holder2);
                    } else {
                        holder2 = (RemoteMessageViewHolder) convertView.getTag();
                    }
                    
                    holder2.name.setText(msg.getFrom());
                    holder2.content.setText(msg.getContent());
                    break;
                
                default:
                    break;
            }
            
            return convertView;
        }
        
    }

    static class SelfMessageViewHolder {
        TextView content;
    }
    
    static class RemoteMessageViewHolder {
        TextView name;
        TextView content;
    }
    
    @Override
    public void onMessageSent() {
        mAdapter.notifyDataSetChanged();
        
    }

    @Override
    public void onMessageSentFailed() {
        mAdapter.notifyDataSetChanged();
        
    }

    @Override
    public void onMessageReceived() {
        mAdapter.notifyDataSetChanged();
        
    }

    @Override
    public void afterTextChanged(Editable s) {
        int resId = s.length() > 0 ? android.R.drawable.ic_menu_send : android.R.drawable.ic_input_add;
        mMultiMediaButton.setImageResource(resId);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mCurrentChat = (BaseChat) mChatSessionListAdapter.getItem(itemPosition);
        mAdapter.notifyDataSetChanged();
        mChatSessionListAdapter.notifyDataSetChanged();
        return true;
    }
    
    public void onDestroy() {
        super.onDestroy();
        mChatManager.removeEmptyChats();
    }
}
