import { Component, inject, OnInit, signal } from '@angular/core';
import { PainelHeader } from './components/painel-header/painel-header';
import { VisaoGeral } from './components/visao-geral/visao-geral';
import { MinhaMaleta } from './components/minha-maleta/minha-maleta';
import { HistoricoAcertos } from './components/historico-acertos/historico-acertos';
import { MaterialApoio } from './components/material-apoio/material-apoio';
import type { PainelTabId } from './painel-tabs';
import { RevendedoraService } from '../../core/services/revendedora.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-painel-revendedora',
  imports: [PainelHeader, VisaoGeral, MinhaMaleta, HistoricoAcertos, MaterialApoio],
  templateUrl: './painel-revendedora.html',
  styleUrl: './painel-revendedora.css',
})
export class PainelRevendedora implements OnInit {
  private readonly revendedoraService = inject(RevendedoraService);

  protected readonly painelData = this.revendedoraService.revendedoraData;

  protected readonly isLoading = signal<boolean>(true);
  protected readonly errorMessage = signal<string>('');

  protected readonly abaAtiva = signal<PainelTabId>('visao-geral');

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    this.revendedoraService
      .getRevendedoraData()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        error: (error) => {
          console.error('Erro ao carregar os dados da revendedora:', error);
          this.errorMessage.set('Erro ao carregar os dados da revendedora.');
        },
      });
  }

  protected mudarAba(id: PainelTabId): void {
    this.abaAtiva.set(id);
  }

  protected marcarComoVendido(codigo: string): void {

  }

  protected desmarcarVendido(codigo: string): void {

  }

  tentarNovamente(){

  }
}
