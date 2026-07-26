import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ErrorMessage } from '../../../../shared/components/error-message/error-message';

export interface NovaSenhaData {
  novaSenha: string;
  confirmarNovaSenha: string;
}

@Component({
  selector: 'app-nova-senha-form',
  imports: [FormsModule, RouterLink, ErrorMessage],
  templateUrl: './nova-senha-form.html',
  styleUrl: './nova-senha-form.css',
})
export class NovaSenhaForm {
  errorMessage = input.required<string>();
  sucesso = input<boolean>(false);

  novaSenha = '';
  confirmarNovaSenha = '';

  onRedefinirSenha = output<NovaSenhaData>();

  redefinirSenha(): void {
    this.onRedefinirSenha.emit({
      novaSenha: this.novaSenha,
      confirmarNovaSenha: this.confirmarNovaSenha,
    });
  }
}
