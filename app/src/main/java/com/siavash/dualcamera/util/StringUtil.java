package com.siavash.dualcamera.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.siavash.dualcamera.Constants;

public class StringUtil {
    private final static String TAG = StringUtil.class.getSimpleName();

    public static final String FONT_NAZANIN_BOLD = "b_nazanin_bold.ttf";
    public static final String FONT_AFSANEH = "a_afsaneh.ttf";
    public static final String FONT_SANS = "a_iranian_sans.ttf";
    public static final String FONT_MASHIN_TAHRIR = "a_mashin_tahrir.ttf";
    public static final String FONT_NASKH = "a_naskh_tahrir.ttf";
    public static final String FONT_NEGAR = "a_negar.ttf";
    public static final String FONT_FANTECY = "fantecy.ttf";
    public static final String FONT_DAST_NEVESHTE = "b_kamran_bold.ttf";
    public static final String FONT_KOODAK = "b_koodak.ttf";
    public static final String FONT_SETAREH = "b_setareh_bold.ttf";
    public static final String FONT_DROID_ARABIK = "droid_arabic_naskh.ttf";
    public static final String FONT_URDU = "urdu.ttf";
    public static final String FONT_IRAN_NASTALIQ = "Iran_nastaliq.ttf";
    public static final float APP_DEFAULT_FONT_SIZE_SMALL = 18;
    public static final float APP_DEFAULT_FONT_SIZE_MEDIUM = 20;
    public static final float APP_DEFAULT_FONT_SIZE_LARGE = 22;
    public static final float APP_DEFAULT_FONT_SIZE_X_LARGE = 25;
    public static final float APP_DEFAULT_FONT_SIZE_XX_LARGE = 28;
    private static final String FONT_PATH_PREFIX = "fonts/";
    private static Typeface nazanin, afsaneh, sans, mashinTahrir, naskh, negar,
            fantecy, dastNeveshte, koodak, serareh, droid, urdu, iranNastaliq;

    public static Typeface getAppMainFont(Context context) {
        return getFont(context, FONT_AFSANEH);
    }

    public static Typeface getDefaultPoemFont(Context context) {
        return getFont(context, FONT_MASHIN_TAHRIR);
    }

    public static Typeface getFont(Context context, String fontName) {
        switch (fontName) {
            case FONT_AFSANEH:
                return (afsaneh == null ? (afsaneh = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_AFSANEH))
                        : afsaneh);
            case FONT_DAST_NEVESHTE:
                return (dastNeveshte == null ? (dastNeveshte = Typeface
                        .createFromAsset(context.getAssets(), FONT_PATH_PREFIX
                                + FONT_DAST_NEVESHTE)) : dastNeveshte);
            case FONT_DROID_ARABIK:
                return (droid == null ? (droid = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_DROID_ARABIK))
                        : droid);
            case FONT_FANTECY:
                return (fantecy == null ? (fantecy = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_FANTECY))
                        : fantecy);
            case FONT_IRAN_NASTALIQ:
                return (iranNastaliq == null ? (iranNastaliq = Typeface
                        .createFromAsset(context.getAssets(), FONT_PATH_PREFIX
                                + FONT_IRAN_NASTALIQ)) : iranNastaliq);
            case FONT_KOODAK:
                return (koodak == null ? (koodak = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_KOODAK))
                        : koodak);
            case FONT_MASHIN_TAHRIR:
                return (mashinTahrir == null ? (mashinTahrir = Typeface
                        .createFromAsset(context.getAssets(), FONT_PATH_PREFIX
                                + FONT_MASHIN_TAHRIR)) : mashinTahrir);
            case FONT_NASKH:
                return (naskh == null ? (naskh = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_NASKH))
                        : naskh);
            case FONT_NAZANIN_BOLD:
                return (nazanin == null ? (nazanin = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_NAZANIN_BOLD))
                        : nazanin);
            case FONT_NEGAR:
                return (negar == null ? (negar = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_NEGAR))
                        : negar);
            case FONT_SANS:
                return (sans == null ? (sans = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_SANS)) : sans);
            case FONT_SETAREH:
                return (serareh == null ? (serareh = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_SETAREH))
                        : serareh);
            case FONT_URDU:
                return (urdu == null ? (urdu = Typeface.createFromAsset(
                        context.getAssets(), FONT_PATH_PREFIX + FONT_URDU)) : urdu);
        }
        if (Constants.IS_DEBUG) Log.d(TAG, "Error in loading typeface:  " + fontName);
        return null;
    }

    /**
     * @param content original text
     * @param start   start position in the content to apply the desired tag
     * @param end     end position in the content to apply the desired tag
     * @param flags   flags like Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, Spannable.SPAN_MASK_MASK and more...
     * @param tags    styles which applied to text
     * @return a CharSequence with zero or more tags applied on the original text
     */
    public static CharSequence applyTextStyle(CharSequence content, int start, int end, int flags, Object... tags) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(content);
        applyTags(text, start, end, flags, tags);
        return text;
    }

    // TODO should be checked for optimization

    /**
     * Iterates over an array of tags and applies them to the specified
     * Spannable object so that text appended to the text will have the styling
     * applied to it. Do not call this method directly.
     */
    private static void applyTags(SpannableStringBuilder text, int start, int end, int flags, Object[] tags) {
        for (Object tag : tags) text.setSpan(tag, start, end, flags);
    }

    // TODO should be checked for redundancy

    /**
     * "Closes" the specified tags on a Spannable by updating the spans to be
     * endpoint-exclusive so that future text appended to the end will not take
     * on the same styling. Do not call this method directly.
     */
    private static void applyTags(SpannableStringBuilder text, int start, int end, Object[] tags) {
        applyTags(text, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, tags);
    }
}
