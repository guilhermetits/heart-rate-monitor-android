package titsch.guilherme.heartratemonitor.bluetooth.central.client

import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.data.Data.FORMAT_UINT16_LE
import no.nordicsemi.android.ble.data.Data.FORMAT_UINT8

internal object HeartRateMapper {
    fun map(data: Data): Int? {
        val flag = data.getIntValue(FORMAT_UINT8, FLAG_OFFSET)
        var heartRateValue: Int? = null
        if (flag != null) {
            val heartRateFormat = if (flag and 0x01 != 0) FORMAT_UINT16_LE else FORMAT_UINT8
            heartRateValue = data.getIntValue(heartRateFormat, HEART_RATE_MEASUREMENT_OFFSET)
        }
        return heartRateValue
    }

    private const val FLAG_OFFSET = 0
    private const val HEART_RATE_MEASUREMENT_OFFSET = 1
}