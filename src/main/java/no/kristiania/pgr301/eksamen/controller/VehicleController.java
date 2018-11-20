package no.kristiania.pgr301.eksamen.controller;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;*/
import no.kristiania.pgr301.eksamen.converter.VehicleConverter;
import no.kristiania.pgr301.eksamen.dto.VehicleDto;
import no.kristiania.pgr301.eksamen.entity.VehicleEntity;
import no.kristiania.pgr301.eksamen.hateos.Format;
import no.kristiania.pgr301.eksamen.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Api(value = "/vehicles", description ="Information about various vehicles throughout the galaxy")
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class VehicleController {

    private VehicleRepository repo;
    private MetricRegistry metric;

    public VehicleController(VehicleRepository repo, MetricRegistry registry) {
        this.repo = repo;
        this.metric = registry;

        List<VehicleEntity> defaultEntities = new ArrayList<>();
        defaultEntities.add(new VehicleEntity(null, "Sand Crawler", "Digger Crawler"));
        defaultEntities.add(new VehicleEntity(null, "Sail Barge", "Modified Luxury Sail Barge"));
        defaultEntities.add(new VehicleEntity(null, "TIE Bomber", "TIE/SA Bomber"));
        defaultEntities.add(new VehicleEntity(null, "Snowspeeder", "T-47 Airspeeder"));
        defaultEntities.add(new VehicleEntity(null, "AT-AT", "All Terrain Armored Transport"));
        defaultEntities.add(new VehicleEntity(null, "AT-ST", "All Terrain Scout Transport"));

        repo.saveAll(defaultEntities);

    }

    @GetMapping
    public String welcome() {
        metric.meter(MetricRegistry.name(VehicleController.class, "welcome", "count")).mark();
        return "You can do GET and POST at the endpoint /vehicles. The json body for POST is: {'name':'string', 'model':'string'}. " +
                "PUT and DELETE can be done at /vehicles/{id}. PUT expects same body as POST, but with an additional string field named id.";
    }
    //@ApiOperation("Create a new vehicle resource")
    @PostMapping(path = "/vehicles")
    public ResponseEntity<Void> createVehicle(
            //@ApiParam("Vehicle to insert. ID is ignored if supplied.")
            @RequestBody VehicleDto dto
    ) {
        metric.meter(MetricRegistry.name(VehicleController.class, "create", "count")).mark();
        Timer.Context timerContext = metric.timer(MetricRegistry.name(VehicleController.class,
                "create", "timer")).time();

        try {
            if (dto.getName() == null || dto.getName().isEmpty() ||
                    dto.getModel() == null || dto.getModel().isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            dto.setId(null);
            VehicleEntity created = repo.save(VehicleConverter.transform(dto));

            return ResponseEntity.created(
                    UriComponentsBuilder
                    .fromPath("/vehicles/" + created.getId())
                    .build()
                    .toUri()
            ).build();
        } finally {
            timerContext.stop();
        }
    }

    //@ApiOperation("Get all vehicles")
    @GetMapping(path = "/vehicles", produces = Format.HAL_V1)
    public ResponseEntity<Page<VehicleEntity>> getAllVehicles(
            //@ApiParam("Page to retrieve")
            @RequestParam(value = "page", defaultValue = "1")
            int page,

            //@ApiParam("Number of items to retrieve per page")
            @RequestParam(value = "limit", defaultValue = "10")
            int limit
    ) {
        metric.meter(MetricRegistry.name(VehicleController.class, "get-all", "count")).mark();
        Timer.Context timerContext = metric.timer(MetricRegistry.name(VehicleController.class,
                "get-all", "timer")).time();

        try {
            if (page < 1 || limit < 1) {
                return ResponseEntity.status(400).build();
            }

            int pageNum = page - 1;
            Page<VehicleEntity> pageList = repo.findAll(PageRequest.of(pageNum, limit));

            return ResponseEntity.ok(pageList);
        } finally {
            timerContext.stop();
        }
    }

    //@ApiOperation("Get a specific vehicle resource by ID")
    @GetMapping(path = "vehicles/{id}")
    public ResponseEntity<VehicleDto> getVehicle(
        @PathVariable("id")
        String pathId
    ) {
        metric.meter(MetricRegistry.name(VehicleController.class, "get-single", "count")).mark();
        metric.meter(MetricRegistry.name(VehicleController.class, "get-single", pathId)).mark();
        Timer.Context timerContext = metric.timer(MetricRegistry.name(VehicleController.class,
                "get-all", "timer")).time();

        try {
            long id;
            try {
                id = Long.parseLong(pathId);
            } catch (Exception e) {
                return ResponseEntity.status(404).build();
            }

            VehicleEntity entity = repo.findById(id).orElse(null);
            if (entity == null) return ResponseEntity.status(404).build();

            return ResponseEntity.ok(VehicleConverter.transform(entity));
        } finally {
            timerContext.stop();
        }
    }

    //@ApiOperation("Delete an existing vehicle resource by ID")
    @DeleteMapping(path = "vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(
            //@ApiParam("Vehicle ID to delete")
            @PathVariable("id")
            String pathId
    ) {
        metric.meter(MetricRegistry.name(VehicleController.class, "delete", "count")).mark();
        metric.meter(MetricRegistry.name(VehicleController.class, "delete", pathId)).mark();
        Timer.Context timerContext = metric.timer(MetricRegistry.name(VehicleController.class,
                "delete", "timer")).time();

        try {
            Long id;
            try {
                id = Long.parseLong(pathId);
            } catch (Exception e) {
                return ResponseEntity.status(404).build();
            }

            repo.deleteById(id);
            return ResponseEntity.status(204).build();
        } finally {
            timerContext.stop();
        }
    }

    //@ApiOperation("Replace an existing vehicle resource by ID")
    @PutMapping(path = "vehicles/{id}")
    public ResponseEntity<VehicleDto> replaceVehicle(
            //@ApiParam("Vehicle ID")
            @PathVariable("id")
            String pathId,

            //@ApiParam("Data to replace current resource with. Path ID and data ID must match.")
            @RequestBody VehicleDto dto
    ) {
        metric.meter(MetricRegistry.name(VehicleController.class, "replace", "count")).mark();
        metric.meter(MetricRegistry.name(VehicleController.class, "replace", pathId)).mark();
        Timer.Context timerContext = metric.timer(MetricRegistry.name(VehicleController.class,
                "delete", "timer")).time();

        try {
            Long id = null;
            Long dtoId = null;
            try {
                id = Long.parseLong(pathId);
                dtoId = Long.parseLong(dto.getId());
            } catch (Exception e) {
                ResponseEntity.status(404).build();
            }

            if (!Objects.equals(dtoId, id)) {
                return ResponseEntity.status(409).build();
            }

            if (!repo.existsById(dtoId)) {
                return ResponseEntity.status(404).build();
            }

            if (dto.getName() == null || dto.getName().isEmpty()  ||
                    dto.getModel() == null || dto.getModel().isEmpty()) {
                return ResponseEntity.status(400).build();
            }

            repo.save(VehicleConverter.transform(dto));
            return ResponseEntity.status(204).build();

        } finally {
            timerContext.stop();
        }
    }

}
