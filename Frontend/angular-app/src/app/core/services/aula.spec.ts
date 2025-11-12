import { TestBed } from '@angular/core/testing';

import { Aula } from './aula';

describe('Aula', () => {
  let service: Aula;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Aula);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
