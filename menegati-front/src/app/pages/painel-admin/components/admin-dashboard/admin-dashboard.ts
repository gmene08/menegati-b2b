import { Component, computed, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { MOCK_REVENDEDORAS } from '../../admin-mock-data';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CurrencyPipe],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  private readonly revendedoras = signal(MOCK_REVENDEDORAS);

  protected readonly totalEmAberto = computed(() =>
    this.revendedoras().reduce((soma, r) => soma + r.saldoDevedor, 0),
  );

  protected readonly totalEmAtraso = computed(
    () => this.revendedoras().filter((r) => r.diasAtraso > 0).length,
  );

  protected readonly maletasAtivas = computed(
    () => this.revendedoras().filter((r) => r.loteStatus === 'ABERTO').length,
  );

  protected readonly exposicaoTotal = computed(() =>
    this.revendedoras().reduce((soma, r) => soma + r.exposicaoMaleta, 0),
  );

  protected readonly maioresDevedoras = computed(() =>
    this.revendedoras()
      .filter((r) => r.saldoDevedor > 0)
      .slice()
      .sort((a, b) => b.saldoDevedor - a.saldoDevedor)
      .slice(0, 5),
  );

  protected readonly alertasAtraso = computed(() =>
    this.revendedoras()
      .filter((r) => r.diasAtraso > 30)
      .slice()
      .sort((a, b) => b.diasAtraso - a.diasAtraso),
  );
}
