import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginImg } from './login-img';

describe('LoginImg', () => {
  let component: LoginImg;
  let fixture: ComponentFixture<LoginImg>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginImg],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginImg);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
