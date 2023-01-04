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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants
import titsch.guilherme.heartratemonitor.bluetooth.Constants.DEVICE_NAMES
import titsch.guilherme.heartratemonitor.bluetooth.central.ServiceNotStartedException
import titsch.guilherme.heartratemonitor.bluetooth.peripheral.server.HeartRateServer
import titsch.guilherme.heartratemonitor.bluetooth.toConnectedDevice
import titsch.guilherme.heartratemonitor.core.bluetooth.BluetoothStateObserver
import titsch.guilherme.heartratemonitor.core.model.ConnectedDevice
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("MissingPermission")
class PeripheralManager internal constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothStateObserver: BluetoothStateObserver,
    private val heartRateServer: HeartRateServer,
) {
    private val initialized = AtomicBoolean(false)
    val isInitialized get() = initialized.get()

    private val _advertisementStateFlow = MutableSharedFlow<Boolean>()
    val advertisementStateFlow: SharedFlow<Boolean> = _advertisementStateFlow

    private val _connectedDevicesFlow = MutableSharedFlow<List<ConnectedDevice>>()
    val connectedDevicesFlow: SharedFlow<List<ConnectedDevice>> = _connectedDevicesFlow

    private var coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var advertisementCallback: Callback? = null
    private var stateObservingJob: Job? = null

    suspend fun start(allowConnections: Boolean = true) {
        heartRateServer.open()
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
        heartRateServer.connectedDevicesFlow
            .map { it.map { device -> device.toConnectedDevice() } }
            .onEach { _connectedDevicesFlow.emit(it) }
            .launchIn(coroutineScope)

        initialized.set(true)
        if (allowConnections) {
            allowNewConnections()
        }
    }

    suspend fun allowNewConnections() {
        checkIfInitialized()
        heartRateServer.open()
        Timber.d("Starting advertisement")
        advertisementCallback = Callback()
        bluetoothAdapter.name = DEVICE_NAMES.first()
        bluetoothAdapter.bluetoothLeAdvertiser?.startAdvertising(
            advertisementSettings,
            advertisementData,
            advertisementCallback
        )
        _advertisementStateFlow.emit(true)
    }

    suspend fun denyNewConnections() {
        Timber.d("Stopping advertisement")
        checkIfInitialized()
        if (advertisementCallback != null) {
            bluetoothAdapter.bluetoothLeAdvertiser?.stopAdvertising(advertisementCallback)
            advertisementCallback = null
        }
        _advertisementStateFlow.emit(false)
    }

    suspend fun stop() {
        denyNewConnections()
        stateObservingJob?.cancel()
        heartRateServer.disconnectAllClients()
        heartRateServer.close()
        initialized.set(false)
    }

    suspend fun restart() {
        stop()
        start(false)
    }

    fun emitHeartRate(heartRateMeasurement: Int) {
        Timber.d("emitHeartRate $heartRateMeasurement")
        checkIfInitialized()
        heartRateServer.notifyNewHeartRate(heartRateMeasurement)
    }

    private fun checkIfInitialized() {
        if (!initialized.get()) {
            throw ServiceNotStartedException()
        }
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

    inner class Callback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            Timber.d("Advertisement start failed")
            coroutineScope.launch { _advertisementStateFlow.emit(false) }
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Timber.d("Advertisement start success")
            coroutineScope.launch { _advertisementStateFlow.emit(true) }
        }
    }
}
