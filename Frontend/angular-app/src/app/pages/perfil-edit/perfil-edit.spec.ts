import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerfilEdit } from './perfil-edit';

describe('PerfilEdit', () => {
  let component: PerfilEdit;
  let fixture: ComponentFixture<PerfilEdit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerfilEdit]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PerfilEdit);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
