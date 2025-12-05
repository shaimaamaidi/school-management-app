import { TestBed } from '@angular/core/testing';
import { jwtInterceptor } from './jwt-interceptor-interceptor';

describe('jwtInterceptor', () => {
  let interceptor: jwtInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        jwtInterceptor,
      ]
    });
    interceptor = TestBed.inject(jwtInterceptor);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });
});