import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PainelRevendedora } from './painel-revendedora';

describe('PainelRevendedora', () => {
  let component: PainelRevendedora;
  let fixture: ComponentFixture<PainelRevendedora>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PainelRevendedora],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PainelRevendedora);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
