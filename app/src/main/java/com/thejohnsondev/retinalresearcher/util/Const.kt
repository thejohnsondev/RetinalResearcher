package com.thejohnsondev.retinalresearcher.util

object Const {

    object DefaultValues {
        const val REQUEST_CODE_PERMISSION = 10
        const val BASE_PREVIEW_ROTATION = 90F
        const val BASE_IMAGE_SIZE = 2048
        const val DEFAULT_CONTRAST_VALUE = 1F
        const val DEFAULT_SEEK_BAR_VALUE = 100
        const val DEFAULT_WHITE_TEMPERATURE = 5000
        const val CONTRAST_VALUE_DIVIDEND = 10F
        const val SEEK_BAR_VALUE_DIVIDEND = 100F
        const val CONTRAST_MAX = 50
        const val CONTRAST_MIN = 10
        const val BRIGHTNESS_MAX = 100
        const val BRIGHTNESS_MIN = 0
        const val SATURATION_MAX = 100
        const val SATURATION_MIN = 0
        const val WHITE_TEMP_MAX = 10000
        const val WHITE_TEMP_MIN = 2300
        const val RGB_MAX = 100
        const val RGB_MIN = 0
        const val SECOND_IN_MILLIS = 1000
        const val BITMAP_COMPRESS_QUALITY = 100
        const val MEGAPIXEL = 1000000
        const val ZERO = 0
        const val ZERO_F = 0F
    }

    object FilterOptions {
        const val CONTRAST = "Contrast"
        const val BRIGHTNESS = "Brightness"
        const val RGB = "RGB filter"
    }

    object ArgKey {
        const val OPTION_EMPTY = "empty"
        const val OPTION_CONTRAST = "contrast"
        const val OPTION_BRIGHTNESS = "brightness"
        const val OPTION_GRAY_SCALE = "gray_scale"
        const val OPTION_SATURATION = "saturation"
        const val OPTION_WHITE_BALANCE = "white_balance"
        const val OPTION_RGB = "rgb"
    }

    object MediaPath {
        const val DEFAULT_GALLERY_PATH = "Pictures/"
        const val IMAGE_EXTENSION = ".png"
        const val IMAGE_CONTENT_TYPE = "image/png"
    }

}