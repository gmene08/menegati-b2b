import { Component, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

export type FormaPagamento = 'DINHEIRO' | 'PIX' | 'CARTAO' | 'TRANSFERENCIA';

export interface RegistrarPagamentoPayload {
  valor: number;
  data: string;
  forma: FormaPagamento;
  observacao: string;
}

@Component({
  selector: 'app-registrar-pagamento-modal',
  imports: [FormsModule, CurrencyPipe],
  templateUrl: './registrar-pagamento-modal.html',
  styleUrl: './registrar-pagamento-modal.css',
})
export class RegistrarPagamentoModal {
  readonly nomeRevendedora = input.required<string>();
  readonly saldoDevedor = input.required<number>();

  readonly onConfirmar = output<RegistrarPagamentoPayload>();
  readonly onCancelar = output<void>();

  protected readonly valor = signal<number | null>(null);
  protected readonly data = signal<string>('');
  protected readonly forma = signal<FormaPagamento>('DINHEIRO');
  protected readonly observacao = signal<string>('');

  protected confirmar(): void {
    this.onConfirmar.emit({
      valor: this.valor() ?? 0,
      data: this.data(),
      forma: this.forma(),
      observacao: this.observacao(),
    });
  }

  protected cancelar(): void {
    this.onCancelar.emit();
  }
}
