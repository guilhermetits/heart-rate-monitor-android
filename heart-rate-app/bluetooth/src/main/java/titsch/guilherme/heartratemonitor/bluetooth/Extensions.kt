package titsch.guilherme.heartratemonitor.bluetooth

import android.bluetooth.BluetoothDevice
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

internal fun BluetoothDevice.toConnectedDevice(): ConnectedDevice {
    return ConnectedDevice(this.address)
}