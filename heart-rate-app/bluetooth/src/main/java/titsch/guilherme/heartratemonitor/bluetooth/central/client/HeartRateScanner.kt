package titsch.guilherme.heartratemonitor.bluetooth.central.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import titsch.guilherme.heartratemonitor.bluetooth.Constants.DEVICE_NAME
import titsch.guilherme.heartratemonitor.bluetooth.Constants.HEART_RATE_SERVICE_UUID
import kotlin.coroutines.resume

class HeartRateScanner(private val bluetoothAdapter: BluetoothAdapter) {
    @SuppressLint("MissingPermission")
    suspend fun scan(): ScanResult? = suspendCancellableCoroutine { continuation ->
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                Timber.d("Device found")
                result?.device?.name?.let { deviceName ->
                    if (deviceName == DEVICE_NAME) {
                        bluetoothAdapter.bluetoothLeScanner?.stopScan(this)
                        continuation.resume(result)
                    } else {
                        Timber.d("Found device with name $deviceName expected $DEVICE_NAME")
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Timber.d("Scan failed with code $errorCode")
                bluetoothAdapter.bluetoothLeScanner?.stopScan(this)
                continuation.resume(null)
            }
        }

        Timber.d("Scan Starting")
        bluetoothAdapter.bluetoothLeScanner?.startScan(
            listOf(scanFilters),
            scanSettings,
            scanCallback
        )

        continuation.invokeOnCancellation {
            Timber.d("Scan Cancelled")
            bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
        }
    }

    private val scanFilters
        get() = ScanFilter
            .Builder()
            .setServiceUuid(ParcelUuid.fromString(HEART_RATE_SERVICE_UUID.toString()))
            .build()

    private val scanSettings
        get() = ScanSettings
            .Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
}