package titsch.guilherme.heartratemonitor.bluetooth.peripheral.server

import android.bluetooth.BluetoothDevice
import android.content.Context

internal class ConnectionManagerFactory(private val context: Context) {
    fun create(device: BluetoothDevice) = ConnectionManager(device, context)
}
