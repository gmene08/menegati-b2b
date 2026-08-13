import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricoMaleta } from './historico-maleta';

describe('HistoricoMaleta', () => {
  let component: HistoricoMaleta;
  let fixture: ComponentFixture<HistoricoMaleta>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricoMaleta],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoricoMaleta);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('acertos', []);
    fixture.componentRef.setInput('cargas', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
