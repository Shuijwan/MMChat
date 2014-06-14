/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * AccountType.java
 *
 */

package com.aaron.mmchat.core;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;

import com.aaron.mmchat.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @Title: AccountType.java
 * @Package: com.aaron.mmchat.core
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class AccountType implements Parcelable {
    
    private static ArrayList<AccountType> sKnownAccountTypes;
    
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
    
    public static AccountType getAccountTypeById(String id) {
        for(AccountType accountType : sKnownAccountTypes) {
            if(accountType.id.equals(id)) {
                return accountType;
            }
        }
        return null;
    }
    
    public static List<AccountType> getKnownAccountTypes() {
        return Collections.unmodifiableList(sKnownAccountTypes);
    }
    
    public static void loadKnownAccoutType(Context context) {
        if(sKnownAccountTypes != null) {
            return;
        }
        
        XmlResourceParser parser = context.getResources().getXml(R.xml.known_account_type);
        sKnownAccountTypes = new ArrayList<AccountType>();
        try {  
            AccountType account;
            String tagname;
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {  
                 
                if (parser.getEventType() == XmlResourceParser.START_TAG) {  
                    tagname = parser.getName();  
                    if(tagname.equals("account-type")) {
                        account = new AccountType();
                        account.id = parser.getAttributeValue(0);
                        account.icon = getResourceId(context, parser.getAttributeValue(1));
                        account.name = parser.getAttributeValue(2);
                        account.domain = parser.getAttributeValue(3);
                        account.port = Integer.parseInt(parser.getAttributeValue(4));
                        account.ssl = Integer.parseInt(parser.getAttributeValue(5)) == 1;
                        sKnownAccountTypes.add(account);
                    } 
                }             
                parser.next();  
            }  
           
        } catch (XmlPullParserException e) {  
            e.printStackTrace();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    private static int getResourceId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
