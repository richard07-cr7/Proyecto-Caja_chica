import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { NotificacionService } from '../../core/services/notificacion.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {

  username: string | null = null;
  esAdmin = false;
  noLeidas = 0;

  constructor(
    private authService: AuthService,
    private notificacionService: NotificacionService,
    private router: Router
  ) {}

  ngOnInit() {
    this.username = this.authService.getUsername();
    this.esAdmin = this.authService.getRol() === 'ADMIN';

    if (this.authService.estaLogueado()) {
      this.cargarNoLeidas();
    }
  }

  cargarNoLeidas() {
    this.notificacionService.noLeidas().subscribe({
      next: (response) => this.noLeidas = response.count ?? 0,
      error: () => this.noLeidas = 0
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}