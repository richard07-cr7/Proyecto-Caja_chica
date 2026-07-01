import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Usuario } from '../../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {

  private url = `${environment.apiUrl}/api/usuarios`;

  constructor(private http: HttpClient) {}

  private headers() {
    return new HttpHeaders({ Authorization: `Bearer ${localStorage.getItem('token')}` });
  }

  listar() {
    return this.http.get<Usuario[]>(this.url, { headers: this.headers() });
  }

  buscar(username: string) {
    return this.http.get<Usuario>(`${this.url}/${username}`, { headers: this.headers() });
  }

  registrar(usuario: Usuario) {
    return this.http.post<Usuario>(this.url, usuario, { headers: this.headers() });
  }

  actualizar(id: number, usuario: Usuario) {
    return this.http.put<Usuario>(`${this.url}/${id}`, usuario, { headers: this.headers() });
  }

  eliminar(id: number) {
    return this.http.delete(`${this.url}/${id}`, { headers: this.headers() });
  }
}