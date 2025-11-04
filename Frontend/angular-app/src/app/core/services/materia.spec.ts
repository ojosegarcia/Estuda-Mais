import { TestBed } from '@angular/core/testing';

import { Materia } from './materia';

describe('Materia', () => {
  let service: Materia;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Materia);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
