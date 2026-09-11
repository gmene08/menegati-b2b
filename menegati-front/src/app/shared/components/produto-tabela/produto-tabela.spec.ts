import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProdutoTabela } from './produto-tabela';

describe('ProdutoTabela', () => {
  let component: ProdutoTabela<{ codigo: string }>;
  let fixture: ComponentFixture<ProdutoTabela<{ codigo: string }>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProdutoTabela],
    }).compileComponents();

    fixture = TestBed.createComponent<ProdutoTabela<{ codigo: string }>>(ProdutoTabela);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('linhas', []);
    fixture.componentRef.setInput('colunas', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
