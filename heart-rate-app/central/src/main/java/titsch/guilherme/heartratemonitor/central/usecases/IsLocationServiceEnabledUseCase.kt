package titsch.guilherme.heartratemonitor.central.usecases

import titsch.guilherme.heartratemonitor.core.location.LocationService

class IsLocationServiceEnabledUseCase(private val locationService: LocationService) {
    operator fun invoke() = locationService.isLocationEnabled()
}