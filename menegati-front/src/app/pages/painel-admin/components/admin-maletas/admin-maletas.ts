import { Component, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MOCK_PROCESSAMENTOS } from '../../admin-mock-data';
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
  imports: [CurrencyPipe, NovaCargaCard, NovoAcertoCard],
  templateUrl: './admin-maletas.html',
  styleUrl: './admin-maletas.css',
})
export class AdminMaletas {
  protected readonly processamentos = signal(MOCK_PROCESSAMENTOS);
  protected readonly tipoLabel = TIPO_LABEL;
  protected readonly origemLabel = ORIGEM_LABEL;
}
