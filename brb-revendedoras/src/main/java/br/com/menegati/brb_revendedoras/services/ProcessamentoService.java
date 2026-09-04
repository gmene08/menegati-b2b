package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.mapper.PainelAdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessamentoService {
    private final CargaService cargaService;
    private final AcertoService acertoService;
    private final DocumentoService documentoService;
    private final PainelAdminMapper mapper;

    public List<PainelAdminService.ProcessamentoView> getUltimosProcessamentos(){
        List<PainelAdminService.ProcessamentoView> processamentos = new ArrayList<>();

        processamentos.addAll(cargaService.getCargasParaFeed().stream().map(mapper::toView).toList());
        processamentos.addAll(acertoService.getAcertosParaFeed().stream().map(mapper::toView).toList());
        processamentos.addAll(documentoService.getDocumentosEstoqueParaFeed().stream().map(mapper::toView).toList());

        processamentos.sort(Comparator.comparing(PainelAdminService.ProcessamentoView::dataProcessamento).reversed());

        return processamentos.stream().limit(10).toList();
    }
}
