import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { LoginImg } from './components/login-img/login-img';
import { LoginForm } from './components/login-form/login-form';
import { AuthService, LoginData } from '../../core/services/auth.service';
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
export class Login implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  returnUrl: string = '/';

  loginErrorMsg = signal<string>('');

  recuperarSenhaErrorMsg = signal<string>('');
  recuperarSenhaMsg = signal<string>('');
  isLoadingRecuperar = signal<boolean>(false);
  esqueceuSenha = signal<boolean>(false);

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  login(loginData: LoginData): void {
    this.authService
      .login({
        login: loginData.login,
        password: loginData.password,
        rememberMe: loginData.rememberMe,
      })
      .subscribe({
        next: (user) => {
          console.log('Login bem sucedido!');
          this.loginErrorMsg.set('');

          if (this.returnUrl && this.returnUrl !== '/') {
            this.router.navigate([this.returnUrl]);
          } else {
            const destination = user.role === Role.REVENDEDOR ? '/painel-revendedora' : '/';
            this.router.navigate([destination]);
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

    this.authService
      .forgotPassword(emailOrCpf)
      .pipe(finalize(() => this.isLoadingRecuperar.set(false)))
      .subscribe({
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
