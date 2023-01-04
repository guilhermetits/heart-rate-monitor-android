package titsch.guilherme.heartratemonitor.core.bluetooth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class LocationActivityContract : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit) =
        Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}