package titsch.guilherme.heartratemonitor.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.server.Constants
import titsch.guilherme.heartratemonitor.bluetooth.server.HeartRateServer

@SuppressLint("MissingPermission")
class PeripheralManager(private val context: Context) {
    private var heartRateServer: HeartRateServer? = null
    private var advertisementCallback: AdvertiseCallback? = null

    fun start() {
        Timber.d("Starting advertisement")
        heartRateServer = HeartRateServer(context)
        heartRateServer?.open()

        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        advertisementCallback = Callback()
        bluetoothManager.adapter?.name = "HeartRate"
        bluetoothManager.adapter.bluetoothLeAdvertiser?.startAdvertising(
            advertisementSettings,
            advertisementData,
            advertisementCallback
        )
    }

    fun stop() {
        Timber.d("Stopping  advertisement")
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter.bluetoothLeAdvertiser?.stopAdvertising(advertisementCallback)

        heartRateServer?.close()
        heartRateServer = null
    }

    fun emitHeartRate(heartRateMeasurement: Int) {
        Timber.d("emitHeartRate $heartRateMeasurement")
        heartRateServer?.notifyNewHeartRate(heartRateMeasurement)
    }

    private val advertisementSettings
        get() =
            AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build()

    private val advertisementData
        get() = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(false)
            .addServiceUuid(ParcelUuid(Constants.HEART_RATE_SERVICE_UUID))
            .build()

    class Callback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            Timber.d("Advertisement start failed")
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Timber.d("Advertisement start success")
        }
    }
}
