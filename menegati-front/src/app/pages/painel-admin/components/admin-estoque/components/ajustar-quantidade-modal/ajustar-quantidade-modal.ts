import { Component, computed, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type { ProdutoEstoque } from '../../../../../../core/models/admin-data';

export type TipoMovimento = 'ENTRADA' | 'SAIDA';

export interface AjustarQuantidadePayload {
  tipo: TipoMovimento;
  quantidade: number;
  motivo: string;
}

@Component({
  selector: 'app-ajustar-quantidade-modal',
  imports: [FormsModule],
  templateUrl: './ajustar-quantidade-modal.html',
  styleUrl: './ajustar-quantidade-modal.css',
})
export class AjustarQuantidadeModal {
  readonly produto = input.required<ProdutoEstoque>();

  readonly onConfirmar = output<AjustarQuantidadePayload>();
  readonly onCancelar = output<void>();

  protected readonly tipo = signal<TipoMovimento>('ENTRADA');
  protected readonly quantidade = signal<number | null>(null);
  protected readonly motivo = signal<string>('');

  protected readonly quantidadeResultante = computed(() => {
    const qtd = this.quantidade() ?? 0;
    const atual = this.produto().quantidadeDisponivel;
    return this.tipo() === 'ENTRADA' ? atual + qtd : atual - qtd;
  });

  protected confirmar(): void {
    const qtd = this.quantidade();
    if (!qtd || qtd <= 0) return;
    this.onConfirmar.emit({ tipo: this.tipo(), quantidade: qtd, motivo: this.motivo() });
  }

  protected cancelar(): void {
    this.onCancelar.emit();
  }
}
