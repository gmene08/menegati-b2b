import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RevendedoraFormulario } from './revendedora-formulario';

describe('RevendedoraFormulario', () => {
  let component: RevendedoraFormulario;
  let fixture: ComponentFixture<RevendedoraFormulario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RevendedoraFormulario],
    }).compileComponents();

    fixture = TestBed.createComponent(RevendedoraFormulario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
