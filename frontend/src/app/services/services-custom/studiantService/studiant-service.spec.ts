import { TestBed } from '@angular/core/testing';

import { StudiantService } from './studiant-service';

describe('StudiantService', () => {
  let service: StudiantService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudiantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
