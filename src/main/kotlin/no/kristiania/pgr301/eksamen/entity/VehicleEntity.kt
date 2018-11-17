package no.kristiania.pgr301.eksamen.entity

import java.time.ZonedDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Table(name = "vehicles")
@Entity
class VehicleEntity (
        @get:Id @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,

        @get:NotBlank @get:Size(max = 70)
        var name: String,

        @get:NotBlank @get:Size(max = 70)
        var model: String,

        @get:NotNull
        var creationTime: ZonedDateTime? = null,

        @get:NotNull
        var updated: ZonedDateTime? = null
) {
    @PrePersist
    fun onCreate() {
        creationTime = ZonedDateTime.now()
        updated = ZonedDateTime.now()
    }

    @PreUpdate
    fun onUpdate() {
        updated = ZonedDateTime.now()
    }
}