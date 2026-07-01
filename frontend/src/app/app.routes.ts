import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GastosComponent } from './pages/gastos/gastos.component';
import { RegistrarGastoComponent } from './pages/registrar-gasto/registrar-gasto.component';
import { PresupuestoComponent } from './pages/presupuesto/presupuesto.component';
import { UsuariosComponent } from './pages/usuarios/usuarios.component';
import { NotificacionesComponent } from './pages/notificaciones/notificaciones.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'gastos', component: GastosComponent },
  { path: 'registrar-gasto', component: RegistrarGastoComponent },
  { path: 'presupuesto', component: PresupuestoComponent },
  { path: 'usuarios', component: UsuariosComponent },
  { path: 'notificaciones', component: NotificacionesComponent },
  { path: '**', redirectTo: 'login' }
];