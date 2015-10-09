package com.siavash.dualcamera.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.siavash.dualcamera.Constants;
import com.siavash.dualcamera.R;
import com.siavash.dualcamera.util.StringUtil;
import com.siavash.dualcamera.util.Util;
import com.siavash.dualcamera.util.customviews.Toolbar;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Share or save photos to file
 * Created by siavash on 3/6/2015.
 */
public class ShareFragment extends Fragment implements Toolbar.OnBackClickListener {

    private static final String TAG = ShareFragment.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar<ShareFragment> toolbar;
    @Bind({R.id.facebook, R.id.whatsapp, R.id.telegram, R.id.instagram, R.id.line, R.id.more}) List<Button> socialNetworks;
    @Bind(R.id.share_to) TextView shareTextView;
    @Bind(R.id.photo_container) ImageView image;

    private OnFragmentChange mCallback;
    private String mImageUrl;

    private ShareFragment() {
    }

    public static ShareFragment newInstance(String imageUrl) {
        ShareFragment shareFragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(Constants.IMAGE_URL, imageUrl);
        shareFragment.setArguments(args);
        return shareFragment;
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnFragmentChange) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentChange");
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int imageWidth = metrics.widthPixels;
        int imageHeight = metrics.heightPixels;

        mImageUrl = getArguments().getString(Constants.IMAGE_URL);
        Bitmap bitmap = Util.decodeSampledBitmap(Util.getFile(mImageUrl), imageWidth, imageHeight);
        image.setImageBitmap(bitmap);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        ButterKnife.bind(this, view);
        // Set up toolbar
        toolbar.setTitle("اشتراک گذاری");
        toolbar.setCallback(this);

        setTypefaces();
        setListeners();

        return view;
    }

    private void setTypefaces(){
        shareTextView.setTypeface(StringUtil.getFont(getActivity(), StringUtil.FONT_AFSANEH));
        for (Button button : socialNetworks){
            button.setTypeface(StringUtil.getFont(getActivity(), StringUtil.FONT_NAZANIN_BOLD));
        }
    }

    private void setListeners() {
        OnClickListener onClickListener = new OnClickListener();
        socialNetworks.get(0).setOnClickListener(onClickListener);
        socialNetworks.get(1).setOnClickListener(onClickListener);
        socialNetworks.get(2).setOnClickListener(onClickListener);
        socialNetworks.get(3).setOnClickListener(onClickListener);
        socialNetworks.get(4).setOnClickListener(onClickListener);
        socialNetworks.get(5).setOnClickListener(onClickListener);
    }

    private void shareIntent(String intentName, String socialName) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mImageUrl)));
        generateCustomIntent(intent, intentName, socialName);
    }


    private void generateCustomIntent(Intent prototype, String appNameToShareWith, String appNameInPersian) {
        List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(prototype, PackageManager.MATCH_DEFAULT_ONLY);
        boolean resolved = false;
        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo.name.contains(appNameToShareWith)) {
                    prototype.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    resolved = true;
                    break;
                }
            }
        }

        if (resolved) startActivity(prototype);
        else {
            Toast.makeText(getActivity(), appNameInPersian + " نصب نیست!", Toast.LENGTH_LONG).show();
        }
    }

    @Override public void goBack() {
        getActivity().onBackPressed();
    }

    private class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == socialNetworks.get(0).getId()) {
                shareIntent("facebook", "فیسبوک");
            } else if (id == socialNetworks.get(1).getId()) {
                shareIntent("whatsapp", "واتس اپ");
            } else if (id == socialNetworks.get(2).getId()) {
                shareIntent("telegram", "تلگرام");
            } else if (id == socialNetworks.get(3).getId()) {
                shareIntent("instagram", "اینستاگرام");
            } else if (id == socialNetworks.get(4).getId()) {
                shareIntent("line", "لاین");
            } else if (id == socialNetworks.get(5).getId()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mImageUrl)));
                startActivity(intent);
            }
        }
    }
}

