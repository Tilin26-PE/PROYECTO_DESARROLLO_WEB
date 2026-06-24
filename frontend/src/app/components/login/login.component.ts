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

        <div style="display: flex; align-items: center; gap: 10px; margin: 20px 0;">
  <div style="flex: 1; height: 1px; background: var(--border);"></div>
  <span style="color: var(--muted); font-size: 0.82rem;">o</span>
  <div style="flex: 1; height: 1px; background: var(--border);"></div>
</div>

<button (click)="loginWithGoogle()" type="button"
  style="width: 100%; padding: 12px; border-radius: 12px; border: 1px solid var(--border); background: white; color: #333; font-size: 0.95rem; font-weight: 600; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 10px;">
  <svg width="20" height="20" viewBox="0 0 48 48">
    <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/>
    <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
    <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
    <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.18 1.48-4.97 2.31-8.16 2.31-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
  </svg>
  Continuar con Google
</button>

<div style="margin-top: 20px; text-align: center; font-size: 0.88rem; color: var(--muted);">
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
  ) { }
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
