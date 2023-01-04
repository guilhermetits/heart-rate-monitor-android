package titsch.guilherme.heartratemonitor.core.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow

class BluetoothStateObserver(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) {
    operator fun invoke() = channelFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                    val extraState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    if (extraState == BluetoothAdapter.STATE_ON) {
                        this@channelFlow.trySend(true)
                    } else if (extraState == BluetoothAdapter.STATE_OFF) {
                        this@channelFlow.trySend(false)
                    }
                }
            }
        }

        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )

        trySend(isEnabled())

        awaitClose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    fun isEnabled() = bluetoothAdapter.isEnabled
}