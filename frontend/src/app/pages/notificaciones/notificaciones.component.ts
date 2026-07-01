import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacionService } from '../../core/services/notificacion.service';
import { Notificacion } from '../../models/notificacion.model';

@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notificaciones.component.html'
})
export class NotificacionesComponent implements OnInit {

  notificaciones: Notificacion[] = [];
  mensaje = '';
  tipoMensaje = '';

  constructor(private notificacionService: NotificacionService) {}

  ngOnInit() {
    this.cargarNotificaciones();
  }

  cargarNotificaciones() {
    this.notificacionService.misNotificaciones().subscribe({
      next: (data) => this.notificaciones = data,
      error: () => this.mostrarMensaje('Error al cargar notificaciones.', 'error')
    });
  }

  marcarLeida(id: number) {
    this.notificacionService.marcarLeida(id).subscribe({
      next: () => this.cargarNotificaciones()
    });
  }

  marcarTodasLeidas() {
    this.notificacionService.marcarTodasLeidas().subscribe({
      next: () => {
        this.mostrarMensaje('Todas las notificaciones marcadas como leídas.', 'ok');
        this.cargarNotificaciones();
      }
    });
  }

  badgeTipo(tipo: string) {
    if (tipo === 'APROBADO') return 'badge badge-aprobado';
    if (tipo === 'RECHAZADO') return 'badge badge-rechazado';
    if (tipo === 'PRESUPUESTO_AGOTADO') return 'badge badge-pendiente';
    return 'badge badge-empleado';
  }

  mostrarMensaje(texto: string, tipo: string) {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }
}