import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private url = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<any>(`${this.url}/login`, { username, password });
  }

  guardarSesion(data: any) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('rol', data.rol);
    localStorage.setItem('id', data.id);
    localStorage.setItem('username', data.username);
    localStorage.setItem('departamentoId', data.departamentoId);
    localStorage.setItem('departamentoNombre', data.departamentoNombre);
  }

  getToken() { return localStorage.getItem('token'); }
  getRol() { return localStorage.getItem('rol'); }
  getId() { return localStorage.getItem('id'); }
  getUsername() { return localStorage.getItem('username'); }
  estaLogueado() { return !!this.getToken(); }
  getDepartamentoId() { return localStorage.getItem('departamentoId'); }
  getDepartamentoNombre() { return localStorage.getItem('departamentoNombre'); }

  logout() { localStorage.clear(); }
}