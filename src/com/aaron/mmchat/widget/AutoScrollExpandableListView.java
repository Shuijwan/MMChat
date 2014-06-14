package com.aaron.mmchat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

public class AutoScrollExpandableListView extends ExpandableListView implements
        OnGroupClickListener {
  
    private static final int SCROLL_DURATION = 200;

    public AutoScrollExpandableListView(Context context) {
        this(context, null);
    }

    public AutoScrollExpandableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnGroupClickListener(this);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View view, final int groupPosition,
            long groupId) {
        if (isGroupExpanded(groupPosition)) {
            collapseGroup(groupPosition);
        } else {
            expandGroup(groupPosition, false);

            if (getExpandableListAdapter().getChildrenCount(groupPosition) > 0) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        int distance = 0;

                        final int targetPos = getFlatListPosition(ExpandableListView
                                .getPackedPositionForGroup(groupPosition)) + 1;
                        final int lastVisiblePos = getLastVisiblePosition();

                        if (targetPos >= lastVisiblePos) {
                            final View lastView = getChildAt(getChildCount() - 1);
                            final int lastViewHeight = lastView.getHeight();
                            final int lastViewPixelsShowing = getHeight() - lastView.getTop();

                            distance += lastViewHeight - lastViewPixelsShowing;

                            if (targetPos > lastVisiblePos) {
                                final View targetView = getExpandableListAdapter()
                                        .getChildView(
                                                groupPosition,
                                                0,
                                                getExpandableListAdapter().getChildrenCount(
                                                        groupPosition) == 1, null,
                                                AutoScrollExpandableListView.this);
                                measureChild(targetView, MeasureSpec.makeMeasureSpec(getWidth(),
                                        MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                                        getHeight(), MeasureSpec.UNSPECIFIED));

                                distance += getDividerHeight() + targetView.getMeasuredHeight();
                            }
                        }

                        smoothScrollBy(distance, SCROLL_DURATION);
                    }
                });
            }
        }

        return true;
    }
}
