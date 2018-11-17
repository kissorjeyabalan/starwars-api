package no.kristiania.pgr301.eksamen.controller

import com.codahale.metrics.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.kristiania.pgr301.eksamen.converter.VehicleConverter
import no.kristiania.pgr301.eksamen.hateos.HalPage
import no.kristiania.pgr301.eksamen.dto.VehicleDto
import no.kristiania.pgr301.eksamen.dto.WrappedResponse
import no.kristiania.pgr301.eksamen.entity.VehicleEntity
import no.kristiania.pgr301.eksamen.hateos.Format
import no.kristiania.pgr301.eksamen.hateos.HalLink
import no.kristiania.pgr301.eksamen.repository.VehicleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception

@Api(value = "/vehicles", description = "Information about various vehicles throughout the galaxy")
@RequestMapping(
        path = ["/vehicles"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class VehicleController {
    @Autowired lateinit var repo: VehicleRepository
    @Autowired lateinit var metrics: MetricRegistry

    @ApiOperation("Replace an existing vehicle resource by ID")
    @PutMapping(path = ["/{id}"])
    fun replaceVehicle(
            @ApiParam("Vehicle ID")
            @PathVariable("id")
            id: String,

            @ApiParam("Data to replace current resource with. Created and updated is ignored.")
            @RequestBody
            dto: VehicleDto
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
        metrics.meter("ReplaceVehicle").mark()
        val timer = metrics.timer("ReplaceVehicle").time()

        try {
            val pathId: Long
            val dtoId: Long
            try {
                pathId = id.toLong()
                dtoId = dto.id!!.toLong()
            } catch (e: Exception) {
                return resourceNotFoundEntity()
            }

            if (dtoId != pathId) {
                return errorEntity(409, "Path and body ID mismatch")
            }

            if (!repo.existsById(dtoId)) {
                return resourceNotFoundEntity()
            }

            if (dto.name.isNullOrEmpty() || dto.model.isNullOrEmpty()) {
                return errorEntity(400, "Illegal state for name or model")
            }

            val vehicleEntity = VehicleConverter.transform(dto)

            try {
                repo.save(vehicleEntity)
            } catch (e: Exception) {
                throw e
            }

            return ResponseEntity.status(204).build()
        } finally {
            timer.stop()
        }
    }

    @ApiOperation("Delete an existing vehicle resource by ID")
    @DeleteMapping(path = ["/{id}"])
    fun deleteVehicle(
            @ApiParam("Vehicle ID")
            @PathVariable("id")
            vehicleId: String
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
        metrics.meter("DeleteVehicle").mark()

        val pathId: Long? = vehicleId.toLongOrNull() ?:
                return ResponseEntity.status(404).build()

        repo.deleteById(pathId!!)
        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Create a new vehicle resource")
    @PostMapping
    fun createVehicle(
            @ApiParam("Model of vehicle to insert. ID, creationTime and updated is ignored if supplied.")
            @RequestBody dto: VehicleDto
    ): ResponseEntity<Void> {
        metrics.meter("CreateVehicle").mark()
        val timer = metrics.timer("CreateVehicle").time()

        try {
            if (dto.name.isNullOrEmpty() || dto.model.isNullOrEmpty()) {
                return ResponseEntity.status(400).build()

            }
            val created = repo.save(VehicleEntity(name = dto.name!!, model = dto.model!!))

            return ResponseEntity.created(
                    UriComponentsBuilder
                            .fromPath("/vehicles/${created.id}")
                            .build()
                            .toUri()
            ).build()
        } finally {
            timer.stop()
        }
    }

    @ApiOperation("Get a specific vehicle resource by ID")
    @GetMapping(path = ["/{id}"])
    fun getVehicle(
        @PathVariable("id")
        pathId: Long?
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
        metrics.meter("GetVehicle").mark()

        val dto = repo.findById(pathId!!).orElse(null) ?:
                return resourceNotFoundEntity()

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = VehicleConverter.transform(dto)
                ).validated()
        )
    }

    @ApiOperation("Get all vehicles")
    @GetMapping(produces = [Format.HAL_V1])
    fun getAllVehicles(
            @ApiParam("Page to retrieve")
            @RequestParam("page", defaultValue = "1")
            page: Int,

            @ApiParam("Number of items in page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int
    ): ResponseEntity<WrappedResponse<HalPage<VehicleDto>>> {
        metrics.meter("GetAllVehicles").mark()
        val timer = metrics.timer("GetAllVehicles").time()

        try {
            if (page < 1 || limit < 1) {
                return ResponseEntity.status(400).body(
                        WrappedResponse<HalPage<VehicleDto>>(
                                code = 400,
                                message = "Malformed page or limit supplied"
                        ).validated()
                )
            }
            val entityList = repo.findAll().toList()
            val dto = VehicleConverter.transform(entityList, page, limit)

            val uriBuilder = UriComponentsBuilder.fromPath("/vehicles")
            dto._self = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("page", page)
                    .queryParam("limit", limit)
                    .build().toString())

            if (!entityList.isEmpty() && page > 1) {
                dto.previous = HalLink(uriBuilder.cloneBuilder()
                        .queryParam("page", page - 1)
                        .queryParam("limit", limit)
                        .build().toString())
            }

            if (((page) * limit) < entityList.size) {
                dto.next = HalLink(uriBuilder.cloneBuilder()
                        .queryParam("page", page + 1)
                        .queryParam("limit", limit)
                        .build().toString())
            }

            return ResponseEntity.ok(
                    WrappedResponse(
                            code = 200,
                            data = dto
                    ).validated()
            )
        } finally {
            timer.stop()
        }
    }

    private fun resourceNotFoundEntity(): ResponseEntity<WrappedResponse<VehicleDto>> {
        return ResponseEntity.status(404).body(
                WrappedResponse<VehicleDto>(
                        code = 404,
                        message = "Resource not found"
                ).validated()
        )
    }

    private fun errorEntity(code: Int, message: String): ResponseEntity<WrappedResponse<VehicleDto>> {
        return ResponseEntity.status(code).body(
                WrappedResponse<VehicleDto>(
                        code = code,
                        message = message
                ).validated()
        )
    }
}