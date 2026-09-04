import { Component, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import type { AcertoAdmin, LancamentoFinanceiro, RevendedoraResumo } from '../../../../../../core/models/admin-data';
import { STATUS_REVENDEDORA_LABEL, TIPO_LANCAMENTO_LABEL } from '../../../../../../core/models/admin-data';
import { RegistrarPagamentoModal, type RegistrarPagamentoPayload } from '../registrar-pagamento-modal/registrar-pagamento-modal';
import { LancarAjusteModal, type LancarAjustePayload } from '../lancar-ajuste-modal/lancar-ajuste-modal';

@Component({
  selector: 'app-revendedora-detalhe',
  imports: [CurrencyPipe, RegistrarPagamentoModal, LancarAjusteModal],
  templateUrl: './revendedora-detalhe.html',
  styleUrl: './revendedora-detalhe.css',
})
export class RevendedoraDetalhe {
  readonly revendedora = input.required<RevendedoraResumo>();
  readonly lancamentos = input<LancamentoFinanceiro[]>([]);
  readonly acertos = input<AcertoAdmin[]>([]);

  readonly onVoltar = output<void>();
  readonly onRegistrarPagamento = output<RegistrarPagamentoPayload>();
  readonly onLancarAjuste = output<LancarAjustePayload>();

  protected readonly statusLabel = STATUS_REVENDEDORA_LABEL;
  protected readonly tipoLancamentoLabel = TIPO_LANCAMENTO_LABEL;

  protected readonly modalPagamentoAberto = signal(false);
  protected readonly modalAjusteAberto = signal(false);

  protected status(r: RevendedoraResumo): 'EM_DIA' | 'EM_ABERTO' | 'ATRASADA' {
    if (r.diasAtraso > 0) return 'ATRASADA';
    if (r.saldoDevedor > 0) return 'EM_ABERTO';
    return 'EM_DIA';
  }

  protected badgeClasse(r: RevendedoraResumo): string {
    switch (this.status(r)) {
      case 'EM_DIA':
        return 'bg-emerald-100 text-emerald-800';
      case 'EM_ABERTO':
        return 'bg-amber-100 text-amber-800';
      case 'ATRASADA':
        return 'bg-red-100 text-red-800';
    }
  }

  protected badgeAcertoClasse(status: AcertoAdmin['status']): string {
    switch (status) {
      case 'QUITADO':
        return 'bg-emerald-100 text-emerald-800';
      case 'PARCIAL':
        return 'bg-amber-100 text-amber-800';
      case 'EM_ABERTO':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-paper-soft text-ink-soft';
    }
  }

  protected acertoStatusLabel(status: AcertoAdmin['status']): string {
    switch (status) {
      case 'QUITADO':
        return 'Quitado';
      case 'PARCIAL':
        return 'Parcial';
      case 'EM_ABERTO':
        return 'Em aberto';
      default:
        return '';
    }
  }

  protected confirmarPagamento(payload: RegistrarPagamentoPayload): void {
    this.onRegistrarPagamento.emit(payload);
    this.modalPagamentoAberto.set(false);
  }

  protected confirmarAjuste(payload: LancarAjustePayload): void {
    this.onLancarAjuste.emit(payload);
    this.modalAjusteAberto.set(false);
  }

  protected voltar(): void {
    this.onVoltar.emit();
  }
}
