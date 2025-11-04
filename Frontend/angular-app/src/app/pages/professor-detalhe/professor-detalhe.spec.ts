import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessorDetalhe } from './professor-detalhe';

describe('ProfessorDetalhe', () => {
  let component: ProfessorDetalhe;
  let fixture: ComponentFixture<ProfessorDetalhe>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfessorDetalhe]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfessorDetalhe);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
