import { TestBed } from '@angular/core/testing';

import { MaintenanceIAService } from './maintenance-ia.service';

describe('MaintenanceIAService', () => {
  let service: MaintenanceIAService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MaintenanceIAService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
