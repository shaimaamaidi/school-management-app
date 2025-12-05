import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { ErrorHandlerService } from '../ErrorHanderService/error-hander-service';
import { Router } from '@angular/router';
import { AuthenticationControllerService } from '../../services';
import { TokenService } from '../../token/token.service';
import { AuthenticationResponse,AuthenticationRequest } from '../../models';

@Injectable({
  providedIn: 'root',
})

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private authApi: AuthenticationControllerService,
    private tokenService: TokenService,
    private  toastr: ToastrService,
    private errorHandler: ErrorHandlerService,
    private router : Router
  ) {}

  login(username: string, password: string): Observable<void> {
    const request: AuthenticationRequest = {
      username: username,
      password: password
    };
    return this.authApi.authenticate({ body: request }).pipe(
      tap(response => {
        if (response.access_token && response.refresh_token) {
          localStorage.setItem("isLoggedIn", "true");
          this.tokenService.setAccessToken(response.access_token);
          this.tokenService.setRefreshToken(response.refresh_token);
          this.startTokenTimer();
        } else {
          this.toastr.error("Une erreur est survenue. Réessayez plus tard.", 'Erreur');
        }
      }),
      map(() => void 0),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  signup(username: string, password: string): Observable<void> {
    const request: AuthenticationRequest = {
      username: username,
      password: password
    };

    return this.authApi.registreAdmin({ body: request }).pipe(
      tap(response => {
        if (response.access_token && response.refresh_token) {
          this.tokenService.setAccessToken(response.access_token);
          this.tokenService.setRefreshToken(response.refresh_token);
          this.startTokenTimer();
        } else {
          this.toastr.error("Une erreur est survenue. Réessayez plus tard.", 'Erreur');
        }
      }),
      map(() => void 0),
      catchError(err => this.errorHandler.handle(err))
    );
  }  
  
  refreshToken(): Observable<AuthenticationResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      this.tokenService.clear?.();
      this.toastr.info('Votre session a expiré. Veuillez vous reconnecter.');
      this.router.navigate(['login']);
      return throwError(() => new Error('SESSION_EXPIRED'));
    }

    return this.authApi.refreshToken().pipe(
      map(response => response ?? { access_token: '', refresh_token: '' }),
      catchError(err => {
        this.tokenService.clear?.();
        this.toastr.info('Votre session a expiré. Veuillez vous reconnecter.');
        this.router.navigate(['/login']);
        return throwError(() => new Error('SESSION_EXPIRED'));
      })
    );
  }

  getRole(): string | undefined {
    return this.tokenService.getuserRole();
  }

  private startTokenTimer() {
    const exp = this.tokenService.getExpiration() * 1000;
    const now = Date.now();
    const timeout = exp - now - 120000;

    if (timeout > 0) {
      setTimeout(() => {
        this.refreshToken().subscribe(newTokens => {
          if (newTokens.access_token && newTokens.refresh_token) {
            this.tokenService.setAccessToken(newTokens.access_token);
            this.tokenService.setRefreshToken(newTokens.refresh_token);
            this.startTokenTimer();
          }
        });
      }, timeout);
    }
  }

  logout(): Observable<void> {
    return this.authApi.logOut().pipe(
      tap(() => {
        this.tokenService.clear();
      }),
      catchError(err => this.errorHandler.handle(err))
    );
  }

}