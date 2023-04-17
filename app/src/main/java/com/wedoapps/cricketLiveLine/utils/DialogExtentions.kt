package com.wedoapps.cricketLiveLine.utils

import android.app.Dialog

fun Dialog.isVisible(isShow: Boolean, dialog: Dialog?) = if (isShow) dialog!!.show() else {
    dialog!!.dismiss()
    dialog.cancel()
}