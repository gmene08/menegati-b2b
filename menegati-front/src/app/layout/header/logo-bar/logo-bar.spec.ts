import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { LogoBar } from './logo-bar';

describe('LogoBar', () => {
  let component: LogoBar;
  let fixture: ComponentFixture<LogoBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogoBar],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(LogoBar);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
