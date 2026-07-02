import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { GastoService } from '../../core/services/gasto.service';
import { CajaService } from '../../core/services/caja.service';
import { PresupuestoService } from '../../core/services/presupuesto.service';
import { AuthService } from '../../core/services/auth.service';
import { CajaChica } from '../../models/caja-chica.model';
import { PresupuestoArea } from '../../models/presupuesto-area.model';
import { Gasto } from '../../models/gasto.model';

@Component({
  selector: 'app-registrar-gasto',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registrar-gasto.component.html'
})
export class RegistrarGastoComponent implements OnInit {

  cajas: CajaChica[] = [];
  presupuestos: PresupuestoArea[] = [];
  misGastos: Gasto[] = [];

  areaNombre = '';
  cajaId: number | null = null;
  presupuestoId: number | null = null;
  categoria = '';
  monto: number | null = null;
  descripcion = '';
  archivo: File | null = null;

  mensaje = '';
  tipoMensaje = '';

  constructor(
    private gastoService: GastoService,
    private cajaService: CajaService,
    private presupuestoService: PresupuestoService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.areaNombre = this.authService.getDepartamentoNombre() || 'Sin área';
    this.deptoId = Number(this.authService.getDepartamentoId());
    this.cargarCajas();
    this.cargarPresupuestos();
    this.cargarMisGastos();
  }

  cargarCajas() {
  this.cajaService.listar().subscribe({
    next: (data) => {
      this.cajas = data.filter(c =>
        c.departamento?.id === this.deptoId
      );
    }
  });
}

mesActual = new Date().toISOString().slice(0, 7); // "2026-07"
deptoId: number | null = null;

cargarPresupuestos() {
  this.presupuestoService.listarPresupuestos().subscribe({
    next: (data) => {
      this.presupuestos = data.filter(p =>
        p.departamento?.id === this.deptoId &&
        p.mes === this.mesActual
      );
    }
  });
}

  cargarMisGastos() {
    const id = Number(this.authService.getId());
    this.gastoService.listarPorUsuario(id).subscribe({
      next: (data) => this.misGastos = data.slice(0, 5) // últimos 5
    });
  }

  onArchivoSeleccionado(event: any) {
    this.archivo = event.target.files[0] || null;
  }

  registrar() {
    if (!this.cajaId || !this.presupuestoId || !this.categoria || !this.monto || !this.descripcion) {
      this.mostrarMensaje('Completa todos los campos obligatorios.', 'error');
      return;
    }

    const gasto: any = {
      categoria: this.categoria,
      monto: this.monto,
      descripcion: this.descripcion
    };

    this.gastoService.registrar(gasto, this.cajaId, this.presupuestoId).subscribe({
      next: (gastoCreado) => {
        if (this.archivo && gastoCreado.id) {
          this.gastoService.subirComprobante(gastoCreado.id, this.archivo).subscribe();
        }
        this.mostrarMensaje('Gasto registrado correctamente.', 'ok');
        this.limpiar();
        this.cargarMisGastos();
      },
      error: () => this.mostrarMensaje('Error al registrar el gasto.', 'error')
    });
  }

  limpiar() {
    this.cajaId = null;
    this.presupuestoId = null;
    this.categoria = '';
    this.monto = null;
    this.descripcion = '';
    this.archivo = null;
  }

  badgeClase(estado: string) {
    if (estado === 'APROBADO') return 'badge badge-aprobado';
    if (estado === 'RECHAZADO') return 'badge badge-rechazado';
    return 'badge badge-pendiente';
  }

  mostrarMensaje(texto: string, tipo: string) {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }
}