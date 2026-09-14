import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Tabela } from './tabela';

describe('Tabela', () => {
  let component: Tabela<{ id: number }>;
  let fixture: ComponentFixture<Tabela<{ id: number }>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Tabela],
    }).compileComponents();

    fixture = TestBed.createComponent<Tabela<{ id: number }>>(Tabela);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('linhas', []);
    fixture.componentRef.setInput('colunas', []);
    fixture.componentRef.setInput('chaveLinha', 'id');
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
