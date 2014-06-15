package com.aaron.mmchat.home;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aaron.mmchat.R;
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

public class ContactListFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private ContactListAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle(R.string.contact);

        final View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mExpandableListView = (StickyHeaderExpandableListView) root.findViewById(R.id.contact_list);
        mExpandableListView.setEmptyView(root.findViewById(R.id.empty_view));
        mAdapter = new ContactListAdapter();

        mExpandableListView.setAdapter(mAdapter);

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
            View view = convertView;
            ViewHolder holder;

            if (view instanceof LinearLayout) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = mLayoutInflater.inflate(R.layout.item_contact_list_contact, parent, false);

                holder = new ViewHolder();

                holder.avator = (ImageView) view.findViewById(R.id.contact_avatar);

                holder.name = (TextView) view.findViewById(R.id.contact_display_name);
                holder.presence = (TextView) view.findViewById(R.id.contact_presence_text);

                view.setTag(holder);
            }

            final Contact data = getChild(groupPosition, childPosition);

            holder.name.setText(data.getName());
            holder.presence.setText(data.getPresenceStatus());

            return view;
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
        public void onContactRemovedFailed(String clientJid, String contact, int errorcode) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onContactRemoved(String clientJid, String contact) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onContactAdded(String clientJid, String contact) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onContactAddedFailed(String clientJid, String contact, int errorcode) {
            // TODO Auto-generated method stub
            
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

        @Override
        public void onContactUpdated(String clientJid, String contact) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onContactPresenceUpdated(String clientJid, String contact) {
            // TODO Auto-generated method stub
            
        }

    }

    static class ViewHolder {
        ImageView avator;
        TextView name;
        TextView presence;
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

}
