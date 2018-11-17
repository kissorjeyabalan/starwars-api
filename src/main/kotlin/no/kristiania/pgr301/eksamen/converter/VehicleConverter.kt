package no.kristiania.pgr301.eksamen.converter

import no.kristiania.pgr301.eksamen.hateos.HalPage
import no.kristiania.pgr301.eksamen.dto.VehicleDto
import no.kristiania.pgr301.eksamen.entity.VehicleEntity
import java.time.ZonedDateTime
import kotlin.streams.toList

class VehicleConverter {
    companion object {
        fun transform(entity: VehicleEntity): VehicleDto {
            return VehicleDto(
                    id = entity.id?.toString(),
                    name = entity.name,
                    model = entity.model,
                    created = entity.creationTime,
                    updated = entity.updated
            )
        }

        fun transform(dto: VehicleDto): VehicleEntity {
            return VehicleEntity(
                    id = dto.id?.toLong(),
                    name = dto.name!!,
                    model = dto.model!!,
                    creationTime = dto.created,
                    updated = dto.updated
            )
        }

        fun transform(entities: Iterable<VehicleEntity>): List<VehicleDto> {
            return entities.map { transform(it) }
        }


        fun transform(entities: List<VehicleEntity>, page: Int, limit: Int): HalPage<VehicleDto> {
            val offset = ((page -1) * limit).toLong()
            val dtoList: MutableList<VehicleDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<VehicleDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit) +1).toInt()

            return pageDto
        }
    }
}