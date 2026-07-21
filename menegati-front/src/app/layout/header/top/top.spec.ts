import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Top } from './top';

describe('Top', () => {
  let component: Top;
  let fixture: ComponentFixture<Top>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Top],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Top);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
