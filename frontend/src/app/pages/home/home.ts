import { Component } from '@angular/core';
import { StudiantDto } from '../../services/models';
import { StudiantService } from '../../services/services-custom/studiantService/studiant-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AddForm } from "../../components/add-form/add-form";
import { ImportForm } from "../../components/import-form/import-form";
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, AddForm, ImportForm],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  students: StudiantDto[] | undefined= [] ;
  student: StudiantDto | null = null;
  currentPage : number = 0;
  pageSize : number = 5;
  totalPages : number = 0;
  pages: number[] = [];
  update: boolean = false;
  showStudentForm: boolean = false;
  showImportForm: boolean = false;

  filterBy: string = '';
  filterValue: string = '';

  constructor(
    private studentService: StudiantService,
    private toastr : ToastrService
  ) {}

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents(page: number = 0) {
    this.studentService.getAllStudiants(page, this.pageSize).subscribe((res) => {
      if (!res) return; 
      this.students = res.content;
      this.currentPage = res.pageable?.pageNumber ?? 0;
      this.totalPages = res.totalPages ?? 0;
      this.pages = Array.from({ length: this.totalPages }, (_, i) => i );
      console.log(this.students);
    });
  }


  applyFilter() {
    if (!this.filterBy || !this.filterValue) return;

    if (this.filterBy === 'id') {
      const id = Number(this.filterValue);
      if (isNaN(id)) {
        this.students = []; 
        this.toastr.warning('ID should be a number!')
        return;
      }
      this.studentService.getStudiantById(id).subscribe(res => {
        this.students = res ? [res] : [];
      });
    } else if (this.filterBy === 'username') {
      this.studentService.searchStudiant(this.filterValue, this.currentPage, this.pageSize)
        .subscribe(res => {
          this.students = res?.content ?? [];
        });
    }
  }

  clearFilter() {
    this.filterValue = '';
    this.filterBy = '';
    this.loadStudents();
  }

  prevPage() {
    if (this.currentPage  && this.currentPage > 0) this.loadStudents(this.currentPage - 1);
  }

  nextPage() {
    if (this.currentPage && this.totalPages && this.currentPage < this.totalPages - 1) this.loadStudents(this.currentPage + 1);
  }

  goToPage(page: number) {
    this.loadStudents(page);
  }

  showAddForm() {
    this.showStudentForm=true;
    this.update=false;
  }

  editStudent(student: StudiantDto) {
    this.student=student;
    this.showStudentForm=true;
    this.update=true;
  }

  closeForm(){
    this.student=null;
    this.showStudentForm=false;
    this.update=false;
    this.showImportForm=false;
  }

  deleteStudent(id: number) {
    Swal.fire({
      title: 'Are you sure?',
      text: 'This action cannot be undone.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.studentService.deleteStudiant(id).subscribe(() => {
          this.loadStudents(this.currentPage);
          this.toastr.success('The student has been removed.');
        });
      }
    });
  }


  import(){
    this.showImportForm = true;
  }

  export() {
    this.studentService.exportCSV().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'students.csv'; 
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.toastr.error("Erreur export CSV!");
      }
    });
  }
}
