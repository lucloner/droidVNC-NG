package net.vicp.biggee.kotlin.vnc

import android.graphics.BitmapFactory
import android.widget.ImageView
import net.christianbeier.droidvnc_ng.MainService
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object SnapShot {
    var img = WeakReference<ImageView>(null)
    var exe = Executors.newSingleThreadScheduledExecutor()

    fun startDemo() {
        exe.shutdown()
        exe = Executors.newSingleThreadScheduledExecutor()
        exe.scheduleAtFixedRate({
            val iv = img.get() ?: return@scheduleAtFixedRate
            val byteArray = MainService.rawData
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            iv.setImageBitmap(bitmap)
        }, 1, 1, TimeUnit.SECONDS)
    }
}