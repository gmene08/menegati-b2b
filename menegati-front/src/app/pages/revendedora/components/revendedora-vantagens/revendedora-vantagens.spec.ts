import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RevendedoraVantagens } from './revendedora-vantagens';

describe('RevendedoraVantagens', () => {
  let component: RevendedoraVantagens;
  let fixture: ComponentFixture<RevendedoraVantagens>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RevendedoraVantagens],
    }).compileComponents();

    fixture = TestBed.createComponent(RevendedoraVantagens);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
