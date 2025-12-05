import { HttpInterceptorFn } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, take, switchMap, tap } from 'rxjs/operators';
import { TokenService } from '../token/token.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AuthService } from '../services-custom/authService/auth-service';

@Injectable()
export class jwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(
    private authService: AuthService,
    private tokenService : TokenService,
    private toastr: ToastrService,
    private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler) {
    if (request.url.includes('/auth/authenticate') || request.url.includes('/auth/register')) {
      return next.handle(request);
    }

    if(request.url.includes('/auth/refresh_token')){
      request=this.addTokenHeader(request, this.tokenService.getRefreshToken()!);
    }else{
      const token = this.tokenService.getAccessToken();
      if (token) {
        request = this.addTokenHeader(request, token);
      }
    }
    return next.handle(request).pipe(
      catchError(err => {
        if (err instanceof HttpErrorResponse && (err.status === 401)) {
          if (request.url.includes('/api/auth/refresh_token')) {
            this.tokenService.clear(); 
            this.toastr.info('Votre session a expiré. Veuillez vous reconnecter.');
            this.router.navigate(['login']);
            return throwError(() => new Error('SESSION_EXPIRED'));
          }
          return this.handle401Error(request, next);
        }
        return throwError(() => err);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap(response => {
          this.isRefreshing = false;
          if(response.access_token && response.refresh_token) {
            this.tokenService.setAccessToken(response.access_token);
            this.tokenService.setRefreshToken(response.refresh_token);
            this.refreshTokenSubject.next(response.access_token);
            return next.handle(this.addTokenHeader(request, response.access_token));
          } else {
            this.toastr.error('Une erreur interne est survenue. Réessayez plus tard.');
            this.router.navigate(['login']);
            return throwError(() => new Error('REFRESH_FAILED'));
          }
        }),
        catchError(err => {
          this.isRefreshing = false;
          this.tokenService.clear(); 
          this.toastr.info('Votre session a expiré. Veuillez vous reconnecter.');
          this.router.navigate(['/login']);
          return throwError(() => new Error('SESSION_EXPIRED'));
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token !== null),
        take(1),
        switchMap(token => next.handle(this.addTokenHeader(request, token!)))
      );
    }
  }

  private addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

}
