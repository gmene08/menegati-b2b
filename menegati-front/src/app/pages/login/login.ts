import { Component, inject, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { LoginImg } from './components/login-img/login-img';
import { LoginForm } from './components/login-form/login-form';
import { Auth, LoginData } from '../../core/services/auth';
import { RecuperarSenhaForm } from './components/recuperar-senha-form/recuperar-senha-form';
import { HttpErrorResponse } from '@angular/common/http';
import { VoltarButton } from './components/voltar-button/voltar-button';
import { ConviteRevendedora } from './components/convite-revendedora/convite-revendedora';
import { finalize } from 'rxjs';
import { Role } from '../../core/models/role';

@Component({
  selector: 'app-login',
  imports: [RouterLink, LoginImg, LoginForm, RecuperarSenhaForm, VoltarButton, ConviteRevendedora],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private authService = inject(Auth);
  private router = inject(Router);

  loginErrorMsg = signal<string>('');

  recuperarSenhaErrorMsg = signal<string>('');
  recuperarSenhaMsg = signal<string>('');
  isLoadingRecuperar = signal<boolean>(false);
  esqueceuSenha = signal<boolean>(false);

  login(loginData: LoginData): void {
    this.authService
      .login({
        login: loginData.login,
        password: loginData.password,
        rememberMe: loginData.rememberMe,
      })
      .subscribe({
        next: (res) => {
          console.log('Login bem sucedido!');
          this.loginErrorMsg.set('');

          if(res.role === Role.REVENDEDOR ){
            this.router.navigate(['/painel-revendedora']);
          } else{
            this.router.navigate(['/']);
          }
        },
        error: (error: HttpErrorResponse) => {
          const message = error.error?.message || 'Erro desconhecido';
          console.error('Erro ao fazer login:', error);
          this.loginErrorMsg.set(message);
        },
      });
  }

  recuperarSenha(emailOrCpf: string): void {
    this.recuperarSenhaErrorMsg.set('');
    this.recuperarSenhaMsg.set('');

    this.isLoadingRecuperar.set(true);

    this.authService.forgotPassword(emailOrCpf).pipe(
      finalize(()=>this.isLoadingRecuperar.set(false))
    ).
    subscribe({
      next: (res) => {
        this.recuperarSenhaMsg.set(res.message);
      },
      error: (error: HttpErrorResponse) => {
        this.recuperarSenhaErrorMsg.set(
          'Ocorreu um erro no servidor. Por favor, tente novamente mais tarde.',
        );
      },
    });
  }

  esqueceuSenhaToggle() {
    this.esqueceuSenha.set(!this.esqueceuSenha());
    this.recuperarSenhaMsg.set('');
    this.recuperarSenhaErrorMsg.set('');
  }
}
