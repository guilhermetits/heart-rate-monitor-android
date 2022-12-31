package titsch.guilherme.heartratemonitor.bluetooth.peripheral.server

import android.content.Context

class ConnectionManagerFactory(private val context: Context) {
    fun create() = ConnectionManager(context)
}
