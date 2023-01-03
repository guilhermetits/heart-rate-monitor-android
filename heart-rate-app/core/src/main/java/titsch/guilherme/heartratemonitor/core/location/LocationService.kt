package titsch.guilherme.heartratemonitor.core.location

import android.location.LocationManager
import timber.log.Timber

class LocationService(private val locationManager: LocationManager) {
    fun isLocationEnabled(): Boolean {
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Throwable) {
            Timber.e(e)
        }
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Throwable) {
            Timber.e(e)
        }

        return gpsEnabled || networkEnabled
    }
}
