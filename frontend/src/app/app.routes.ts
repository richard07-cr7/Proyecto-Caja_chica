import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GastosComponent } from './pages/gastos/gastos.component';
import { RegistrarGastoComponent } from './pages/registrar-gasto/registrar-gasto.component';
import { PresupuestoComponent } from './pages/presupuesto/presupuesto.component';
import { UsuariosComponent } from './pages/usuarios/usuarios.component';
import { NotificacionesComponent } from './pages/notificaciones/notificaciones.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'gastos', component: GastosComponent, canActivate: [authGuard] },
  { path: 'registrar-gasto', component: RegistrarGastoComponent, canActivate: [authGuard] },
  { path: 'presupuesto', component: PresupuestoComponent, canActivate: [authGuard, adminGuard] },
  { path: 'usuarios', component: UsuariosComponent, canActivate: [authGuard, adminGuard] },
  { path: 'notificaciones', component: NotificacionesComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];