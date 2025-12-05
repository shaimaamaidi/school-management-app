import { Component, EventEmitter, Injector, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { StudiantService } from '../../services/services-custom/studiantService/studiant-service';
import { CommonModule } from '@angular/common';
import { StudiantDto } from '../../services/models';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-form.html',
  styleUrl: './add-form.css',
})
export class AddForm implements OnChanges{
  @Input()showStudentForm: boolean = false;
  @Output() closeForm = new EventEmitter<void>();
  @Output() loadStudents = new EventEmitter<void>();
  @Input() update: boolean = false;  // true = update, false = add
  @Input() studiant: StudiantDto | null = null;
  error = '';

  studentForm: StudiantDto = {
    username: '',
    level: ''
  };

  ngOnChanges(changes: SimpleChanges) {
    if (this.update && this.studiant) {
      this.studentForm = { ...this.studiant };
    } else if (!this.update) {
      this.resetForm();
    }
  }

  constructor(
    private studentService : StudiantService,
    private toastr : ToastrService
  ){}

  closeStudentForm(){
    this.closeForm.emit();
    this.resetForm();
  }

  private resetForm() {
    this.studentForm = { username: '', level: '' };
    this.error = '';
  }

  handleStudentSubmit() {
    this.error = '';

    if (!this.studentForm.username || !this.studentForm.level) {
      this.error = 'All fields are required!';
      return;
    }

     if (this.update) {
      if (this.studentForm.username === this.studiant?.username && this.studentForm.level === this.studiant?.level) {
        this.error = 'No changes detected to update!';
        return;
      }
      console.log(this.studentForm);
      this.studentService.updateStudiant(this.studentForm).subscribe({
        next: () => {
          this.toastr.success(`Student ${this.studentForm.username} updated successfully.`);
          this.closeStudentForm();
        }
      });
    } else {
      this.studentService.addStudiant(this.studentForm).subscribe({
        next: () => {
          this.toastr.success(`Student ${this.studentForm.username} added successfully.`);
          this.closeStudentForm();
        }
      });
    }
  }
} 

