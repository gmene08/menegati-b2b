import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { PainelAdminData } from '../models/admin-data';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = '/api/admin';

  private http = inject(HttpClient);

  adminData = signal<PainelAdminData | null>(null);

  getAdminData() {
    return this.http
      .get<PainelAdminData>(`${this.apiUrl}`)
      .pipe(tap((data) => this.adminData.set(data)));
  }
}
