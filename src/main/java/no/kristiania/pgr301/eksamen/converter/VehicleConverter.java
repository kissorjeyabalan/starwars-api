package no.kristiania.pgr301.eksamen.converter;

import no.kristiania.pgr301.eksamen.dto.VehicleDto;
import no.kristiania.pgr301.eksamen.entity.VehicleEntity;

public class VehicleConverter {
    public static VehicleDto transform(VehicleEntity entity) {
        return new VehicleDto(
                entity.getId().toString(),
                entity.getName(),
                entity.getModel()
        );
    }

    public static VehicleEntity transform(VehicleDto dto) {
        Long id = null;
        if (dto.getId() != null) {
            id = Long.parseLong(dto.getId());
        }
        return new VehicleEntity(
                id,
                dto.getName(),
                dto.getModel()
        );
    }
}
