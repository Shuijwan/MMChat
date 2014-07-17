package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.AccountManager;
import com.aaron.mmchat.core.AccountType;
import com.aaron.mmchat.core.LoginManager.LoginCallback;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.core.AccountManager.Account;
import com.aaron.mmchat.core.LoginManager;
import com.aaron.mmchat.core.ReconnectManager;
import com.aaron.mmchat.core.ReconnectManager.ReconnectCallback;
import com.aaron.mmchat.login.ChooseAccountTypeActivity;

import java.util.List;

/**
 * @Title: MenuFragment.java
 * @Package: com.aaron.mmchat.home
 * @Description:
 * @Author: aaron
 * @Date: 2014-6-14
 */

public class MenuFragment extends Fragment implements OnItemClickListener {

    public static final int MENU_CHATLIST = 0;
    public static final int MENU_CONTACTLIST = 1;
    public static final int MENU_SETTING = 2;
    public static final int MENU_ACCOUNT = MENU_SETTING + 2;
    
    public static final int NORMAL_MENU_COUNT = MENU_SETTING + 1;
    
    public static interface MenuCallback {
        public void onActiveMenuChanged(int menuId);
        public void onMenuClicked();
    }

    private static class NormalMenu {
        int menuId;
        int icon;
        int name;
        
        public NormalMenu(int menuid, int icon, int name) {
            this.menuId = menuid;
            this.icon = icon;
            this.name = name;
        }
    }
    
    private NormalMenu[] NORMAL_MENUS = new NormalMenu[]{
            new NormalMenu(MENU_CHATLIST, R.drawable.nav_chats_normal, R.string.message), 
            new NormalMenu(MENU_CONTACTLIST, R.drawable.nav_contact_normal, R.string.contact), 
            new NormalMenu(MENU_SETTING, R.drawable.nav_settings_normal, R.string.setting)};
    
    private MenuAdapter mAdapter;
    private MenuCallback mActiveMenuChangedCallback;
    private int mCurrentActiveMenuId = 0;
    private ListView mMenuListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActiveMenuChangedCallback = (MenuCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        mMenuListView = listView;
         
        mAdapter = new MenuAdapter();
        listView.setAdapter(mAdapter);
       
        return view;
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        mAdapter.unregisterAccountStatusListener();
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(arg2 == mAdapter.getCount() - 1) {
            createNewAccount();
            return;
        }
        
        if(mCurrentActiveMenuId != arg2) {
            clearLastActivedMenu();
            if(arg2 < NORMAL_MENU_COUNT) {
                mActiveMenuChangedCallback.onActiveMenuChanged(arg2);
            } else if(arg2 >= MENU_ACCOUNT) {
                mActiveMenuChangedCallback.onActiveMenuChanged(arg2);
            }
            mCurrentActiveMenuId = arg2;
            updateActivedMenu();
        }
        mActiveMenuChangedCallback.onMenuClicked();
        
    }
    
    private void createNewAccount() {
        ChooseAccountTypeActivity.startChooseAccountTypeActivity(getActivity());
    }
    
    private void updateActivedMenu() {
        mMenuListView.getChildAt(mCurrentActiveMenuId).setActivated(true);
    }
    
    private void clearLastActivedMenu() {
        mMenuListView.getChildAt(mCurrentActiveMenuId).setActivated(false);
    }
    
    public void onAccountDeleted() {
 
        clearLastActivedMenu();
        mActiveMenuChangedCallback.onActiveMenuChanged(MENU_CHATLIST);
        mCurrentActiveMenuId = MENU_CHATLIST;
        updateActivedMenu();
        
        mActiveMenuChangedCallback.onMenuClicked();
        mAdapter.notifyDataSetChanged();
    }

    class MenuAdapter extends BaseAdapter implements LoginCallback, ReconnectCallback {

        private static final int NORMAL = 0;
        private static final int ACCOUNT_DIVIDER = 1;
        private static final int ACCOUNT = 2;
        private static final int CREATE_NEW_ACCOUNT = 3;

        private List<Account> mAccounts;
        private LayoutInflater mInflater;
        private LoginManager mLoginManager;

        public MenuAdapter() {
            mAccounts = AccountManager.getInstance(getActivity()).getAccounts();
            mInflater = LayoutInflater.from(getActivity());
            mLoginManager = (LoginManager) MMContext.getInstance().getService(MMContext.LOGIN_SERVICE);
            mLoginManager.registerLoginCallback(this);
            ReconnectManager.getInstance().registerReconnectCallback(this);
        }

