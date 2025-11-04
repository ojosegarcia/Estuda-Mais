import { TestBed } from '@angular/core/testing';

import { Professor } from './professor';

describe('Professor', () => {
  let service: Professor;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Professor);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
