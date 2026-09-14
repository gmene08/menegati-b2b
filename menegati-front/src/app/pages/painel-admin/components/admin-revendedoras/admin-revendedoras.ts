import { Component, inject, input, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { finalize } from 'rxjs';
import {
  STATUS_REVENDEDORA_LABEL,
  type DetalheRevendedoraData,
  type RevendedoraResumo,
  type StatusRevendedora,
} from '../../../../core/models/admin-data';
import { AdminService } from '../../../../core/services/admin';
import { RevendedoraDetalhe } from './components/revendedora-detalhe/revendedora-detalhe';
import {
  type ColunaTabela,
  type FiltroTabela,
  type OrdenacaoTabela,
} from '../../../../shared/components/tabela/tabela.tipos';
import { Tabela } from '../../../../shared/components/tabela/tabela';
import { CelulaTabela } from '../../../../shared/components/tabela/celula-tabela';

const STATUS_ORDENADOS: StatusRevendedora[] = ['EM_DIA', 'EM_ABERTO', 'ATRASADA'];

const BADGE_CLASSES: Record<StatusRevendedora, string> = {
  EM_DIA: 'bg-emerald-100 text-emerald-800',
  EM_ABERTO: 'bg-amber-100 text-amber-800',
  ATRASADA: 'bg-red-100 text-red-800',
};

@Component({
  selector: 'app-admin-revendedoras',
  imports: [CurrencyPipe, RevendedoraDetalhe, Tabela, CelulaTabela],
  templateUrl: './admin-revendedoras.html',
  styleUrl: './admin-revendedoras.css',
})
export class AdminRevendedoras {
  private readonly adminService = inject(AdminService);

  readonly revendedoras = input<RevendedoraResumo[]>([]);

  protected readonly colunas: ColunaTabela<RevendedoraResumo>[] = [
    { chave: 'nome', titulo: 'Revendedora', formato: 'principal', ordenavel: true, tipo: 'dados' },
    {
      chave: 'cpf',
      titulo: 'CPF',
      formato: 'mono',
      alinhamento: 'centro',
      tipo: 'dados',
      largura: 'w-36',
    },
    {
      chave: 'saldoDevedor',
      titulo: 'Saldo devedor',
      formato: 'template',
      ordenavel: true,
      tipo: 'dados',
      largura: 'w-36',
    },
    {
      chave: 'exposicaoMaleta',
      titulo: 'Exposição',
      formato: 'moeda',
      alinhamento: 'centro',
      ordenavel: true,
      tipo: 'dados',
    },
    {
      chave: 'diasAtraso',
      titulo: 'Atraso',
      formato: 'template',
      ordenavel: true,
      tipo: 'dados',
      largura: 'w-24',
    },
    { chave: 'status', titulo: 'Status', formato: 'template', tipo: 'dados', largura: 'w-28' },
    { chave: 'detalhes', titulo: 'Ação', tipo: 'livre' },
  ];

  protected readonly filtros: FiltroTabela<RevendedoraResumo>[] = [
    {
      tipo: 'select',
      chave: 'status',
      rotulo: 'Todos os status',
      opcoes: STATUS_ORDENADOS.map((status) => ({
        valor: status,
        rotulo: STATUS_REVENDEDORA_LABEL[status],
      })),
    },
  ];

  protected readonly ordenacaoInicial: OrdenacaoTabela<RevendedoraResumo> = {
    chave: 'saldoDevedor',
    direcao: 'desc',
  };

  protected readonly revendedoraSelecionadaId = signal<number | null>(null);
  protected readonly detalhe = signal<DetalheRevendedoraData | null>(null);
  protected readonly detalheCarregando = signal(false);
  protected readonly detalheErro = signal('');

  protected badgeClasse(status: StatusRevendedora): string {
    return BADGE_CLASSES[status];
  }

  protected statusRotulo(status: StatusRevendedora): string {
    return STATUS_REVENDEDORA_LABEL[status];
  }

  protected abrirDetalhe(r: RevendedoraResumo): void {
    this.revendedoraSelecionadaId.set(r.id);
    this.carregarDetalhe(r.id);
  }

  protected fecharDetalhe(): void {
    this.revendedoraSelecionadaId.set(null);
    this.detalhe.set(null);
    this.detalheErro.set('');
  }

  protected tentarNovamente(): void {
    const id = this.revendedoraSelecionadaId();
    if (id !== null) this.carregarDetalhe(id);
  }

  private carregarDetalhe(id: number): void {
    this.detalhe.set(null);
    this.detalheErro.set('');
    this.detalheCarregando.set(true);

    this.adminService
      .getDetalheRevendedora(id)
      .pipe(finalize(() => this.detalheCarregando.set(false)))
      .subscribe({
        next: (data) => this.detalhe.set(data),
        error: (error) => {
          console.error('Erro ao carregar o detalhe da revendedora:', error);
          this.detalheErro.set('Erro ao carregar os dados da revendedora.');
        },
      });
  }
}
