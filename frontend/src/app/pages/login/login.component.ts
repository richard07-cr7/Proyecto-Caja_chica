import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  username = '';
  password = '';
  mensaje = '';
  tipoMensaje = '';

  constructor(private authService: AuthService, private router: Router) {
    // Si ya está logueado, redirigir al dashboard
    if (this.authService.estaLogueado()) {
      this.router.navigate(['/dashboard']);
    }
  }

  login() {
    this.mensaje = '';

    if (!this.username || !this.password) {
      this.mensaje = 'Por favor completa todos los campos.';
      this.tipoMensaje = 'error';
      return;
    }

    this.authService.login(this.username, this.password).subscribe({
      next: (data) => {
        this.authService.guardarSesion(data);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.mensaje = err.error?.message || 'Usuario o contraseña incorrectos.';
        this.tipoMensaje = 'error';
      }
    });
  }

  onEnter(event: KeyboardEvent) {
    if (event.key === 'Enter') this.login();
  }
}