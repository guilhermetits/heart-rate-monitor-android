package titsch.guilherme.heartratemonitor.bluetooth.central.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.suspend
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants

@SuppressLint("MissingPermission")
internal class HeartRateClient(context: Context) : BleManager(context) {
    override fun getGattCallback(): BleManagerGattCallback = GattCallback()

    suspend fun openConnection(device: BluetoothDevice) {
        Timber.d("openConnection")
        this.connect(device).useAutoConnect(true).suspend()
    }

    suspend fun closeConnection() {
        Timber.d("closeConnection")
        if (this.isConnected) {
            this.disconnect().suspend()
        }
    }

    suspend fun collectHeartRateMeasurements() = channelFlow {
        setNotificationCallback(heartRateCharacteristic)
            .with { _, data -> trySend(data) }

        awaitClose {
            removeNotificationCallback(heartRateCharacteristic)
        }
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    private var heartRateCharacteristic: BluetoothGattCharacteristic? = null

    private inner class GattCallback : BleManagerGattCallback() {
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            Timber.d("Validating required properties")
            val heartRateService = gatt.getService(Constants.HEART_RATE_SERVICE_UUID)
            heartRateCharacteristic =
                heartRateService?.getCharacteristic(Constants.HEART_RATE_CHARACTERISTIC_UUID)
            val characteristicProperties = heartRateCharacteristic?.properties ?: 0
            return (characteristicProperties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0).also {
                if (!it) {
                    Timber.w("The device doesn't have all the required properties")
                }
            }
        }

        override fun initialize() {
            beginAtomicRequestQueue()
                .add(enableNotifications(heartRateCharacteristic)
                         .fail { device: BluetoothDevice?, status: Int ->
                             log(
                                 Log.ERROR,
                                 "Failed to  register for notification for device:${device?.name} " +
                                     "with status:$status"
                             )
                             disconnect().enqueue()
                         }
                )
                .done {
                    log(Log.INFO, "Target initialized")
                }
                .enqueue()
        }

        override fun onServicesInvalidated() {
            heartRateCharacteristic = null
        }
    }
}