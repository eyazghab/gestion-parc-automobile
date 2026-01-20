import { TestBed } from '@angular/core/testing';

import { BonCarburantService } from './bon-carburant.service';

describe('BonCarburantService', () => {
  let service: BonCarburantService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BonCarburantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
