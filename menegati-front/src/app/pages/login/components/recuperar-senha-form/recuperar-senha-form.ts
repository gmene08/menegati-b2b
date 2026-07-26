import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ErrorMessage } from '../../../../shared/components/error-message/error-message';

@Component({
  selector: 'app-recuperar-senha-form',
  imports: [FormsModule, ErrorMessage],
  templateUrl: './recuperar-senha-form.html',
  styleUrl: './recuperar-senha-form.css',
})
export class RecuperarSenhaForm {
  emailOrCpf = '';

  message = input.required<string>();
  errorMessage = input.required<string>();
  isLoading = input.required<boolean>();

  onRecuperarSenha = output<string>();
  onVoltarParaLogin = output<void>();

  recuperarSenha(): void {
    if(this.isLoading() || !this.emailOrCpf.trim()) return;

    this.onRecuperarSenha.emit(this.emailOrCpf);
    this.emailOrCpf = '';
  }

  voltarParaLogin(): void {
    this.onVoltarParaLogin.emit();
  }
}
