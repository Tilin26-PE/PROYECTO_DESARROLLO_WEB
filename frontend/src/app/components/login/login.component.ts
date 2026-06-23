import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <main class="page-shell" style="max-width: 460px; margin: 40px auto;">
      <div class="tabs-container" style="padding: 30px;">
        <h2 style="margin: 0 0 8px 0; text-align: center; color: white; font-weight: 800;">Iniciar Sesión</h2>
        <p style="color: var(--muted); text-align: center; font-size: 0.9rem; margin-bottom: 24px;">Ingresa tus credenciales para acceder a tu perfil y carrito.</p>

        <form (ngSubmit)="onSubmit()" class="admin-form" style="display: flex; flex-direction: column; gap: 16px;">
          
          <div>
            <label for="username" style="display: block; margin-bottom: 6px; font-weight: 600;">Nombre de Usuario</label>
            <input 
              type="text" 
              id="username" 
              name="username" 
              [(ngModel)]="username" 
              required 
              placeholder="Tu usuario"
              style="width: 100%; padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div>
            <label for="password" style="display: block; margin-bottom: 6px; font-weight: 600;">Contraseña</label>
            <input 
              type="password" 
              id="password" 
              name="password" 
              [(ngModel)]="password" 
              required 
              placeholder="••••••••"
              style="width: 100%; padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div *ngIf="errorMessage()" style="color: #ef4444; font-weight: bold; font-size: 0.88rem; text-align: center; margin: 4px 0;">
            ⚠️ {{ errorMessage() }}
          </div>

          <div style="margin-top: 10px;">
            <button type="submit" class="button-solid" style="width: 100%; padding: 12px; font-size: 1rem;">Entrar</button>
          </div>

        </form>

        <div style="border-top: 1px solid var(--border); margin-top: 24px; padding-top: 18px; text-align: center; font-size: 0.88rem; color: var(--muted);">
          ¿No tienes una cuenta aún? 
          <a routerLink="/register" style="color: var(--accent-2); font-weight: bold; text-decoration: none;">Regístrate aquí</a>
        </div>
      </div>
    </main>
  `
})
export class LoginComponent {
  public username = '';
  public password = '';
  public errorMessage = signal<string>('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  loginWithGoogle() {
  window.location.href =
    'http://localhost:8080/oauth2/authorization/google';
}
  onSubmit() {
    this.errorMessage.set('');
    if (this.username.trim() && this.password.trim()) {
      this.authService.login({
        username: this.username,
        password: this.password
      }).subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.errorMessage.set(err.error?.error || 'Usuario o contraseña incorrectos.');
        }
      });
    }
  }
}
