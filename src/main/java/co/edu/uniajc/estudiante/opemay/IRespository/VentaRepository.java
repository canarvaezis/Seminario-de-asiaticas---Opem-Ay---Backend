package co.edu.uniajc.estudiante.opemay.IRespository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.edu.uniajc.estudiante.opemay.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findByUsuarioId(Long usuarioId);

    @Query("""
            SELECT v
            FROM Venta v
            LEFT JOIN v.comprador c
            WHERE (:termino IS NULL OR :termino = ''
                OR LOWER(COALESCE(c.nombre, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR LOWER(COALESCE(v.facturaElectronicaNumero, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR LOWER(COALESCE(v.facturaElectronicaCufe, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
                OR LOWER(COALESCE(v.facturaElectronicaEstado, '')) LIKE LOWER(CONCAT('%', :termino, '%'))
            )
            ORDER BY v.fecha DESC
            """)
    List<Venta> buscarPorTermino(@Param("termino") String termino);
}
