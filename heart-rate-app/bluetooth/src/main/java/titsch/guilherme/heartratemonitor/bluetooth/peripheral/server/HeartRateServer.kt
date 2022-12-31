package titsch.guilherme.heartratemonitor.bluetooth.peripheral.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.observer.ServerObserver
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants
import java.nio.charset.StandardCharsets

@SuppressLint("MissingPermission")
class HeartRateServer(private val context: Context) : BleServerManager(context), ServerObserver {
    private val serverConnections = mutableMapOf<String, ServerConnection>()
    override fun initializeServer(): MutableList<BluetoothGattService> {
        setServerObserver(this)

        return services
    }

    override fun onServerReady() {
        Timber.d("onServerReady")
    }

    fun notifyNewHeartRate(heartRate: Int) {
        Timber.d("notifyNewHeartRate $heartRate")
        serverConnections.values.forEachIndexed { index, serverConnection ->
            Timber.d(
                "notifying client ${index + 1} ${serverConnection.bluetoothDevice?.name}" +
                    " with address ${serverConnection.bluetoothDevice?.address}"
            )

            serverConnection.sendHeartRate(heartRateCharacteristic, heartRate)
        }
    }

    override fun onDeviceConnectedToServer(device: BluetoothDevice) {
        serverConnections[device.address] = ServerConnection(context).apply {
            useServer(this@HeartRateServer)
            connect(device).enqueue()
        }
    }

    override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
        // The device has disconnected. Forget it and close.
        serverConnections.remove(device.address)?.close()
    }

    private val heartRateCharacteristic by lazy {
        sharedCharacteristic(
            Constants.HEART_RATE_CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ,
            descriptor(
                Constants.CCCD_UUID,
                BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE,
                byteArrayOf(0, 0)
            )
        )
    }
    private val manufacturerNameCharacteristic = sharedCharacteristic(
        Constants.MANUFACTURER_NAME_CHAR_UUID,
        BluetoothGattCharacteristic.PROPERTY_READ,
        BluetoothGattCharacteristic.PERMISSION_READ,
        "Guilherme".toByteArray(StandardCharsets.UTF_8)
    )

    private val heartRateService = service(
        // UUID:
        Constants.HEART_RATE_SERVICE_UUID,
        // Characteristics (just one in this case):
        heartRateCharacteristic
    )

    private val deviceInformationService =
        service(
            // UUID:
            Constants.DEVICE_INFORMATION_SERVICE_UUID,
            // Characteristics (just one in this case):
            manufacturerNameCharacteristic
        )

    private val services = mutableListOf(deviceInformationService, heartRateService)
}


