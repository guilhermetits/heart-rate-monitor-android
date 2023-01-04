package titsch.guilherme.heartratemonitor.central.usecases.bluetooth

import titsch.guilherme.heartratemonitor.core.location.LocationService

class IsLocationServiceEnabledUseCase(private val locationService: LocationService) {
    operator fun invoke() = locationService.isLocationEnabled()
}