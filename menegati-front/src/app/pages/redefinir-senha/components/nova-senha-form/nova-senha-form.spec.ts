import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { NovaSenhaForm } from './nova-senha-form';

describe('NovaSenhaForm', () => {
  let component: NovaSenhaForm;
  let fixture: ComponentFixture<NovaSenhaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovaSenhaForm],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(NovaSenhaForm);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('errorMessage', '');
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
