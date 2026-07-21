import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MinhaMaleta } from './minha-maleta';

describe('MinhaMaleta', () => {
  let component: MinhaMaleta;
  let fixture: ComponentFixture<MinhaMaleta>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MinhaMaleta],
    }).compileComponents();

    fixture = TestBed.createComponent(MinhaMaleta);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('itens', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
