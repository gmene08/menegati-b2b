import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionsTeaser } from './collections-teaser';

describe('CollectionsTeaser', () => {
  let component: CollectionsTeaser;
  let fixture: ComponentFixture<CollectionsTeaser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CollectionsTeaser],
    }).compileComponents();

    fixture = TestBed.createComponent(CollectionsTeaser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
