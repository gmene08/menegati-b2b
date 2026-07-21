import { Component, signal } from '@angular/core';
import { PainelHeader } from './components/painel-header/painel-header';
import { VisaoGeral } from './components/visao-geral/visao-geral';
import { MinhaMaleta } from './components/minha-maleta/minha-maleta';
import { HistoricoAcertos } from './components/historico-acertos/historico-acertos';
import { MaterialApoio } from './components/material-apoio/material-apoio';
import type { PainelTabId } from './painel-tabs';
import { HISTORICO_ACERTOS_MOCK, ITENS_MALETA_MOCK, LOTE_MOCK, REVENDEDORA_MOCK } from './painel-data';

@Component({
  selector: 'app-painel-revendedora',
  imports: [PainelHeader, VisaoGeral, MinhaMaleta, HistoricoAcertos, MaterialApoio],
  templateUrl: './painel-revendedora.html',
  styleUrl: './painel-revendedora.css',
})
export class PainelRevendedora {
  protected readonly abaAtiva = signal<PainelTabId>('visao-geral');

  protected readonly revendedora = REVENDEDORA_MOCK;
  protected readonly lote = LOTE_MOCK;
  protected readonly historico = HISTORICO_ACERTOS_MOCK;
  protected readonly itens = signal(ITENS_MALETA_MOCK);

  protected mudarAba(id: PainelTabId): void {
    this.abaAtiva.set(id);
  }

  protected marcarComoVendido(codigo: string): void {
    this.itens.update((itens) =>
      itens.map((item) =>
        item.codigo === codigo && item.status === 'ENCARREGADO'
          ? { ...item, status: 'MARC_VENDIDO_REV' as const }
          : item,
      ),
    );
  }
}
