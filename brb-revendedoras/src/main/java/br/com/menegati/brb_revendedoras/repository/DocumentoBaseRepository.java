package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.DocumentoBase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface DocumentoBaseRepository<T extends DocumentoBase> extends JpaRepository<T, Long> {
    Optional<T> findByNomeArquivo(String nomeArquivo);

    List<T> findAllByOrderByDataProcessamentoDesc(Pageable pageable);
}
