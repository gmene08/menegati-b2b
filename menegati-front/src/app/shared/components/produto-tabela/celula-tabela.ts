import { Directive, inject, input, TemplateRef } from '@angular/core';

/**
 * Marca um `<ng-template>` como a célula de uma coluna da ProdutoTabela.
 * O nome casa com a `chave` de uma coluna `formato: 'template'`,
 * ou com a palavra reservada "acao" para a última coluna.
 */
@Directive({
  selector: 'ng-template[appCelula]',
})
export class CelulaTabela {
  readonly appCelula = input.required<string>();

  readonly template = inject<TemplateRef<{ $implicit: any }>>(TemplateRef);
}
