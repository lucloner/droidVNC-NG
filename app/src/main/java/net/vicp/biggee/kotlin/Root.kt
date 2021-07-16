package net.vicp.biggee.kotlin

import android.annotation.SuppressLint
import android.content.Context
import java.util.*

@SuppressLint("ConstantLocale")
object Root {
    val isRoot by lazy {
        var ret = false
        try {
            ret = Shell.exeCmdByRoot("ls")
        } catch (_: Exception) {

        }
        return@lazy ret
    }

    val cpuinfo by lazy {
        Shell.getCPUInfo()
    }

    val intel by lazy {
        return@lazy cpuinfo.lowercase(Locale.getDefault()).contains("intel")
    }

    val arm by lazy {
        return@lazy cpuinfo.lowercase(Locale.getDefault()).contains("arm")
    }

    fun getRoot(context: Context) = Shell.exeCmdByRoot("chmod 777 ${context.packageCodePath}")
}