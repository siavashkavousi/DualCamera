package com.siavash.dualcamera.util.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.siavash.dualcamera.R;
import com.siavash.dualcamera.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Customized toolbar
 * Created by sia on 8/26/15.
 */
public class Toolbar<T> extends RelativeLayout implements View.OnClickListener {

    @Bind(R.id.title) TextView title;
    @Bind(R.id.back_btn) ImageButton backButton;
    @Bind(R.id.action_btn) Button actionButton;

    private OnBackClickListener mBackCallback;
    private OnActionClickListener mActionCallback;

    public Toolbar(Context context) {
        super(context);
        setUp(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public Toolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(context);
    }

    public void setUp(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_toolbar, this, true);
        ButterKnife.bind(this, view);

        backButton.setOnClickListener(this);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setCallback(T callback) {
        if (backButton.getVisibility() == View.VISIBLE)
            mBackCallback = (OnBackClickListener) callback;
        if (actionButton.getVisibility() == View.VISIBLE)
            mActionCallback = (OnActionClickListener) callback;
    }

    public void setRightButtonResource(int resId) {
        backButton.setImageResource(resId);
    }

    public void setRightButtonBitmap(Bitmap bitmap) {
        backButton.setImageBitmap(bitmap);
    }

    public void setActionButtonText(String text){
        actionButton.setText(text);
        actionButton.setUpFont(getContext(), StringUtil.FONT_AFSANEH);
    }

    public void setActionButtonVisibility(int visibility) {
        actionButton.setVisibility(visibility);
        setActionButtonCallback();
    }

    private void setActionButtonCallback() {
        actionButton.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        int id = v.getId();
        if (id == backButton.getId()) {
            mBackCallback.goBack();
        } else if (id == actionButton.getId()) {
            mActionCallback.doAction();
        }
    }

    public interface OnBackClickListener {
        void goBack();
    }

    public interface OnActionClickListener {
        void doAction();
    }
}
