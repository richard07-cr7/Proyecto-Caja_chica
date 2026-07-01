import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Gasto } from '../../models/gasto.model';

@Injectable({ providedIn: 'root' })
export class GastoService {

  private url = `${environment.apiUrl}/api/gastos`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  listarTodos() {
    return this.http.get<Gasto[]>(this.url, { headers: this.headers() });
  }

  listarPorUsuario(usuarioId: number) {
    return this.http.get<Gasto[]>(`${this.url}/mis-gastos/${usuarioId}`, { headers: this.headers() });
  }

  listarPorEstado(valor: string) {
    const params = new HttpParams().set('valor', valor);
    return this.http.get<Gasto[]>(`${this.url}/estado`, { headers: this.headers(), params });
  }

  listarPorCategoria(valor: string) {
    const params = new HttpParams().set('valor', valor);
    return this.http.get<Gasto[]>(`${this.url}/categoria`, { headers: this.headers(), params });
  }

  registrar(gasto: Gasto, cajaId: number, presupuestoId: number) {
    const params = new HttpParams()
      .set('cajaId', String(cajaId))
      .set('presupuestoId', String(presupuestoId));
    return this.http.post<Gasto>(this.url, gasto, { headers: this.headers(), params });
  }

  aprobar(id: number) {
    return this.http.put<Gasto>(`${this.url}/${id}/aprobar`, null, { headers: this.headers() });
  }

  rechazar(id: number) {
    return this.http.put<Gasto>(`${this.url}/${id}/rechazar`, null, { headers: this.headers() });
  }

  subirComprobante(id: number, archivo: File) {
    const formData = new FormData();
    formData.append('archivo', archivo);
    return this.http.post<Gasto>(`${this.url}/${id}/comprobante`, formData, { headers: this.headers() });
  }
}