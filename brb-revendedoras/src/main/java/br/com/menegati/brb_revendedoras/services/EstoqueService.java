package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.*;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.repository.EntradaEstoqueRepository;
import br.com.menegati.brb_revendedoras.repository.ProdutoRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final UserRepository userRepository;
    private final EntradaEstoqueRepository entradaEstoqueRepository;

    @Transactional
    public List<Long> registrarEstoque(MultipartFile file, String CPFRegistrador) {
        Admin admin = (Admin) userRepository.findByCpf(CPFRegistrador).orElseThrow( () -> new ResourceNotFoundException("CPF inválido"));
        List<ItemEntradaEstoque> itens = new ArrayList<>();
        EntradaEstoque entradaEstoque = new EntradaEstoque();

        log.info("Arquivo CSV recebido: {}", file.getOriginalFilename());
        List<Long> linhasIgnoradas = new ArrayList<>();
        List<Produto> produtosParaSalvar = new ArrayList<>();

        try(
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1));
                CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setDelimiter(';')
                        .build())
                ){

            log.info("Iniciando processamento do arquivo CSV");
            for (var record : csvParser) {
                try{
                    if(!validarLinha(record)){
                        linhasIgnoradas.add(record.getRecordNumber());
                        continue;
                    }

                    Produto produto = produtoRepository.findByCodigo(record.get("Codigo").trim()).orElse(new Produto());

                    String codigo = record.get("Codigo").trim();
                    String nome = record.get("Nome").trim();
                    String cest = !record.get("CEST").isEmpty() ? record.get("CEST").trim() : null;
                    String barras = !record.get("Barras").isEmpty() ? record.get("Barras").trim() : null;
                    String unidade = !record.get("Unidade").isEmpty() ? record.get("Unidade").trim() : null;
                    String ncm = !record.get("NCM").isEmpty() ? record.get("NCM").trim() : null;
                    String precoRaw = record.get("Venda").trim();
                    String precoString = precoRaw.replace(".", "").replace(",", ".");

                    produto.setCodigo(codigo);
                    produto.setNome(nome);
                    produto.setNcm(ncm);
                    produto.setCest(cest);
                    produto.setBarras(barras);
                    produto.setUnidade(unidade);
                    produto.setPrecoVenda(new BigDecimal(precoString));
                    produto.setQuantidadeDisponivel(1);
                    produto.setAtivo(true);

                    itens.add(criarItemEntradaEstoque(entradaEstoque,produto, 1));

                    produto.setUltimaAtualizacao(LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId()));

                    log.info("Produto {} da linha {} salvo com sucesso", codigo, record.getRecordNumber());
                    produtosParaSalvar.add(produto);

                } catch (Exception e){
                    log.error("Erro ao processar linha: {}", record.getRecordNumber());
                }
            }

            if(!produtosParaSalvar.isEmpty()){
                produtoRepository.saveAll(produtosParaSalvar);
                log.info("Arquivo CSV processado com sucesso! Produtos salvos: {} | Linhas ignoradas {}", produtosParaSalvar.size(), linhasIgnoradas.size());
            }

        } catch (IOException e) {
            log.error("Erro ao ler arquivo CSV: {}", e.getMessage());
            throw new RuntimeException("Falha ao processar CSV de estoque: " + e);
        }

        salvarEntradaEstoque(entradaEstoque,itens, admin.getName(), "Feito por documento csv: " + produtosParaSalvar.size() + " produtos salvos");
        entradaEstoqueRepository.save(entradaEstoque);

        return linhasIgnoradas.isEmpty() ? null : linhasIgnoradas;
    }

    private ItemEntradaEstoque criarItemEntradaEstoque(EntradaEstoque entradaEstoque,Produto produto, int quantidade){
        ItemEntradaEstoque itemEntradaEstoque = new ItemEntradaEstoque();
        itemEntradaEstoque.setEntrada(entradaEstoque);
        itemEntradaEstoque.setQuantidade(quantidade);
        itemEntradaEstoque.setProduto(produto);
        return itemEntradaEstoque;
    }


    private void salvarEntradaEstoque(EntradaEstoque entradaEstoque, List<ItemEntradaEstoque> itens, String registradoPor, String observacao){
        entradaEstoque.setItens(itens);
        entradaEstoque.setRegistradoPor(registradoPor);
        entradaEstoque.setObservacao(observacao);
    }


    public boolean validarLinha(CSVRecord record){
        if(!record.isMapped("Codigo") || !record.isMapped("Nome") || !record.isMapped("Venda") || !record.isMapped("NCM")
                || !record.isMapped("CEST") || !record.isMapped("Unidade") || !record.isMapped("Barras"))
        {
            log.warn("Linha {} ignorada: Falta algum cabeçalho", record.getRecordNumber());
            return false;
        }

        String codigoRaw = record.get("Codigo").trim();

        if(codigoRaw.isEmpty()) {
            log.warn("Linha ignorada por codigo vazio: {}", record.getRecordNumber());
            return false;
        }

        try {
            long codigo = Long.parseLong(codigoRaw);
            if(codigo < 100001) {
                log.warn("Linha {} ignorada por código ser menor que 100001", record.getRecordNumber());
                return false;
            }
        } catch (NumberFormatException e) {
            log.warn("Linha {} ignorada por formato de código inválido: {}", record.getRecordNumber(), codigoRaw);
            return false;
        }

        if (record.get("Nome").isEmpty()) {
            log.warn("Produto com codigo '{}' ignorado pois o nome está em branco! Linha : {}", codigoRaw, record.getRecordNumber());
            return false;
        }

        String precoRaw = record.get("Venda").trim();
        if(precoRaw.isEmpty()){
            log.warn("Produto com codigo '{}'  ignorado por falta de preco de venda! Linha : {}", codigoRaw, record.getRecordNumber());
            return false;
        }

        try{
            String precoString = precoRaw.replace(".", "").replace(",", ".");
            new BigDecimal(precoString);
         } catch (NumberFormatException e) {
             log.warn("Produto com codigo '{}' por preco de venda invalido: {}",codigoRaw, record.getRecordNumber());
             return false;
         }

        return true;
    }
}
