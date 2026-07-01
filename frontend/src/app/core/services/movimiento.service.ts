import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Movimiento } from '../../models/movimiento.model';

@Injectable({ providedIn: 'root' })
export class MovimientoService {

  private url = `${environment.apiUrl}/api/movimientos`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  listarTodos() {
    return this.http.get<Movimiento[]>(this.url, { headers: this.headers() });
  }

  listarPorCaja(cajaId: number) {
    return this.http.get<Movimiento[]>(`${this.url}/caja/${cajaId}`, { headers: this.headers() });
  }

  registrar(cajaId: number, tipo: string, monto: number, descripcion: string) {
    const params = new HttpParams()
      .set('cajaId', String(cajaId))
      .set('tipo', tipo)
      .set('monto', String(monto))
      .set('descripcion', descripcion);
    return this.http.post<Movimiento>(this.url, null, { headers: this.headers(), params });
  }
}