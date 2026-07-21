import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricoAcertos } from './historico-acertos';

describe('HistoricoAcertos', () => {
  let component: HistoricoAcertos;
  let fixture: ComponentFixture<HistoricoAcertos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoricoAcertos],
    }).compileComponents();

    fixture = TestBed.createComponent(HistoricoAcertos);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('historico', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
