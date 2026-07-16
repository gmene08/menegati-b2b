package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.*;
import br.com.menegati.brb_revendedoras.enums.Role;
import br.com.menegati.brb_revendedoras.enums.StatusItemLote;
import br.com.menegati.brb_revendedoras.enums.StatusLote;
import br.com.menegati.brb_revendedoras.enums.TipoDocumento;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoteService {

    private final UserRepository userRepository;
    private final LoteRepository loteRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemConsignacaoRepository itemConsignacaoRepository;
    private final DocumentoMaletaRepository documentoMaletaRepository;

    public static final String REGEX_LINHAS_PRODUTOS = "^(\\d{6})\\s+([\\s\\S]+?)\\s+([0-9.,]+)\\s+([0-9.,]+)\\s+([0-9.,]+)$";

    public record ProcessamentoLoteResult(LoteConsignacao lote, Map<Long, String> produtosAlertas, BigDecimal valorTotalDestePdf, int numeroDePecasDestePdf) {}

    public record DadosDoPdf(String cpf, String consignacao) {}

    @Transactional
    public ProcessamentoLoteResult importarCargaMaletaPdf(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("Arquivo PDF recebido: {}", fileName);
        Map<Long, String> produtosAlertas = new LinkedHashMap<>();
        BigDecimal valorTotalEstimadoDestePDF = BigDecimal.ZERO;

            String texto = extrairTextoDoPdf(file);

            // _______ DATA EXTRACTION _______
            DadosDoPdf dados = extrairDadosPrincipaisDoPdf(texto);
            String cpf = dados.cpf();
            User user = userRepository.findByCpf(cpf).orElseThrow(()->new BusinessException(
                    "A revendedora com o CPF " + cpf + " nao esta cadastrada"));

            String consignacao = dados.consignacao();

            // _______ VALIDATION _______
            validarDadosPrincipaisDoPdf(user, consignacao);

            // _______ LOTE CREATION_______
            Revendedor revendedor = (Revendedor) user;

            LoteConsignacao lote = loteRepository.findByRevendedorIdAndStatus(user.getId(), StatusLote.ABERTO).orElseGet(()-> LoteConsignacao.builder()
                    .revendedor(revendedor)
                    .status(StatusLote.ABERTO)
                    .dataAbertura(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                    .valorTotalEstimado(BigDecimal.ZERO)
                    .valorTotalAcertado(BigDecimal.ZERO)
                    .itens(new ArrayList<>())
                    .build());

            Matcher itemMatcher = Pattern.compile(REGEX_LINHAS_PRODUTOS,Pattern.MULTILINE).matcher(texto);

            int itensCriados = 0;
            int itensAlertas = 0;

            while (itemMatcher.find()) {
                String codigo = itemMatcher.group(1).trim();

                String descricao = itemMatcher.group(2).replaceAll("\\r?\\n", " ").trim();

                String quantidadeRaw = itemMatcher.group(3).trim();
                int quantidade = Integer.parseInt(quantidadeRaw.split(",")[0]);

                String precoRaw = itemMatcher.group(4).trim();

                Produto produto = produtoRepository.findByCodigo(codigo).orElse(null);

                if(produto == null){
                    log.warn("Produto com codigo '{}' nao encontrado no estoque. Ignorando item: {}", codigo, descricao);
                    itensAlertas++;
                    produtosAlertas.put(Long.parseLong(codigo), "ITEM IGNORADO: Produto nao encontrado no estoque.");
                    continue;
                }

                if((produto.getQuantidadeDisponivel() - quantidade) < 0  ){
                    log.warn("Produto com codigo '{}' sem quantidade disponivel. Ignorando item: {}", codigo, descricao);
                    itensAlertas++;
                    produtosAlertas.put(Long.parseLong(codigo), "ITEM IGNORADO: Estoque insuficiente para esta peca.");
                    continue;
                }

                String precoString = precoRaw.replace(".", "").replace(",", ".");
                BigDecimal precoCobrado = new BigDecimal(precoString);


                ItemConsignacao item = itemConsignacaoRepository.findFirstByProdutoCodigoAndLoteIdAndStatusItem(produto.getCodigo(), lote.getId(), StatusItemLote.ENCARREGADO).orElse(null);
                if(item != null){
                    item.setQuantidade(item.getQuantidade() + quantidade);
                } else {
                    item = ItemConsignacao.builder()
                            .produto(produto)
                            .quantidade(quantidade)
                            .valorUnitarioCongelado(precoCobrado)
                            .lote(lote)
                            .documentoEntrada(consignacao)
                            .dataAtualizacao(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                            .statusItem(StatusItemLote.ENCARREGADO)
                            .build();
                }

                lote.getItens().add(item);

                // deduz do estoque
                produto.setQuantidadeDisponivel(produto.getQuantidadeDisponivel() - quantidade);
                produtoRepository.save(produto);

                BigDecimal totalDesteItem = precoCobrado.multiply(BigDecimal.valueOf(quantidade));
                valorTotalEstimadoDestePDF = valorTotalEstimadoDestePDF.add(totalDesteItem);

                itensCriados++;
            }

            if(lote.getItens().isEmpty()){
                throw new BusinessException("Nenhum item foi encontrado no PDF.");
            }

            // atualiza valores do lote
            BigDecimal valorTotalEstimadoAtual = lote.getValorTotalEstimado() !=null ? lote.getValorTotalEstimado() : BigDecimal.ZERO;
            lote.setValorTotalEstimado(valorTotalEstimadoAtual.add(valorTotalEstimadoDestePDF));

            LoteConsignacao salvoLote = loteRepository.save(lote);
            log.info("Lote consignado com sucesso! Itens criados: {} | Itens com alerta: {}", itensCriados, itensAlertas);

            salvarHistoricoDoDocumento(fileName, consignacao, TipoDocumento.MALETA_ENTRADA, revendedor, lote);

            return new ProcessamentoLoteResult(salvoLote, produtosAlertas, valorTotalEstimadoDestePDF, itensCriados);

    }

    @Transactional
    public ProcessamentoLoteResult acertarCargaMaletaPdf(MultipartFile file){

        String f = file.getOriginalFilename();
        log.info("Arquivo PDF recebido: {}", f);

        Map<Long, String> produtosAlerta = new LinkedHashMap<>();
        BigDecimal valorTotalAcertadoDestePdf = BigDecimal.ZERO;

        // _______ DATA EXTRACTION _______
        String texto = extrairTextoDoPdf(file);
        System.out.println(texto);

        DadosDoPdf dadosDoPdf = extrairDadosPrincipaisDoPdf(texto);

        String cpf = dadosDoPdf.cpf();
        User user = userRepository.findByCpf(cpf).orElseThrow(()->new BusinessException("A revendedora com o CPF " + cpf + " nao esta cadastrada"));
        String consignacao = dadosDoPdf.consignacao();

        validarDadosPrincipaisDoPdf(user, consignacao);

        // _______ LOTE ACERTO_______
        Revendedor revendedor = (Revendedor) user;
        LoteConsignacao lote = loteRepository.findByRevendedorIdAndStatus(user.getId(), StatusLote.ABERTO).orElseThrow(()->new BusinessException("A revendedora " + user.getName() + " nao possui uma maleta aberta."));

        Matcher itemMatcher = Pattern.compile(REGEX_LINHAS_PRODUTOS,Pattern.MULTILINE).matcher(texto);

        int itensAcertados = 0;
        int itensAlertas = 0;

        while (itemMatcher.find()) {
            String codigo = itemMatcher.group(1).trim();

            String quantidadeRaw = itemMatcher.group(3).trim();
            int quantidadeNoPdf = Integer.parseInt(quantidadeRaw.split(",")[0]);

            ItemConsignacao item = itemConsignacaoRepository.findFirstByProdutoCodigoAndLoteIdAndStatusItem(codigo, lote.getId(), StatusItemLote.ENCARREGADO)
                    .orElse(null);

            if(item != null){
                int quantidadeNoBanco = item.getQuantidade();

                if(quantidadeNoPdf < quantidadeNoBanco){
                    // VENDA PARCIAL
                    item.setQuantidade(quantidadeNoBanco - quantidadeNoPdf);

                    ItemConsignacao itemVendido = ItemConsignacao.builder()
                            .lote(lote)
                            .produto(item.getProduto())
                            .quantidade(quantidadeNoPdf)
                            .valorUnitarioCongelado(item.getValorUnitarioCongelado())
                            .statusItem(StatusItemLote.ACERTADO_VENDIDO)
                            .documentoEntrada(item.getDocumentoEntrada())
                            .documentoAcerto(dadosDoPdf.consignacao())
                            .dataAtualizacao(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                            .build();

                    lote.getItens().add(itemVendido);

                } else if(quantidadeNoPdf == quantidadeNoBanco){
                    //VENDA TOTAL
                    item.setStatusItem(StatusItemLote.ACERTADO_VENDIDO); // (Assumindo que este PDF lista o que foi vendido)
                    item.setDocumentoAcerto(dadosDoPdf.consignacao()); // Salva qual documento de Acerto finalizou esta peça
                    item.setDataAtualizacao(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()));
                } else {
                    //ANOMALIA: limite ao que tem no banco
                    produtosAlerta.put(Long.parseLong(codigo), "ALERTA: Quantidade no PDF (" + quantidadeNoPdf + ") maior que a carregada. Limitado ao estoque da maleta (" + quantidadeNoBanco + ").");
                    itensAlertas++;

                    item.setStatusItem(StatusItemLote.ACERTADO_VENDIDO);
                    item.setDocumentoAcerto(dadosDoPdf.consignacao());
                    item.setDataAtualizacao(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()));
                    quantidadeNoPdf = quantidadeNoBanco;
                }

                valorTotalAcertadoDestePdf = valorTotalAcertadoDestePdf.add(item.getValorUnitarioCongelado().multiply(BigDecimal.valueOf(quantidadeNoPdf)));
                itensAcertados++;
            } else {
                produtosAlerta.put(Long.parseLong(codigo), "ITEM IGNORADO: Peça não encontrada na maleta ou já foi acertada anteriormente.");
                itensAlertas++;
            }
        }

        BigDecimal valorTotalAcertadoAtual = lote.getValorTotalAcertado() != null ? lote.getValorTotalAcertado() : BigDecimal.ZERO;
        lote.setValorTotalAcertado(valorTotalAcertadoAtual.add(valorTotalAcertadoDestePdf));

        BigDecimal valorEstimadoAtual = lote.getValorTotalEstimado() != null ? lote.getValorTotalEstimado() : BigDecimal.ZERO;
        lote.setValorTotalEstimado(valorEstimadoAtual.subtract(valorTotalAcertadoDestePdf));

        salvarHistoricoDoDocumento(f, consignacao, TipoDocumento.MALETA_ACERTO,revendedor, lote);

        loteRepository.save(lote);

        log.info("Lote consignado com sucesso! Itens criados: {} | Itens com alerta: {}", itensAcertados, itensAlertas);

        return new ProcessamentoLoteResult(lote, produtosAlerta, valorTotalAcertadoDestePdf, itensAcertados);

    }

    public String extrairTextoDoPdf(MultipartFile file) {
        try (PDDocument pdfDocument = PDDocument.load(file.getInputStream())){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdfDocument);
        } catch (IOException e) {
            log.error("Erro ao processar arquivo PDF: {}", e.getMessage());
            throw new BusinessException("Erro ao processar arquivo PDF: " + e.getMessage());
        }
    }

    public DadosDoPdf extrairDadosPrincipaisDoPdf(String texto){

        // _______ CPF _______
        Pattern cpfPattern = Pattern.compile("CPF:\\s*([0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2})");
        Matcher cpfMatcher = cpfPattern.matcher(texto);

        if(!cpfMatcher.find()){
            throw new BusinessException("Não foi possivel localizar o CPF da revendedora no PDF.");
        }

        String cpfLimpo = cpfMatcher.group(1).replaceAll("[^0-9]", "");
        log.info("CPF da revendedora: {}", cpfLimpo);

        // _______ CONSIGNACAO  _______
        Pattern consignacaoPattern = Pattern.compile("Consignação\\s*:\\s*([0-9]+)");
        Matcher consignacaoMatcher = consignacaoPattern.matcher(texto);

        if(!consignacaoMatcher.find()){
            throw new BusinessException("Nao foi possivel localizar o numero da consignacao no PDF.");
        }

        String consignacao = consignacaoMatcher.group(1);

        return new DadosDoPdf(cpfLimpo, consignacao);
    }

    public void validarDadosPrincipaisDoPdf(User user, String consignacao){

        // _______ ROLE VALIDATION _______
        if (user.getRole() != Role.REVENDEDOR) {
            throw new BusinessException("O perfil do cpf informado nao e revendedor.");
        }

        // _______ CONSIGNACAO VALIDATION _______
        if(documentoMaletaRepository.existsByNumeroConsignacao(consignacao)){
            throw new BusinessException("Documento com esse numero de consignação já foi processado uma vez.");
        }

    }

    @Transactional
    public void salvarHistoricoDoDocumento(String fileName, String consignacao, TipoDocumento tipoDocumento, Revendedor revendedor, LoteConsignacao lote){
        DocumentoMaleta documentoMaleta = DocumentoMaleta.builder()
                .nomeArquivo(fileName)
                .numeroConsignacao(consignacao)
                .tipoDocumento(tipoDocumento)
                .dataProcessamento(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()))
                .revendedor(revendedor)
                .lote(lote)
                .build();

        documentoMaletaRepository.save(documentoMaleta);
    }
}