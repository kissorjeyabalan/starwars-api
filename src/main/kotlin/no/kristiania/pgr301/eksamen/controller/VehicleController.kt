package no.kristiania.pgr301.eksamen.controller

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
import java.time.ZonedDateTime

@Api(value = "/vehicles", description = "Information about various vehicles throughout the galaxy")
@RequestMapping(
        path = ["/vehicles"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class VehicleController {
    @Autowired lateinit var repo: VehicleRepository

    @ApiOperation("Update an existing vehicle resource by ID")
    @PutMapping(path = ["/{id}"])
    fun replaceVehicle(
            @ApiParam("Vehicle ID")
            @PathVariable("id")
            id: String,

            @ApiParam("Data to replace current resource with. Created and updated is ignored.")
            @RequestBody
            dto: VehicleDto
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
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
    }

    @ApiOperation("Delete an existing vehicle resource by ID")
    @DeleteMapping(path = ["/{id}"])
    fun deleteVehicle(
            @ApiParam("Vehicle ID")
            @PathVariable("id")
            vehicleId: String
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
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
        if (dto.name.isNullOrEmpty() || dto.model.isNullOrEmpty()) {
            return ResponseEntity.status(400).build()

        }
        val created = repo.save(
                VehicleEntity(name = dto.name!!, model = dto.model!!,
                creationTime = ZonedDateTime.now(), updated = ZonedDateTime.now())
        )

        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromPath("/vehicles/${created.id}")
                        .build()
                        .toUri()
        ).build()
    }

    @ApiOperation("Get a specific vehicle resource by ID")
    @GetMapping(path = ["/{id}"])
    fun getVehicle(
        @PathVariable("id")
        pathId: Long?
    ): ResponseEntity<WrappedResponse<VehicleDto>> {
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