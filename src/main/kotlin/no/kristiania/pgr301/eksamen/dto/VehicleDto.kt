package no.kristiania.pgr301.eksamen.dto

import java.time.ZonedDateTime

data class VehicleDto (
    var id: String? = null,
    var name: String? = null,
    var model: String? = null,
    var created: ZonedDateTime? = null,
    var updated: ZonedDateTime? = null
)