import { TestBed } from '@angular/core/testing';

import { RevendedoraService } from './revendedora.service';

describe('Revendedora', () => {
  let service: RevendedoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RevendedoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
