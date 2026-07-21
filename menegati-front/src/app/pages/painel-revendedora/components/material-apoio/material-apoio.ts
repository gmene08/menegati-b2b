import { Component } from '@angular/core';
import { MATERIAL_APOIO_MOCK } from '../../painel-data';

@Component({
  selector: 'app-material-apoio',
  imports: [],
  templateUrl: './material-apoio.html',
  styleUrl: './material-apoio.css',
})
export class MaterialApoio {
  protected readonly materiais = MATERIAL_APOIO_MOCK;
}
