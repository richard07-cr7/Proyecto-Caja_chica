import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CajaService } from '../../core/services/caja.service';
import { PresupuestoService } from '../../core/services/presupuesto.service';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import { CajaChica } from '../../models/caja-chica.model';
import { Departamento } from '../../models/departamento.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  cajas: CajaChica[] = [];
  departamentos: Departamento[] = [];
  esAdmin = false;

  // Stats
  totalCajas = 0;
  totalGastos = 0;
  totalPendientes = 0;
  saldoTotal = 0;

  // Formulario nueva caja
  deptoId: number | null = null;
  montoInicial: number | null = null;
  mensaje = '';
  tipoMensaje = '';

  constructor(
    private cajaService: CajaService,
    private presupuestoService: PresupuestoService,
    private gastoService: GastoService,
    private authService: AuthService
  ) {
    this.esAdmin = this.authService.getRol() === 'ADMIN';
  }



  ngOnInit() {
  setTimeout(() => {
    this.cargarCajas();
    if (this.esAdmin) {
      this.cargarDepartamentos();
      this.cargarGastos();
    }
  }, 100);
}

  cargarCajas() {
  this.cajaService.listar().subscribe({
    next: (data) => {
      if (this.esAdmin) {
        this.cajas = data;
      } else {
        const deptoId = Number(this.authService.getDepartamentoId());
        this.cajas = data.filter(c => c.departamento?.id === deptoId);
      }
      this.totalCajas = this.cajas.length;
      this.saldoTotal = this.cajas.reduce((acc, c) => acc + Number(c.saldoActual), 0);
    },
    error: () => this.mostrarMensaje('Error al cargar las cajas.', 'error')
  });
}

  cargarDepartamentos() {
    this.presupuestoService.listarDepartamentos().subscribe({
      next: (data) => this.departamentos = data
    });
  }

  cargarGastos() {
    this.gastoService.listarTodos().subscribe({
      next: (data) => {
        this.totalGastos = data.length;
        this.totalPendientes = data.filter(g => g.estado === 'PENDIENTE').length;
      }
    });
  }

  crearCaja() {
    if (!this.deptoId || !this.montoInicial) {
      this.mostrarMensaje('Completa todos los campos.', 'error');
      return;
    }
    this.cajaService.crear(this.montoInicial, this.deptoId).subscribe({
      next: () => {
        this.mostrarMensaje('Caja creada correctamente.', 'ok');
        this.deptoId = null;
        this.montoInicial = null;
        this.cargarCajas();
      },
      error: () => this.mostrarMensaje('Error al crear la caja.', 'error')
    });
  }

  mostrarMensaje(texto: string, tipo: string) {
    this.mensaje = texto;
    this.tipoMensaje = tipo;
  }

  activa(caja: CajaChica) {
    return Number(caja.saldoActual) > 0;
  }
}