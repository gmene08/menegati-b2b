import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginData } from '../../../../core/services/auth.service';
import { ErrorMessage } from '../../../../shared/components/error-message/error-message';

@Component({
  selector: 'app-login-form',
  imports: [FormsModule, ErrorMessage],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  protected emailOrCpf = '';
  protected password = '';
  protected rememberMe = false;

  errorMessage = input.required<string>();

  onEsqueceuSenha = output<void>();
  onLogin = output<LoginData>()

  login(): void {
    this.onLogin.emit({login: this.emailOrCpf, password: this.password, rememberMe: this.rememberMe});
  }

  esqueceuSenha(): void {
    this.onEsqueceuSenha.emit();
  }
}
