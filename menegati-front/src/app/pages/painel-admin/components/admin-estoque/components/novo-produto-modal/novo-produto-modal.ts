import { Component, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import type { ProdutoEstoque } from '../../../../../../core/models/admin-data';

@Component({
  selector: 'app-novo-produto-modal',
  imports: [FormsModule],
  templateUrl: './novo-produto-modal.html',
  styleUrl: './novo-produto-modal.css',
})
export class NovoProdutoModal {
  readonly onConfirmar = output<ProdutoEstoque>();
  readonly onCancelar = output<void>();

  protected readonly codigo = signal<string>('');
  protected readonly nome = signal<string>('');
  protected readonly precoVenda = signal<number | null>(null);
  protected readonly quantidadeDisponivel = signal<number | null>(null);

  protected confirmar(): void {
    if (!this.codigo().trim() || !this.nome().trim()) return;
    this.onConfirmar.emit({
      codigo: this.codigo().trim(),
      nome: this.nome().trim(),
      precoVenda: this.precoVenda() ?? 0,
      quantidadeDisponivel: this.quantidadeDisponivel() ?? 0,
    });
  }

  protected cancelar(): void {
    this.onCancelar.emit();
  }
}
