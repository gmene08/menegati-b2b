import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoltarButton } from './voltar-button';

describe('VoltarButton', () => {
  let component: VoltarButton;
  let fixture: ComponentFixture<VoltarButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoltarButton],
    }).compileComponents();

    fixture = TestBed.createComponent(VoltarButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
