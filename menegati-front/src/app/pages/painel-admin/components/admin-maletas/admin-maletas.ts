import { Component, input } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import type { ProcessamentoLog, RevendedoraResumo } from '../../../../core/models/admin-data';
import { NovaCargaCard } from './components/nova-carga-card/nova-carga-card';
import { NovoAcertoCard } from './components/novo-acerto-card/novo-acerto-card';

const TIPO_LABEL: Record<string, string> = {
  ESTOQUE: 'Atualização de estoque',
  CARGA: 'Nova carga recebida',
  ACERTO: 'Acerto realizado',
};

const ORIGEM_LABEL: Record<string, string> = {
  PDF: 'PDF',
  MANUAL: 'Manual',
  CSV: 'CSV',
};

@Component({
  selector: 'app-admin-maletas',
  imports: [CurrencyPipe, DatePipe, NovaCargaCard, NovoAcertoCard],
  templateUrl: './admin-maletas.html',
  styleUrl: './admin-maletas.css',
})
export class AdminMaletas {
  readonly processamentos = input<ProcessamentoLog[]>([]);
  readonly revendedoras = input<RevendedoraResumo[]>([]);
  protected readonly tipoLabel = TIPO_LABEL;
  protected readonly origemLabel = ORIGEM_LABEL;
}
