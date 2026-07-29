import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginImg } from '../login/components/login-img/login-img';
import { VoltarButton } from '../login/components/voltar-button/voltar-button';
import { NovaSenhaForm, NovaSenhaData } from './components/nova-senha-form/nova-senha-form';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-redefinir-senha',
  imports: [LoginImg, VoltarButton, NovaSenhaForm],
  templateUrl: './redefinir-senha.html',
  styleUrl: './redefinir-senha.css',
})
export class RedefinirSenha implements OnInit {
  authService = inject(AuthService);
  route = inject(ActivatedRoute);

  token: string | null = null;
  senhaAlteradaComSucesso = signal(false);
  redefinirSenhaErrorMsg = signal<string>('');

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  redefinirSenha({ novaSenha, confirmarNovaSenha }: NovaSenhaData): void {
    this.redefinirSenhaErrorMsg.set('');

    if (!this.token) {
      this.redefinirSenhaErrorMsg.set('Link de redefinição inválido ou expirado.');
      return;
    }

    if (novaSenha !== confirmarNovaSenha) {
      this.redefinirSenhaErrorMsg.set("'Confirmar senha' deve ser igual a 'Nova senha'.");
      return;
    }

    this.authService.resetPassword({ token: this.token, newPassword: novaSenha }).subscribe({
      next: () => {
        this.senhaAlteradaComSucesso.set(true);
      },
      error: (error: HttpErrorResponse) => {
        this.redefinirSenhaErrorMsg.set(error.error?.message || 'Erro desconhecido');
      },
    });
  }
}
