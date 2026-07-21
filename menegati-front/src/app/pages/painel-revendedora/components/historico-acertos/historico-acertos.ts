import { Component, input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import type { AcertoHistorico, TipoDocumentoMaleta } from '../../painel-data';

const TIPO_DOCUMENTO_LABEL: Record<TipoDocumentoMaleta, string> = {
  MALETA_ENTRADA: 'Nova carga recebida',
  MALETA_ACERTO: 'Acerto realizado',
};

@Component({
  selector: 'app-historico-acertos',
  imports: [CurrencyPipe],
  templateUrl: './historico-acertos.html',
  styleUrl: './historico-acertos.css',
})
export class HistoricoAcertos {
  readonly historico = input.required<AcertoHistorico[]>();

  protected readonly tipoLabel = TIPO_DOCUMENTO_LABEL;

  protected icone(tipo: TipoDocumentoMaleta): string {
    return tipo === 'MALETA_ACERTO'
      ? 'M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z'
      : 'M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z';
  }
}