        public void unregisterAccountStatusListener() {
            mLoginManager.unregisterLoginCallback(this);
            ReconnectManager.getInstance().unregisterReconnectCallback(this);
        }
        
        @Override
        public int getItemViewType(int position) {
            if (position < NORMAL_MENU_COUNT) {
                return NORMAL;
            }
            if (position == NORMAL_MENU_COUNT) {
                return ACCOUNT_DIVIDER;
            } 
            if(position == NORMAL_MENU_COUNT + mAccounts.size() + 1) {
                return CREATE_NEW_ACCOUNT;
            }
            return ACCOUNT;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public int getCount() {
            return NORMAL_MENU_COUNT + mAccounts.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            int type = getItemViewType(position);
            switch (type) {
                case NORMAL:
                    return NORMAL_MENUS[position];
                case ACCOUNT_DIVIDER:
                case CREATE_NEW_ACCOUNT:
                    return null;
                default:
                    return mAccounts.get(position - NORMAL_MENU_COUNT - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            Object item = getItem(position);
            switch (type) {
                case NORMAL:
                     convertView = getNormalMenuView(convertView, (NormalMenu)item);
                     break;
                case ACCOUNT_DIVIDER:
                    return getAccountDividerView();
                case CREATE_NEW_ACCOUNT:
                    return getCreateAccountView();
                default:
                    convertView = getAccountView(convertView, mAccounts.get(position - NORMAL_MENU_COUNT - 1));
                    break;
            }
            if(position == mCurrentActiveMenuId) {
                convertView.setActivated(true);
            } else {
                convertView.setActivated(false);
            }
            return convertView;
        }

        private View getAccountView(View convertView, Account account) {
            AccountMenuHolder holder;
            if(convertView == null || !(convertView.getTag() instanceof Account)) {
                convertView = mInflater.inflate(R.layout.item_account_menu, null);
                holder = new AccountMenuHolder();
                holder.textView = ((TextView)convertView.findViewById(R.id.account_name));
                holder.iconView = (ImageView) convertView.findViewById(R.id.account_icon);
                holder.statusView = (ImageView) convertView.findViewById(R.id.account_status);
                convertView.setTag(holder);
            } else {
                holder = (AccountMenuHolder) convertView.getTag();
            }
           
            holder.textView.setText(account.username);
            AccountType accountType = AccountType.getAccountTypeById(account.accountTypeId);
            if(accountType != null) {
                holder.iconView.setImageResource(accountType.icon);
            } else {
                holder.iconView.setImageResource(R.drawable.app_logo); 
            }
            if(mLoginManager.isSignedIn(account.jid)) {
                holder.statusView.setVisibility(View.GONE);
            } else {
                holder.statusView.setImageResource(android.R.drawable.ic_dialog_alert);
            }
            return convertView;
        }
        
        private View getAccountDividerView() {
            return mInflater.inflate(R.layout.item_account_divider_menu, null);
        }
        
        private View getCreateAccountView() {
            return mInflater.inflate(R.layout.add_new_account_menu, null);
        }
        
        private View getNormalMenuView(View convertView, NormalMenu item) {
            NormalMenuHolder holder;
            if(convertView == null || !(convertView.getTag() instanceof NormalMenu)) {
                convertView = mInflater.inflate(R.layout.item_normal_menu, null);
                holder = new NormalMenuHolder();
                holder.imageView = ((ImageView)convertView.findViewById(R.id.menu_item_icon));
                holder.textView = ((TextView)convertView.findViewById(R.id.menu_item_name));
                convertView.setTag(holder);
            } else {
                holder = (NormalMenuHolder) convertView.getTag();
            }
            
            holder.imageView.setImageResource(item.icon);
            holder.textView.setText(item.name);
            
            return convertView;
        }

        @Override
        public void onLoginSuccessed(String clientJid) {
            notifyDataSetChanged();
            
        }

        @Override
        public void onLoginFailed(String clientJid, int errorcode) {
            // TODO Auto-generated method stub
            notifyDataSetChanged();
        }

        @Override
        public void onLogoutFinished(String clientJid, boolean remove) {
            // TODO Auto-generated method stub
            notifyDataSetChanged();
        }

        @Override
        public void onConnected(String clientJid) {
            // TODO Auto-generated method stub
            notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(String clientJid) {
            // TODO Auto-generated method stub
            notifyDataSetChanged();
        }
    }
    
    static class NormalMenuHolder {
        ImageView imageView;
        TextView textView;
    }
    
    static class AccountMenuHolder {
        TextView textView;
        ImageView iconView;
        ImageView statusView;
    }
}
