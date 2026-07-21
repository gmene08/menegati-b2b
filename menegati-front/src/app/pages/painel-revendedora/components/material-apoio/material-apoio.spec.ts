import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialApoio } from './material-apoio';

describe('MaterialApoio', () => {
  let component: MaterialApoio;
  let fixture: ComponentFixture<MaterialApoio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaterialApoio],
    }).compileComponents();

    fixture = TestBed.createComponent(MaterialApoio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
