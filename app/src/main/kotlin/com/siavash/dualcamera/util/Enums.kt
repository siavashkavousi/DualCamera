package com.siavash.dualcamera.util

/**
 * Created by sia on 10/25/15.
 */
enum class FragmentId {
    CAMERA_BACK, CAMERA_FRONT, PHOTO, SHARE, ABOUT
}

enum class CameraId(val address: String, val id: Int) {
    BACK(".back", 0), FRONT(".front", 1)
}

enum class OnTouchAction {
    NONE, DRAG, ZOOM
}

enum class Font(val resourceId: String) {
    NAZANIN_BOLD("b_nazanin_bold.ttf"), AFSANEH("a_afsaneh.ttf"), SANS("a_iranian_sans.ttf"),
    MASHIN_TAHRIR("a_mashin_tahrir.ttf"), NASKH("a_naskh_tahrir.ttf"), NEGAR("a_negar.ttf"),
    FANTECY("fantecy.ttf"), DAST_NEVESHTE("b_kamran_bold.ttf"), KOODAK("b_koodak.ttf"),
    SETAREH("b_setareh_bold.ttf"), DROID_ARABIK("droid_arabic_naskh.ttf"), URDU("urdu.ttf"),
    IRAN_NASTALIQ("Iran_nastaliq.ttf")
}

enum class FontSize(size: Float) {
    SMALL (18f), MEDIUM (20f), LARGE (22f), X_LARGE (25f), XX_LARGE (28f)
}