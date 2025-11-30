import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ExperienciaProfissional } from '../../models';

@Component({
  selector: 'app-experiencia-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './experiencia-form-modal.html',
  styleUrls: ['./experiencia-form-modal.css']
})
export class ExperienciaFormModalComponent implements OnInit {
  @Input() experiencia: ExperienciaProfissional | null = null; // null = criar novo
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<Partial<ExperienciaProfissional>>();

  form!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.buildForm();
  }

  buildForm(): void {
    this.form = this.fb.group({
      cargo: [this.experiencia?.cargo || '', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      instituicao: [this.experiencia?.instituicao || '', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      periodo: [this.experiencia?.periodo || '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descricao: [this.experiencia?.descricao || '', [Validators.maxLength(1000)]]
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const data: Partial<ExperienciaProfissional> = {
        ...this.form.value
      };
      if (this.experiencia?.id) {
        data.id = this.experiencia.id;
      }
      this.save.emit(data);
    } else {
      Object.keys(this.form.controls).forEach(key => {
        this.form.get(key)?.markAsTouched();
      });
    }
  }

  onClose(): void {
    this.form.reset();
    this.close.emit();
  }

  get isEditMode(): boolean {
    return !!this.experiencia;
  }
}
