import { Component } from '@angular/core';

interface MaterialApoioItem {
  titulo: string;
  descricao: string;
  formato: string;
  icone: string;
}

const MATERIAL_APOIO_MOCK: MaterialApoioItem[] = [
  {
    titulo: 'Catálogo digital de peças',
    descricao: 'Fotos e descrições das coleções atuais para mostrar às suas clientes.',
    formato: 'PDF · 12 MB',
    icone: 'M12 4.5v15m7.5-7.5h-15M19.5 12a7.5 7.5 0 11-15 0 7.5 7.5 0 0115 0z',
  },
  {
    titulo: 'Tabela de preços vigente',
    descricao: 'Valores de venda sugeridos, atualizados a cada nova carga da maleta.',
    formato: 'PDF · 850 KB',
    icone: 'M9 7h6m-6 4h6m-6 4h4M5 3.75h14A1.25 1.25 0 0120.25 5v14A1.25 1.25 0 0119 20.25H5A1.25 1.25 0 013.75 19V5A1.25 1.25 0 015 3.75z',
  },
  {
    titulo: 'Guia de cuidados com as joias',
    descricao: 'Dicas para orientar as clientes sobre conservação e brilho das peças.',
    formato: 'PDF · 1,2 MB',
    icone: 'M12 21c-4.97-3.14-8-6.86-8-10.5A5.5 5.5 0 0112 5a5.5 5.5 0 018 5.5c0 3.64-3.03 7.36-8 10.5z',
  },
  {
    titulo: 'Roteiro de atendimento',
    descricao: 'Script com sugestões de abordagem para apresentar a maleta às clientes.',
    formato: 'PDF · 430 KB',
    icone: 'M8 10h8M8 14h5M21 12a9 9 0 11-6.219-8.56L21 5l-1.56 4.219A8.96 8.96 0 0121 12z',
  },
];

@Component({
  selector: 'app-material-apoio',
  imports: [],
  templateUrl: './material-apoio.html',
  styleUrl: './material-apoio.css',
})
export class MaterialApoio {
  protected readonly materiais = MATERIAL_APOIO_MOCK;
}
