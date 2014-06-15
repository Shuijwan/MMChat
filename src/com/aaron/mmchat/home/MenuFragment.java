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
import com.aaron.mmchat.core.AccountManager.Account;

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

        mAdapter = new MenuAdapter();
        listView.setAdapter(mAdapter);
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(mCurrentActiveMenuId != arg2) {
            if(arg2 < NORMAL_MENU_COUNT) {
                mActiveMenuChangedCallback.onActiveMenuChanged(arg2);
            } else if(arg2 >= MENU_ACCOUNT) {
                mActiveMenuChangedCallback.onActiveMenuChanged(arg2);
            }
            mCurrentActiveMenuId = arg2;
        }
        mActiveMenuChangedCallback.onMenuClicked();
        
    }

    class MenuAdapter extends BaseAdapter {

        private static final int NORMAL = 0;
        private static final int ACCOUNT_DIVIDER = 1;
        private static final int ACCOUNT = 2;

        private List<Account> mAccounts;
        private LayoutInflater mInflater;

        public MenuAdapter() {
            mAccounts = AccountManager.getInstance(getActivity()).getAccounts();
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getItemViewType(int position) {
            if (position < NORMAL_MENU_COUNT) {
                return NORMAL;
            }
            if (position == NORMAL_MENU_COUNT) {
                return ACCOUNT_DIVIDER;
            }
            return ACCOUNT;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getCount() {
            return NORMAL_MENU_COUNT + mAccounts.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            int type = getItemViewType(position);
            switch (type) {
                case NORMAL:
                    return NORMAL_MENUS[position];
                case ACCOUNT_DIVIDER:
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
                    return getNormalMenuView(convertView, (NormalMenu)item);
                case ACCOUNT_DIVIDER:
                    return getAccountDividerView();
                default:
                    return getAccountView(convertView, mAccounts.get(position - NORMAL_MENU_COUNT - 1));
            }
        }

        private View getAccountView(View convertView, Account account) {
            AccountMenuHolder holder;
            if(convertView == null || !(convertView.getTag() instanceof Account)) {
                convertView = mInflater.inflate(R.layout.item_account_menu, null);
                holder = new AccountMenuHolder();
                holder.textView = ((TextView)convertView.findViewById(R.id.account_name));
                convertView.setTag(holder);
            } else {
                holder = (AccountMenuHolder) convertView.getTag();
            }
           
            holder.textView.setText(account.username);
            
            return convertView;
        }
        
        private View getAccountDividerView() {
            return mInflater.inflate(R.layout.item_account_divider_menu, null);
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
        
        

    }
    
    static class NormalMenuHolder {
        ImageView imageView;
        TextView textView;
    }
    
    static class AccountMenuHolder {
        TextView textView;
    }
}
