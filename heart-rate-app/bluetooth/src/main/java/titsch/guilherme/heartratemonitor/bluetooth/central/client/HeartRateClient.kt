package titsch.guilherme.heartratemonitor.bluetooth.central.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.suspend
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants

@SuppressLint("MissingPermission")
class HeartRateClient(context: Context) : BleManager(context) {
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

    private inner class GattCallback : BleManagerGattCallback() {
        private var heartRateCharacteristic: BluetoothGattCharacteristic? = null

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            Timber.d("Validating required properties")
            val heartRateService = gatt.getService(Constants.HEART_RATE_SERVICE_UUID)
            heartRateCharacteristic =
                heartRateService?.getCharacteristic(Constants.HEART_RATE_CHARACTERISTIC_UUID)
            val characteristicProperties = heartRateCharacteristic?.properties ?: 0
            return characteristicProperties and BluetoothGattCharacteristic.PROPERTY_READ != 0 &&
                characteristicProperties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0
        }

        override fun initialize() {
            setNotificationCallback(heartRateCharacteristic).with { _, data ->
                if (data.value != null) {
                    val heartRateMeasurement = HeartRateMapper.map(data)
                    Timber.d("New Heart Rate Measurement Received: $heartRateMeasurement")
                }
            }

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