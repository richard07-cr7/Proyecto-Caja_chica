import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CajaChica } from '../../models/caja-chica.model';

@Injectable({ providedIn: 'root' })
export class CajaService {

  private url = `${environment.apiUrl}/api/caja`;

  constructor(private http: HttpClient) {}

  listar() {
    return this.http.get<CajaChica[]>(this.url);
  }

  buscarPorId(id: number) {
    return this.http.get<CajaChica>(`${this.url}/${id}`);
  }

  crear(montoInicial: number, departamentoId: number) {
    const params = new HttpParams()
      .set('montoInicial', String(montoInicial))
      .set('departamentoId', String(departamentoId));
    return this.http.post<CajaChica>(this.url, null, { params });
  }

  actualizar(id: number, nuevoMonto: number) {
    const params = new HttpParams().set('nuevoMonto', String(nuevoMonto));
    return this.http.put<CajaChica>(`${this.url}/${id}`, null, { params });
  }

  eliminar(id: number) {
    return this.http.delete(`${this.url}/${id}`);
  }
}