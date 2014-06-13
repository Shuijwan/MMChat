/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * ChooseAccountTypeActivity.java
 *
 */

package com.aaron.mmchat.login;

import android.R.interpolator;
import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aaron.mmchat.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

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

public class ChooseAccountTypeActivity extends Activity implements OnItemSelectedListener {
    
    private Spinner mAccountSpinner;
    private Button mCustomButton;
    private ArrayList<AccountType> mAccounts;
    
    public static class AccountType implements Parcelable {
        public String id;
        public int icon;
        public String name;
        public String domain;
        public int port;
        public boolean ssl;
        
        @Override
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(id);
            out.writeInt(icon);
            out.writeString(name);
            out.writeString(domain);
            out.writeInt(port);
            out.writeInt(ssl ? 1 : 0);
        }

        public static final Parcelable.Creator<AccountType> CREATOR
                = new Parcelable.Creator<AccountType>() {
            public AccountType createFromParcel(Parcel in) {
                return new AccountType(in);
            }

            public AccountType[] newArray(int size) {
                return new AccountType[size];
            }
        };
        
        private AccountType(Parcel in) {
            id = in.readString();
            icon = in.readInt();
            name = in.readString();
            domain = in.readString();
            port = in.readInt();
            ssl = in.readInt() == 1 ;
        }
        
        public AccountType() {
            
        }
        
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            AccountType account = (AccountType) msg.obj;
            LoginActivity.startLoginActivity(ChooseAccountTypeActivity.this, account);
        }
    };
    
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setTitle(R.string.select_account);
        setContentView(R.layout.activity_choose_account_type);
        
        mAccountSpinner = (Spinner) findViewById(R.id.account_types);
        mCustomButton = (Button) findViewById(R.id.custom_account);
        
        mAccounts = loadKnownAccoutType();
        mAccountSpinner.setAdapter(new AccountAdapter(mAccounts));
        mAccountSpinner.setOnItemSelectedListener(this);
    }
    
    private ArrayList<AccountType> loadKnownAccoutType() {
        
        XmlResourceParser parser = getResources().getXml(R.xml.known_account_type);
        ArrayList<AccountType> accounts = new ArrayList<AccountType>();
        accounts.add(new AccountType());
        try {  
            AccountType account;
            String tagname;
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {  
                 
                if (parser.getEventType() == XmlResourceParser.START_TAG) {  
                    tagname = parser.getName();  
                    if(tagname.equals("account-type")) {
                        account = new AccountType();
                        account.id = parser.getAttributeValue(0);
                        account.icon = getResourceId(parser.getAttributeValue(1));
                        account.name = parser.getAttributeValue(2);
                        account.domain = parser.getAttributeValue(3);
                        account.port = Integer.parseInt(parser.getAttributeValue(4));
                        account.ssl = Integer.parseInt(parser.getAttributeValue(5)) == 1;
                        accounts.add(account);
                    } 
                }             
                parser.next();  
            }  
           
        } catch (XmlPullParserException e) {  
            e.printStackTrace();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return accounts;
    }
    
    class AccountAdapter extends BaseAdapter {
     
        private ArrayList<AccountType> mAccounts;
        private LayoutInflater mInflater;
        
        public AccountAdapter(ArrayList<AccountType> accounts) {
            mAccounts = accounts;
            mInflater = LayoutInflater.from(ChooseAccountTypeActivity.this);
        }
        
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mAccounts.size();
        }

        @Override
        public Object getItem(int position) {
            return mAccounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
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
    
    private int getResourceId(String name) {
        return getResources().getIdentifier(name, "drawable", getPackageName());
    }
    
    static class Holder {
        ImageView icon;
        TextView name;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        AccountType account = mAccounts.get(arg2);
        mHandler.removeMessages(1);
        if(arg2 != 0) {
            Message msg = Message.obtain(mHandler, 1, account);
            mHandler.sendMessageDelayed(msg, 1500);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        
    }
}
