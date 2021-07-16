package net.vicp.biggee.kotlin

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

object Shell {
    val TAG = "Shell"
    val logger = StringBuilder()

    /**
     * 执行shell指令
     * @param strings 指令集
     * @return 指令集是否执行成功
     */
    fun exeCmdByRoot(vararg strings: String, isRoot: Boolean = true): Boolean {
        try {
            val su =
                if (isRoot) Runtime.getRuntime().exec("su") else Runtime.getRuntime().exec("sh")
            val outputStream = DataOutputStream(su.outputStream)
            val mReader = BufferedReader(InputStreamReader(su.inputStream))

            for (s in strings) {
                outputStream.writeBytes(s + "\n")
                outputStream.flush()
            }
            outputStream.writeBytes("exit\n")
            outputStream.flush()
            su.waitFor()
            outputStream.close()

            val mRespBuff = StringBuffer()
            val buff = CharArray(1024)
            var ch = mReader.read(buff)
            while (ch != -1) {
                mRespBuff.append(buff, 0, ch)
                ch = mReader.read(buff)
            }
            mReader.close()
            logger.append("shell return:$mRespBuff")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            logger.append("shell exception:$e")
            return false
        }
    }

    fun getCPUInfo(): String {
        logger.clear()
        val cmdOK = exeCmdByRoot("cat /proc/cpuinfo", isRoot = false)
        if (!cmdOK) {
            return ""
        }
        val cmdResult = logger.toString()
        logger.clear()
        val ret = ArrayList<String>()
        val nameProcessor = "Processor"
        val nameFeatures = "Features"
        val nameModel = "model name"
        val nameCpuFamily = "cpu family"
        val kes = arrayOf(nameProcessor, nameCpuFamily, nameModel, nameFeatures)
        val lines = cmdResult.split("\n")
        lines.iterator().forEach {
            val args = it.split(":")
            kes.iterator().forEach {
                try {
                    if (args[0].trim().lowercase(Locale.getDefault()) == it.trim().lowercase(
                            Locale.getDefault()
                        )
                    ) {
                        ret.add(args[1])
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return Arrays.toString(ret.toTypedArray())
    }
}