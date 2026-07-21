import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Revendedora } from './revendedora';

describe('Revendedora', () => {
  let component: Revendedora;
  let fixture: ComponentFixture<Revendedora>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Revendedora],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Revendedora);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
