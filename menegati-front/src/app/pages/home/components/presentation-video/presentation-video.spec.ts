import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PresentationVideo } from './presentation-video';

describe('PresentationVideo', () => {
  let component: PresentationVideo;
  let fixture: ComponentFixture<PresentationVideo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresentationVideo],
    }).compileComponents();

    fixture = TestBed.createComponent(PresentationVideo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
