package titsch.guilherme.heartratemonitor.bluetooth.central

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateClient
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateMapper
import titsch.guilherme.heartratemonitor.bluetooth.central.client.HeartRateScanner
import titsch.guilherme.heartratemonitor.bluetooth.toConnState
import java.util.concurrent.atomic.AtomicBoolean
import titsch.guilherme.heartratemonitor.core.model.ConnectionState as ConnState

class CentralManager internal constructor(
    private val heartRateScanner: HeartRateScanner,
    private val heartRateClient: HeartRateClient
) {
    private val _heartRateFlow = MutableSharedFlow<Int>(replay = 1)
    val heartRateFlow: SharedFlow<Int> = _heartRateFlow

    private val _connectionState = MutableSharedFlow<ConnState>(replay = 1)
    val connectionState: SharedFlow<ConnState> = _connectionState
    val isInitialized get() = initialized.get()

    private val initialized = AtomicBoolean(false)
    private var connectJob: Job? = null
    private var connectionChangesJob: Job? = null
    private var coroutineScope: CoroutineScope? = null
    private var restartConnection = false

    suspend fun start(connectToDevice: Boolean = true) {
        Timber.d("start $connectToDevice")
        coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
            .also {
                heartRateClient.stateAsFlow()
                    .map { state -> state.toConnState() }
                    .onEach { connState -> _connectionState.emit(connState) }
                    .launchIn(it)
            }

        initialized.set(true)
        if (connectToDevice) {
            connect()
        }
    }

    suspend fun stop() {
        Timber.d("stop")
        checkIfInitialized()
        disconnect()
        _connectionState.emit(ConnState.DISCONNECTED)
        coroutineScope?.coroutineContext?.cancelChildren()
        coroutineScope = null
        initialized.set(false)
    }

    private fun checkIfInitialized() {
        if (!initialized.get()) {
            throw ServiceNotStartedException()
        }
    }

    suspend fun connect() {
        Timber.d("connect")
        checkIfInitialized()
        if (heartRateClient.isConnected) return
        withTimeout(timeMillis = 60000) {
            connectJob = this.coroutineContext.job
            _connectionState.emit(ConnState.SCANNING)
            heartRateScanner.scan()?.let { scanResult ->
                try {
                    heartRateClient.openConnection(scanResult.device)
                } catch (e: Throwable) {
                    Timber.e(e)
                    return@withTimeout
                }
            }

            // wait until connection state is Ready
            heartRateClient.stateAsFlow().filterIsInstance<ConnectionState.Ready>().first()

            coroutineScope?.let {
                heartRateClient.collectHeartRateMeasurements().onEach { data ->
                    Timber.d("emitting new value")
                    _heartRateFlow.emit(HeartRateMapper.map(data))
                }.launchIn(it)
            }
            restartConnection = true
            listenConnectionChanges()
        }
        if (!heartRateClient.isConnected) {
            _connectionState.emit(ConnState.DISCONNECTED)
        }
        connectJob = null
    }

    private fun listenConnectionChanges() {
        coroutineScope?.let {
            connectionChangesJob = heartRateClient.stateAsFlow().onEach { connectionState ->
                when (connectionState) {
                    is ConnectionState.Connecting -> if (restartConnection) connect()
                    is ConnectionState.Disconnected -> if (restartConnection) connect()
                    else -> Timber.d("Connection Update: ${connectionState::class.simpleName}")
                }
            }.launchIn(it)
        }
    }

    suspend fun disconnect() {
        Timber.d("disconnect")
        restartConnection = false
        connectJob?.cancel()
        connectionChangesJob?.cancel()
        connectionChangesJob = null
        connectJob = null
        heartRateClient.closeConnection()
        _connectionState.emit(ConnState.DISCONNECTED)
    }
}