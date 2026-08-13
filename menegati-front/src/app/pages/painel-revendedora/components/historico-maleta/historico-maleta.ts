import { Component, input, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import type { Acerto, Carga } from '../../../../core/models/painel-revendedora-data';

@Component({
  selector: 'app-historico-maleta',
  imports: [CurrencyPipe],
  templateUrl: './historico-maleta.html',
  styleUrl: './historico-maleta.css',
})
export class HistoricoMaleta {
  readonly acertos = input.required<Acerto[]>();
  readonly cargas = input.required<Carga[]>();

  private readonly expandido = signal<Set<string>>(new Set());

  protected chaveAcerto(acerto: Acerto): string {
    return `${acerto.documentoMaleta}-${acerto.dataAcerto}`;
  }

  protected estaExpandido(acerto: Acerto): boolean {
    return this.expandido().has(this.chaveAcerto(acerto));
  }

  protected alternarExpandido(acerto: Acerto): void {
    const chave = this.chaveAcerto(acerto);
    this.expandido.update((atual) => {
      const novo = new Set(atual);
      if (novo.has(chave)) {
        novo.delete(chave);
      } else {
        novo.add(chave);
      }
      return novo;
    });
  }

  private parseDataBr(data: string): number {
    const [dia, mes, ano] = data.split('/').map(Number);
    if (!dia || !mes || !ano) return 0;
    return new Date(ano, mes - 1, dia).getTime();
  }

  protected vencido(acerto: Acerto): boolean {
    return this.parseDataBr(acerto.dataVencimento) < Date.now();
  }

  protected chaveCarga(carga: Carga): string {
    return `${carga.documentoMaleta}-${carga.dataAbertura}`;
  }
}
