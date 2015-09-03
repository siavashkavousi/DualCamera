package com.siavash.dualcamera.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.siavash.dualcamera.R;

/**
 * Custom Toast with default Persian typeface
 * Created by sia on 9/1/15.
 */
public class CustomToast extends Toast {

    private Context mContext;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    private CustomToast(Context context, CharSequence message) {
        super(context);
        mContext = context;
        setUpCustomToast(message);
    }

    @NonNull public static Toast makeText(Context context, CharSequence text) {
        return new CustomToast(context, text);
    }

    private void setUpCustomToast(CharSequence message) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast_layout, null);

        TextView text = (TextView) view.findViewById(R.id.message);
        text.setText(message);

        setGravity(Gravity.BOTTOM, 0, 0);
        setDuration(Toast.LENGTH_LONG);
        setView(view);
    }
}
