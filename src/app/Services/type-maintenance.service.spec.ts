import { TestBed } from '@angular/core/testing';

import { TypeMaintenanceService } from './type-maintenance.service';

describe('TypeMaintenanceService', () => {
  let service: TypeMaintenanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TypeMaintenanceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
