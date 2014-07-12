
package com.aaron.mmchat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aaron.mmchat.R;
import com.aaron.mmchat.core.Contact;

public class AvatarView extends FrameLayout {
    
    private ImageView mAvatarView;

    private ImageView mPresenceView;

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.avatar_view_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAvatarView = (ImageView) findViewById(R.id.avatar_view);
        mPresenceView = (ImageView) findViewById(R.id.presence_view);
    }

    public void setAvator(int resId) {
        mAvatarView.setImageResource(resId);
    }
    
    public void setAvator(Bitmap bitmap) {
        mAvatarView.setImageBitmap(bitmap);
    }
    
    public void setPresence(int presence) {
        switch (presence) {
            case Contact.AVAILABLE:
            case Contact.AWAY:
            case Contact.DND:
                mPresenceView.setImageResource(R.drawable.presence_available);
                break;
            case Contact.UNAVAILABLE:
                mPresenceView.setImageResource(R.drawable.presence_unavailable);
                break;
            default:
                break;
        }
    }
    
}
