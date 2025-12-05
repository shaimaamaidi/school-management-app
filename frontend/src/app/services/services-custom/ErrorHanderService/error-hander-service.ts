import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(
    private toastr: ToastrService,
    private router: Router
  ) {}

  handle(error: HttpErrorResponse): Observable<never> {

    if (error.status === 401) {
      this.toastr.error('You are not authenticated. Please log in again.');
      this.router.navigate(['login']);
      return throwError(() => new Error('UNAUTHORIZED'));
    }


    if (error.status === 0) {
      this.toastr.error('Cannot connect to the server. Please check your connection.', 'Connection Error');
      return throwError(() => new Error('SERVER_UNREACHABLE'));
    }

 
    let errorMsg = error.error?.message || 'An unexpected error occurred. Please try again later.';

    this.toastr.error(errorMsg, 'Error');

    return throwError(() => new Error(errorMsg));
  }
}
