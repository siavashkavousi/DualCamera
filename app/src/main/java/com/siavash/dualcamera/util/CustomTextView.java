package com.siavash.dualcamera.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.siavash.dualcamera.util.StringUtil;

/**
 * Custom Text View with default Persian typeface
 * Created by sia on 8/26/15.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpFont(context, StringUtil.FONT_AFSANEH);
    }

    public void setUpFont(Context context, String fontName){
        Typeface typeface = StringUtil.getFont(context, fontName);
        setTypeface(typeface);
    }

    public void setUpText(String text) {
        setText(text);
    }
}
