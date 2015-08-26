package com.siavash.dualcamera.util;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siavash.dualcamera.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Customized toolbar
 * Created by sia on 8/26/15.
 */
public class Toolbar implements View.OnClickListener {

    @Bind(R.id.title) TextView mTextViewTitle;
    @Bind(R.id.back_btn) ImageButton mBackButton;
    @Bind(R.id.next_btn) ImageButton mNextButton;

    private String mTitle;
    private int mNextButtonResource, mBackButtonResource;
    private Bitmap mNextButtonImage, mBackButtonImage;

    private OnClickListener mCallback;

    private Toolbar(Builder builder) {
        if (builder.parent instanceof Fragment) {
            RelativeLayout layout = (RelativeLayout) builder.view.findViewById(builder.resLayoutId);
            ButterKnife.bind(this, layout);
        } else {
            throw new ClassCastException("Toolbar parent is not an instance of fragment");
        }

        mCallback = (OnClickListener) builder.parent;

        mTitle = builder.title;
        mNextButtonResource = builder.nextButtonResource;
        mBackButtonResource = builder.backButtonResource;
        mNextButtonImage = builder.nextButtonImage;
        mBackButtonImage = builder.backButtonImage;
        setText();
        setNextButtonResource();
        setBackButtonResource();
        setNextButtonImage();
        setBackButtonImage();
    }

    private void setText() {
        if (mTitle != null)
            mTextViewTitle.setText(mTitle);
    }

    private void setNextButtonResource() {
        if (mNextButtonResource != 0)
            mNextButton.setImageResource(mNextButtonResource);
    }

    private void setBackButtonResource() {
        if (mBackButtonResource != 0)
            mBackButton.setImageResource(mBackButtonResource);
    }

    private void setNextButtonImage() {
        if (mNextButtonImage != null)
            mNextButton.setImageBitmap(mNextButtonImage);
    }

    private void setBackButtonImage() {
        if (mBackButtonImage != null)
            mBackButton.setImageBitmap(mBackButtonImage);
    }

    @Override public void onClick(View v) {
        int id = v.getId();
        if (id == mNextButton.getId()) {
            mCallback.nextButtonOnClick();
        } else if (id == mBackButton.getId()) {
            mCallback.backButtonOnClick();
        }
    }

    public interface OnClickListener {
        void nextButtonOnClick();

        void backButtonOnClick();
    }

    public static class Builder<T> {
        private T parent;
        private View view;
        private int resLayoutId;
        private String title;
        private int nextButtonResource, backButtonResource;
        private Bitmap nextButtonImage, backButtonImage;

        public Builder(T parent, int resLayoutId, View view) {
            this.parent = parent;
            this.resLayoutId = resLayoutId;
            this.view = view;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getNextButtonResource() {
            return nextButtonResource;
        }

        public Builder setNextButtonResource(int nextButtonResource) {
            this.nextButtonResource = nextButtonResource;
            return this;
        }

        public int getBackButtonResource() {
            return backButtonResource;
        }

        public Builder setBackButtonResource(int backButtonResource) {
            this.backButtonResource = backButtonResource;
            return this;
        }

        public Bitmap getNextButtonImage() {
            return nextButtonImage;
        }

        public Builder setNextButtonImage(Bitmap nextButtonImage) {
            this.nextButtonImage = nextButtonImage;
            return this;
        }

        public Bitmap getBackButtonImage() {
            return backButtonImage;
        }

        public Builder setBackButtonImage(Bitmap backButtonImage) {
            this.backButtonImage = backButtonImage;
            return this;
        }

        public Toolbar build() {
            return new Toolbar(this);
        }
    }
}
