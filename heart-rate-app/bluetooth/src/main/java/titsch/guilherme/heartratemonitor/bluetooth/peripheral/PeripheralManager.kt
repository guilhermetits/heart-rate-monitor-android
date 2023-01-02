package titsch.guilherme.heartratemonitor.bluetooth.peripheral

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.os.ParcelUuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants
import titsch.guilherme.heartratemonitor.bluetooth.Constants.DEVICE_NAMES
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.server.HeartRateServer
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothStateObserver

@SuppressLint("MissingPermission")
class PeripheralManager(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothStateObserver: BluetoothStateObserver,
    private val heartRateServer: HeartRateServer,
) {
    private var coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var advertisementCallback: Callback? = null
    private var stateObservingJob: Job? = null

    suspend fun start(allowConnections: Boolean = true) {
        heartRateServer.open()
        if (allowConnections) {
            allowNewConnections()
        }
        stateObservingJob = coroutineScope.launch {
            bluetoothStateObserver().collect {
                if (it) {
                    Timber.d("bluetooth enabled")
                    heartRateServer.open()
                } else {
                    Timber.d("bluetooth disabled")
                    denyNewConnections()
                    heartRateServer.disconnectAllClients()
                    heartRateServer.close()
                }
            }
        }
    }

    fun allowNewConnections() {
        heartRateServer.open()
        Timber.d("Starting advertisement")
        advertisementCallback = Callback()
        bluetoothAdapter.name = DEVICE_NAMES.first()
        bluetoothAdapter.bluetoothLeAdvertiser.startAdvertising(
            advertisementSettings,
            advertisementData,
            advertisementCallback
        )
    }

    fun denyNewConnections() {
        Timber.d("Stopping advertisement")
        bluetoothAdapter.bluetoothLeAdvertiser.stopAdvertising(advertisementCallback)
    }

    fun stop() {
        denyNewConnections()
        stateObservingJob?.cancel()
        heartRateServer.close()
    }

    fun emitHeartRate(heartRateMeasurement: Int) {
        Timber.d("emitHeartRate $heartRateMeasurement")
        heartRateServer.notifyNewHeartRate(heartRateMeasurement)
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
