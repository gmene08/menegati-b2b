import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, finalize, Observable, of, tap } from 'rxjs';
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
export class AuthService {
  private apiUrl = '/api/auth'

  private http = inject(HttpClient);

  readonly user = signal<LoginResponse | null>(this.getInitialUserState());

  constructor() {}

  validateSession(): Observable<LoginResponse | null>{
    return this.http.get<LoginResponse>(`${this.apiUrl}/me`).pipe(
      tap((response) => {
        this.user.set(response);
      }),
      catchError((error) => {
        if(error.status === 401){
          this.clearSession();
        }
        return of(null);
      })
    );
  }

  private getInitialUserState(): LoginResponse | null {
    const name = localStorage.getItem('name') || sessionStorage.getItem('name');
    const role = localStorage.getItem('role') || sessionStorage.getItem('role');
    return name && role ? {name: name, role: role as Role} : null;
  }

  login(loginData: LoginData): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, loginData).pipe(
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

  logout(){
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      finalize(() => {
        this.clearSession();
      })
    );
  }

  private setSession(loginRes: LoginResponse, rememberMe: boolean) {
    const storage = rememberMe ? localStorage : sessionStorage;

    storage.setItem('name', loginRes.name);
    storage.setItem('role', loginRes.role);
    this.user.set(loginRes);
  }

  clearSession() {
    localStorage.removeItem('name');
    localStorage.removeItem('role');
    sessionStorage.removeItem('name');
    sessionStorage.removeItem('role');
    this.user.set(null);
  }

  isLoggedIn(){
    return !!this.user();
  }

}

