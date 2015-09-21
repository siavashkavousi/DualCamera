package com.siavash.dualcamera.util.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.siavash.dualcamera.util.StringUtil;

/**
 * Custom Button with default Persian typeface
 * Created by sia on 9/19/15.
 */
public class Button extends android.widget.Button {

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpFont(context, StringUtil.FONT_NAZANIN_BOLD);
    }

    public void setUpFont(Context context, String fontName) {
        Typeface typeface = StringUtil.getFont(context, fontName);
        setTypeface(typeface);
    }

    public void setUpText(String text) {
        setText(text);
    }
}
