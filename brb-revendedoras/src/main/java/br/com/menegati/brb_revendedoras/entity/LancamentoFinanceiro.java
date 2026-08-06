package br.com.menegati.brb_revendedoras.entity;

import br.com.menegati.brb_revendedoras.enums.CarteiraLancamento;
import br.com.menegati.brb_revendedoras.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lancamento_financeiro")
public class LancamentoFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // updatable = false garante imutabilidade a nível de Hibernate para todas as colunas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revendedor_id", nullable = false, updatable = false)
    private Revendedor revendedor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime data;

    @PrePersist
    protected void onCreate() {
        if (this.data == null) this.data = LocalDateTime.now(TimeZone.getTimeZone("America/Sao_Paulo").toZoneId());
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TipoLancamento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private CarteiraLancamento carteira;

    @Column(nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal valor;

    @Column(nullable = false, length = 255, updatable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acerto_id", updatable = false)
    private Acerto acerto;

    // Preparado para entrada futura de um módulo de pagamentos
    @Column(name = "pagamento_id", updatable = false)
    private Long pagamentoId;

    @Column(name = "criado_por", length = 100, updatable = false)
    private String criadoPor;

    @Column(length = 500, updatable = false)
    private String observacao;
}
