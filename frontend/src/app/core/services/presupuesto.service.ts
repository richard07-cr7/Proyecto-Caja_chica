import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PresupuestoArea } from '../../models/presupuesto-area.model';
import { Departamento } from '../../models/departamento.model';

@Injectable({ providedIn: 'root' })
export class PresupuestoService {

  private url = `${environment.apiUrl}/api/presupuesto`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  listarPresupuestos() {
    return this.http.get<PresupuestoArea[]>(this.url, { headers: this.headers() });
  }

  listarDepartamentos() {
    return this.http.get<Departamento[]>(`${this.url}/departamentos`, { headers: this.headers() });
  }

  crearDepartamento(nombre: string) {
    const params = new HttpParams().set('nombre', nombre);
    return this.http.post<Departamento>(`${this.url}/departamento`, null, { headers: this.headers(), params });
  }

  crearPresupuesto(departamentoId: number, mes: string, presupuestoMensual: number) {
    const params = new HttpParams()
      .set('departamentoId', String(departamentoId))
      .set('mes', mes)
      .set('presupuestoMensual', String(presupuestoMensual));
    return this.http.post<PresupuestoArea>(this.url, null, { headers: this.headers(), params });
  }

  registrarConsumo(presupuestoId: number, monto: number) {
    const params = new HttpParams()
      .set('presupuestoId', String(presupuestoId))
      .set('monto', String(monto));
    return this.http.post<PresupuestoArea>(`${this.url}/consumo`, null, { headers: this.headers(), params });
  }
}