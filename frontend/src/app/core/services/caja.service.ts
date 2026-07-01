import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { CajaChica } from '../../models/caja-chica.model';

@Injectable({ providedIn: 'root' })
export class CajaService {

  private url = `${environment.apiUrl}/api/caja`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  listar() {
    return this.http.get<CajaChica[]>(this.url, { headers: this.headers() });
  }

  buscarPorId(id: number) {
    return this.http.get<CajaChica>(`${this.url}/${id}`, { headers: this.headers() });
  }

  crear(montoInicial: number, departamentoId: number) {
    const params = new HttpParams()
      .set('montoInicial', String(montoInicial))
      .set('departamentoId', String(departamentoId));
    return this.http.post<CajaChica>(this.url, null, { headers: this.headers(), params });
  }

  actualizar(id: number, nuevoMonto: number) {
    const params = new HttpParams().set('nuevoMonto', String(nuevoMonto));
    return this.http.put<CajaChica>(`${this.url}/${id}`, null, { headers: this.headers(), params });
  }

  eliminar(id: number) {
    return this.http.delete(`${this.url}/${id}`, { headers: this.headers() });
  }
}