package titsch.guilherme.heartratemonitor.bluetooth.central

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateClient
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateMapper
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateScanner
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class CentralManager(
    private val heartRateScanner: HeartRateScanner,
    private val heartRateClient: HeartRateClient
) {
    val heartRateFlow = MutableSharedFlow<Int>()
    val isInitialized get() = initialized.get()

    private val initialized = AtomicBoolean(false)
    private var coroutineScope: CoroutineScope by Delegates.notNull()

    suspend fun start(connectToDevice: Boolean = true) {
        Timber.d("start $connectToDevice")
        coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        initialized.set(true)
        if (connectToDevice) {
            connect()
        }
    }

    suspend fun stop() {
        Timber.d("stop")
        checkIfInitialized()
        coroutineScope.coroutineContext.cancelChildren()
        disconnect()
        initialized.set(false)
    }

    private fun checkIfInitialized() {
        if (!initialized.get()) {
            throw ServiceNotStartedException()
        }
    }

    private suspend fun connect(): Boolean {
        Timber.d("connect")
        checkIfInitialized()
        var connected = false

        withTimeout(timeMillis = 60000) {
            heartRateScanner.scan()?.let { scanResult ->
                heartRateClient.openConnection(scanResult.device)
                listenConnectionChanges()
                connected = true
            }
        }

        heartRateClient.collectHeartRateMeasurements().onEach { data ->
            Timber.d("New Heart Rate Received")
            heartRateFlow.emit(HeartRateMapper.map(data))
        }.launchIn(coroutineScope)

        return connected
    }

    private fun listenConnectionChanges() {
        heartRateClient.stateAsFlow().onEach {
            when (it) {
                is ConnectionState.Disconnected -> connect()
                else -> Timber.d("Connection Update: ${it::class.simpleName}")
            }
        }.launchIn(coroutineScope)
    }

    private suspend fun disconnect() {
        Timber.d("disconnect")
        heartRateClient.closeConnection()
    }
}