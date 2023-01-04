package titsch.guilherme.heartratemonitor.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import titsch.guilherme.heartratemonitor.core.model.ConnectedDevice
import titsch.guilherme.heartratemonitor.core.model.ConnectionState as ConnState

internal fun ConnectionState.toConnState(): ConnState {
    return when (this) {
        ConnectionState.Connecting -> ConnState.CONNECTING
        ConnectionState.Initializing -> ConnState.CONNECTING
        ConnectionState.Ready -> ConnState.CONNECTED
        ConnectionState.Disconnecting -> ConnState.DISCONNECTED
        is ConnectionState.Disconnected -> ConnState.DISCONNECTED
    }
}

@SuppressLint("MissingPermission")
internal fun BluetoothDevice.toConnectedDevice(): ConnectedDevice {
    val alias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) this.alias else null
    return ConnectedDevice(this.address, this.name, alias)
}