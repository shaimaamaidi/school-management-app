import { Injectable } from '@angular/core';
import { StudiantControllerService } from '../../services';
import { catchError, map, Observable, of } from 'rxjs';
import { PageStudiantDto, StudiantDto } from '../../models';
import { ErrorHandlerService } from '../ErrorHanderService/error-hander-service';

@Injectable({
  providedIn: 'root',
})
export class StudiantService {
  constructor(
    private studiantApi : StudiantControllerService,
    private errorHandler : ErrorHandlerService
  ) {}

  getAllStudiants(page : number=0,size : number=10 ) : Observable<PageStudiantDto | undefined>{
    return this.studiantApi.getAllStudents({ page , size}).pipe(
      map(response => response ?? undefined),
      catchError(err =>{ 
        this.errorHandler.handle(err);
        return of(undefined);
      })
    );
  }

  getStudiantById(id : number) : Observable<StudiantDto | undefined>{
    return this.studiantApi.getStudentById({ id }).pipe(
      map(response => response ?? undefined),
      catchError(err => {
        this.errorHandler.handle(err);
        return of(undefined);
      })
    );
  }

  addStudiant(studiant : StudiantDto) : Observable<string>{
    return this.studiantApi.addStudent({ body : studiant}).pipe(
      map(response => response ?? ""),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  updateStudiant(studiant : StudiantDto) :Observable<string>{
    return this.studiantApi.updateStudent({ body : studiant}).pipe(
      map(response => response ?? ""),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  deleteStudiant(id : number) : Observable<string>{
    return this.studiantApi.deleteStudent({ id }).pipe(
      map(response => response ?? ""),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  searchStudiant(username: string, page : number=0,size : number=10) : Observable<PageStudiantDto | undefined>{
    return this.studiantApi.searchStudents({ username, page, size}).pipe(
      map(response => response ?? undefined),
      catchError(err => {
        this.errorHandler.handle(err);
        return of(undefined);
      })
    );
  }

  filterByLevel(level: string, page : number=0,size : number=10) : Observable<PageStudiantDto | undefined>{
    return this.studiantApi.filterByLevel({ level, page, size}).pipe(
      map(response => response ?? undefined),
      catchError(err => {
        this.errorHandler.handle(err);
        return of(undefined);})
    );
  }

  importCSV(file: File): Observable<string> {
    return this.studiantApi.importCsv({ body: { file } }).pipe(
      map(response => response ?? ""),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  exportCSV(): Observable<Blob> {
    return this.studiantApi.exportCsv().pipe(
      catchError(err => this.errorHandler.handle(err))
    );
  }
}
