import { Departamento } from './departamento.model';

export interface CajaChica {
  id: number;
  montoInicial: number;
  saldoActual: number;
  departamento?: Departamento;
}