import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AdminService } from './admin';
import type { DetalheRevendedoraData } from '../models/admin-data';

describe('AdminService', () => {
  let service: AdminService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AdminService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getDetalheRevendedora busca GET /api/admin/revendedora/{id} e devolve o corpo', () => {
    const detalhe: DetalheRevendedoraData = {
      resumo: {
        id: 7,
        nome: 'Ana',
        cpf: '00000000000',
        telefone: '',
        saldoDevedor: 500,
        saldoBonus: 0,
        exposicaoMaleta: 1200,
        diasAtraso: 0,
        status: 'EM_DIA',
        dataUltimoAcerto: '2026-01-10',
        loteStatus: 'ABERTO',
      },
      lancamentos: [],
      acertos: [],
    };

    let recebido: DetalheRevendedoraData | undefined;
    service.getDetalheRevendedora(7).subscribe((d) => (recebido = d));

    const req = http.expectOne('/api/admin/revendedora/7');
    expect(req.request.method).toBe('GET');
    req.flush(detalhe);

    expect(recebido).toEqual(detalhe);
  });
});
