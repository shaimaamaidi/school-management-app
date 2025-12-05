import { Injectable } from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private jwtHelper = new JwtHelperService();

  setAccessToken(token: string) {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  setRefreshToken(token: string) {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  isAccessTokenValid(): boolean {
    const token = this.getAccessToken();
    if (!token) {
      return false;
    }
    return !this.jwtHelper.isTokenExpired(token);
  }

  isRefreshTokenValid(): boolean {
    const token = this.getRefreshToken();
    if (!token) {
      return false;
    }
    return !this.jwtHelper.isTokenExpired(token);
  }

  clearTokens() {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  getuserRole(): string | undefined  {
    const token = this.getAccessToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      return decodedToken?.role ;
    }
    return undefined ;
  }

  getUserId():number | undefined {
    const token = this.getAccessToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      return decodedToken?.user_id;
    }
    return undefined;
  }

  getMail():string | undefined {
    const token = this.getAccessToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      return decodedToken?.sub ;
    }
    return undefined ;
  }

  getExpiration():number{
    const token = this.getRefreshToken();
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      return decodedToken?.exp || 0;
    }
    return 0;
  }

  clear(): void {
    this.setAccessToken('');
    this.setRefreshToken('');
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }
}
