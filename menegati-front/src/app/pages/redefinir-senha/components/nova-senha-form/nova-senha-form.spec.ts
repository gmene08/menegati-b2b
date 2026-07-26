import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NovaSenhaForm } from './nova-senha-form';

describe('NovaSenhaForm', () => {
  let component: NovaSenhaForm;
  let fixture: ComponentFixture<NovaSenhaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovaSenhaForm],
    }).compileComponents();

    fixture = TestBed.createComponent(NovaSenhaForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
