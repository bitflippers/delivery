import { TestBed } from '@angular/core/testing';

import { SpinnerServiceService } from './spinner-service.service';

describe('SpinnerServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SpinnerServiceService = TestBed.get(SpinnerServiceService);
    expect(service).toBeTruthy();
  });
});
