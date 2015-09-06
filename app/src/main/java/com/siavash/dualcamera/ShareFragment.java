//package com.hojjat.nameInPoem;
//
//import android.app.Fragment;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Typeface;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.siavash.dualcamera.R;
//import com.siavash.dualcamera.util.CustomToast;
//import com.siavash.dualcamera.util.StringUtil;
//
//import java.io.File;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Created by siavash on 3/6/2015.
// */
//public class ShareFragment extends Fragment {
//
//    private static final String TAG = ShareFragment.class.getSimpleName();
//
//    @Bind({R.id.facebook, R.id.twitter, R.id.viber, R.id.instagram, R.id.whatsapp, R.id.line, R.id.flickr, R.id.tango}) List<Button> socialNetworks;
//    @Bind(R.id.share) TextView shareTextView;
//    @Bind(R.id.editText) EditText dedicatedEditText;
//    @Bind(R.id.image_layout) ImageView image;
//
//    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_share, container, false);
//        ButterKnife.bind(this, view);
//
//        setUpImageView();
//        setFonts();
//        setTexts();
//        setListeners();
//        return view;
//    }
//
//    private void setUpToolbar() {
//        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
//        toolbarTitle = (TextView) toolbar.findViewById(R.id.title);
//
//        previousButton = (ImageButtona) toolbar.findViewById(R.id.back_btn);
//        nextButton = (ImageButton) toolbar.findViewById(R.id.next_btn);
//    }
//
//    private void setUpImageView() {
//        String path = getIntent().getStringExtra(Constants.IMAGE_PATH);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        // images are loaded by ARG_8888 but just for sure
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        if (bitmap != null) image.setImageBitmap(bitmap);
//    }
//
//    private void setFonts() {
//        Typeface typeface = StringUtil.getFont(this, StringUtil.FONT_AFSANEH);
//        toolbarTitle.setTypeface(typeface);
//        typeface = StringUtil.getFont(this, StringUtil.FONT_IRAN_NASTALIQ);
//        shareTextView.setTypeface(typeface);
//    }
//
//    private void setTexts() {
//        toolbarTitle.setText(PersianReshape.reshape("اشتراک گذاری"));
//        shareTextView.setText("شبکه های اجتماعی");
//        dedicatedEditText.setText(PersianReshape.reshape("تقدیم به ..."));
//    }
//
//    private void setListeners() {
//        OnClickListener onClickListener = new OnClickListener();
//        facebook.setOnClickListener(onClickListener);
//        twitter.setOnClickListener(onClickListener);
//        viber.setOnClickListener(onClickListener);
//        instagram.setOnClickListener(onClickListener);
//        whatsapp.setOnClickListener(onClickListener);
//        line.setOnClickListener(onClickListener);
//        flicker.setOnClickListener(onClickListener);
//        tango.setOnClickListener(onClickListener);
//
//        previousButton.setOnClickListener(onClickListener);
//        nextButton.setOnClickListener(onClickListener);
//    }
//
//    private void shareIntent(String intentName, String socialName) {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("image/jpeg");
//        String path = getIntent().getStringExtra(Constants.IMAGE_PATH);
//        File imageFile = new File(path);
//        intent.putExtra(Intent.EXTRA_TEXT, dedicatedEditText.getText());
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
//        generateCustomIntent(intent, intentName, socialName);
//    }
//
//
//    private void generateCustomIntent(Intent prototype, String appNameToShareWith, String appNameInPersian) {
//        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(prototype, PackageManager.MATCH_DEFAULT_ONLY);
//        boolean resolved = false;
//        if (!resInfo.isEmpty()) {
//            for (ResolveInfo resolveInfo : resInfo) {
//                if (resolveInfo.activityInfo.name.contains(appNameToShareWith)) {
//                    prototype.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
//                    resolved = true;
//                    break;
//                }
//            }
//        }
//
//        if (resolved) startActivity(prototype);
//        else {
//            CustomToast.makeText(getActivity(), "تو که " + appNameInPersian + " رو نصب نداری! ");
//        }
//    }
//
//    private class OnClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//            if (id == facebook.getId()) {
//                shareIntent("facebook", "فیسبوک");
//            } else if (id == twitter.getId()) {
//                shareIntent("twitter", "توییتر");
//            } else if (id == viber.getId()) {
//                shareIntent("viber", "وایبر");
//            } else if (id == instagram.getId()) {
//                shareIntent("instagram", "اینستاگرام");
//            } else if (id == whatsapp.getId()) {
//                shareIntent("whatsapp", "واتس اپ");
//            } else if (id == line.getId()) {
//                shareIntent("line", "لاین");
//            } else if (id == flicker.getId()) {
//                shareIntent("flicker", "فلیکر");
//            } else if (id == tango.getId()) {
//                shareIntent("tango", "تانگو");
//            } else if (id == previousButton.getId()) {
//                onBackPressed();
//            } else if (id == nextButton.getId()) {
//                // TODO save image instructions
//            }
//        }
//    }
//}
//
