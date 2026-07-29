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
    fixture.componentRef.setInput('revendedora', { nome: 'Teste', email: 'teste@menegati.com', metaMensal: 1000, valorDevidoAtual: 0 });
    fixture.componentRef.setInput('lote', { status: 'ABERTO', dataAbertura: '01/01/2026', valorTotalEstimado: 0, valorTotalAcertado: 0, itens: [] });
    fixture.componentRef.setInput('itens', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
