import { TestBed } from '@angular/core/testing';

import { CarburantIaService } from './carburant-ia.service';

describe('CarburantIaService', () => {
  let service: CarburantIaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CarburantIaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
