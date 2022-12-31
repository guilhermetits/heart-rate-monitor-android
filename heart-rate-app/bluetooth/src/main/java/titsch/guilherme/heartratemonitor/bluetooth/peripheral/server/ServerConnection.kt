package titsch.guilherme.heartratemonitor.bluetooth.peripheral.server

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.data.MutableData

class ServerConnection(context: Context) : BleManager(context) {

    private lateinit var gattCallback: GattCallback

    override fun getGattCallback(): BleManagerGattCallback {
        gattCallback = GattCallback()
        return gattCallback
    }

    fun sendHeartRate(serverCharacteristic: BluetoothGattCharacteristic, heartRate: Int) {
        MutableData(ByteArray(RECORD_SIZE)).apply {
            setValue(FLAGS, Data.FORMAT_UINT8, FLAGS_OFFSET)
            setValue(heartRate, Data.FORMAT_UINT16_LE, MEASUREMENT_OFFSET)
        }.also {
            super.writeCharacteristic(
                serverCharacteristic, it,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            ).enqueue()
            super.sendNotification(serverCharacteristic, it).enqueue()
        }
    }

    private inner class GattCallback : BleManagerGattCallback() {

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            return true
        }

        override fun onServicesInvalidated() {
            /* No Action */
        }
    }

    companion object {
        private const val RECORD_SIZE = 3

        // 16Bit Heart Rate Measurement
        // No Sensor Contact
        // No Energy Expended
        // No RR Intervals
        private const val FLAGS = 0x01
        private const val FLAGS_OFFSET = 0
        private const val MEASUREMENT_OFFSET = 1
    }
}
