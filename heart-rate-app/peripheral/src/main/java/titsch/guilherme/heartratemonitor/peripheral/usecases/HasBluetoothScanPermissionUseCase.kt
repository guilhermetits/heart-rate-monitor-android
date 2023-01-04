package titsch.guilherme.heartratemonitor.peripheral.usecases

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class HasBluetoothScanPermissionUseCase(private val context: Context) {
    operator fun invoke() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}