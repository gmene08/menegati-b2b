import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecuperarSenhaForm } from './recuperar-senha-form';

describe('RecuperarSenhaForm', () => {
  let component: RecuperarSenhaForm;
  let fixture: ComponentFixture<RecuperarSenhaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecuperarSenhaForm],
    }).compileComponents();

    fixture = TestBed.createComponent(RecuperarSenhaForm);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('message', '');
    fixture.componentRef.setInput('errorMessage', '');
    fixture.componentRef.setInput('isLoading', false);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
