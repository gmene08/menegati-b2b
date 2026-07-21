import { Component } from '@angular/core';

interface Vantagem {
  titulo: string;
  descricao: string;
  icone: string;
}

@Component({
  selector: 'app-revendedora-vantagens',
  imports: [],
  templateUrl: './revendedora-vantagens.html',
  styleUrl: './revendedora-vantagens.css',
})
export class RevendedoraVantagens {
  protected readonly vantagens: Vantagem[] = [
    {
      titulo: 'Zero risco inicial',
      descricao:
        'Trabalhe com o nosso sistema de consignação seguro. Pague apenas pelas peças que vender, sem necessidade de comprar stock antecipadamente.',
      icone:
        'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    },
    {
      titulo: 'Maleta exclusiva',
      descricao:
        'Receba um mostruário luxuoso e organizado, cuidadosamente montado com as peças mais desejadas e as últimas tendências do mercado.',
      icone:
        'M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z',
    },
    {
      titulo: 'Lucros atrativos',
      descricao:
        'Tenha acesso a comissões progressivas e escalonáveis através do nosso sistema de bónus. O seu esforço e dedicação ditam os seus rendimentos.',
      icone: 'M13 7h8m0 0v8m0-8l-8 8-4-4-6 6',
    },
    {
      titulo: 'Painel da revendedora',
      descricao:
        'Acompanhe a sua maleta e marque as peças vendidas diretamente no nosso painel online, sempre que quiser.',
      icone:
        'M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7m0 10a2 2 0 002 2h2a2 2 0 002-2V7a2 2 0 00-2-2h-2a2 2 0 00-2 2',
    },
  ];
}
