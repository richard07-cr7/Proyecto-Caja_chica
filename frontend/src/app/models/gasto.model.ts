import { Usuario } from './usuario.model';
import { CajaChica } from './caja-chica.model';
import { PresupuestoArea } from './presupuesto-area.model';

export interface Gasto {
  id: number;
  descripcion: string;
  monto: number;
  categoria: string;
  comprobante?: string;
  estado?: string;
  fecha?: string;
  cajaChica?: CajaChica;
  usuario?: Usuario;
  presupuestoArea?: PresupuestoArea;
}