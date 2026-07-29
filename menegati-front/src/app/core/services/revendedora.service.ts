import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PainelRevendedoraData } from '../models/painel-revendedora-data';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RevendedoraService {
  private apiUrl = '/api/revendedora';

  private http = inject(HttpClient);

  revendedoraData= signal<PainelRevendedoraData | null>(null);

  getRevendedoraData(){
    return this.http.get<PainelRevendedoraData>(`${this.apiUrl}`).pipe(
      tap(data => this.revendedoraData.set(data))
    );
  }
}
