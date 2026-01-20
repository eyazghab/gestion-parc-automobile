import { TestBed } from '@angular/core/testing';

import { AffectationIaService } from './affectation-ia.service';

describe('AffectationIaService', () => {
  let service: AffectationIaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AffectationIaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
