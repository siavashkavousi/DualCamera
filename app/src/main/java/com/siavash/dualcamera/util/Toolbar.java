package com.siavash.dualcamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
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
public class Toolbar extends RelativeLayout implements View.OnClickListener {

    @Bind(R.id.title) TextView titleTextView;
    @Bind(R.id.back_btn) ImageButton backButton;
    @Bind(R.id.next_btn) ImageButton nextButton;

    private OnClickListener mCallback;

    public Toolbar(Context context, Builder builder) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbar, this, true);
        ButterKnife.bind(this, view);

        mCallback = (OnClickListener) builder.parent;
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        if (builder.title != null) titleTextView.setText(builder.title);
        if (builder.backButtonImage != null) backButton.setImageBitmap(builder.backButtonImage);
        if (builder.nextButtonImage != null) nextButton.setImageBitmap(builder.nextButtonImage);
        if (builder.backButtonResource != 0)
            backButton.setImageResource(builder.backButtonResource);
        if (builder.nextButtonResource != 0)
            nextButton.setImageResource(builder.nextButtonResource);

        if (builder.attachedToParent) {
            FrameLayout layout = (FrameLayout) builder.view;
            layout.addView(this);
            layout.requestLayout();
        }
    }

    @Override public void onClick(View v) {
        int id = v.getId();
        if (id == nextButton.getId()) {
            mCallback.nextButtonOnClick();
        } else if (id == backButton.getId()) {
            mCallback.backButtonOnClick();
        }
    }

    public interface OnClickListener {
        void nextButtonOnClick();

        void backButtonOnClick();
    }

    public static class Builder<T> {
        private Context context;
        private View view;
        private boolean attachedToParent;
        private T parent;
        private String title;
        private int nextButtonResource, backButtonResource;
        private Bitmap nextButtonImage, backButtonImage;

        public Builder(Context context, T parent, View view, boolean attachedToParent) {
            this.context = context;
            this.attachedToParent = attachedToParent;
            this.view = view;
            this.parent = parent;
        }

        public String getTitle() {
            return title;
        }

        public Builder<T> setTitle(String title) {
            this.title = title;
            return this;
        }

        public int getNextButtonResource() {
            return nextButtonResource;
        }

        public Builder<T> setNextButtonResource(int nextButtonResource) {
            this.nextButtonResource = nextButtonResource;
            return this;
        }

        public int getBackButtonResource() {
            return backButtonResource;
        }

        public Builder<T> setBackButtonResource(int backButtonResource) {
            this.backButtonResource = backButtonResource;
            return this;
        }

        public Bitmap getNextButtonImage() {
            return nextButtonImage;
        }

        public Builder<T> setNextButtonImage(Bitmap nextButtonImage) {
            this.nextButtonImage = nextButtonImage;
            return this;
        }

        public Bitmap getBackButtonImage() {
            return backButtonImage;
        }

        public Builder<T> setBackButtonImage(Bitmap backButtonImage) {
            this.backButtonImage = backButtonImage;
            return this;
        }

        public boolean isAttachedToParent() {
            return attachedToParent;
        }

        public Builder<T> setAttachedToParent(boolean attachedToParent) {
            this.attachedToParent = attachedToParent;
            return this;
        }

        public View getParentView() {
            return view;
        }

        public Builder<T> setParentView(View parentView) {
            view = parentView;
            return this;
        }

        public T getParent(){
            return parent;
        }

        public Builder<T> setParent(T parent){
            this.parent = parent;
            return this;
        }

        public Toolbar build() {
            return new Toolbar(context, this);
        }
    }
}
