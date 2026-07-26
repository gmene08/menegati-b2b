import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Role } from '../models/role';

export interface LoginData {
  login: string;
  password: string;
  rememberMe?: boolean;
}

export interface ResetPasswordData {
  token: string;
  newPassword: string;
}

export interface ForgotPasswordResponse {
  message: string;
}

export interface LoginResponse {
  name: string;
  role: Role
}

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private apiUrl = '/api/auth'

  private http = inject(HttpClient);

  login(loginData: LoginData): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {login: loginData.login, password: loginData.password }).pipe(
      tap((response)=>{
        this.setSession(response, loginData.rememberMe || false);
      })
    );
  }

  forgotPassword(emailOrCpf: string){
    return this.http.post<ForgotPasswordResponse>(`${this.apiUrl}/forgot-password`, {emailOrCpf: emailOrCpf});
  }

  resetPassword(resetPasswordData: ResetPasswordData){
    return this.http.post(`${this.apiUrl}/reset-password`, {token: resetPasswordData.token, newPassword: resetPasswordData.newPassword});
  }

  private setSession(loginRes: LoginResponse, remember: boolean) {
    const storage = remember ? localStorage : sessionStorage;

    storage.setItem('name', loginRes.name);
    storage.setItem('role', loginRes.role);
  }

  logout(){
    localStorage.clear();
    sessionStorage.clear();
  }

  isLoggedIn(){
    return !!(localStorage.getItem('token') || sessionStorage.getItem('token'));
  }

  getToken(){
    return localStorage.getItem('token') || sessionStorage.getItem('token');
  }
}
