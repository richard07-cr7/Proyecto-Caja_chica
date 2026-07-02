import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../core/services/usuario.service';
import { PresupuestoService } from '../../core/services/presupuesto.service';
import { Usuario } from '../../models/usuario.model';
import { Departamento } from '../../models/departamento.model';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.component.html'
})
export class UsuariosComponent implements OnInit {

  usuarios: Usuario[] = [];
  departamentos: Departamento[] = [];

  username = '';
  password = '';
  rol = 'EMPLEADO';
  deptoId: number | null = null;
  mensaje = '';
  tipoMensaje = '';

  mostrarModal = false;
  editId: number | null = null;
  editUsername = '';
  editPassword = '';
  editRol = 'EMPLEADO';
  editDeptoId: number | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private presupuestoService: PresupuestoService
  ) {}

  ngOnInit() {
    this.cargarUsuarios();
    this.cargarDepartamentos();
  }

  cargarUsuarios() {
    this.usuarioService.listar().subscribe({
      next: (data) => this.usuarios = data
    });
  }

  cargarDepartamentos() {
    this.presupuestoService.listarDepartamentos().subscribe({
      next: (data) => this.departamentos = data
    });
  }

  registrar() {
    if (!this.username || !this.password || !this.rol) {
      this.mostrarMensaje('Completa los campos obligatorios.', 'error');
      return;
    }
    const usuario: any = {
      username: this.username,
      password: this.password,
      rol: this.rol,
      departamento: this.deptoId ? { id: this.deptoId } : null
    };
    this.usuarioService.registrar(usuario).subscribe({
      next: () => {
        this.mostrarMensaje('Usuario registrado correctamente.', 'ok');
        this.username = '';
        this.password = '';
        this.rol = 'EMPLEADO';
        this.deptoId = null;
        this.cargarUsuarios();
      },
      error: () => this.mostrarMensaje('Error al registrar el usuario.', 'error')
    });
  }

  abrirModal(u: Usuario) {
    this.editId = u.id;
    this.editUsername = u.username;
    this.editPassword = '';
    this.editRol = u.rol;
    this.editDeptoId = u.departamento?.id || null;
    this.mostrarModal = true;
  }

  cerrarModal() {
    this.mostrarModal = false;
  }

  guardarEdicion() {
    if (!this.editId) return;
    const datos: any = {
      username: this.editUsername,
      password: this.editPassword,
      rol: this.editRol,
      departamento: this.editDeptoId ? { id: this.editDeptoId } : null
    };
    this.usuarioService.actualizar(this.editId, datos).subscribe({
      next: () => {
        this.mostrarMensaje('Usuario actualizado correctamente.', 'ok');
        this.cerrarModal();
        this.cargarUsuarios();
      },
      error: () => this.mostrarMensaje('Error al actualizar el usuario.', 'error')
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar usuario?')) return;
    this.usuarioService.eliminar(id).subscribe({
      next: () => {
        this.mostrarMensaje('Usuario eliminado.', 'ok');
        this.cargarUsuarios();
      },
      error: () => this.mostrarMensaje('Error al eliminar el usuario.', 'error')
    });
  }

  mostrarMensaje(texto: string, tipo: string) {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }
}