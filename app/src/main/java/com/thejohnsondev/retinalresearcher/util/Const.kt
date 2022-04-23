package com.thejohnsondev.retinalresearcher.util

object Const {

    object DefaultValues {
        const val REQUEST_CODE_PERMISSION = 10
        const val BASE_PREVIEW_ROTATION = 90F
        const val BASE_IMAGE_SIZE = 2048
        const val DEFAULT_CONTRAST_VALUE = 1F
        const val DEFAULT_BRIGHTNESS_VALUE = 0F
        const val CONTRAST_VALUE_DIVIDEND = 10F
        const val BRIGHTNESS_VALUE_DIVIDEND = 100F
        const val CONTRAST_MAX = 50
        const val CONTRAST_MIN = 10
        const val BRIGHTNESS_MAX = 100
        const val BRIGHTNESS_MIN = 0
        const val SECOND_IN_MILLIS = 1000
        const val BITMAP_COMPRESS_QUALITY = 100
        const val MEGAPIXEL = 1000000
        const val ZERO = 0
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
    }

    object MediaPath {
        const val DEFAULT_GALLERY_PATH = "Pictures/"
        const val IMAGE_EXTENSION = ".png"
        const val IMAGE_CONTENT_TYPE = "image/png"
    }

}