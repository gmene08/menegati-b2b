import { Component, computed, input, output, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

export type TipoAjuste = 'AJUSTE_CREDITO' | 'AJUSTE_DEBITO';

export interface LancarAjustePayload {
  tipo: TipoAjuste;
  valor: number;
  justificativa: string;
}

@Component({
  selector: 'app-lancar-ajuste-modal',
  imports: [FormsModule, CurrencyPipe],
  templateUrl: './lancar-ajuste-modal.html',
  styleUrl: './lancar-ajuste-modal.css',
})
export class LancarAjusteModal {
  readonly nomeRevendedora = input.required<string>();
  readonly saldoDevedor = input.required<number>();

  readonly onConfirmar = output<LancarAjustePayload>();
  readonly onCancelar = output<void>();

  protected readonly tipo = signal<TipoAjuste>('AJUSTE_DEBITO');
  protected readonly valor = signal<number | null>(null);
  protected readonly justificativa = signal<string>('');

  protected readonly justificativaValida = computed(() => this.justificativa().trim().length >= 10);

  protected confirmar(): void {
    if (!this.justificativaValida() || !this.valor()) return;
    this.onConfirmar.emit({
      tipo: this.tipo(),
      valor: this.valor() ?? 0,
      justificativa: this.justificativa().trim(),
    });
  }

  protected cancelar(): void {
    this.onCancelar.emit();
  }
}
