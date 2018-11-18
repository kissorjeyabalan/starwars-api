package no.kristiania.pgr301.eksamen.repository;

import no.kristiania.pgr301.eksamen.entity.VehicleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VehicleRepository extends CrudRepository<VehicleEntity, Long>, PagingAndSortingRepository<VehicleEntity, Long> {
}