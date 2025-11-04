import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardProfessor } from './card-professor';

describe('CardProfessor', () => {
  let component: CardProfessor;
  let fixture: ComponentFixture<CardProfessor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardProfessor]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardProfessor);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
