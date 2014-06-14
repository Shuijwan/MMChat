package com.aaron.mmchat.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.lang.ref.WeakReference;

/**
 * 
 * @Title: StickyHeaderExpandableListView.java
 * @Package: com.aaron.mmchat.widgets
 * @Description:
 * 
 * @Author: aaron
 * @Date: 2013-4-28
 * 
 */

public class StickyHeaderExpandableListView extends AutoScrollExpandableListView implements OnScrollListener, OnClickListener
{

    private StickyHeaderExpandableListViewWrapper mWrapper;

    private final static int ZERO = 0;

    private boolean isListScrolling;

    private boolean isGroupViewInVisible = false;

    private WeakReference<View> groupViewRef;

    private final DataSetObserver mDataSetObserver = new DataSetObserver()
    {

        @Override
        public void onChanged()
        {
            mWrapper.initHeaderState();
        }

        @Override
        public void onInvalidated()
        {
            mWrapper.initHeaderState();
        }
    };

    public boolean isScrolling()
    {
        return isListScrolling;
    }

    public StickyHeaderExpandableListView(Context context)
    {
        this(context, null);
    }

    public StickyHeaderExpandableListView(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.expandableListViewStyle);
    }

    public StickyHeaderExpandableListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setOnScrollListener(this);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (mWrapper == null)
        {
            ViewGroup parent = (ViewGroup) getParent();
            int listIndex = parent.indexOfChild(this);
            parent.removeView(this);

            setVisibility(View.VISIBLE);

            mWrapper = new StickyHeaderExpandableListViewWrapper(getContext());

            ViewGroup.MarginLayoutParams marginParams = (MarginLayoutParams) getLayoutParams();

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            setLayoutParams(params);

            mWrapper.addView(this);

            mWrapper.setLayoutParams(marginParams);
            parent.addView(mWrapper, listIndex);
        }
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter)
    {
        if (!(adapter instanceof AbstractStickyHeaderExpandableListViewAdapter))
        {
            throw new IllegalArgumentException("adapter must be StickyHeaderExpandableListViewAdapter!");
        }
        super.setAdapter(adapter);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

        AbstractStickyHeaderExpandableListViewAdapter adapter = (AbstractStickyHeaderExpandableListViewAdapter) getExpandableListAdapter();

        if (adapter != null && mWrapper != null)
        {
            if (mWrapper.getHeaderView() == null)
            {
                View headerView = adapter.getHeaderView(mWrapper);
                headerView.setOnClickListener(this);
                mWrapper.setHeaderView(headerView);
                adapter.registerDataSetObserver(mDataSetObserver);
            }

            if (adapter.isEmpty())
            {
                mWrapper.setHeaderVisiable(View.GONE);
                return;
            }

            ExpandableListView expandableView = (ExpandableListView) view;
            int firstViewPos = view.getFirstVisiblePosition();
            int lastViewPos = view.getLastVisiblePosition();

            long packedPos = expandableView.getExpandableListPosition(firstViewPos);
            int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
            int type = ExpandableListView.getPackedPositionType(packedPos);

            int lastgroupPos = mWrapper.getHeaderGroupPos();

            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
            {
                setChildView(adapter, expandableView, firstViewPos, lastViewPos, groupPos, lastgroupPos);

            }
            else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
            {
                setGroupView(adapter, expandableView, groupPos, lastgroupPos);
            }
        }

    }

    private void setGroupView(AbstractStickyHeaderExpandableListViewAdapter adapter, ExpandableListView expandableView, int groupPos, int lastgroupPos)
    {
        View findView;
        if (expandableView.isGroupExpanded(groupPos))
        {
            findView = expandableView.getChildAt(0);

            if (findView == null)
            {
                return;
            }

            if (findView.getTop() == ZERO)// this may not happen if scroll too fast
            {
                mWrapper.setHeaderVisiable(View.GONE);
                adapter.updateGroupViewVisible(findView, true);
                adapter.setInvisibleGroupViewIndex(-1);
                isGroupViewInVisible = false;
            }
            else
            {
                mWrapper.setHeaderVisiable(View.VISIBLE);
                if (lastgroupPos != groupPos)
                {
                    adapter.updateHeaderViewContent(mWrapper.getHeaderView(), groupPos);
                }
                mWrapper.setHeaderGroupPos(groupPos);
                mWrapper.updateHeaderYOffset(0);
                adapter.updateGroupViewVisible(findView, false);
                adapter.setInvisibleGroupViewIndex(groupPos);
                groupViewRef = new WeakReference<View>(findView);
                isGroupViewInVisible = true;
            }
        }
        else
        {
            adapter.setInvisibleGroupViewIndex(-1);
            mWrapper.setHeaderVisiable(View.GONE);
            if (isGroupViewInVisible)// if isGroupViewInVisible is true, need reset the visibility manually
            {
                View groupView = groupViewRef.get();
                if (groupView != null)
                {
                    adapter.updateGroupViewVisible(groupView, true);
                    isGroupViewInVisible = false;
                }
            }
        }
    }

    private void setChildView(AbstractStickyHeaderExpandableListViewAdapter adapter, ExpandableListView expandableView, int firstViewPos,
            int lastViewPos, int groupPos, int lastgroupPos)
    {

        long packedPos;
        int type;
        View findView;
        mWrapper.setHeaderVisiable(View.VISIBLE);
        adapter.setInvisibleGroupViewIndex(-1);
        if (isGroupViewInVisible)// if isGroupViewInVisible is true, need reset the visibility manually
        {
            View groupView = groupViewRef.get();
            if (groupView != null)
            {
                adapter.updateGroupViewVisible(groupView, true);
                isGroupViewInVisible = false;
            }
        }
        if (lastgroupPos != groupPos)
        {
            adapter.updateHeaderViewContent(mWrapper.getHeaderView(), groupPos);
        }
        mWrapper.setHeaderGroupPos(groupPos);
        int childNumber = 0;
        for (int i = firstViewPos + 1; i < lastViewPos; i++)
        {
            packedPos = expandableView.getExpandableListPosition(i);
            type = ExpandableListView.getPackedPositionType(packedPos);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
            {
                break;
            }
            childNumber++;
        }
        findView = expandableView.getChildAt(childNumber);
        int headerOffset;

        int bottom = findView.getBottom();
        int headerHeight = mWrapper.getHeaderViewHeight();
        if (bottom >= headerHeight)
        {
            headerOffset = 0;
        }
        else
        {
            headerOffset = bottom - headerHeight;
        }

        mWrapper.updateHeaderYOffset(headerOffset);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL)
        {
            isListScrolling = true;
        }
        else
        {
            isListScrolling = false;
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view == mWrapper.getHeaderView())
        {
            int groupPos = mWrapper.getHeaderGroupPos();
            collapseGroup(groupPos);
        }
    }
}
