import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Conquista } from '../../models';


@Component({
  selector: 'app-conquista-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './conquista-form-modal.html',
  styleUrls: ['./conquista-form-modal.css']
})
export class ConquistaFormModalComponent implements OnInit {
  @Input() conquista: Conquista | null = null; // null = criar novo
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<Partial<Conquista>>();

  form!: FormGroup;
  anoAtual = new Date().getFullYear();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.buildForm();
  }

  buildForm(): void {
    this.form = this.fb.group({
      tituloConquista: [this.conquista?.tituloConquista || '', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      ano: [this.conquista?.ano || this.anoAtual, [Validators.required, Validators.min(1900), Validators.max(2100)]],
      descricao: [this.conquista?.descricao || '', [Validators.maxLength(1000)]]
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const data: Partial<Conquista> = {
        ...this.form.value
      };
      if (this.conquista?.id) {
        data.id = this.conquista.id;
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
    return !!this.conquista;
  }
}
