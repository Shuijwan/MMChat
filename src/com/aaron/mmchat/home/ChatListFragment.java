/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChatListFragment.java
 *
 */

package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.chat.ChatActivity;
import com.aaron.mmchat.core.BaseChat;
import com.aaron.mmchat.core.ChatManager;
import com.aaron.mmchat.core.GroupChat;
import com.aaron.mmchat.core.InstantMessage;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.P2PChat;
import com.aaron.mmchat.invitegroupchat.InviteGroupchatActivity;

import java.util.ArrayList;

/**
 *
 * @Title: ChatListFragment.java
 * @Package: com.aaron.mmchat.home
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class ChatListFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    private ChatListAdapter mAdapter;
    private ChatManager mChatManager;
    
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mChatManager = (ChatManager) MMContext.getInstance().getService(MMContext.CHAT_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, null);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
       
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        mAdapter = new ChatListAdapter();
        listView.setAdapter(mAdapter);
        
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        
        getActivity().getActionBar().setTitle(R.string.message);
        return view;
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    class ChatListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ArrayList<P2PChat> mP2pChats;
        private ArrayList<GroupChat> mGroupChats;
        
        public ChatListAdapter() {  
            mInflater = LayoutInflater.from(getActivity());
            mP2pChats = mChatManager.getP2PChatList();
            mGroupChats = mChatManager.getGroupChatList();
        }
        
        @Override
        public int getCount() {
            return mP2pChats.size() + mGroupChats.size();
        }

        @Override
        public Object getItem(int position) {
            if(position < mP2pChats.size()) {
                return mP2pChats.get(position);
            }
            return mGroupChats.get(position - mP2pChats.size());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.chat_list_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.lastMessage = (TextView) convertView.findViewById(R.id.last_message);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.unreadCount = (TextView) convertView.findViewById(R.id.unread_message_count);
                
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            Object item = getItem(position);
            if(item instanceof P2PChat) {
                P2PChat p2pChat = (P2PChat) item;
                holder.name.setText(p2pChat.getParticipantName());
                InstantMessage last = (InstantMessage) p2pChat.getLastMessage();
                if(last != null) {
                    holder.lastMessage.setText(last.getContent());
                } else {
                    holder.lastMessage.setText("");
                }
                holder.time.setText("10:10");
                holder.unreadCount.setText("1");
            } else {
                
            }
            return convertView;
        }
        
    }
    
    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView lastMessage;
        TextView time;
        TextView unreadCount;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chatlist_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_start_groupchat:
                InviteGroupchatActivity.startInviteGroupchatActivity(getActivity());
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        P2PChat chat = (P2PChat) mAdapter.getItem(arg2);
        ChatActivity.startP2PChatActivity(getActivity(), chat.getClientJid(), chat.getParticipantJid());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        final BaseChat chat = (BaseChat) mAdapter.getItem(arg2);
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle(chat.getChatName());
        builder.setItems(R.array.chat_list_operation, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    
                } else if(which == 1) {
                    mChatManager.removeChat(chat);
                }
                mAdapter.notifyDataSetChanged();
            }
        }).show();
        return true;
    }
}
