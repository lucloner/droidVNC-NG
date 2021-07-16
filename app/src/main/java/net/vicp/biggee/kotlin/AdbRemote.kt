package net.vicp.biggee.kotlin

object AdbRemote {
    val TAG = "adbRemote"
    var done = false
    val port = 5555
    var interrupt = false
    fun start() {
        if (done) {
            return
        }
        while (!Shell.exeCmdByRoot(
                "setprop service.adb.tcp.port 5555",
                "stop adbd",
                "start adbd"
            )
        ) {
            Thread.sleep(1000)
            if (interrupt) {
                interrupt = false
                return
            }
        }
        done = true
    }

    fun stop() = { interrupt = true }
}