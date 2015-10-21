package com.siavash.dualcamera;

/**
 * App Constants
 * Created by sia on 8/18/15.
 */
public class Constants {
    //region Camera specific constants
    public static final String CAMERA_BOTH_IMAGE_URL = "camera_both_url";
    public static final String CAMERA_FRONT_IMAGE_URL = "camera_front_url";
    public static final String CAMERA_BACK_IMAGE_URL = "camera_back_url";
    public static final int DISPLAY_ORIENTATION = 90;
    public static final int COMPRESS_QUALITY = 90;
    public static final String IMAGE_PATH = "image_url";
    //endregion

    //region Fragments
    public static final int PHOTO_FRAGMENT = 0;
    public static final int CAMERA_BACK_FRAGMENT = 1;
    public static final int CAMERA_FRONT_FRAGMENT = 2;
    public static final int SHARE_FRAGMENT = 3;

    public static final String PHOTO_FRAGMENT_TAG = "photoFragment";
    //endregion

    public static final boolean IS_DEBUG = true;
}
