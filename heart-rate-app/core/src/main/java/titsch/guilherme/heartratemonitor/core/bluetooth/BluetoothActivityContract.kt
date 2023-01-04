package titsch.guilherme.heartratemonitor.core.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class BluetoothActivityContract : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit) =
        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}