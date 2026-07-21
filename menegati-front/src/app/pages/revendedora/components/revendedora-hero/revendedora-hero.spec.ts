import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RevendedoraHero } from './revendedora-hero';

describe('RevendedoraHero', () => {
  let component: RevendedoraHero;
  let fixture: ComponentFixture<RevendedoraHero>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RevendedoraHero],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(RevendedoraHero);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
