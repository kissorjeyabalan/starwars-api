package no.kristiania.pgr301.eksamen.repository

import no.kristiania.pgr301.eksamen.entity.VehicleEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleRepository: CrudRepository<VehicleEntity, Long>