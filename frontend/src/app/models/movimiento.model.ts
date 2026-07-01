import { CajaChica } from './caja-chica.model';

export interface Movimiento {
  id: number;
  tipo: string;
  monto: number;
  descripcion: string;
  fecha: string;
  cajaChica: CajaChica;
}