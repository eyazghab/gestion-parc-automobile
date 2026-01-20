import { TestBed } from '@angular/core/testing';

import { TypeCarrosserieService } from './type-carrosserie.service';

describe('TypeCarrosserieService', () => {
  let service: TypeCarrosserieService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TypeCarrosserieService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
