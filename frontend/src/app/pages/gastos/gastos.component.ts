import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import { Gasto } from '../../models/gasto.model';

@Component({
  selector: 'app-gastos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gastos.component.html'
})
export class GastosComponent implements OnInit {

  gastos: Gasto[] = [];
  esAdmin = false;
  mensaje = '';
  tipoMensaje = '';

  constructor(
    private gastoService: GastoService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.esAdmin = this.authService.getRol() === 'ADMIN';
    this.cargarGastos();
  }

  cargarGastos() {
    if (this.esAdmin) {
      this.gastoService.listarTodos().subscribe({
        next: (data) => this.gastos = data,
        error: () => this.mostrarMensaje('Error al cargar los gastos.', 'error')
      });
    } else {
      const id = Number(this.authService.getId());
      this.gastoService.listarPorUsuario(id).subscribe({
        next: (data) => this.gastos = data,
        error: () => this.mostrarMensaje('Error al cargar tus gastos.', 'error')
      });
    }
  }

  mostrarMensaje(texto: string, tipo: string) {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }

  badgeClase(estado: string) {
    if (estado === 'APROBADO') return 'badge badge-aprobado';
    if (estado === 'RECHAZADO') return 'badge badge-rechazado';
    return 'badge badge-pendiente';
  }

  aprobar(id: number) {
    this.gastoService.aprobar(id).subscribe({
      next: () => {
        this.mostrarMensaje('Gasto aprobado correctamente.', 'ok');
        this.cargarGastos();
      },
      error: () => this.mostrarMensaje('Error al aprobar el gasto.', 'error')
    });
  }

  rechazar(id: number) {
    this.gastoService.rechazar(id).subscribe({
      next: () => {
        this.mostrarMensaje('Gasto rechazado correctamente.', 'ok');
        this.cargarGastos();
      },
      error: () => this.mostrarMensaje('Error al rechazar el gasto.', 'error')
    });
  }
}