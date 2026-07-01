import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Notificacion } from '../../models/notificacion.model';

@Injectable({ providedIn: 'root' })
export class NotificacionService {

  private url = `${environment.apiUrl}/api/notificaciones`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  misNotificaciones() {
    return this.http.get<Notificacion[]>(`${this.url}/mis-notificaciones`, { headers: this.headers() });
  }

  noLeidas() {
    return this.http.get<{ count: number }>(`${this.url}/no-leidas`, { headers: this.headers() });
  }

  marcarLeida(id: number) {
    return this.http.put(`${this.url}/${id}/leer`, null, { headers: this.headers() });
  }

  marcarTodasLeidas() {
    return this.http.put(`${this.url}/leer-todas`, null, { headers: this.headers() });
  }
}