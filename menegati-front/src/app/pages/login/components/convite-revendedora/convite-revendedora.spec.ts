import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConviteRevendedora } from './convite-revendedora';

describe('ConviteRevendedora', () => {
  let component: ConviteRevendedora;
  let fixture: ComponentFixture<ConviteRevendedora>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConviteRevendedora],
    }).compileComponents();

    fixture = TestBed.createComponent(ConviteRevendedora);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
