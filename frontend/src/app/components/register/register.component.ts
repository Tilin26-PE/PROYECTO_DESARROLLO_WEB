import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <main class="page-shell" style="max-width: 480px; margin: 40px auto;">
      <div class="tabs-container" style="padding: 30px;">
        <h2 style="margin: 0 0 8px 0; text-align: center; color: white; font-weight: 800;">Crear una Cuenta</h2>
        <p style="color: var(--muted); text-align: center; font-size: 0.9rem; margin-bottom: 24px;">Únete a nuestra plataforma para guardar favoritos y realizar compras.</p>

        <form (ngSubmit)="onSubmit()" class="admin-form" style="display: flex; flex-direction: column; gap: 16px;">
          
          <div>
            <label for="username" style="display: block; margin-bottom: 6px; font-weight: 600;">Nombre de Usuario</label>
            <input 
              type="text" 
              id="username" 
              name="username" 
              [(ngModel)]="username" 
              required 
              placeholder="Ej. gamer123"
              style="width: 100%; padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div>
            <label for="email" style="display: block; margin-bottom: 6px; font-weight: 600;">Correo Electrónico</label>
            <input 
              type="email" 
              id="email" 
              name="email" 
              [(ngModel)]="email" 
              required 
              placeholder="tuemail&#64;dominio.com"
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
              placeholder="Mínimo 6 caracteres"
              style="width: 100%; padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div>
            <label for="confirmPassword" style="display: block; margin-bottom: 6px; font-weight: 600;">Confirmar Contraseña</label>
            <input 
              type="password" 
              id="confirmPassword" 
              name="confirmPassword" 
              [(ngModel)]="confirmPassword" 
              required 
              placeholder="Repite la contraseña"
              style="width: 100%; padding: 12px 16px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div *ngIf="errorMessage()" style="color: #ef4444; font-weight: bold; font-size: 0.88rem; text-align: center; margin: 4px 0;">
            ⚠️ {{ errorMessage() }}
          </div>

          <div style="margin-top: 10px;">
            <button type="submit" class="button-solid" style="width: 100%; padding: 12px; font-size: 1rem;">Registrarse</button>
          </div>

        </form>

        <div style="border-top: 1px solid var(--border); margin-top: 24px; padding-top: 18px; text-align: center; font-size: 0.88rem; color: var(--muted);">
          ¿Ya posees una cuenta? 
          <a routerLink="/login" style="color: var(--accent-2); font-weight: bold; text-decoration: none;">Inicia sesión aquí</a>
        </div>
      </div>
    </main>
  `
})
export class RegisterComponent {
  public username = '';
  public email = '';
  public password = '';
  public confirmPassword = '';
  public errorMessage = signal<string>('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.errorMessage.set('');
    
    if (!this.username.trim() || !this.email.trim() || !this.password.trim()) {
      this.errorMessage.set('Todos los campos son requeridos.');
      return;
    }

    if (this.password.length < 4) {
      this.errorMessage.set('La contraseña debe tener al menos 4 caracteres.');
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorMessage.set('Las contraseñas ingresadas no coinciden.');
      return;
    }

    this.authService.register({
      username: this.username,
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.error || 'No se pudo crear la cuenta.');
      }
    });
  }
}
