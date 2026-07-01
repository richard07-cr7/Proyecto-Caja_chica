import { Departamento } from './departamento.model';

export interface PresupuestoArea {
  id: number;
  mes: string;
  presupuestoMensual: number;
  consumoActual: number;
  departamento?: Departamento;
}