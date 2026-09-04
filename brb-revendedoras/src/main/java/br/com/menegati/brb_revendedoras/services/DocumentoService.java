package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.DocumentoBase;
import br.com.menegati.brb_revendedoras.entity.DocumentoEstoque;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.mapper.PainelAdminMapper;
import br.com.menegati.brb_revendedoras.repository.DocumentoBaseRepository;
import br.com.menegati.brb_revendedoras.repository.DocumentoEstoqueRepository;
import br.com.menegati.brb_revendedoras.repository.DocumentoMaletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoService {
    private final DocumentoEstoqueRepository documentoEstoqueRepository;

    public List<DocumentoEstoque> getDocumentosEstoqueParaFeed(){
        return documentoEstoqueRepository.findAllByOrderByDataProcessamentoDesc(PageRequest.of(0, 20));

    }
}
