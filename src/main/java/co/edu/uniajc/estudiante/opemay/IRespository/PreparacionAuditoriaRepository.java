package co.edu.uniajc.estudiante.opemay.IRespository;

import co.edu.uniajc.estudiante.opemay.model.PreparacionAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreparacionAuditoriaRepository extends JpaRepository<PreparacionAuditoria, Long> {
	void deleteByPreparacionId(Long preparacionId);
}
