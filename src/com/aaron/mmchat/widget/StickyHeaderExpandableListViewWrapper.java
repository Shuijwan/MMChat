package com.aaron.mmchat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 
 * @Title: StickyHeaderExpandableListViewWrapper.java
 * @Package: com.aaron.mmchat.widgets
 * @Description:
 * 
 * @Author: aaron
 * @Date: 2013-4-28
 * 
 */

public class StickyHeaderExpandableListViewWrapper extends FrameLayout
{

    private View mStickyHeader;// the sticky header view

    private int mHeaderGroupPos = -1;// current group position the header view indicate

    private int mHeaderYOffset;// the Y offset of header view

    public StickyHeaderExpandableListViewWrapper(Context context)
    {
        this(context, null, 0);
    }

    public StickyHeaderExpandableListViewWrapper(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public StickyHeaderExpandableListViewWrapper(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setHeaderVisiable(int visiable)
    {
        if (mStickyHeader.getVisibility() != visiable)
        {
            mStickyHeader.setVisibility(visiable);
        }
    }

    public void setHeaderView(View view)
    {
        mStickyHeader = view;
        View list = getChildAt(0);
        LayoutParams params = new LayoutParams(list.getMeasuredWidth() - list.getPaddingLeft() - list.getPaddingRight(), view.getLayoutParams().height);
        params.leftMargin = list.getPaddingLeft();
        params.rightMargin = list.getPaddingRight();
        mStickyHeader.setLayoutParams(params);
        addView(mStickyHeader);
    }

    public View getHeaderView()
    {
        return mStickyHeader;
    }

    public int getHeaderViewHeight()
    {
        MarginLayoutParams params = (MarginLayoutParams) mStickyHeader.getLayoutParams();
        int width = getMeasuredWidth() - (params == null ? 0 : params.leftMargin + params.rightMargin);
        int parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int parentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY);
        measureChild(mStickyHeader, parentWidthMeasureSpec, parentHeightMeasureSpec);
        return mStickyHeader.getMeasuredHeight();
    }

    public void updateHeaderYOffset(int yoffset)
    {
        if (mHeaderYOffset != yoffset)
        {
            mStickyHeader.setTranslationY(yoffset);
            mHeaderYOffset = yoffset;
        }
    }

    public void setHeaderGroupPos(int groupPos)
    {
        mHeaderGroupPos = groupPos;
    }

    public int getHeaderGroupPos()
    {
        return mHeaderGroupPos;
    }

    public int getHeaderYOffset()
    {
        return mHeaderYOffset;
    }

    public void initHeaderState()
    {
        mHeaderGroupPos = -1;
    }
}
