/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChooseAccountTypeActivity.java
 *
 */

package com.aaron.mmchat.login;

import android.R.interpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountType;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Title: ChooseAccountTypeActivity.java
 * @Package: com.aaron.mmchat.login
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-13
 *
 */

public class ChooseAccountTypeActivity extends Activity implements OnItemSelectedListener, OnClickListener {
    
    public static void startChooseAccountTypeActivity(Context context) {
        Intent intent = new Intent(context, ChooseAccountTypeActivity.class);
        context.startActivity(intent);
    }
    
    private Spinner mAccountSpinner;
    private Button mCustomButton;
    private List<AccountType> mAccounts;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            AccountType account = (AccountType) msg.obj;
            LoginActivity.startLoginActivity(ChooseAccountTypeActivity.this, account);
            finish();
        }
    };
    
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setTitle(R.string.select_account);
        setContentView(R.layout.activity_choose_account_type);
        
        mAccountSpinner = (Spinner) findViewById(R.id.account_types);
        mCustomButton = (Button) findViewById(R.id.custom_account);
        mCustomButton.setOnClickListener(this);
        
        mAccounts = AccountType.getKnownAccountTypes();
        mAccountSpinner.setAdapter(new AccountAdapter(mAccounts));
        mAccountSpinner.setOnItemSelectedListener(this);
    }
    
    class AccountAdapter extends BaseAdapter {
     
        private List<AccountType> mAccounts;
        private LayoutInflater mInflater;
        
        public AccountAdapter(List<AccountType> accounts) {
            mAccounts = accounts;
            mInflater = LayoutInflater.from(ChooseAccountTypeActivity.this);
        }
        
        @Override
        public int getCount() {
            return 1 + mAccounts.size();
        }

        @Override
        public Object getItem(int position) {
            return position == 0 ? null : mAccounts.get(position-1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AccountType account = (AccountType) getItem(position);
            
            Holder holder;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.account_type_item, null);
                holder = new Holder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            
            if(position == 0) {
                holder.icon.setVisibility(View.GONE);
                holder.name.setText(R.string.select_account_info);
            } else {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(account.icon);
                holder.name.setText(account.name);
            }
            return convertView;
        }
        
        
    }
      
    static class Holder {
        ImageView icon;
        TextView name;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mHandler.removeMessages(1);
        if(arg2 != 0) {
            AccountType account = mAccounts.get(arg2-1);
            Message msg = Message.obtain(mHandler, 1, account);
            mHandler.sendMessageDelayed(msg, 1500);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onClick(View v) {
        if(v == mCustomButton) {
            LoginActivity.startLoginActivity(this, null);
        }
        
    }
}
