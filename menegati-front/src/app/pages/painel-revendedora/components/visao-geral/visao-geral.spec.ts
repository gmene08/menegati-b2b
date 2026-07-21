import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VisaoGeral } from './visao-geral';

describe('VisaoGeral', () => {
  let component: VisaoGeral;
  let fixture: ComponentFixture<VisaoGeral>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VisaoGeral],
    }).compileComponents();

    fixture = TestBed.createComponent(VisaoGeral);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('revendedora', { nome: 'Teste', codigo: '000.000', metaMensal: 1000, valorDevidoAtual: 0 });
    fixture.componentRef.setInput('lote', { numeroConsignacao: '1', status: 'ABERTO', dataAbertura: '01/01/2026' });
    fixture.componentRef.setInput('itens', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
