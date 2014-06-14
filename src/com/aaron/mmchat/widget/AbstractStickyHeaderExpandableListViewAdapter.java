package com.aaron.mmchat.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 *
 * @Title: StickyHeaderExpandableListViewAdapter.java
 * @Package: com.aaron.mmchat.widgets
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2013-4-28
 *
 */

public abstract class AbstractStickyHeaderExpandableListViewAdapter extends BaseExpandableListAdapter {
    
    private int mInvisibleGroupViewIndex = -1;
    
    public abstract View getHeaderView(ViewGroup parent);
    
    public abstract void updateHeaderViewContent(View headerView, int groupPos);
    
    public abstract void updateGroupViewVisible(View groupView, boolean visible);
    
    public void setInvisibleGroupViewIndex(int groupIndex)
    {
        mInvisibleGroupViewIndex = groupIndex;
    }
    
    public int getInvisibleGroupViewIndex()
    {
        return mInvisibleGroupViewIndex;
    }
}
