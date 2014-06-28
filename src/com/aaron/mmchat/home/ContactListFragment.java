package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.aaron.mmchat.R;
import com.aaron.mmchat.chat.ChatActivity;
import com.aaron.mmchat.core.ChatManager;
import com.aaron.mmchat.core.Contact;
import com.aaron.mmchat.core.ContactGroup;
import com.aaron.mmchat.core.ContactManager;
import com.aaron.mmchat.core.ContactManager.ContactListCallback;
import com.aaron.mmchat.core.MMContext;
import com.aaron.mmchat.widget.AbstractStickyHeaderExpandableListViewAdapter;
import com.aaron.mmchat.widget.StickyHeaderExpandableListView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @Title: ContactListFragment.java
 * @Package: com.aaron.mmchat.home
 * @Description:
 * @Author: aaron
 * @Date: 2014-6-14
 */

public class ContactListFragment extends Fragment implements OnChildClickListener, OnItemLongClickListener {

    private ExpandableListView mExpandableListView;
    private ContactListAdapter mAdapter;
    private ChatManager mChatManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mChatManager = (ChatManager) MMContext.getInstance(activity).getService(MMContext.CHAT_SERVICE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle(R.string.contact);

        final View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mExpandableListView = (StickyHeaderExpandableListView) root.findViewById(R.id.contact_list);
        mExpandableListView.setEmptyView(root.findViewById(R.id.empty_view));
        mAdapter = new ContactListAdapter();

        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setOnChildClickListener(this);
        mExpandableListView.setOnItemLongClickListener(this);

        return root;
    }

    private class ContactListAdapter extends AbstractStickyHeaderExpandableListViewAdapter implements ContactListCallback {

        private LayoutInflater mLayoutInflater;
        private Map<String, ArrayList<ContactGroup>> mAllContactList;
        private ContactManager mContactManager;
        private SparseArray<ContactGroup> mSparseArray;

        public ContactListAdapter() {
            mLayoutInflater = LayoutInflater.from(getActivity());
            mContactManager = (ContactManager) MMContext.getInstance(getActivity()).getService(
                    MMContext.CONTACT_SERVICE);
            mAllContactList = mContactManager.getAllContactList();
            mSparseArray = new SparseArray<ContactGroup>();
            mContactManager.registerContactListCallback(this);
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getGroupCount() {
            int count = 0;
            Collection<ArrayList<ContactGroup>> valus = mAllContactList.values();
            Iterator<ArrayList<ContactGroup>> iterator = valus.iterator();
            while (iterator.hasNext()) {
                count += iterator.next().size();
            }
            return count;
        }

        @Override
        public ContactGroup getGroup(int groupPosition) {
            if (mSparseArray.get(groupPosition) != null) {
                return mSparseArray.get(groupPosition);
            }

            int current = 0;
            int size;
            Collection<ArrayList<ContactGroup>> valus = mAllContactList.values();
            Iterator<ArrayList<ContactGroup>> iterator = valus.iterator();
            ArrayList<ContactGroup> groups;
            while (iterator.hasNext()) {
                groups = iterator.next();
                size = groups.size();
                if (current + size <= groupPosition) {
                    current += size;
                    continue;
                } else {
                    ContactGroup group = groups.get(groupPosition - current);
                    mSparseArray.put(groupPosition, group);
                    return group;
                }
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (view == null) {
                view = (TextView) mLayoutInflater.inflate(R.layout.item_contact_list_group, parent,
                        false);
            }

            setGroupName(view, groupPosition);

            view.setCompoundDrawablesWithIntrinsicBounds(
                    isExpanded ? R.drawable.group_indicator_expanded
                            : R.drawable.group_indicator_collapsed, 0, 0, 0);

            return view;
        }

        @Override
        public View getHeaderView(ViewGroup parent) {
            return mLayoutInflater.inflate(R.layout.item_contact_list_group, parent, false);
        }

        @Override
        public void updateHeaderViewContent(View headerView, int groupPosition) {
            setGroupName(headerView, groupPosition);
        }

        private void setGroupName(View groupView, int groupPos) {
            String groupName = getGroup(groupPos).getName();

            final TextView groupTextView = (TextView) groupView;
            if (getInvisibleGroupViewIndex() != groupPos) {
                groupTextView.setVisibility(View.VISIBLE);
            }
            groupTextView.setText(groupName);
        }

        @Override
        public void updateGroupViewVisible(View groupView, boolean visible) {
            final int visibility = visible ? View.VISIBLE : View.INVISIBLE;
            groupView.setVisibility(visibility);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            ContactGroup group = getGroup(groupPosition);
            return group.getContacts().size();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public Contact getChild(int groupPosition, int childPosition) {
            ContactGroup group = getGroup(groupPosition);
            return group.getContacts().get(childPosition);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.item_contact_list_contact, parent, false);

                holder = new ViewHolder();

                holder.avator = (ImageView) convertView.findViewById(R.id.contact_avatar);

                holder.name = (TextView) convertView.findViewById(R.id.contact_display_name);
                holder.presence = (TextView) convertView.findViewById(R.id.contact_presence_text);

                convertView.setTag(holder);
                
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Contact data = getChild(groupPosition, childPosition);

            holder.avator.setTag(data);
            holder.name.setText(data.getName());
            holder.presence.setText(data.getPresenceStatus());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void onContactListAllRefreshed(String clientJid) {
            mSparseArray.clear();
            notifyDataSetChanged();
        }

        @Override
        public void onContactGroupsAdded(String clientJid, Collection<String> groups) {
            mSparseArray.clear();
            notifyDataSetChanged();
            
        }

        @Override
        public void onContactGroupsRemoved(String clientJid, Collection<String> groups) {
            mSparseArray.clear();
            notifyDataSetChanged();
            
        }
    }

    public static class ViewHolder {
        ImageView avator;
        TextView name;
        TextView presence;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contactlist_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contactlist_menu_search:
                
                break;
            case R.id.contactlist_menu_groupchat:
                break;
            default:
                break;
        }

        return true;
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
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        ContactGroup contactGroup = mAdapter.getGroup(groupPosition);
        Contact contact = mAdapter.getChild(groupPosition, childPosition);
        ChatActivity.startP2PChatActivity(getActivity(), contactGroup.getClientJid(), contact.getJid());
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ExpandableListView listView = (ExpandableListView) arg0;
        long packedPos = mExpandableListView.getExpandableListPosition(arg2);
        int type = ExpandableListView.getPackedPositionType(packedPos);
        int firstViewPos = listView.getFirstVisiblePosition();
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)  {
            int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
            ContactGroup group = mAdapter.getGroup(groupPos);
            View view = mExpandableListView.getChildAt(arg2 - firstViewPos);
            ViewHolder holder = (ViewHolder) view.getTag();
            Contact contact = (Contact) holder.avator.getTag();
            showContactOperationDialog(group, contact);
        }
            
        return true;
    }

    private void showContactOperationDialog(final ContactGroup group, final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(contact.getName());
        String[] operations;
        boolean available = contact.getPresence() == Contact.AVAILABLE;
        if(available) {
            operations = new String[1];
            operations[0] = "delete";
        } else {
            operations = new String[2];
            operations[0] = "delete";
            operations[1] = "need him(her)";
        }
        builder.setItems(operations, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    group.removeContact(contact);
                } else if(which == 1) {
                    
                }
                
            }
        });
        builder.show();
    }
}
