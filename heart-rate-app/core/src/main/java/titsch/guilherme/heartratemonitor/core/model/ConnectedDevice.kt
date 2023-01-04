package titsch.guilherme.heartratemonitor.core.model

data class ConnectedDevice(
    val address: String,
    val name: String?,
    val alias: String?,
)