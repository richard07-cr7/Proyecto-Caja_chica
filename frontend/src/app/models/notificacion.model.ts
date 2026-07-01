import { Usuario } from './usuario.model';
import { Gasto } from './gasto.model';
import { PresupuestoArea } from './presupuesto-area.model';

export interface Notificacion {
  id: number;
  mensaje: string;
  tipo: string;
  leido: boolean;
  fechaCreacion: string;
  usuario?: Usuario;
  gasto?: Gasto;
  presupuestoArea?: PresupuestoArea;
}