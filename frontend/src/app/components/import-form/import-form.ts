import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StudiantService } from '../../services/services-custom/studiantService/studiant-service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-import-form',
  standalone:true,
  imports: [CommonModule,FormsModule],
  templateUrl: './import-form.html',
  styleUrl: './import-form.css',
})
export class ImportForm {
  @Input() showImportForm = false;
  @Output() closeForm = new EventEmitter<void>();
  @Output() loadStudents = new EventEmitter<void>();
  error: string | null = null;

  studentForm = {
    file: null as File | null,
  };
  constructor(
    private studentService: StudiantService,
    private toastr : ToastrService
  ) {}

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (file.type !== 'text/csv' && !file.name.endsWith('.csv')) {
        this.error = 'Only CSV files are allowed!';
        this.studentForm.file = null;
      } else {
        this.error = null;
        this.studentForm.file = file;
      }
    }
  }

  handleStudentSubmit() {
    if (!this.studentForm.file) {
      this.error = 'Please select a CSV file!';
      return;
    }

    this.studentService.importCSV(this.studentForm.file).subscribe({
      next: (res) => {
        this.toastr.success('File imported successfully.')
        this.error = null;
        this.loadStudents.emit(); 
      },
      error: (err) => {
        this.error = 'Failed to import CSV!';
      },
    });
  }

  closeStudentForm() {
    this.closeForm.emit();
  }
}
