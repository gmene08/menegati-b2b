import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PainelHeader } from './painel-header';

describe('PainelHeader', () => {
  let component: PainelHeader;
  let fixture: ComponentFixture<PainelHeader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PainelHeader],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PainelHeader);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('revendedora', { nome: 'Teste', codigo: '000.000', metaMensal: 0, valorDevidoAtual: 0 });
    fixture.componentRef.setInput('abaAtiva', 'visao-geral');
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
