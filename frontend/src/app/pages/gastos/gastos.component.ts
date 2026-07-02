import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import { PresupuestoService } from '../../core/services/presupuesto.service';
import { Gasto } from '../../models/gasto.model';
import { Departamento } from '../../models/departamento.model';

@Component({
  selector: 'app-gastos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gastos.component.html'
})
export class GastosComponent implements OnInit {

  gastos: Gasto[] = [];
  gastosFiltrados: Gasto[] = [];
  departamentos: Departamento[] = [];
  esAdmin = false;
  deptoId: number | null = null;

  filtroMes = '';
  filtroDepto = '';
  ordenDesc = true;

  mensaje = '';
  tipoMensaje = '';

  constructor(
    private gastoService: GastoService,
    private authService: AuthService,
    private presupuestoService: PresupuestoService
  ) {}

  ngOnInit() {
    this.esAdmin = this.authService.getRol() === 'ADMIN';
    this.deptoId = Number(this.authService.getDepartamentoId());
    this.cargarGastos();
    if (this.esAdmin) {
      this.cargarDepartamentos();
    }
  }

  cargarGastos() {
    if (this.esAdmin) {
      this.gastoService.listarTodos().subscribe({
        next: (data) => {
          this.gastos = data;
          this.aplicarFiltros();
        },
        error: () => this.mostrarMensaje('Error al cargar los gastos.', 'error')
      });
    } else {
      const id = Number(this.authService.getId());
      this.gastoService.listarPorUsuario(id).subscribe({
        next: (data) => {
          this.gastos = data;
          this.aplicarFiltros();
        },
        error: () => this.mostrarMensaje('Error al cargar tus gastos.', 'error')
      });
    }
  }

  cargarDepartamentos() {
    this.presupuestoService.listarDepartamentos().subscribe({
      next: (data) => this.departamentos = data
    });
  }

  aplicarFiltros() {
    let resultado = [...this.gastos];

    if (this.filtroMes) {
      resultado = resultado.filter(g =>
        g.fecha && g.fecha.startsWith(this.filtroMes)
      );
    }

    if (this.esAdmin && this.filtroDepto) {
      resultado = resultado.filter(g =>
        g.usuario?.departamento?.id === Number(this.filtroDepto)
      );
    }

    if (!this.esAdmin) {
      resultado = resultado.filter(g =>
        g.usuario?.departamento?.id === this.deptoId
      );
    }

    resultado.sort((a, b) => {
      const fechaA = a.fecha ? new Date(a.fecha).getTime() : 0;
      const fechaB = b.fecha ? new Date(b.fecha).getTime() : 0;
      return this.ordenDesc ? fechaB - fechaA : fechaA - fechaB;
    });

    this.gastosFiltrados = resultado;
  }

  toggleOrden() {
    this.ordenDesc = !this.ordenDesc;
    this.aplicarFiltros();
  }

  limpiarFiltros() {
    this.filtroMes = '';
    this.filtroDepto = '';
    this.aplicarFiltros();
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
    if (!confirm('¿Estás seguro de aprobar este gasto?')) return;

    this.gastoService.aprobar(id).subscribe({
      next: () => {
        this.mostrarMensaje('Gasto aprobado correctamente.', 'ok');
        this.cargarGastos();
      },
      error: () => this.mostrarMensaje('Error al aprobar el gasto.', 'error')
    });
  }

  rechazar(id: number) {
    if (!confirm('¿Estás seguro de rechazar este gasto?')) return;

    this.gastoService.rechazar(id).subscribe({
      next: () => {
        this.mostrarMensaje('Gasto rechazado.', 'ok');
        this.cargarGastos();
      },
      error: () => this.mostrarMensaje('Error al rechazar el gasto.', 'error')
    });
  }
}