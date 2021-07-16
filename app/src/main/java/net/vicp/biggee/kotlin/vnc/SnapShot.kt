package net.vicp.biggee.kotlin.vnc

import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import net.christianbeier.droidvnc_ng.MainService
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object SnapShot {
    var img = WeakReference<ImageView>(null)
    var exe = Executors.newSingleThreadScheduledExecutor()

    fun startDemo(cap: Runnable) {
        exe.shutdown()
        exe = Executors.newSingleThreadScheduledExecutor()
        exe.scheduleAtFixedRate({
            try {
                val iv = img.get() ?: return@scheduleAtFixedRate
                iv.visibility = View.VISIBLE
                cap.run()
//            val byteArray = MainService.rawData
//            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                val img = MainService.rawData


                val buffer = img.planes[0].buffer
                val byteArray = ByteArray(buffer.capacity())
                buffer.get(byteArray)
                val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                iv.setImageBitmap(bmp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 1, 1, TimeUnit.SECONDS)
    }
}