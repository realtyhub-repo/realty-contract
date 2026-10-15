package service.contratos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.contratos.entity.PlantillaContrato;
import service.contratos.enums.TipoContrato;

import java.util.Optional;
import java.util.UUID;

public interface PlantillaContratoRepository extends JpaRepository<PlantillaContrato, UUID> {

    Optional<PlantillaContrato> findByActivaIsTrueAndTipoAndPais(TipoContrato tipoContrato,  String pais);
}
