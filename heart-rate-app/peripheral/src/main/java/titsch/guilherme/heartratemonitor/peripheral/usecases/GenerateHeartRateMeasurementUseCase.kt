package titsch.guilherme.heartratemonitor.peripheral.usecases

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class GenerateHeartRateMeasurementUseCase {
    operator fun invoke(interval: Long, from: Int = 25, until: Int = 220) = flow {
        while (true) {
            delay(interval)
            this.emit(Random.nextInt(from = from, until = until))
        }
    }
}