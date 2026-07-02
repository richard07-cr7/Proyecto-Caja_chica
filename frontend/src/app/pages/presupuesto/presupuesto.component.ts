import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PresupuestoService } from '../../core/services/presupuesto.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { PresupuestoArea } from '../../models/presupuesto-area.model';
import { Departamento } from '../../models/departamento.model';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-presupuesto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './presupuesto.component.html'
})
export class PresupuestoComponent implements OnInit {

  presupuestos: PresupuestoArea[] = [];
  departamentos: Departamento[] = [];
  usuarios: Usuario[] = [];

  nombreDepto = '';
  mensajeDepto = '';
  tipoMensajeDepto = '';

  deptoId: number | null = null;
  mes = '';
  presupuestoMensual: number | null = null;
  mensajePresupuesto = '';
  tipoMensajePresupuesto = '';
  mesMinimo = new Date().toISOString().slice(0, 7);

  constructor(
    private presupuestoService: PresupuestoService,
    private usuarioService: UsuarioService
  ) { }

  ngOnInit() {
    this.cargarDepartamentos();
    this.cargarPresupuestos();
    this.cargarUsuarios();
  }

  cargarDepartamentos() {
    this.presupuestoService.listarDepartamentos().subscribe({
      next: (data) => this.departamentos = data
    });
  }

  cargarPresupuestos() {
    this.presupuestoService.listarPresupuestos().subscribe({
      next: (data) => this.presupuestos = data
    });
  }

  cargarUsuarios() {
    this.usuarioService.listar().subscribe({
      next: (data) => this.usuarios = data
    });
  }

  empleadosPorDepto(deptoId: number): number {
    return this.usuarios.filter(u => u.departamento?.id === deptoId).length;
  }

  validarMes() {
    if (this.mes < this.mesMinimo) {
      this.mes = this.mesMinimo;
    }
  }
  crearDepartamento() {
    if (!this.nombreDepto) {
      this.mensajeDepto = 'Ingresa un nombre para el departamento.';
      this.tipoMensajeDepto = 'error';
      return;
    }
    this.presupuestoService.crearDepartamento(this.nombreDepto).subscribe({
      next: () => {
        this.mensajeDepto = 'Departamento creado correctamente.';
        this.tipoMensajeDepto = 'ok';
        this.nombreDepto = '';
        this.cargarDepartamentos();
      },
      error: () => {
        this.mensajeDepto = 'Error al crear el departamento.';
        this.tipoMensajeDepto = 'error';
      }
    });
  }

  asignarPresupuesto() {
    if (!this.deptoId || !this.mes || !this.presupuestoMensual) {
      this.mensajePresupuesto = 'Completa todos los campos.';
      this.tipoMensajePresupuesto = 'error';
      return;
    }
    this.presupuestoService.crearPresupuesto(this.deptoId, this.mes, this.presupuestoMensual).subscribe({
      next: () => {
        this.mensajePresupuesto = 'Presupuesto asignado correctamente.';
        this.tipoMensajePresupuesto = 'ok';
        this.deptoId = null;
        this.mes = '';
        this.presupuestoMensual = null;
        this.cargarPresupuestos();
      },
      error: () => {
        this.mensajePresupuesto = 'Error al asignar presupuesto.';
        this.tipoMensajePresupuesto = 'error';
      }
    });
  }

  disponible(presupuesto: PresupuestoArea) {
    return (presupuesto.presupuestoMensual || 0) - (presupuesto.consumoActual || 0);
  }
}
