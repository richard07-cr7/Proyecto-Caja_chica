import { Departamento } from './departamento.model';

export interface Usuario {
  id: number;
  username: string;
  password?: string;
  rol: string;
  departamento?: Departamento;
}