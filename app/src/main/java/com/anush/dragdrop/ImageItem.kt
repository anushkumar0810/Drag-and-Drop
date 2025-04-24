package com.anush.dragdrop

import android.net.Uri

sealed class ImageItem {
    data class UriItem(val uri: Uri) : ImageItem()
    object AddButton : ImageItem()
}