import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './pages/navbar/navbar.component';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NavbarComponent],
  templateUrl: './app.html'
})
export class AppComponent {
  constructor(private authService: AuthService) {}

  // Oculta el navbar en la página de login
  mostrarNavbar() {
    return this.authService.estaLogueado();
  }
}