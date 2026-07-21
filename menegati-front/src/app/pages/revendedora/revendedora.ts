import { Component } from '@angular/core';
import { RevendedoraHero } from './components/revendedora-hero/revendedora-hero';
import { RevendedoraVantagens } from './components/revendedora-vantagens/revendedora-vantagens';
import { RevendedoraFormulario } from './components/revendedora-formulario/revendedora-formulario';

@Component({
  selector: 'app-revendedora',
  imports: [RevendedoraHero, RevendedoraVantagens, RevendedoraFormulario],
  templateUrl: './revendedora.html',
  styleUrl: './revendedora.css',
})
export class Revendedora {}
