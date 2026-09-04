package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.controller.AdminController;
import br.com.menegati.brb_revendedoras.dto.revendedora.PainelRevendedoraResponseDTO;
import br.com.menegati.brb_revendedoras.entity.CargaConsignacao;
import br.com.menegati.brb_revendedoras.entity.DocumentoMaleta;
import br.com.menegati.brb_revendedoras.entity.LoteConsignacao;
import br.com.menegati.brb_revendedoras.entity.Revendedor;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.entity.Acerto;
import br.com.menegati.brb_revendedoras.mapper.PainelRevendedoraMapper;
import br.com.menegati.brb_revendedoras.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevendedoraService {
    private final PainelRevendedoraMapper mapper;
    private final UserRepository userRepository;
    private final LoteRepository loteRepository;
    private final DocumentoMaletaRepository documentoMaletaRepository;
    private final AcertoRepository acertoRepository;
    private final CargaConsignacaoRepository cargaConsignacaoRepository;
    private final ContaCorrenteService contaCorrenteService;
    private final RevendedoraRepository revendedoraRepository;


    public PainelRevendedoraResponseDTO getRevendedoraData(String cpf) {
        User user = userRepository.findByCpf(cpf).orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        if(!(user instanceof Revendedor revendedora)){
            throw new BusinessException("Usuário não é revendedora");
        }

        LoteConsignacao loteAtual = loteRepository.findByRevendedorIdAndStatus(revendedora.getId(), StatusLote.ABERTO).orElse(null);
        List<DocumentoMaleta> historicoDocumentos = documentoMaletaRepository.findByRevendedorIdOrderByLoteIdDesc(revendedora.getId());
        List<Acerto> historicoAcertos = acertoRepository.findByRevendedorIdOrderByDataAcertoDesc(revendedora.getId());
        List<CargaConsignacao> historicoCargas = cargaConsignacaoRepository.findByRevendedorIdOrderByDataAberturaDesc(revendedora.getId());

        PainelRevendedoraResponseDTO responseDTO = new PainelRevendedoraResponseDTO();
        responseDTO.setPerfil(mapper.toRevendedorDTO(revendedora, contaCorrenteService.getSaldoDevedor(revendedora.getId())));
        responseDTO.setLoteAtual(mapper.toLoteDTO(loteAtual));
        responseDTO.setHistoricoDocumentos(mapper.toDocumentoDTOList(historicoDocumentos));
        responseDTO.setHistoricoAcertos(mapper.toAcertoDTOList(historicoAcertos));
        responseDTO.setHistoricoCargas(mapper.toCargaDTOList(historicoCargas));

        return responseDTO;
    }

    public List<Revendedor> getAllRevendedoras(){
        return revendedoraRepository.findAll();
    }

}
