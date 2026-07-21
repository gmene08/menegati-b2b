import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PresentationArticle } from './presentation-article';

describe('PresentationArticle', () => {
  let component: PresentationArticle;
  let fixture: ComponentFixture<PresentationArticle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresentationArticle],
    }).compileComponents();

    fixture = TestBed.createComponent(PresentationArticle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
