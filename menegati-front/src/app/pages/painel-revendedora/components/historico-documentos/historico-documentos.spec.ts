import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricoDocumentos } from './historico-documentos';

describe('HistoricoDocumentos', () => {
  let component: HistoricoDocumentos;
  let fixture: ComponentFixture<HistoricoDocumentos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricoDocumentos],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoricoDocumentos);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('documentos', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
