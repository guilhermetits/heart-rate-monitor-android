package titsch.guilherme.heartratemonitor.bluetooth.central

import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateClient
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateScanner

class CentralManager(context: Context) {
    private val heartRateScanner: HeartRateScanner
    private val heartRateClient = HeartRateClient(context)

    init {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        heartRateScanner = HeartRateScanner(bluetoothManager.adapter)
    }

    suspend fun start(connectToDevice: Boolean = true) {
        Timber.d("start $connectToDevice")
        if (connectToDevice) {
            connect()
        }
    }

    suspend fun stop() {
        Timber.d("stop")
        disconnect()
    }

    private suspend fun connect(): Boolean {
        var connected = false
        withTimeout(timeMillis = 60000) {
            heartRateScanner.scan()?.let { scanResult ->
                heartRateClient.openConnection(scanResult.device)
                connected = true
            }
        }
        return connected
    }

    private suspend fun disconnect() {
        heartRateClient.closeConnection()
    }
}