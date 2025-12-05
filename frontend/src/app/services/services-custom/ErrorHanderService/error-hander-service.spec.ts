import { TestBed } from '@angular/core/testing';

import { ErrorHanderService } from './error-hander-service';

describe('ErrorHanderService', () => {
  let service: ErrorHanderService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorHanderService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
