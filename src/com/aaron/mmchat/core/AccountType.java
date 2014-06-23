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
 * @Description: represent a Account Type, see known_account_type.xml for known account types.
 * 
 * @Author: aaron
 * @Date: 2014-6-14
 *
 */

public class AccountType implements Parcelable {
    
    private static ArrayList<AccountType> sKnownAccountTypes;
    
    public String id;
    public int icon;
    public int name;
    public String domain;
    public int port;
    public boolean ssl;
    public boolean needSrv;
    public String description;
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeInt(icon);
        out.writeInt(name);
        out.writeString(domain);
        out.writeInt(port);
        out.writeInt(ssl ? 1 : 0);
        out.writeInt(needSrv ? 1 : 0);
        out.writeString(description);
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
        name = in.readInt();
        domain = in.readString();
        port = in.readInt();
        ssl = in.readInt() == 1 ;
        needSrv = in.readInt() == 1;
        description = in.readString();
    }
    
    public AccountType() {
        
    }
    
    /**
     * return @AccounType according to the account type id
     * @param id : the account type id defined in known_account_type.xml
     * 
    **/
    public static AccountType getAccountTypeById(String id) {
        for(AccountType accountType : sKnownAccountTypes) {
            if(accountType.domain.equals(id)) {
                return accountType;
            }
        }
        return null;
    }
    
    /**
     * return all known account types
     * 
    **/
    public static List<AccountType> getKnownAccountTypes() {
        return Collections.unmodifiableList(sKnownAccountTypes);
    }
    
    /**
     * load current known account type
     * see res/xml/known_account_type.xml file
     * 
     * */
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
                        account.icon = getResourceId(context, parser.getAttributeValue(1), "drawable");
                        account.name = getResourceId(context, parser.getAttributeValue(2), "string");
                        account.domain = parser.getAttributeValue(3);
                        account.port = Integer.parseInt(parser.getAttributeValue(4));
                        account.ssl = Integer.parseInt(parser.getAttributeValue(5)) == 1;
                        account.needSrv = Integer.parseInt(parser.getAttributeValue(6)) == 1;
                        account.description = parser.getAttributeValue(7);
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
    
    private static int getResourceId(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }
}
