package net.vicp.biggee.kotlin.vnc

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatWriter
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.qrcode.QRCodeReader
import net.christianbeier.droidvnc_ng.MainService
import net.christianbeier.droidvnc_ng.R
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.random.Random


object SnapShot {
    var img = WeakReference<ImageView>(null)
    var exe = Executors.newSingleThreadScheduledExecutor()
    var notify=Consumer<String>{}

    fun startDemo(cap: Runnable) {
        exe.shutdown()
        exe = Executors.newSingleThreadScheduledExecutor()
        exe.scheduleAtFixedRate({
            Log.d("BdeBug","scheduleAtFixedRate")
            try {
                val iv = img.get() ?: return@scheduleAtFixedRate
                iv.visibility = View.VISIBLE
                cap.run()
                val bmp = MainService.rawData//?:return@scheduleAtFixedRate
                if(bmp==null){
                    if((System.currentTimeMillis()/1000).mod(2)==0){
                        iv.setImageDrawable(ResourcesCompat.getDrawable(iv.resources,R.mipmap.ic_launcher,null))
                        Log.e("BdeBug","no bmp 1")
                    }
                    else{
                        val matrix = MultiFormatWriter().encode(
                            "test:${Random(0).nextInt(100)}",
                            BarcodeFormat.QR_CODE,
                            300,
                            300
                        )
                        val width = matrix.width
                        val height = matrix.height
                        val pixels = IntArray(width * height)
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                if (matrix.get(x, y)) {
                                    pixels[y * width + x] = -0x1000000
                                } else {
                                    pixels[y * width + x] = Color.WHITE
                                }
                            }
                        }
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                        iv.setImageBitmap(bitmap)
                        Log.e("BdeBug","no bmp 2")
                    }
                }
                else{
                    iv.setImageBitmap(bmp)

                    val width = bmp.width
                    val height = bmp.height
                    val pixels = IntArray(width * height)
                    bmp.getPixels(pixels, 0, width, 0, 0, width, height)
                    val text = QRCodeReader().decode(
                        BinaryBitmap(
                            GlobalHistogramBinarizer(RGBLuminanceSource(width, height, pixels))
                        )
                    ).text?:return@scheduleAtFixedRate
                    Log.e("BdeBug","bmpPosted:$text ${bmp.hashCode()}")
                    if(text.isNotBlank()){
                        notify.accept(text)
                    }
                }
                iv.postInvalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                MainService.rawData=null
            }
        }, 1, 1, TimeUnit.SECONDS)
    }
}