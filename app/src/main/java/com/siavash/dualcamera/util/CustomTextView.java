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

    private String[] fonts = {StringUtil.FONT_AFSANEH, StringUtil.FONT_DAST_NEVESHTE,
            StringUtil.FONT_DROID_ARABIK, StringUtil.FONT_FANTECY,
            StringUtil.FONT_IRAN_NASTALIQ, StringUtil.FONT_KOODAK,
            StringUtil.FONT_MASHIN_TAHRIR, StringUtil.FONT_NASKH,
            StringUtil.FONT_NAZANIN_BOLD, StringUtil.FONT_NEGAR,
            StringUtil.FONT_SANS, StringUtil.FONT_SETAREH};

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpFont(context, 5);
    }

    private void setUpFont(Context context, int index){
        Typeface typeface = StringUtil.getFont(context, fonts[index]);
        setTypeface(typeface);
    }

    public void setUpText(String text) {
        setText(text);
    }
}
